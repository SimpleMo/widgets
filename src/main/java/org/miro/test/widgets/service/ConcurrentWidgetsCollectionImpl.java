package org.miro.test.widgets.service;

import org.miro.test.widgets.model.ConcurrentWidget;
import org.miro.test.widgets.model.Widget;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ConcurrentWidgetsCollectionImpl implements WidgetCollection {
    private Map<UUID, ConcurrentWidget> widgets = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return widgets.isEmpty();
    }

    @Override
    public Collection<Widget> values() {
        return widgets.values().stream().map(ConcurrentWidget::getWrappee).collect(Collectors.toList());
    }

    @Override
    public Widget get(UUID uuid) {
        return widgets.get(uuid) != null ? widgets.get(uuid).getWrappee() : null;
    }

    @Override
    public Widget put(UUID key, Widget value) {
        return widgets.put(key, new ConcurrentWidget(value));
    }

    @Override
    public Widget remove(UUID key) {
        ConcurrentWidget removed = widgets.remove(key);
        return removed != null ? removed.getWrappee() : null;
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
