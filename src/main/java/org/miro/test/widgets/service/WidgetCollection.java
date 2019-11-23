package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;

import java.util.Collection;
import java.util.UUID;

public interface WidgetCollection {

    boolean isEmpty();
    Collection<Widget> values();
    Widget get(UUID uuid);
    Widget put(UUID key, Widget value);
    Widget remove(UUID key);

    /**
     * Сдвигаем все виджеты в списке, у которых Z-Index >= переданному в большую сторону
     * @param zIndex индекс
     */
    void correctZIndex(Long zIndex);
}
