package ru.parma.ventslavovich.hazelcast.data.filter;

import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchDeclarationFilterSpecification {

    public static PagingPredicate toPagingPredicate(SearchDeclarationPageFilter filter) {
        if (emptyFilter(filter)) return new PagingPredicate(Predicates.alwaysFalse(), 1);
        PagingPredicate predicate = new PagingPredicate(
                toPredicate(filter),
                new SearchDeclarationComparator(),
                filter.getLimit());
        predicate.setPage(filter.getOffset());

        return predicate;
    }

    public static Predicate toPredicate(SearchDeclarationFilter filter) {
        if (emptyFilter(filter)) return Predicates.greaterEqual("id", 1);
        if (filter == null) filter = new SearchDeclarationFilter();
        List<Predicate> predicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filter.getId())) {
            predicates.add(Predicates.in("id", filter.getId().toArray(new Long[0])));
        }
        if (!CollectionUtils.isEmpty(filter.getIdStatus())) {
            predicates.add(Predicates.in("idStatus", filter.getIdStatus().toArray(new Integer[0])));
        }
        if (filter.getDeclDateStart() != null || filter.getDeclDateEnd() != null) {
            Predicate predicate = datePredicate("declDate", filter.getDeclDateStart(), filter.getDeclDateEnd());
            if (predicate != null) predicates.add(predicate);
        }
        if (!StringUtils.isEmpty(filter.getSearch())) {
            predicates.add(Predicates.and(Stream.of(filter.getSearch().trim().split(" "))
                    .map(s -> {
                        final String search = "%"+s+"%";
                        return Predicates.or(
                        Stream.of("number", "customDeclNumber", "appNumber", "technicalReglaments", "group", "productSingleList", "declType", "declSchema", "declObjectType")
                                .map(column -> Predicates.ilike(column, search))
                                .collect(Collectors.toList())
                                .toArray(new Predicate[0])
                        );
                    })
                    .collect(Collectors.toList())
                    .toArray(new Predicate[0])
            ));
        }
        if (!CollectionUtils.isEmpty(filter.getActive())) {
            boolean notActive = filter.getActive().contains(1);
            boolean active = filter.getActive().contains(2);
            if (notActive && !active) {
                predicates.add(Predicates.equal("active", false));
            } else if (!notActive && active) {
                predicates.add(Predicates.equal("active", true));
            } else if (!notActive && !active) {
                predicates.add(Predicates.alwaysFalse());
            }
        }

        return Predicates.and(predicates.toArray(new Predicate[0]));
    }

    private static Predicate datePredicate(String attribute, LocalDate dateStart, LocalDate dateEnd) {
        if (dateStart == null) {
            return Predicates.lessEqual(attribute, dateEnd);
        } else if (dateEnd == null) {
            return Predicates.greaterEqual(attribute, dateStart);
        } else if (Objects.equals(dateStart, dateEnd)){
            return Predicates.equal(attribute, dateStart);
        } else {
            return Predicates.between(attribute, dateStart, dateEnd);
        }
    }

    private static boolean emptyFilter(SearchDeclarationPageFilter filter) {
        if (filter == null) return true;
        return emptyFilter((SearchDeclarationFilter)filter)
                && (filter.getOffset() < 0 || filter.getLimit() <= 0);
    }

    private static boolean emptyFilter(SearchDeclarationFilter filter) {
        if (filter == null) return true;
        return CollectionUtils.isEmpty(filter.getId())
                && CollectionUtils.isEmpty(filter.getIdStatus())
                && filter.getDeclDateStart() == null
                && filter.getDeclDateEnd() == null
                && StringUtils.isEmpty(filter.getSearch())
                && CollectionUtils.isEmpty(filter.getActive());

    }
}
