package org.miro.test.widgets.model;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Widget {
    private Long x;
    private Long y;
    private Long width;
    private Long height;
    private Long zIndex;
    private UUID uuid = UUID.randomUUID();

    public Widget() {
    }

    public Widget(Long x, Long y, Long width, Long height, Long zIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getzIndex() {
        return zIndex;
    }

    //TODO подумать. Возможно, тут нужен lighweight (ссылка на некий объект - слой, поскольку вохможна ситуация с несколькими виджетами на слое)
    public void setzIndex(Long zIndex) {
        this.zIndex = zIndex;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Рееализация шаблона Посетитель, предназначен для добавления функциональности классу виджетов.
     * Пока используем только для увеличения zIndex. Если что-то ещё понадобится, достаточно будет просто вызвать данный меитод с нудной лямбдой.
     *
     * @param visitor класс, инкапсулирующий поведение
     */
    public void visit(Consumer<Widget> visitor) {
        visitor.accept(this);
    }

    /**
     * Реализация шаблона Посетитель, предназначен для вычисления предикатов над виджетом.
     * Пока используем только для вычисления пересечения. Если понадобится, достаточно вызвать с другой лямбдой.
     *
     * @param visitor класс, инкапсулирующий вычисление предиката
     * @return результат вычислений
     */
    public Boolean test(Predicate<Widget> visitor) {
        return visitor.test(this);
    }

    /**
     * Равенство двух виджетов устанавливаем через равенство их uuid
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Widget widget = (Widget) o;

        return uuid.equals(widget.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
