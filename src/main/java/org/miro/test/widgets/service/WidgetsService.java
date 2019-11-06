package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;

import java.util.UUID;

public interface WidgetsService {
    Widget findByUuid(UUID uuid);
}
