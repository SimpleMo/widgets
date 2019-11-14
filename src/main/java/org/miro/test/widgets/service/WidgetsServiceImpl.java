package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class WidgetsServiceImpl implements WidgetsService {
    private Map<UUID, Widget> widgets = new HashMap<>();
    private Long defaultZIndex = 0L;

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

    @Override
    public Widget updateWidget(UUID uuid, Long x, Long y, Long zIndex, Long width, Long height) {
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
        if (zIndex == null) { // в э том случае помещаем на передний план
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
