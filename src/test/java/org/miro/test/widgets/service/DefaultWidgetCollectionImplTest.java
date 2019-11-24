package org.miro.test.widgets.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultWidgetCollectionImplTest {

    @Autowired
    @Qualifier("concurrentWidgetsCollectionImpl")
    private WidgetCollection widgetCollection;
    private Set<Widget> widgets = new HashSet<>();
    private Widget widget;

    /**
     * Перед каждым тестом готовим пример
     */
    @BeforeEach
    void beforeTest(){
        //Строим множество виджетов
        widgets.clear();
        widgetCollection.clear();
        widgets.add(new Widget(0L, 9L, 3L, 4L, 0L));
        widgets.add(new Widget(7L,9L,4L,6L,1L));
        widgets.add(new Widget(4L,4L,2L,2L,2L));
        widget = widgets.stream().findFirst().get();
        widgets.stream().forEach(widget -> widgetCollection.put(widget.getUuid(), widget));
    }

    /**
     * Вовращает true, если коллекция пуста
     */
    @Test
    void isEmpty() {
        widgetCollection.clear(); // заодно проверим, что работает clear
        assertTrue(widgetCollection.isEmpty()); // ничего ещё не добавили, потому так
    }

    /**
     * Возвращает false, если что-то в коллекции есть
     */
    @Test
    void isNotEmpty() {
        widgets.stream().forEach(widget -> widgetCollection.put(widget.getUuid(), widget));
        assertFalse(widgetCollection.isEmpty());
    }

    /**
     * Возвращаем все значения, которые в коллекции есть
     */
    @Test
    void values() {
        Collection<Widget> values = widgetCollection.values();
        assertEquals(widgets.size(), values.size()); // вернулось всё...
        assertEquals(0, values.stream().filter(value -> !widgets.contains(value)).count()); // и именно то, что добавили
    }

    @Test
    void get() {
        Widget found = widgetCollection.get(widget.getUuid());
        assertWidget(widget, found);

    }

    @Test
    void put() {
        Widget newWidget = new Widget(0L, 3L, 3L, 3L, 3L);
        widgetCollection.put(newWidget.getUuid(), newWidget);

        //Проверяем, что добавленное нашлось в коллекции
        assertWidget(newWidget, widgetCollection.get(newWidget.getUuid()));
    }

    private void assertWidget(Widget fidgetToFind, Widget found) {
        //Проверям, что нашлось и нашлось то, что нужно
        assertNotNull(found);
        assertEquals(fidgetToFind.getX(), found.getX());
        assertEquals(fidgetToFind.getY(), found.getY());
        assertEquals(fidgetToFind.getzIndex(), found.getzIndex());
    }

    @Test
    void remove() {
        widgetCollection.remove(widget.getUuid());

        //Проверяем, что удалено
        Widget found = widgetCollection.get(this.widget.getUuid());
        assertNull(found);
    }

    @Test
    void correctZIndex() {
        widgetCollection.correctZIndex(1L);

        //После предыдущей операции не должно быть виджетов с индексом = 1
        assertEquals(0, widgetCollection.values().stream().filter(item -> item.getzIndex().equals(1L)).count());
    }

    @Test
    void correctBiggerZIndex() {
        widgetCollection.correctZIndex(4L);

        //После предыдущей операции ничего поменяться не должнло, поскольку все Z-Index меньше
        assertEquals(1, widgetCollection.values().stream().filter(item -> item.getzIndex().equals(0L)).count());
        assertEquals(1, widgetCollection.values().stream().filter(item -> item.getzIndex().equals(1L)).count());
        assertEquals(1, widgetCollection.values().stream().filter(item -> item.getzIndex().equals(2L)).count());
        assertEquals(0, widgetCollection.values().stream().filter(item -> item.getzIndex().compareTo(2L) > 0).count());
    }
}