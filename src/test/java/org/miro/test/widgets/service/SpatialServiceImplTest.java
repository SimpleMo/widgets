package org.miro.test.widgets.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpatialServiceImplTest {

    @Autowired
    @Qualifier("spatialServiceStub")
    private SpatialService spatialService;

    @Autowired
    private MatrixHelper matrixHelper;
    private Set<Widget> widgets = new HashSet<>();
    private Widget widget; // для некоторых тестов нужна ссылка на какой-нибудь виджет

    /**
     * Перед каждым тестом готовим пример
     */
    @BeforeEach
    void beforeTest(){
        //Строим множество виджетов
        widgets.clear();
        widgets.add(new Widget(0L, 9L, 3L, 4L, 0L));
        widgets.add(new Widget(7L,9L,4L,6L,1L));
        widgets.add(new Widget(4L,4L,2L,2L,2L));
        widget = widgets.stream().findFirst().get(); // нам подойдёт любойЮ но лучше первый...

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
            Set<UUID> byX = matrixHelper.getIndexByLeftSide(widget.getX());
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
            Set<UUID> byY = matrixHelper.getIndexByTopSide(widget.getY());
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
            Set<UUID> byWidth = matrixHelper.getIndexByRightSide(widget.getX() + widget.getWidth());
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
            Set<UUID> byHeight = matrixHelper.getIndexByBottomSide(widget.getY() - widget.getHeight());
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

    /**
     * Ни одного не найдётся. Паттерн среди виджетов
     */
    @Test
    void noWidgetsAreFilteredByPosition() {
        Set<UUID> found = spatialService.findByPosition(0L, 3L, 3L, 3L);
        assertEquals(0, found.size());
    }

    /**
     * Ни одного не найдётся, паттерн правее виджетов
     */
    @Test
    void leftSideOfPatternIsToTheRightOfAnyWidget() {
        Set<UUID> found = spatialService.findByPosition(12L, 9L, 6L, 9L);
        assertEquals(0, found.size());
    }

    /**
     * Ни одного не найдётся, паттерн левее виджетов
     */
    @Test
    void rightSideOfPatternIsToTheLeftOfAnyWidget(){
        Set<UUID> found = spatialService.findByPosition(-4L, 9L, 2L, 9L);
        assertEquals(0, found.size());
    }

    /**
     * Паттерн пересекает один из виджетов, но не содержит цедиком ни одного => ничего не должно вернуться
     */
    @Test
    void patternIntersectsWidget(){
        Set<UUID> found = spatialService.findByPosition(-4L, 9L, 5L, 9L);
        assertEquals(0, found.size());
    }

    /**
     * Паттерн пересекает один виджет и полностью содержит внутри другой.
     */
    @Test
    void patternContainsOneWidgetAndIntersectsAnotherOne(){
        Set<UUID> found = spatialService.findByPosition(-4L, 9L, 9L, 9L);
        assertEquals(1, found.size());
    }

    @Test
    void removeFromIndexes() {
        spatialService.removeFromIndexes(widget);

        //Не должно остаться следов от удалённого виджета
        assertFalse(matrixHelper.getIndexByLeftSide(widget.getX()).contains(widget.getUuid()));
        assertFalse(matrixHelper.getIndexByTopSide(widget.getY()).contains(widget.getUuid()));
        assertFalse(matrixHelper.getIndexByRightSide(widget.getX() + widget.getWidth()).contains(widget.getUuid()));
        assertFalse(matrixHelper.getIndexByBottomSide(widget.getY() - widget.getHeight()).contains(widget.getUuid()));
    }

    /**
     * Проверяем, что при попытке удаления из индексов того, чего там нет, не приводит к сбою и изменению состава индексов
     */
    @Test
    void removeNonexistentWidgetFormIndexes(){
        Integer initialCapacity = matrixHelper.getAllItems().stream().map(Set::size).reduce(0, Integer::sum);

        spatialService.removeFromIndexes(new Widget(0L,3L,3L,3L,null));

        //Поскольку удаляем то, чего и не было, то количество элементов в инждексах поменяться не должно
        assertEquals(initialCapacity, matrixHelper.getAllItems().stream().map(Set::size).reduce(0, Integer::sum));
    }

    /**
     * Проверяем, что инексы перестраиваются правильно для.
     */
    @Test
    void rearrangeByTopSideIndex(){
        int initialSize = matrixHelper.getIndexByTopSide(widget.getY()).size();
        spatialService.rearrangeByTopSideIndex(widget.getY(), 10L, widget.getUuid());
        assertEquals(initialSize - 1, matrixHelper.getIndexByTopSide(widget.getY()).size()); // удалилось по старому ключу
        assertEquals(1, matrixHelper.getIndexByTopSide(10L).size()); // добавилось по новому
        assertTrue(matrixHelper.getIndexByTopSide(10L).contains(widget.getUuid())); // и добавилось то, что нужно
    }

    @Test
    void rearrangeByBottomSideIndex(){
        int initialSize = matrixHelper.getIndexByBottomSide(widget.getY() - widget.getHeight()).size();
        spatialService.rearrangeByBottomSideIndex(widget.getY() - widget.getHeight(), 1L, widget.getUuid());
        assertEquals(initialSize - 1, matrixHelper.getIndexByBottomSide(widget.getY() - widget.getHeight()).size()); // удалилось по старому ключу
        assertEquals(1, matrixHelper.getIndexByBottomSide(1L).size()); // добавилось по новому
        assertTrue(matrixHelper.getIndexByBottomSide(1L).contains(widget.getUuid())); // и добавилось то, что нужно
    }

    @Test
    void rearrangeByLeftSideIndex(){
        int initialSize = matrixHelper.getIndexByLeftSide(widget.getX()).size();
        spatialService.rearrangeByLeftSideIndex(widget.getX(), -1L, widget.getUuid());
        assertEquals(initialSize - 1, matrixHelper.getIndexByLeftSide(widget.getX()).size()); // удалилось по старому ключу
        assertEquals(1, matrixHelper.getIndexByLeftSide(-1L).size()); // добавилось по новому
        assertTrue(matrixHelper.getIndexByLeftSide(-1L).contains(widget.getUuid())); // и добавилось то, что нужно
    }

    @Test
    void rearrangeByRightSideIndex(){
        int initialSize = matrixHelper.getIndexByRightSide(widget.getX() + widget.getWidth()).size();
        spatialService.rearrangeByRightSideIndex(widget.getX() + widget.getWidth(), 4L, widget.getUuid());
        assertEquals(initialSize - 1, matrixHelper.getIndexByRightSide(widget.getX() + widget.getWidth()).size()); // удалилось по старому ключу
        assertEquals(1, matrixHelper.getIndexByRightSide(4L).size()); // добавилось по новому
        assertTrue(matrixHelper.getIndexByRightSide(4L).contains(widget.getUuid())); // и добавилось то, что нужно
    }

    /**
     * Проверяем, что если пытаемся исправить несуществующий ключ индекса, то сбоя не происходит
     */
    @Test
    void rearrangeIndexWithWrongOldValue(){
        spatialService.rearrangeByTopSideIndex(8L, 10L, widget.getUuid());
        assertEquals(1, matrixHelper.getIndexByTopSide(10L).size()); // добавилось по новому
        assertTrue(matrixHelper.getIndexByTopSide(10L).contains(widget.getUuid())); // и добавилось то, что нужно
    }

}