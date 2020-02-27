package ru.parma.ventslavovich.hazelcast.data.filter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel("Фильтр страницы витрины деклараций соответствия")
public class SearchDeclarationPageFilter extends SearchDeclarationFilter {
    @ApiModelProperty(value = "Номер страницы", position = 4)
    private Integer offset;
    @ApiModelProperty(value = "Элементов на странице", position = 5)
    private Integer limit;

    public Integer getOffset() {
        return offset != null ? offset : 0;
    }

    public Integer getLimit() {
        return limit != null ? limit : 10;
    }
}
