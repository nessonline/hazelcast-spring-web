package ru.parma.ventslavovich.hazelcast.data.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
@ApiModel("Ответ с количеством элементов")
public class CountResponse {
    @ApiModelProperty(value = "Количество элементов", position = 1)
    private long count;
}
