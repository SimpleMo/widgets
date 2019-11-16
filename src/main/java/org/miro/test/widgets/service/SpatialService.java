package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.lang.NonNull;

import java.util.Set;
import java.util.UUID;

public interface SpatialService {
    void addToByXIndex (Long key, UUID value);
    void addToByYIndex (Long key, UUID value);
    void addToByWidthIndex (Long key, UUID value);
    void addToByHeightIndex (Long key, UUID value);

    void removeFromIndexes(Widget widget);

    void rearrangeByXIndex (Long oldKey, Long newKey, UUID value);
    void rearrangeByYIndex (Long oldKey, Long newKey, UUID value);
    void rearrangeByWidthIndex (Long oldKey, Long newKey, UUID value);
    void rearrangeByHeightIndex (Long oldKey, Long newKey, UUID value);

    Set<UUID> findByPosition(@NonNull Long x, @NonNull Long y, @NonNull Long width, @NonNull Long height);
}
