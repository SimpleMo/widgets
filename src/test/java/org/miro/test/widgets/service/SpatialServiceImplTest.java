package org.miro.test.widgets.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpatialServiceImplTest {

    @Autowired
    @Qualifier("spatialServiceStub")
    private SpatialService spatialService;

    @Autowired
    private MatrixHelper matrixHelper;
    private Set<Widget> widgets = new HashSet<>();

    /**
     * Перед каждым тестом готовим пример
     */
    @BeforeEach
    void beforeTest(){
        //Строим множество виджетов
        widgets.clear();
        widgets.add(new Widget(0L,9L,3L,4L,0L));
        widgets.add(new Widget(7L,9L,4L,6L,1L));
        widgets.add(new Widget(4L,4L,2L,2L,2L));

        //Добавляем те виджеты, что получились, в инжексы
        widgets
            .stream()
            .forEach(widget -> {
                spatialService.addToByLeftSideIndex(widget.getX(), widget.getUuid());
                spatialService.addToByTopSideIndex(widget.getY(), widget.getUuid());
                spatialService.addToByRightSideIndex(widget.getX() + widget.getWidth(), widget.getUuid());
                spatialService.addToByBottomSideIndex(widget.getY() - widget.getHeight(), widget.getUuid());
            });
    }

    /**
     * чистим индексы после каждого теста.
     */
    @AfterEach
    void  afterTest(){
        matrixHelper.clearIndexes();
    }

    /**
     * В нашем тестовом примере у всех виджетов координата по Ox разная => по каждому ключу индекса должно быть множество с ровно одним элементом
     */
    @Test
    void addToByLeftSideIndex() {
        //Проверяем, что заполнен индекс по X
        widgets.stream().forEach(widget -> {
            Set<UUID> byX = matrixHelper.getIndexByX(widget.getX());
            assertNotNull(byX); // чего-то нашлось
            assertEquals(1, byX.size()); // но всего с одним элементом

        });
    }

    /**
     * В тестовом примере два виджета с одниковой координатой y = 9
     */
    @Test
    void addToByTopSideIndex() {
        //Проверяем, что заполнен индекс по X
        widgets.stream().forEach(widget -> {
            Set<UUID> byY = matrixHelper.getIndexByY(widget.getY());
            assertNotNull(byY); // чего-то нашлось
            assertEquals(widget.getY() == 9L ? 2 : 1, byY.size()); // но всего с одним элементом, если речь не о y = 9
        });
    }

    /**
     * Координаты по X и ширина у виджетов разная => по каждому ключу множество из одного элемента
     */
    @Test
    void addToByRightSideIndex() {
        widgets.stream().forEach(widget -> {
            Set<UUID> byWidth = matrixHelper.getIndexByWidth(widget.getX() + widget.getWidth());
            assertNotNull(byWidth); // чего-то нашлось
            assertEquals(1, byWidth.size()); // но всего с одним элементом
        });
    }

    /**
     * Координаты по Y и высота у всех виджетов разная высота => по каждому ключу из одного элемента
     */
    @Test
    void addToByBottomSideIndex() {
        //Проверяем, что заполнен индекс по Height
        widgets.stream().forEach(widget -> {
            Set<UUID> byHeight = matrixHelper.getIndexByHeight(widget.getY() - widget.getHeight());
            assertNotNull(byHeight); // чего-то нашлось
            assertEquals(1, byHeight.size()); // но всего с одним элементом
        });
    }

    /**
     * Возможна ситуация, когда отберутся все виджеты
     */
    @Test
    void allWidgetsAreFilteredByPosition() {
        Set<UUID> found = spatialService.findByPosition(0L, 9L, 11L, 9L);
        assertEquals(3, found.size());
    }

    /**
     * Возможна ситуация, когда найдётся только два виджета
     */
    @Test
    void twoWidgetsAreFilteredByPosition() {
        Set<UUID> found = spatialService.findByPosition(0L, 9L, 6L, 9L);
        assertEquals(2, found.size());
    }

    @Test
    void noWidgetsAreFilteredByPosition() {
        Set<UUID> found = spatialService.findByPosition(0L, 3L, 3L, 3L);
        assertEquals(0, found.size());
    }

    @Test
    void leftSideOfPatternIsToTheRightOfAnyWidget() {
        Set<UUID> found = spatialService.findByPosition(12L, 9L, 6L, 9L);
        assertEquals(0, found.size());
    }
}