package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public interface WidgetsService {
    Widget findByUuid(UUID uuid);
    Widget createWidget(@NonNull Long x, @NonNull Long y, @Nullable Long zIndex, Long width, Long height);
    Widget updateWidget(@NonNull UUID uuid, @Nullable Long x, @Nullable  Long y, @Nullable Long zIndex, @Nullable Long width, @Nullable Long height);
}
