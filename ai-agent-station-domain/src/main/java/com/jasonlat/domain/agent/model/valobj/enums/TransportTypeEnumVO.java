package com.jasonlat.domain.agent.model.valobj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TransportTypeEnumVO {

    SSE("sse", "sse mcp tool"),
    STDIO("stdio", "stdio mcp tool"),

    ;

    private String code;
    private String info;
}
