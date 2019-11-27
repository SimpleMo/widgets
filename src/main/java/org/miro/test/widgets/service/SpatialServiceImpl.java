package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
public class SpatialServiceImpl implements SpatialService {
    protected SortedMap<Long, Set<UUID>> indexByLeftSide = new TreeMap<>(Long::compareTo);
    protected SortedMap<Long, Set<UUID>> indexByTopSide = new TreeMap<>(Long::compareTo);
    protected SortedMap<Long, Set<UUID>> indexByRightside = new TreeMap<>(Long::compareTo);
    protected SortedMap<Long, Set<UUID>> indexByBottomSide = new TreeMap<>(Long::compareTo);

    @Override
    public Set<UUID> findByPosition(Long x, Long y, Long width, Long height) {
        //Строим промежуточные множества по X, Y, Width и Height
        Set<UUID> byLeftSide = getValuesFromIndex(indexByLeftSide.tailMap(x)); // правее левой границы
        Set<UUID> byBottomSide = getValuesFromIndex(indexByBottomSide.tailMap(y - height)); // выше нижней

        Set<UUID> byRightSide = getValuesFromIndex(indexByRightside.headMap(x + width)); // левее правой границы
        if(!CollectionUtils.isEmpty(indexByRightside.get(x + width))){
            byRightSide.addAll(indexByRightside.get(x + width));
        }
        Set<UUID> byTopSide = getValuesFromIndex(indexByTopSide.headMap(y)); // ниже верхней границы
        if(!CollectionUtils.isEmpty(indexByTopSide.get(y))){
            byTopSide.addAll(indexByTopSide.get(y));
        }

        //Нам подходят только те UUID, которые есть во всех множествах одновременно. Собираем из них результат.
        Set<UUID> result =
                byLeftSide.stream().filter(byTopSide::contains).filter(byRightSide::contains).filter(byBottomSide::contains).collect(Collectors.toSet());
        return result;
    }

    private Set<UUID> getValuesFromIndex(Map<Long, Set<UUID>> map) {
        Set<UUID> uuids = new HashSet<>();
        for(Set<UUID> value : map.values()){
            uuids.addAll(value);
        }
        return uuids;
    }

    private void addToByLeftSideIndex(Long key, UUID value) {
        addToIndex(key, value, indexByLeftSide);
    }

    private void addToByTopSideIndex(Long key, UUID value) {
        addToIndex(key, value, indexByTopSide);
    }

    private void addToByRightSideIndex(Long key, UUID value) {
        addToIndex(key, value, indexByRightside);
    }

    private void addToByBottomSideIndex(Long key, UUID value) {
        addToIndex(key, value, indexByBottomSide);
    }

    @Override
    public void addToIndexes(Widget widget) {
        addToByLeftSideIndex(widget.getX(), widget.getUuid());
        addToByTopSideIndex(widget.getY(), widget.getUuid());
        addToByRightSideIndex(widget.getX() + widget.getWidth(), widget.getUuid());
        addToByBottomSideIndex(widget.getY() - widget.getHeight(), widget.getUuid());
    }

    @Override
    public void removeFromIndexes(Widget widget) {
        removeFromIndex(indexByLeftSide.get(widget.getX()), widget.getUuid());
        removeFromIndex(indexByTopSide.get(widget.getY()), widget.getUuid());
        removeFromIndex(indexByRightside.get(widget.getX() + widget.getWidth()), widget.getUuid());
        removeFromIndex(indexByBottomSide.get(widget.getY() - widget.getHeight()), widget.getUuid());
    }

    /**
     * Если переданное множество не пусто, удаляем из него переданное значение
     *
     * @param set           множдество, из которого нужно удалить значение
     * @param valueToRemove значение, которое удаляем
     */
    private void removeFromIndex(Set<UUID> set, UUID valueToRemove) {
        if (CollectionUtils.isEmpty(set)) {
            return;
        }
        set.remove(valueToRemove);
    }

    @Override
    public void rearrangeByLeftSideIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByLeftSide);
    }

    @Override
    public void rearrangeByTopSideIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByTopSide);
    }

    @Override
    public void rearrangeByRightSideIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByRightside);
    }

    @Override
    public void rearrangeByBottomSideIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByBottomSide);
    }

    /**
     * Добавляет в индекс новый элемент
     *
     * @param key   ключ
     * @param value значение, которое добавляем
     * @param index индекс, в который добавляем значение
     */
    private void addToIndex(Long key, UUID value, SortedMap<Long, Set<UUID>> index) {
        Set<UUID> values;
        if (CollectionUtils.isEmpty(index.get(key))) {
            values = new HashSet<>();
            index.put(key, values);
        } else {
            values = index.get(key);
        }

        values.add(value);
    }

    /**
     * Перестраивает индекс. Удаляется по ключу oldKey значение value и добавляется с ключём newKey
     *
     * @param oldKey старый ключ
     * @param newKey новый ключ
     * @param value  значение
     * @param index  индекс, который перестраиваем
     */
    private void rearrangeIndex(Long oldKey, Long newKey, UUID value, SortedMap<Long, Set<UUID>> index) {
        Set<UUID> uuids = index.get(oldKey);
        if(!CollectionUtils.isEmpty(uuids)){
            uuids.remove(value);
        }

        addToIndex(newKey, value, index);
    }
}
