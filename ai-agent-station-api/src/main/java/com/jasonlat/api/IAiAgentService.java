package com.jasonlat.api;

import com.jasonlat.api.dto.AutoAgentRequestDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IAiAgentService {

    /**
     * 自动问答 - 流式返回
     * @param request 请求参数
     */
    ResponseBodyEmitter autoAgent(AutoAgentRequestDTO request, HttpServletResponse response);
}
