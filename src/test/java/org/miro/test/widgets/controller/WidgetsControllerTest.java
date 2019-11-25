package org.miro.test.widgets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miro.test.widgets.model.Widget;
import org.miro.test.widgets.service.SpatialService;
import org.miro.test.widgets.service.WidgetCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WidgetCollection widgetCollection;

    @Autowired
    private SpatialService spatialService;

    private Set<Widget> widgets = new HashSet<>();
    private Widget widget; // для некоторых тестов нужна ссылка на какой-нибудь виджет

    /**
     * Перед каждым тестом готовим пример
     */
    @BeforeEach
    void before() {
        //Строим множество виджетов
        widgets.clear();
        widgets.add(new Widget(0L, 9L, 3L, 4L, 0L));
        widgets.add(new Widget(7L, 9L, 4L, 6L, 1L));
        widgets.add(new Widget(4L, 4L, 2L, 2L, 2L));
        widget = widgets.stream().findFirst().get(); // нам подойдёт любойЮ но лучше первый...
    }

    /**
     * Очищаем результаты работы теста
     */
    @AfterEach
    void after(){
        widgetCollection.values().stream().forEach(item -> spatialService.removeFromIndexes(item));
        widgetCollection.clear();
    }


    @Test
    void createWithNullZIndex() throws Exception {
        widget.setzIndex(null);
        String postRequestBody = mapper.writeValueAsString(widget);

        //Проверяем, что запрос срабатывает
        ResultActions request = mockMvc.perform(post("/widgets").contentType(MediaType.APPLICATION_JSON).content(postRequestBody));
        request = request.andExpect(status().isOk());

        //Проверяем что возвращается то, что требуется
        Widget createdWidget = mapper.readValue(request.andReturn().getResponse().getContentAsString(), Widget.class);
        assertNotNull(createdWidget);
        assertNotNull(createdWidget.getUuid());
        assertEquals(0L, createdWidget.getzIndex());
        assertEquals(widget.getX(), createdWidget.getX());
        assertEquals(widget.getY(), createdWidget.getY());
        assertEquals(widget.getWidth(), createdWidget.getWidth());
        assertEquals(widget.getHeight(), createdWidget.getHeight());
    }

    @Test
    void createWithZIndex() throws Exception {
        String postRequestBody = mapper.writeValueAsString(widget);

        //Проверяем, что запрос срабатывает
        ResultActions request = mockMvc.perform(post("/widgets").contentType(MediaType.APPLICATION_JSON).content(postRequestBody));
        request = request.andExpect(status().isOk());

        //Проверяем что возвращается то, что требуется
        Widget createdWidget = mapper.readValue(request.andReturn().getResponse().getContentAsString(), Widget.class);
        assertNotNull(createdWidget);
        assertNotNull(createdWidget.getUuid());
        assertEquals(widget.getX(), createdWidget.getX());
        assertEquals(widget.getY(), createdWidget.getY());
        assertEquals(widget.getWidth(), createdWidget.getWidth());
        assertEquals(widget.getHeight(), createdWidget.getHeight());
        assertEquals(widget.getzIndex(), createdWidget.getzIndex());
    }

    @Test
    void creatSeveralWidgets() throws Exception {
        creatWidgetsFromSet();

        //Проверяем, что добавилось всё
        assertEquals(widgets.size(), widgetCollection.values().size());
    }

    @Test
    void getWidgets() throws Exception {
        creatWidgetsFromSet();

        String responseAsString = mockMvc.perform(get("/widgets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Widget> responseAsList = mapper.readValue(responseAsString, mapper.getTypeFactory().constructCollectionType(List.class, Widget.class));
        assertEquals(widgets.size(), responseAsList.size());

        //Проверяем, что всё вернулось, что добавилось
        for(Widget responseItem : responseAsList){
            long count = widgets.stream()
                    .filter(item -> item.getX().equals(responseItem.getX()) && item.getY().equals(responseItem.getY()) && item.getWidth().equals(responseItem.getWidth()) && item.getHeight().equals(responseItem.getHeight()) && item.getzIndex().equals(responseItem.getzIndex()))
                    .count();
            assertEquals(1, count);
        }
    }

    @Test
    void getWidget() throws Exception {
        creatWidgetsFromSet();
        Widget theWidget = widgetCollection.values().stream().findFirst().get();

        String responseAsString = mockMvc.perform(get("/widgets/" + theWidget.getUuid().toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Widget found = mapper.readValue(responseAsString, Widget.class);

        //Проверем, что по запросу конкретного виджета что-то вернётся
        assertNotNull(found);
        assertEquals(theWidget, found);
    }

    @Test
    void deleteWidget() throws Exception {
        creatWidgetsFromSet();

        //Проверяем, что произвольный виджет из списка удаляется без сбоев
        Widget theWidget = widgetCollection.values().stream().findFirst().get();
        mockMvc.perform(delete("/widgets/" + theWidget.getUuid().toString())).andExpect(status().isOk());

        //Проверяем, что действительно удалилось и именно то, что заказывали...
        assertEquals(0, widgetCollection.values().stream().filter(item -> theWidget.equals(item)).count());
    }

    @Test
    void notFound() throws Exception {
        creatWidgetsFromSet();

        mockMvc.perform(get("/widgets/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByPositions() throws Exception {
        creatWidgetsFromSet();

        //Данный запрос доллжен вернуть всё ранее добавленное
        String responseAsString = mockMvc.perform(get("/widgets/by-position")
                .param("x", String.valueOf(0L))
                .param("y", String.valueOf(9L))
                .param("width", String.valueOf(11L))
                .param("height", String.valueOf(9L)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Widget> responseAsList = mapper.readValue(responseAsString, mapper.getTypeFactory().constructCollectionType(List.class, Widget.class));

        //Проверяем, что всё вернулось, что добавилось
        assertEquals(widgets.size(), responseAsList.size());
        for(Widget responseItem : responseAsList){
            long count = widgets.stream()
                    .filter(item -> item.getX().equals(responseItem.getX()) && item.getY().equals(responseItem.getY()) && item.getWidth().equals(responseItem.getWidth()) && item.getHeight().equals(responseItem.getHeight()) && item.getzIndex().equals(responseItem.getzIndex()))
                    .count();
            assertEquals(1, count);
        }

    }

    private void creatWidgetsFromSet() throws Exception {
        //Заполняем список виджетов
        for(Widget item : widgets){
            String postRequestBody = mapper.writeValueAsString(item);
            mockMvc.perform(post("/widgets").contentType(MediaType.APPLICATION_JSON).content(postRequestBody));
        }
    }

}