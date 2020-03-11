package ru.parma.ventslavovich.hazelcast.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationFilter;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationPageFilter;
import ru.parma.ventslavovich.hazelcast.repository.SearchDeclarationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchDeclarationService {

    private final SearchDeclarationRepository repository;

    public List<SearchDeclaration> findAll(SearchDeclarationPageFilter filter) {
        return repository.findAll(filter);
    }

    public Long countAll(SearchDeclarationFilter filter) {
        return repository.countAll(filter);
    }

    public Long countAll() {
        return repository.countAll();
    }

    public SearchDeclaration findOne(long id) {
        return Optional.ofNullable(repository.findOne(id)).orElseThrow(() -> new RuntimeException(String.format("Декларация соответствия #%s не найдена", id)));
    }

    public Long createOne(SearchDeclaration entity) {
        if (entity == null) throw new RuntimeException(String.format("Декларация соотвествия не заполнена"));
        entity.setId(null);
        entity = repository.saveOne(entity);
        return entity.getId();
    }

    public void saveOne(SearchDeclaration entity) {
        if (entity == null) throw new RuntimeException(String.format("Декларация соотвествия не заполнена"));
        findOne(entity.getId());
        repository.saveOne(entity);
    }

    public boolean deleteOne(long id) {
        findOne(id);
        return repository.deleteOne(id);
    }

    public boolean deleteAll() {
        return repository.deleteAll();
    }

    public void generate(long size) {
        repository.generate(size);
    }
}
