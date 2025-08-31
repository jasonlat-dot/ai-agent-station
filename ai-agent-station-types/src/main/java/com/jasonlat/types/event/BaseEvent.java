package com.jasonlat.types.event;

import com.jasonlat.types.snow.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author jasonlat
 */
@Data
public abstract class BaseEvent<T> {

    @Resource
    protected SnowflakeIdGenerator snowflakeIdGenerator;

    public abstract EventMessage<T> buildEventMessage(T data);

    public abstract String topic();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventMessage<T> {
        private String id;
        private Date timestamp;
        private T data;
    }
}
