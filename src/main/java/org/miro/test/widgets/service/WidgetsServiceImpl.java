package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class WidgetsServiceImpl implements WidgetsService {
    @Autowired
    @Qualifier("concurrentWidgetsCollectionImpl")
    private WidgetCollection widgets;
    private Long defaultZIndex = 0L;

    private ReentrantLock newWidgetAsResource = new ReentrantLock(); // лок для работы с создаваемыми виджетами
    private ReentrantLock widgetsAsResource = new ReentrantLock(); // лок для работы с коллецией виджетов

    @Autowired
    @Qualifier("concurrentSpatialServiceDecorator")
    private SpatialService spatialService;

    @Override
    public Collection<Widget> getWidgets() {
        if(widgets.isEmpty()){
            return Collections.emptyList(); // комментарий 1. Теперь будет возвращаться всегда один и тот же пустой список
        }

        return widgets.values(); // тут не получим редактируемый виджет, поскольку элементы в список добавляются в защищенном коде
    }

    @Override
    public Widget findByUuid(UUID uuid) {
        widgetsAsResource.lock(); // синхронизировано, чтобы избежать проблем при конкурентном удалении (вернулось то, что по факту уже удалено).
        try {
            return widgets.get(uuid);
        } finally {
            widgetsAsResource.unlock();
        }
    }

    @Override
    public Widget createWidget(Long x, Long y, Long zIndex, Long width, Long height) {
        Widget widget = getNewWidget(x, y, zIndex, width, height);

        widgetsAsResource.lock(); // синхронизировано, чтобы избежать проблем при конкурентной работе со списком. needCorrectZIndex должны выполнить совместно с correctZIndex
        try {
            if(needCorrectZIndex(zIndex)){
                widgets.correctZIndex(widget.getzIndex());
            }

        } finally {
            widgetsAsResource.unlock();
        }

        widgets.put(widget.getUuid(), widget);

        //Ну и добавим в индексы...
        spatialService.addToIndexes(widget);

        return widget;
    }

    /**
     * @param zIndex Z-Index добавляемого виджета
     * @return true, если нужно корректировать индексы ранее добавленных виджетов, в противном случае - false
     */
    private boolean needCorrectZIndex(Long zIndex) {
        if(zIndex == null){
            return widgets.values().stream().anyMatch(item -> defaultZIndex.equals(item.getzIndex()));
        }

        return widgets.values().stream().anyMatch(item -> zIndex.equals(item.getzIndex()));
    }

    @Override
    public Widget updateWidget(UUID uuid, Long x, Long y, Long zIndex, Long width, Long height) {
        widgetsAsResource.lock(); // синхронизировано, чтобы не получить проблем, например, при конкурентном удалении и добавлении
        Widget result;
        try {
            result = widgets.get(uuid);
            if(result == null){ // не нашлось, обновлять нечего
                return null;
            }
        } finally {
            widgetsAsResource.unlock();
        }

        //Меняем значения атрибутов виджета и при необходимости перестраиваем индексы
        Consumer<Widget> widgetUpdater =
                widget -> {
                    updateAndRearrangeWithCheck(x, widget.getX(), x, widget.getUuid(), spatialService::rearrangeByLeftSideIndex, widget::setX);
                    updateAndRearrangeWithCheck(y, widget.getY(), y, widget.getUuid(), spatialService::rearrangeByTopSideIndex, widget::setY);
                    if(x != null || width != null){
                        updateAndRearrangeWithCheck(
                                (x != null ? x : widget.getX()) + (width != null ? width : widget.getWidth()),
                                widget.getX() + widget.getWidth(),
                                width, widget.getUuid(),
                                spatialService::rearrangeByRightSideIndex, widget::setWidth);
                    }
                    if (y != null || height != null) {
                        updateAndRearrangeWithCheck(
                                (y != null ? y : widget.getY()) - (height != null ? height : widget.getHeight()),
                                widget.getY() - widget.getHeight(),
                                height, widget.getUuid(),
                                spatialService::rearrangeByBottomSideIndex, widget::setHeight);
                    }
                    widget.setzIndex(zIndex != null ? zIndex : widget.getzIndex());
                };
        result.visit(widgetUpdater); // обновление потокобезопасно на уровне сервиса
        return result;
    }

    /**
     * Вспомогательный метод для обновления атрибутов виджета и перестройки индекса
     *
     * @param key          новое значение индекса, который перестраиваем
     * @param oldKey       старое значение индекса, который перестраиваем
     * @param value        новое значение для вставки в индекс
     * @param uuid         идентификатор виджета
     * @param rearranger   метод для перестройки индекса
     * @param valueChanger метод для смены старого значения на новое
     */
    private void updateAndRearrangeWithCheck(@NonNull Long key, @NonNull Long oldKey, @Nullable Long value, @NonNull UUID uuid, @NonNull TernaryConsumer<Long, Long, UUID> rearranger, @NonNull Consumer<Long> valueChanger) {
        if (key == null || key.compareTo(oldKey) == 0 || value == null) {
            return;
        }
        rearranger.accept(oldKey, key, uuid);
        valueChanger.accept(value);
    }

    @Override
    public Widget deleteWidget(UUID uuid) {
        Widget widget;

        widgetsAsResource.lock(); // синхронизировано, чтобы избежать проблем при конкурентном поиске и удалении + очистка индексов и удаление из коллекции должны быть выполнены атомарно
        try {
            widget = widgets.get(uuid);
            if(widget == null){
                return null;
            }

            //Удаляем найденное из всех индексов и списков
            spatialService.removeFromIndexes(widget);
            widgets.remove(uuid);
        } finally {
            widgetsAsResource.unlock();
        }

        return widget;
    }

    @Override
    public List<Widget> findWidgetByPosition(Long x, Long y, Long width, Long height) {
        Set<UUID> uuids = spatialService.findByPosition(x, y, width, height);
        if(CollectionUtils.isEmpty(uuids)){
            return Collections.emptyList(); // тоже комментарий 1
        }

        return widgets.values().stream().filter(widget -> uuids.contains(widget.getUuid())).collect(Collectors.toList());
    }

    /**
     * Метод возвращает новый виджет с переданными координатами, размерами и Z-Index
     *
     * @param x      x-координата виджета не может быть null
     * @param y      y-координата виджета не может быть null
     * @param zIndex Z-Index- виджета может быть null
     * @param width  ширина виджета не может быть null
     * @param height высота не может быть null
     * @return новый виджет
     */
    private Widget getNewWidget(@NonNull Long x, @NonNull Long y, Long zIndex, @NonNull Long width, @NonNull Long height) {
        newWidgetAsResource.lock();
        try {
            Widget widget = new Widget(x, y, width, height, zIndex == null ? defaultZIndex : zIndex);

            //Передний план мог поменяться...
            if (zIndex == null) {
                defaultZIndex--;
            } else if (defaultZIndex.compareTo(zIndex) >= 0) {
                defaultZIndex = zIndex;
            }

            return widget;
        } finally {
            newWidgetAsResource.unlock();
        }
    }

}
