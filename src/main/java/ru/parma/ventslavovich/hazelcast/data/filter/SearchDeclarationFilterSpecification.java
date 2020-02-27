package ru.parma.ventslavovich.hazelcast.data.filter;

import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SearchDeclarationFilterSpecification {

    public static PagingPredicate toPagingPredicate(SearchDeclarationPageFilter filter) {
        PagingPredicate predicate = Predicates.pagingPredicate(
                toPredicate(filter),
                new AscendingIdComparator(),
                filter.getLimit());
        predicate.setPage(filter.getOffset());

        return predicate;
    }

    public static Predicate toPredicate(SearchDeclarationFilter filter) {
        if (filter == null) filter = new SearchDeclarationFilter();
        List<Predicate> predicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filter.getId())) {
            predicates.add(Predicates.in("id", filter.getId().toArray(new Long[0])));
        }
        if (!CollectionUtils.isEmpty(filter.getIdStatus())) {
            predicates.add(Predicates.in("idStatus", filter.getIdStatus().toArray(new Integer[0])));
        }
        if (!StringUtils.isEmpty(filter.getSearch())) {
            predicates.add(Predicates.ilike("number", "%"+filter.getSearch()+"%"));
        }

        return Predicates.and(predicates.toArray(new Predicate[0]));
    }

    public static class AscendingIdComparator implements Serializable, Comparator<Map.Entry<Long, SearchDeclaration>> {

        @Override
        public int compare(Map.Entry<Long, SearchDeclaration> o1, Map.Entry<Long, SearchDeclaration> o2) {
            SearchDeclaration s1 = o1.getValue();
            SearchDeclaration s2 = o2.getValue();
            return Long.valueOf(s1.getId() - s2.getId()).intValue();
        }

    }

    public static class DescendingIdComparator implements Serializable, Comparator<Map.Entry<Long, SearchDeclaration>> {

        @Override
        public int compare(Map.Entry<Long, SearchDeclaration> o1, Map.Entry<Long, SearchDeclaration> o2) {
            SearchDeclaration s1 = o1.getValue();
            SearchDeclaration s2 = o2.getValue();
            return Long.valueOf(s2.getId() - s1.getId()).intValue();
        }

    }
}
