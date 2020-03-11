package ru.parma.ventslavovich.hazelcast.repository;

import com.google.common.collect.Lists;
import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import org.jeasy.random.EasyRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import ru.parma.ventslavovich.hazelcast.config.HazelcastConfig;
import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationFilter;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationFilterSpecification;
import ru.parma.ventslavovich.hazelcast.data.filter.SearchDeclarationPageFilter;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Component
public class SearchDeclarationRepository {

    private final IMap<Long, SearchDeclaration> hazelcastMap;

    private final Logger log = LoggerFactory.getLogger(SearchDeclarationRepository.class);

    @Value("${hazelcast.declaration-map.init}")
    private boolean init;

    @Value("${hazelcast.declaration-map.size}")
    private long size;

    private int patitionSize = 100000;

    @Autowired
    public SearchDeclarationRepository(HazelcastInstance hazelcastInstance) {
        hazelcastMap = hazelcastInstance.getMap(HazelcastConfig.MAP_SEARCH_DECLARATION_NAME);
        hazelcastMap.addIndex("id", true);
        hazelcastMap.addIndex("idStatus", true);
        hazelcastMap.addIndex("number", true);
        hazelcastMap.addIndex("declDate", true);
        hazelcastMap.addIndex("active", false);
    }

    @PostConstruct
    private void init() {
        if (!init) return;
        log.info(String.format("Start init hazelcast %s size %s", hazelcastMap.getName(), size));
        deleteAll();
        generate(size);
        log.info(String.format("Finish init hazelcast %s", hazelcastMap.getName()));
    }

    public void generate(long size) {
        if (size < 1l) return;
        long startId = generateId();
        long current = System.currentTimeMillis();
        EasyRandom generator = new EasyRandom();
        log.info(String.format("Start generate %s entities in hazelcast %s", size, hazelcastMap.getName()));

        List<Long> allList = LongStream.rangeClosed(startId, startId + size - 1).boxed().collect(Collectors.toList());
        List<List<Long>> partition = Lists.partition(allList, patitionSize);
        log.info(String.format("Split to %s partition's size %s", partition.size(), patitionSize));
        ExecutorService executorService = Executors.newWorkStealingPool();
        AtomicInteger step = new AtomicInteger(0);
        try {
            partition.forEach(list -> {
                long stepStart = System.currentTimeMillis();
                int stepNum = step.incrementAndGet();
                log.info(String.format("Start partition %s of %s size %s", stepNum, partition.size(), list.size()));
                List<Callable<Long>> callables = list.stream().map(id -> (Callable<Long>) () -> {
                    generate(id, generator);
                    return id;
                }).collect(Collectors.toList());
                try {
                    executorService.invokeAll(callables);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                } finally {
                    log.info(String.format("Finish partition %s of %s size %s. worked %s ms", stepNum, partition.size(), list.size(), (System.currentTimeMillis() - stepStart)));
                    callables = null;
                }
            });
        } finally {
            executorService.shutdown();
        }

        log.info(String.format("Finish generate %s entities in hazelcast %s. worked %s ms", size, hazelcastMap.getName(), (System.currentTimeMillis() - current)));
    }

    private void generate(long id, EasyRandom generator) {
        SearchDeclaration entity = generator.nextObject(SearchDeclaration.class);
        entity.setId(Long.valueOf(id));
        entity.setIdStatus(new Random().nextInt(9) + 1);
        entity.setNumber("declaration-" + (id));
        entity.setAppStatus(new Random().nextInt(9) + 1);
        entity.setAppNumber("application-" + (id));
        List<LocalDate> dates = new ArrayList<>();
        dates.add(entity.getAppDate());
        dates.add(entity.getDeclDate());
        dates.add(entity.getDeclEndDate());
        Collections.sort(dates);
        entity.setAppDate(dates.get(0));
        entity.setDeclDate(dates.get(1));
        entity.setDeclEndDate(dates.get(2));
        entity.setDeclType(String.valueOf(new Random().nextInt(4) + 1));
        hazelcastMap.put(entity.getId(), entity);
    }

    public List<SearchDeclaration> findAll(SearchDeclarationPageFilter filter) {
        PagingPredicate predicate = SearchDeclarationFilterSpecification.toPagingPredicate(filter);
        return executeAndLog("findAll", predicate, t -> {
            return Lists.newArrayList(hazelcastMap.values(t));
        });
    }

    public Long countAll(SearchDeclarationFilter filter) {
        Predicate predicate = SearchDeclarationFilterSpecification.toPredicate(filter);
        return executeAndLog("countAll", predicate, t -> {
            return hazelcastMap.aggregate(Aggregators.count(), t);
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
        if (entity.getId() == null) entity.setId(generateId());
        return executeAndLog("saveOne", entity, t -> {
            hazelcastMap.put(t.getId(), t);
            return findOne(t.getId());
        });
    }

    public boolean deleteOne(Long id) {
        return executeAndLog("deleteOne", id, t -> {
            return hazelcastMap.remove(t) != null;
        });
    }

    public boolean deleteAll() {
        hazelcastMap.clear();
        return true;
    }

    private Long generateId() {
        Map.Entry<Long, SearchDeclaration> max = hazelcastMap.aggregate(Aggregators.maxBy("id"));
        return Optional.ofNullable(max).map(es -> es.getKey()+1l).orElse(1l);
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
        if (predicate instanceof PagingPredicate) {
            PagingPredicate pagingPredicate = (PagingPredicate)predicate;
            return String.format(
                    "predicate = %s page = %s pageSize = %s",
                    pagingPredicate.getPredicate() != null ? pagingPredicate.getPredicate().toString() : "",
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
