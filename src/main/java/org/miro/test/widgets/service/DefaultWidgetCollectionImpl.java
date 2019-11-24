package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Primary
public class DefaultWidgetCollectionImpl implements WidgetCollection {
    private Map<UUID, Widget> widgets = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return widgets.isEmpty();
    }

    @Override
    public Collection<Widget> values() {
        return widgets.values();
    }

    @Override
    public Widget get(UUID uuid) {
        return widgets.get(uuid);
    }

    @Override
    public Widget put(UUID key, Widget value) {
        return widgets.put(key, value);
    }

    @Override
    public Widget remove(UUID key) {
        return widgets.remove(key);
    }

    @Override
    public void clear() {
        widgets.clear();
    }

    @Override
    public void correctZIndex(Long zIndex) {
        Consumer<Widget> consumer = widget -> widget.setzIndex(widget.getzIndex() + 1);
        widgets.entrySet().stream()
                .filter(entry -> entry.getValue().getzIndex().compareTo(zIndex) >= 0)
                .forEach(entry ->  entry.getValue().visit(consumer));
    }
}
