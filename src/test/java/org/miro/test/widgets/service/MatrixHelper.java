package org.miro.test.widgets.service;

import java.util.Set;
import java.util.UUID;

public interface MatrixHelper {
    Set<UUID> getIndexByX(Long x);
    Set<UUID> getIndexByY(Long y);
    Set<UUID> getIndexByWidth(Long width);
    Set<UUID> getIndexByHeight(Long height);

    void clearIndexes();
}
