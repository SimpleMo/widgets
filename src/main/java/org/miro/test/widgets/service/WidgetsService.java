package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public interface WidgetsService {

    /**
     * @return список ранее созданных виджетов
     */
    List<Widget> getWidgets();

    /**
     * Возвращает виджет по переданному идентификатору
     * @param uuid идентификатор виджета
     * @return виджет, либо null, если не нашлось
     */
    Widget findByUuid(UUID uuid);

    /**
     * Создаёт виджет по переданным координатам, zIndex, ширине и высоте
     * @param x
     * @param y
     * @param zIndex
     * @param width
     * @param height
     * @return созданный виджет
     */
    Widget createWidget(@NonNull Long x, @NonNull Long y, @Nullable Long zIndex, Long width, Long height);

    /**
     * Обновляет виджет из списка. Обновляются координаты, ширина, высота и Z-Index, но не идентификатор
     * @param uuid идентификатор
     * @param x
     * @param y
     * @param zIndex
     * @param width
     * @param height
     * @return обновлённое представление виджета
     */
    Widget updateWidget(@NonNull UUID uuid, @Nullable Long x, @Nullable  Long y, @Nullable Long zIndex, @Nullable Long width, @Nullable Long height);

    /**
     * Удалеят виджет по переданному идентификатору.
     *
     * @param uuid идентификатор
     * @return представление удалённого виджета либо null, если по идентификатору ничего не нашлось
     */
    Widget deleteWidget(@NonNull UUID uuid);

    List<Widget> getWidgetByPosition();
}
