package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Service
public class ConcurrentSpatialServiceDecorator implements SpatialService {
    private ReentrantLock wrappeeAsResource = new ReentrantLock();

    @Autowired
    private SpatialService wrappee;

    @Override
    public void addToIndexes(Widget widget) {
        visit(widget, wrappee::addToIndexes);
    }

    @Override
    public void removeFromIndexes(Widget widget) {
        visit(widget, wrappee::removeFromIndexes);
    }

    @Override
    public void rearrangeByLeftSideIndex(Long oldKey, Long newKey, UUID value) {
        visit(oldKey, newKey, value, wrappee::rearrangeByLeftSideIndex);
    }

    @Override
    public void rearrangeByTopSideIndex(Long oldKey, Long newKey, UUID value) {
        visit(oldKey, newKey, value, wrappee::rearrangeByTopSideIndex);
    }

    @Override
    public void rearrangeByRightSideIndex(Long oldKey, Long newKey, UUID value) {
        visit(oldKey, newKey, value, wrappee::rearrangeByRightSideIndex);

    }

    @Override
    public void rearrangeByBottomSideIndex(Long oldKey, Long newKey, UUID value) {
        visit(oldKey, newKey, value, wrappee::rearrangeByBottomSideIndex);
    }

    @Override
    public Set<UUID> findByPosition(Long x, Long y, Long width, Long height) {
        wrappeeAsResource.lock();
        try {
            return wrappee.findByPosition(x, y, width, height);
        } finally {
            wrappeeAsResource.unlock();
        }
    }

    /**
     * Служебный метод. Нужен для того, чтобы добавить синхронизацию по wrappeeAsResource к методам wrapee
     *
     * @param widget  обрабатываемый виджет
     * @param visitor действие, которое необходимо выполнить
     */
    private void visit(Widget widget, Consumer<Widget> visitor) {
        wrappeeAsResource.lock();
        try {
            visitor.accept(widget);
        } finally {
            wrappeeAsResource.unlock();
        }
    }

    /**
     * Служебный метод. Нужен для того, чтобы добавить синхронизацию по wrappeeAsResource к методам wrapee
     *
     * @param oldKey  старый ключ
     * @param newKey  новый ключ
     * @param value   значение
     * @param visitor действие, которое нужно выполнить
     */
    private void visit(Long oldKey, Long newKey, UUID value, TernaryConsumer<Long, Long, UUID> visitor) {
        wrappeeAsResource.lock();
        try {
            visitor.accept(oldKey, newKey, value);
        } finally {
            wrappeeAsResource.unlock();
        }
    }
}
