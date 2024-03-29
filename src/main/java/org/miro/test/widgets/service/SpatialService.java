package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.lang.NonNull;

import java.util.Set;
import java.util.UUID;

public interface SpatialService {
    void addToByLeftSideIndex(Long key, UUID value);
    void addToByTopSideIndex(Long key, UUID value);
    void addToByRightSideIndex(Long key, UUID value);
    void addToByBottomSideIndex(Long key, UUID value);

    void removeFromIndexes(Widget widget);

    void rearrangeByLeftSideIndex(Long oldKey, Long newKey, UUID value);
    void rearrangeByTopSideIndex(Long oldKey, Long newKey, UUID value);
    void rearrangeByRightSideIndex(Long oldKey, Long newKey, UUID value);
    void rearrangeByBottomSideIndex(Long oldKey, Long newKey, UUID value);

    Set<UUID> findByPosition(@NonNull Long x, @NonNull Long y, @NonNull Long width, @NonNull Long height);
}
