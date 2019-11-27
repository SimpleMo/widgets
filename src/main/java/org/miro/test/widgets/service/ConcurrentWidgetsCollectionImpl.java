package org.miro.test.widgets.service;

import org.miro.test.widgets.model.ConcurrentWidget;
import org.miro.test.widgets.model.Widget;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ConcurrentWidgetsCollectionImpl implements WidgetCollection {
    private Map<UUID, ConcurrentWidget> widgets = new HashMap<>();
    private ReentrantLock widgetsAsResource = new ReentrantLock();

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
        widgetsAsResource.lock(); // работаем с коллекцией в целом только из защищённого блока.
        try {
            return widgets.put(key, new ConcurrentWidget(value));
        } finally {
            widgetsAsResource.unlock();
        }
    }

    @Override
    public Widget remove(UUID key) {
        widgetsAsResource.lock();
        ConcurrentWidget removed;
        try {
            removed = widgets.remove(key);
        } finally {
            widgetsAsResource.unlock();
        }
        return removed != null ? removed.getWrappee() : null;
    }

    @Override
    public void clear() {
        widgetsAsResource.lock();
        try {
            widgets.clear();
        } finally {
            widgetsAsResource.unlock();
        }
    }

    @Override
    public void correctZIndex(Long zIndex) {
        widgetsAsResource.lock(); // комментарий 4. Работать с коллекцией в целом тут должны в защищённом блоке. Не смотря на то, что с кокретным виджетом уже работаем в защищенных блоках.
        try {
            Consumer<Widget> consumer = widget -> widget.setzIndex(widget.getzIndex() + 1);
            widgets.entrySet().stream()
                    .filter(entry -> entry.getValue().getzIndex().compareTo(zIndex) >= 0)
                    .forEach(entry ->  entry.getValue().visit(consumer));
        } finally {
            widgetsAsResource.unlock();
        }
    }
}
