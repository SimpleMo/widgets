package org.miro.test.widgets.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class SpatialServiceStub extends SpatialServiceImpl implements MatrixHelper {
    @Override
    public Set<UUID> getIndexByX(Long x) {
        return indexByLeftSide.get(x);
    }

    @Override
    public Set<UUID> getIndexByY(Long y) {
        return indexByTopSide.get(y);
    }

    @Override
    public Set<UUID> getIndexByWidth(Long width) {
        return indexByRightside.get(width);
    }

    @Override
    public Set<UUID> getIndexByHeight(Long height) {
        return indexByBottomSide.get(height);
    }

    @Override
    public void clearIndexes() {
        indexByLeftSide.clear();
        indexByTopSide.clear();
        indexByBottomSide.clear();
        indexByRightside.clear();
    }
}
