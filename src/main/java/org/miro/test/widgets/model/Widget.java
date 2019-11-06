package org.miro.test.widgets.model;

import java.util.UUID;

public class Widget {
    private Long x;
    private Long y;
    private Long width;
    private Long height;
    private Long zIndex;
    private UUID uuid = UUID.randomUUID();

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

    public void setzIndex(Long zIndex) {
        this.zIndex = zIndex;
    }

    public UUID getUuid() {
        return uuid;
    }

}
