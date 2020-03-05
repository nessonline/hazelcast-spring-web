package ru.parma.ventslavovich.hazelcast.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

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
    @ApiModelProperty(value = "Дата регистрации декларации", position = 4)
    private LocalDate declDate;
    @ApiModelProperty(value = "Дата окончания действия декларации о соответствии", position = 5)
    private LocalDate declEndDate;
    @ApiModelProperty(value = "Номер таможенной декларации", position = 6)
    private String customDeclNumber;
    @ApiModelProperty(value = "Статус заявления", position = 7)
    private Integer appStatus;
    @ApiModelProperty(value = "Номер заявления на регистрацию декларации", position = 8)
    private String appNumber;
    @ApiModelProperty(value = "Дата заявления на регистрацию декларации", position = 9)
    private LocalDate appDate;
    @ApiModelProperty(value = "Технические регламенты", position = 10)
    private String technicalReglaments;
    @ApiModelProperty(value = "Группа продукции", position = 11)
    private String group;
    @ApiModelProperty(value = "Единый перечень продукции", position = 12)
    private String productSingleList;
    @ApiModelProperty(value = "Тип декларации", position = 13)
    private String declType;
    @ApiModelProperty(value = "Схема декларирования", position = 14)
    private String declSchema;
    @ApiModelProperty(value = "Тип объекта декларирования", position = 15)
    private String declObjectType;
    @ApiModelProperty(value = "Идентификатор типа заявителя", position = 16)
    private Integer idApplicantLegalSubjectType;
    @ApiModelProperty(value = "Тип заявителя", position = 17)
    private String applicantLegalSubjectType;
    @ApiModelProperty(value = "Тип декларанта", position = 18)
    private String applicantType;
    @ApiModelProperty(value = "Заявитель", position = 19)
    private String applicantName;
    @ApiModelProperty(value = "Адрес заявителя", position = 20)
    private String applicantAddress;
    @ApiModelProperty(value = "ИНН заявителя", position = 21)
    private String applicantInn;
    @ApiModelProperty(value = "ОГРН заявителя", position = 22)
    private String applicantOgrn;
    @ApiModelProperty(value = "Идентификатор типа изготовителя", position = 23)
    private Integer idManufacterLegalSubjectType;
    @ApiModelProperty(value = "Тип изготовителя", position = 24)
    private String manufacterLegalSubjectType;
    @ApiModelProperty(value = "Вид изготовителя", position = 25)
    private String manufacterType;
    @ApiModelProperty(value = "Наименование изготовителя", position = 26)
    private String manufacterName;
    @ApiModelProperty(value = "Адрес изготовителя", position = 27)
    private String manufacterAddress;
    @ApiModelProperty(value = "ИНН изготовителя", position = 28)
    private String manufacterInn;
    @ApiModelProperty(value = "ОГРН изготовителя", position = 29)
    private String manufacterOgrn;
    @ApiModelProperty(value = "Номер аттестата аккредитации органа по сертификации", position = 30)
    private String certificationAuthorityAttestatRegNumber;
    @ApiModelProperty(value = "Признак активности", position = 31)
    private Boolean active;

    @Override
    public int compareTo(SearchDeclaration other) {
        return Long.valueOf(this.id - other.id).intValue();
    }
}
