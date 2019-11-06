package org.miro.test.widgets.service;

import org.miro.test.widgets.model.Widget;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WidgetsServiceImpl implements WidgetsService {

    @Override
    public Widget findByUuid(UUID uuid) {
        return null;
    }
}
