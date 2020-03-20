package com.etone.protocolsupply.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description //TODO
 * @Date 2018/12/9 下午5:56
 * @Author maozhihui
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public abstract class PagingDto<T> implements Iterable<T> {

    private PageStatisticsDto statistics = new PageStatisticsDto();
    private String            self;
    private String            prev;
    private String            next;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public abstract void add(T item);
}
