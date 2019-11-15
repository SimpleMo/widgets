package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
public class WidgetsServiceImpl implements WidgetsService {
    private Map<UUID, Widget> widgets = new HashMap<>();
    private Long defaultZIndex = 0L;

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
        correctZIndex(widget.getzIndex());
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

        Consumer<Widget> widgetUpdater =
                widget -> {
                    widget.setX(x != null ? x : widget.getX());
                    widget.setY(y != null ? y : widget.getY());
                    widget.setzIndex(zIndex != null ? zIndex : widget.getzIndex());
                    widget.setWidth(width != null ? width : widget.getWidth());
                    widget.setHeight(height != null ? height : widget.getHeight());
                };
        result.visit(widgetUpdater);
        return result;
    }

    @Override
    public Widget deleteWidget(UUID uuid) {
        Widget result = widgets.get(uuid);
        if(result == null){
            return null;
        }

        widgets.remove(uuid);
        return result;
    }

    @Override
    public List<Widget> getWidgetByPosition() {
        return null;
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
        Widget widget;
        if (zIndex == null) { // в этом случае помещаем на передний план
            return new Widget(x, y, width, height, defaultZIndex);
        }
        widget = new Widget(x, y, width, height, zIndex);

        if (defaultZIndex.compareTo(zIndex) > 0) { // передний план мог поменяться...
            defaultZIndex = zIndex;
        }
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
