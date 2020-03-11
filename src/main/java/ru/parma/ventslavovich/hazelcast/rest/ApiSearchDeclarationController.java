package ru.parma.ventslavovich.hazelcast.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.parma.ventslavovich.hazelcast.config.SwaggerConfig;
import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationFilter;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationPageFilter;
import ru.parma.ventslavovich.hazelcast.data.response.CollectionResponse;
import ru.parma.ventslavovich.hazelcast.data.response.CountResponse;
import ru.parma.ventslavovich.hazelcast.service.SearchDeclarationService;

@RestController
@RequestMapping("/api/v1/app/common/search/declaration")
@Api(tags = SwaggerConfig.API_SEARCH_DECLARATION_CONTROLLER_NAME)
@RequiredArgsConstructor
public class ApiSearchDeclarationController {

    private final SearchDeclarationService service;

    @PostMapping("/filter")
    @ApiOperation("Возвращает страницу деклараций соответствия в витрине, удовлетворяющих условию фильтра")
    public CollectionResponse findAll(@RequestBody SearchDeclarationPageFilter filter) {
        return new CollectionResponse(
                service.findAll(filter),
                service.countAll(filter),
                service.countAll()
        );
    }

    @PostMapping("/count")
    @ApiOperation("Возвращает количество деклараций соответствия в витрине, удовлетворяющих условию фильтра")
    public CountResponse countAll(@RequestBody SearchDeclarationFilter filter) {
        return new CountResponse(
                service.countAll(filter)
        );
    }

    @GetMapping("/{id}")
    @ApiOperation("Возвращает декларацию соответствия в витрине по идентификатору")
    public SearchDeclaration findOne(@PathVariable("id") Long id) {
        return service.findOne(id);
    }

    @PostMapping()
    @ApiOperation("Создание декларации соответствия в витрине")
    public Long createOne(@RequestBody SearchDeclaration entity) {
        entity.setId(null);
        return service.createOne(entity);
    }

    @PutMapping("/{id}")
    @ApiOperation("Сохранение декларации соответствия в витрине по идентификатору")
    public void saveOne(@PathVariable("id") Long id, @RequestBody SearchDeclaration entity) {
        entity.setId(id);
        service.saveOne(entity);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Удаление декларации соответствия в витрине по идентификатору")
    public boolean deleteOne(@PathVariable("id") Long id) {
        return service.deleteOne(id);
    }

    @DeleteMapping()
    @ApiOperation("Удаление декларации соответствия в витрине")
    public boolean deleteAll() {
        return service.deleteAll();
    }

    @PostMapping("/generate/{size}")
    @ApiOperation("Генерация деклараций соответствия в витрине")
    public void generate(@PathVariable("size") Long size) {
        service.generate(size);
    }
}
