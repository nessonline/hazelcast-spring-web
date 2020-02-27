package ru.parma.ventslavovich.hazelcast.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor @AllArgsConstructor
@ApiModel("Витрина деклараций соответствия")
public class SearchDeclaration implements Serializable, Comparable<SearchDeclaration> {

    @ApiModelProperty(value = "Идентификатор", position = 1)
    private Long id;
    @ApiModelProperty(value = "Идентификатор статуса", position = 2)
    private Integer idStatus;
    @ApiModelProperty(value = "Регистрационный номер декларации", position = 3)
    private String number;

    @Override
    public int compareTo(SearchDeclaration other) {
        return Long.valueOf(this.id - other.id).intValue();
    }
}
