package org.miro.test.widgets.controller;

import org.miro.test.widgets.exceptions.NotFoundException;
import org.miro.test.widgets.model.Widget;
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
        List<Widget> widgets = getWidgetList();
        return widgets;
    }

    @GetMapping("{uuid}")
    public Widget getWidget(@PathVariable UUID uuid){
        Widget result = widgetsService.findByUuid(uuid);
        if(result == null){
            throw new NotFoundException();
        }
        return result;
    }

    @PostMapping
    public Widget create(@RequestBody() Long x, @RequestBody() Long y, @RequestBody(required = false) Long zIndex, @RequestBody() Long width, @RequestBody() Long height) {
        Widget widget = widgetsService.createWidget(x, y, zIndex, width, height);
        return widget;

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
