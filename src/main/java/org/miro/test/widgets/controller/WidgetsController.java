package org.miro.test.widgets.controller;

import org.miro.test.widgets.exceptions.NotFoundException;
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

    @GetMapping
    public List<Widget> getWidgets(){
        return widgetsService.getWidgets();
    }

    @GetMapping("{uuid}")
    public Widget getWidget(@PathVariable UUID uuid){
        Widget result = widgetsService.findByUuid(uuid);
        if(result == null){
            throw new NotFoundException();
        }
        return result;
    }

    @GetMapping("/by-position")
    public List<Widget> getByPositions(@RequestParam("x") Long x, @RequestParam("y") Long y, @RequestParam("width") Long width, @RequestParam("height") Long height){
        return widgetsService.findWidgetByPosition(x, y, width, height);
    }

    @PostMapping()
    public Widget create(@RequestBody WidgetRequest widget) {
        Widget result = widgetsService.createWidget(widget.getX(), widget.getY(), widget.getzIndex(), widget.getWidth(), widget.getHeight());
        return result;
    }

    @PutMapping("{uuid}")
    public Widget update(@PathVariable UUID uuid, @RequestBody WidgetRequest widget){
        Widget result = widgetsService.updateWidget(uuid, widget.getX(), widget.getY(), widget.getzIndex(), widget.getWidth(), widget.getHeight());
        if(result == null){
            throw new NotFoundException();
        }

        return result;
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable UUID uuid){
        Widget deleted = widgetsService.deleteWidget(uuid);
        if(deleted == null){
            throw new NotFoundException();
        }
    }


    private List<Widget> getWidgetList() {
        Widget widget = new Widget();
        widget.setX(1L);
        widget.setY(1L);
        widget.setHeight(2L);
        widget.setWidth(2L);
        widget.setzIndex(1L);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(widget);
        return widgets;
    }

}
