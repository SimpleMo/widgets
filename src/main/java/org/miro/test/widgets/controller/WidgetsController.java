package org.miro.test.widgets.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.miro.test.widgets.exceptions.NotFoundException;
import org.miro.test.widgets.exceptions.ThrottlingException;
import org.miro.test.widgets.model.Widget;
import org.miro.test.widgets.model.WidgetRequest;
import org.miro.test.widgets.service.WidgetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("widgets")
public class WidgetsController {

    @Autowired
    protected WidgetsService widgetsService;
    private RateLimiter limiter = RateLimiter.create(100.0);

    @GetMapping
    public List<Widget> getWidgets(){
        return widgetsService.getWidgets();
    }

    @GetMapping("{uuid}")
    public Widget getWidget(@PathVariable UUID uuid){
        tryAcquire();

        Widget result = widgetsService.findByUuid(uuid);
        if(result == null){
            throw new NotFoundException();
        }
        return result;
    }

    @GetMapping("/by-position")
    public List<Widget> getByPositions(@RequestParam("x") Long x, @RequestParam("y") Long y, @RequestParam("width") Long width, @RequestParam("height") Long height){
        tryAcquire();
        return widgetsService.findWidgetByPosition(x, y, width, height);
    }

    @PostMapping()
    public Widget create(@RequestBody WidgetRequest widget) {
        tryAcquire();
        Widget result = widgetsService.createWidget(widget.getX(), widget.getY(), widget.getzIndex(), widget.getWidth(), widget.getHeight());
        return result;
    }

    @PutMapping("{uuid}")
    public Widget update(@PathVariable UUID uuid, @RequestBody WidgetRequest widget){
        tryAcquire();
        Widget result = widgetsService.updateWidget(uuid, widget.getX(), widget.getY(), widget.getzIndex(), widget.getWidth(), widget.getHeight());
        if(result == null){
            throw new NotFoundException();
        }

        return result;
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable UUID uuid){
        tryAcquire();
        Widget deleted = widgetsService.deleteWidget(uuid);
        if(deleted == null){
            throw new NotFoundException();
        }
    }

    /**
     * Проверяет, есть ли доступные подключения к сервису. Если нет, то выкидывает исключение, если есть - захватывает подключение
     */
    private void tryAcquire() {
        if(!limiter.tryAcquire()){
            throw new ThrottlingException();
        }
        limiter.acquire();
    }
}
