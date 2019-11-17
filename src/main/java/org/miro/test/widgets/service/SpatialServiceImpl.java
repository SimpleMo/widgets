package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Primary
public class SpatialServiceImpl implements SpatialService {
    private SortedMap<Long, Set<UUID>> indexByX = new TreeMap<>(Long::compareTo);
    private SortedMap<Long, Set<UUID>> indexByY = new TreeMap<>(Long::compareTo);
    private SortedMap<Long, Set<UUID>> indexByWidth = new TreeMap<>(Long::compareTo);
    private SortedMap<Long, Set<UUID>> indexByHeight = new TreeMap<>(Long::compareTo);

    @Override
    public Set<UUID> findByPosition(Long x, Long y, Long width, Long height) {
        //Строим промежуточные множества по X, Y, Width и Height
        Set<UUID> byX = getValuesFromIndex(indexByX.tailMap(x));

        Set<UUID> byY = getValuesFromIndex(indexByY.headMap(y));
        if(!CollectionUtils.isEmpty(indexByY.get(y))){
            byY.addAll(indexByY.get(y));
        }

        Set<UUID> byWidth = getValuesFromIndex(indexByWidth.headMap(width));
        if(!CollectionUtils.isEmpty(indexByWidth.get(width))){
            byWidth.addAll(indexByWidth.get(width));
        }

        Set<UUID> byHeight = getValuesFromIndex(indexByHeight.headMap(height));
        if(!CollectionUtils.isEmpty(indexByHeight.get(height))){
            byHeight.addAll(indexByHeight.get(height));

        }

        //Нам подходят только те UUID, которые есть во всех множествах одновременно. Собираем из них результат.
        Set<UUID> result =
                byX.stream().filter(byY::contains).filter(byWidth::contains).filter(byHeight::contains).collect(Collectors.toSet());
        return result;
    }

    private Set<UUID> getValuesFromIndex(Map<Long, Set<UUID>> map) {
        Set<UUID> uuids = new HashSet<>();
        for(Set<UUID> value : map.values()){
            uuids.addAll(value);
        }
        return uuids;
    }

    @Override
    public void addToByXIndex(Long key, UUID value) {
        addToIndex(key, value, indexByX);
    }

    @Override
    public void addToByYIndex(Long key, UUID value) {
        addToIndex(key, value, indexByY);
    }

    @Override
    public void addToByWidthIndex(Long key, UUID value) {
        addToIndex(key, value, indexByWidth);
    }

    @Override
    public void addToByHeightIndex(Long key, UUID value) {
        addToIndex(key, value, indexByHeight);
    }

    @Override
    public void removeFromIndexes(Widget widget) {
        removeFromIndex(indexByX.get(widget.getX()), widget.getUuid());
        removeFromIndex(indexByY.get(widget.getY()), widget.getUuid());
        removeFromIndex(indexByWidth.get(widget.getWidth()), widget.getUuid());
        removeFromIndex(indexByHeight.get(widget.getHeight()), widget.getUuid());
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
    public void rearrangeByXIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByX);
    }

    @Override
    public void rearrangeByYIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByY);
    }

    @Override
    public void rearrangeByWidthIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByWidth);
    }

    @Override
    public void rearrangeByHeightIndex(Long oldKey, Long newKey, UUID value) {
        rearrangeIndex(oldKey, newKey, value, indexByHeight);
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
        index.get(oldKey).remove(value);
        addToIndex(newKey, value, index);
    }
}
