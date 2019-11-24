package org.miro.test.widgets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ConcurrentWidgetTest {
    private ConcurrentWidget concurrentWidget;
    private Widget widget;

    /**
     * Для большинства тестов нужен хотя бы один виджет в списке, на который у нас есть ссылка. Потому добавим тут.
     */
    @BeforeEach
    void beforeTest(){
        widget = new Widget(1L, 1L, 1L, 1L, 1L);
        concurrentWidget = new ConcurrentWidget(widget);
    }

    @Test
    void setX() {
        concurrentWidget.setX(2L);
        assertEquals(2L, widget.getX()); // поменялось у того, что обёрнуто
        assertEquals(2L, concurrentWidget.getX()); // обёртка возвращает то, что нужно.
    }

    @Test
    void setY() {
        concurrentWidget.setY(2L);
        assertEquals(2L, widget.getY()); // поменялось у того, что обёрнуто
        assertEquals(2L, concurrentWidget.getY()); // обёртка возвращает то, что нужно.
    }

    @Test
    void setzIndex() {
        concurrentWidget.setzIndex(2L);
        assertEquals(2L, widget.getzIndex()); // поменялось у того, что обёрнуто
        assertEquals(2L, concurrentWidget.getzIndex()); // обёртка возвращает то, что нужно.
    }
}