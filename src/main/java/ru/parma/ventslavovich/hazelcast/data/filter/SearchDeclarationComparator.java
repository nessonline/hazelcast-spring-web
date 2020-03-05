package ru.parma.ventslavovich.hazelcast.data.filter;

import ru.parma.ventslavovich.hazelcast.data.entity.SearchDeclaration;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public class SearchDeclarationComparator implements Serializable, Comparator<Map.Entry<Long, SearchDeclaration>> {

    @Override
    public int compare(Map.Entry<Long, SearchDeclaration> o1, Map.Entry<Long, SearchDeclaration> o2) {
        SearchDeclaration s1 = o1.getValue();
        SearchDeclaration s2 = o2.getValue();
        return Long.valueOf(s1.getId() - s2.getId()).intValue();
    }
}
