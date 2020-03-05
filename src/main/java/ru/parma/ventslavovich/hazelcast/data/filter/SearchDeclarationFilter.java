package ru.parma.ventslavovich.hazelcast.data.filter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel("Фильтр витрины деклараций соответствия")
public class SearchDeclarationFilter implements Serializable {
    @ApiModelProperty(value = "Идентификатор", position = 1)
    private List<Long> id;
    @ApiModelProperty(value = "Статус", position = 2)
    private List<Integer> idStatus;
    @ApiModelProperty(value = "Дата регистрации декларации. Левая граница", position = 3)
    private LocalDate declDateStart;
    @ApiModelProperty(value = "Дата регистрации декларации. Правая граница", position = 4)
    private LocalDate declDateEnd;
    @ApiModelProperty(value = "Поиск", position = 5)
    private String search;
    @ApiModelProperty(value = "Признак активности", position = 6)
    private List<Integer> active;
}
