package org.miro.test.widgets.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SpatialServiceStub extends SpatialServiceImpl implements MatrixHelper {
    @Override
    public Set<UUID> getIndexByLeftSide(Long leftSide) {
        return indexByLeftSide.get(leftSide);
    }

    @Override
    public Set<UUID> getIndexByTopSide(Long topSide) {
        return indexByTopSide.get(topSide);
    }

    @Override
    public Set<UUID> getIndexByRightSide(Long rightSide) {
        return indexByRightside.get(rightSide);
    }

    @Override
    public Set<UUID> getIndexByBottomSide(Long bottomSide) {
        return indexByBottomSide.get(bottomSide);
    }

    @Override
    public List<Set<UUID>> getAllItems() {
        List<Set<UUID>> result = new ArrayList<>();
        result.addAll(indexByTopSide.values());
        result.addAll(indexByBottomSide.values());
        result.addAll(indexByLeftSide.values());
        result.addAll(indexByRightside.values());
        return result;
    }

    @Override
    public void clearIndexes() {
        indexByLeftSide.clear();
        indexByTopSide.clear();
        indexByBottomSide.clear();
        indexByRightside.clear();
    }
}
