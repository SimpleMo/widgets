package org.miro.test.widgets.service;

@FunctionalInterface
public interface TernaryConsumer<F, S, T> {
    void accept(F first, S second, T third);
}
