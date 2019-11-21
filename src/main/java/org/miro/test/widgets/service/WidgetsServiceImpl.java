package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class WidgetsServiceImpl implements WidgetsService {
    private Map<UUID, Widget> widgets = new HashMap<>();
    private Long defaultZIndex = 0L;

    @Autowired
    private SpatialService spatialService;

    @Override
    public List<Widget> getWidgets() {
        if(widgets.isEmpty()){
            return new ArrayList<>();
        }

        return new ArrayList<>(widgets.values());
    }

    @Override
    public Widget findByUuid(UUID uuid) {
        return widgets.get(uuid);
    }

    @Override
    public Widget createWidget(Long x, Long y, Long zIndex, Long width, Long height) {
        Widget widget = getNewWidget(x, y, zIndex, width, height);
        if(zIndex != null){
            correctZIndex(widget.getzIndex());
        }
        widgets.put(widget.getUuid(), widget);

        return widget;
    }

    private void putToSortedMap(SortedMap<Long, List<Widget>> sortedMap, Long key, Widget value) {
        if(!sortedMap.containsKey(key)){
            sortedMap.put(key, new LinkedList<>());
        }
        sortedMap.get(key).add(value);
    }

    @Override
    public Widget updateWidget(UUID uuid, Long x, Long y, Long zIndex, Long width, Long height) {
        Widget result = widgets.get(uuid);
        if(result == null){ // не нашлось, обновлять нечего
            return null;
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
                                spatialService::rearrangeByBottomSideIndex, widget::setWidth);
                    }
                    widget.setzIndex(zIndex != null ? zIndex : widget.getzIndex());
                };
        result.visit(widgetUpdater);
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
        Widget widget = widgets.get(uuid);
        if(widget == null){
            return null;
        }

        //Удаляем найденное из всех индексов и списков
        spatialService.removeFromIndexes(widget);
        widgets.remove(uuid);

        return widget;
    }

    @Override
    public List<Widget> findWidgetByPosition(Long x, Long y, Long width, Long height) {
        Set<UUID> uuids = spatialService.findByPosition(x, y, width, height);
        if(CollectionUtils.isEmpty(uuids)){
            return new ArrayList<>();
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
        Widget widget = new Widget(x, y, width, height, zIndex == null ? defaultZIndex : zIndex);

        //Передний план мог поменяться...
        if (zIndex == null) {
            defaultZIndex--;
        } else if (defaultZIndex.compareTo(zIndex) >= 0) {
            defaultZIndex = zIndex;
        }

        //Ну и добавим в индексы...
        spatialService.addToByLeftSideIndex(widget.getX(), widget.getUuid());
        spatialService.addToByTopSideIndex(widget.getY(), widget.getUuid());
        spatialService.addToByRightSideIndex(widget.getX() + widget.getWidth(), widget.getUuid());
        spatialService.addToByBottomSideIndex(widget.getY() - widget.getHeight(), widget.getUuid());

        return widget;
    }

    /**
     * Сдвигаем все виджеты в списке, у которых Z-Index >= переданному в большую сторону
     * @param zIndex индекс
     */
    private void correctZIndex(Long zIndex) {
        Consumer<Widget> consumer = widget -> widget.setzIndex(widget.getzIndex() + 1);
        widgets.entrySet().stream()
                .filter(entry -> entry.getValue().getzIndex().compareTo(zIndex) >= 0)
                .forEach(entry ->  entry.getValue().visit(consumer));
    }

}
