package ru.parma.ventslavovich.hazelcast.data.response;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@NoArgsConstructor
@Data
@ApiModel("Ответ с количеством элементов и их количеством")
public class CollectionResponse {
    @ApiModelProperty(value = "Список элементов", position = 1)
    private Collection items = new ArrayList();
    @ApiModelProperty(value = "Количество элементов, удовлетворяющих условию фильтра", position = 2)
    private long total = 0L;
    @ApiModelProperty(value = "Общее количество элементов", position = 3)
    private long size = 0L;

    public CollectionResponse(Collection items){
        this.items = Lists.newArrayList(items);
        this.total = items != null ? Long.valueOf(items.size()) : 0;
        this.size = this.total;
    }

    public CollectionResponse(Collection items, Long total, Long size) {
        this.items = Lists.newArrayList(items);
        this.total = total != null ? total : 0;
        this.size = size != null ? size : 0;
    }
}
