package org.miro.test.widgets.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MatrixHelper {
    Set<UUID> getIndexByLeftSide(Long leftSide);
    Set<UUID> getIndexByTopSide(Long topSide);
    Set<UUID> getIndexByRightSide(Long rightSide);
    Set<UUID> getIndexByBottomSide(Long bottomSide);

    List<Set<UUID>> getAllItems();

    void clearIndexes();
}
