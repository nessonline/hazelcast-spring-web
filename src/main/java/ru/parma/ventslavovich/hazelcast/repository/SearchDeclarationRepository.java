package ru.parma.ventslavovich.hazelcast.repository;

import com.google.common.collect.Lists;
import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.predicates.PagingPredicateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationFilter;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationFilterSpecification;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationPageFilter;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class SearchDeclarationRepository {

    private final IMap<Long, SearchDeclaration> hazelcastMap;

    private final Logger log = LoggerFactory.getLogger(SearchDeclarationRepository.class);

    @Value("${hazelcast.declaration-map.init}")
    private boolean init;

    @Autowired
    public SearchDeclarationRepository(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        hazelcastMap = hazelcastInstance.getMap("declaration-map");
        hazelcastMap.addIndex("id", true);
        hazelcastMap.addIndex("idStatus", true);
        hazelcastMap.addIndex("number", true);
    }

    @PostConstruct
    private void init() {
        if (!init) return;
        long start = System.currentTimeMillis();
        log.info(String.format("Start init hazelcast %s", hazelcastMap.getName()));
        hazelcastMap.clear();
        log.info(String.format("Clear hazelcast %s. Working %s ms.", hazelcastMap.getName(), (System.currentTimeMillis() - start)));
        start = System.currentTimeMillis();
        Stream.iterate(1, i -> i + 1).limit(1_000_000).parallel().forEach(id -> {
            SearchDeclaration entity = new SearchDeclaration();
            entity.setId(Long.valueOf(id));
            entity.setIdStatus(new Random().nextInt(9)+1);
            entity.setNumber("declaration-"+(id));
            hazelcastMap.put(entity.getId(), entity);
        });
        log.info(String.format("Fill hazelcast %s. Working %s ms.", hazelcastMap.getName(), (System.currentTimeMillis() - start)));
        log.info(String.format("Finish init hazelcast %s", hazelcastMap.getName()));
    }

    public List<SearchDeclaration> findAll(SearchDeclarationPageFilter filter) {
        return executeAndLog("findAll", filter, t -> {
            PagingPredicate predicate = SearchDeclarationFilterSpecification.toPagingPredicate(t);
            return Lists.newArrayList(hazelcastMap.values(predicate));
        });
    }

    public Long countAll(SearchDeclarationFilter filter) {
        return executeAndLog("countAll", filter, t -> {
            Predicate predicate = SearchDeclarationFilterSpecification.toPredicate(t);
            return hazelcastMap.aggregate(Aggregators.count(), predicate);
        });
    }

    public Long countAll() {
        return executeAndLog("countAll", null, t -> {
            return Long.valueOf(hazelcastMap.size());
        });
    }

    public SearchDeclaration findOne(Long id) {
        return executeAndLog("findOne", id, t -> {
            return hazelcastMap.get(t);
        });
    }

    public SearchDeclaration saveOne(SearchDeclaration entity) {
        return executeAndLog("saveOne", entity, t -> {
            if (t.getId() == null || t.getId() <= 0) {
                Map.Entry<Long, SearchDeclaration> max = hazelcastMap.aggregate(Aggregators.maxBy("id"));
                t.setId((max != null ? max.getKey() : 0)+1);
            }
            hazelcastMap.put(t.getId(), t);
            return findOne(t.getId());
        });
    }

    public boolean deleteOne(Long id) {
        return executeAndLog("deleteOne", id, t -> {
            return hazelcastMap.remove(t) != null;
        });
    }

    private <T, R> R executeAndLog(String method, T object, Function<T, R> function) {
        StopWatch watch = new StopWatch();
        watch.start();
        R result = function.apply(object);
        watch.stop();
        if (log.isDebugEnabled()) {
            String logObject = logObject(object);
            log.debug(String.format(
                    "%s%s. worked %s ms",
                    method,
                    logObject != null ? String.format(": %s", logObject) : "",
                    watch.getTotalTimeMillis()
            ));
        }
        return result;
    }

    private String logObject(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Predicate) {
            return logPredicate((Predicate)object);
        } else if (object instanceof Long) {
            return String.format("#%s", (Long)object);
        } else if (object instanceof SearchDeclaration) {
            return logSearchDeclaration((SearchDeclaration)object);
        }
        return null;
    }

    private String logPredicate(Predicate predicate) {
        if (predicate == null) return null;
        if (predicate instanceof PagingPredicateImpl) {
            PagingPredicateImpl pagingPredicate = (PagingPredicateImpl)predicate;
            return String.format(
                    "predicate = %s page = %s pageSize = %s",
                    pagingPredicate.getPredicate().toString(),
                    pagingPredicate.getPage(),
                    pagingPredicate.getPageSize()
            );
        }
        return String.format(
                "predicate = %s",
                predicate.toString()
        );
    }

    private String logSearchDeclaration(SearchDeclaration entity) {
        if (entity == null) return null;
        return String.format(
                "id = %s idStatus = %s number = '%s'",
                entity.getId(),
                entity.getIdStatus(),
                entity.getNumber()
        );
    }
}
