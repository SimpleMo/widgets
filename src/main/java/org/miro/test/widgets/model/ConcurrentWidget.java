package org.miro.test.widgets.model;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Класс-декоратор, добавляющий к виджету функциональность Concurrent
 */
public class ConcurrentWidget extends Widget {
    private Widget wrappee;
    private ReentrantLock resource = new ReentrantLock();

    public ConcurrentWidget(Widget wrappee) {
        this.wrappee = wrappee;
    }

    /**
     * Возвращаем базовый объект предварительно захватывая ресурс, иными словами, возвращаем базовый объект только в том случае, если он никем не редактируется
     * @return обёрнутый декоратором объект-виджет
     */
    public Widget getWrappee() {
        resource.lock();
        try {
            return wrappee;
        } finally {
            resource.unlock();
        }
    }

    @Override
    public Long getX() {
        return wrappee.getX();
    }

    /**
     * Меняем координату x только в том случае, когда resource можно захватить
     * @param x новая координата x
     */
    @Override
    public void setX(Long x) {
        visit(widget -> widget.setX(x));
    }

    @Override
    public Long getY() {
        return wrappee.getY();
    }

    @Override
    public void setY(Long y) {
        visit(widget -> widget.setY(y));
    }

    @Override
    public Long getWidth() {
        return wrappee.getWidth();
    }

    @Override
    public Long getHeight() {
        return wrappee.getHeight();
    }

    @Override
    public Long getzIndex() {
        return wrappee.getzIndex();
    }

    @Override
    public void setWidth(Long width) {
        visit(widget -> widget.setWidth(width));
    }

    @Override
    public void setHeight(Long height) {
        visit(widget -> widget.setHeight(height));
    }

    /**
     * Меняем zIndex только в том случае, когда resource можно захватить
     *
     * @param zIndex
     */
    @Override
    public void setzIndex(Long zIndex) {
        visit(widget -> widget.setzIndex(zIndex));
    }

    @Override
    public UUID getUuid() {
        return wrappee.getUuid();
    }

    /**
     * Перед тем, как выполнить инкапсулированное в visitor поведение, захватываем ресурс, а после выполнения - освобождаем его
     * @param visitor класс, инкапсулирующий поведение
     */
    @Override
    public void visit(Consumer<Widget> visitor) {
        resource.lock();
        try {
            wrappee.visit(visitor);
        } finally {
            resource.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        return wrappee.equals(o);
    }
}
