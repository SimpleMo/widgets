package org.miro.test.widgets.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WidgetsServiceImplTest {

    @Autowired
    private WidgetsService widgetsService;
    private Widget widget;

    /**
     * Для большинства тестов нужен хотя бы один виджет в списке, на который у нас есть ссылка. Потому добавим тут.
     */
    @BeforeEach
    void beforeTest(){
        widget = widgetsService.createWidget(1L, 1L, 1L, 1L, 1L);
    }

    @Test
    void findByUuid() {
        Widget foundByUuid = widgetsService.findByUuid(widget.getUuid());
        assertNotNull(foundByUuid);
        assertEquals(widget, foundByUuid);
    }

    /**
     * Простейшая проверка, что виджет создаётся и возвращается сервисом
     */
    @Test
    void createWidget() {
        Widget widget = widgetsService.createWidget(1L, 1L, 1L, 1L, 1L);
        assertNotNull(widget);
        assertNotNull(widget.getUuid());
    }

    /**
     * Проверяем, что пересчитывается zIndex в том случае, когда добавляем в список новый виджет с существующим zIndex
     */
    @Test
    void checkZIndexChanges(){
        widgetsService.createWidget(2L, 2L, 1L, 1L, 1L);
        assertEquals(2L, this.widget.getzIndex());
    }
}