package com.jasonlat.domain.agent.service.execute;

import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author jasonlat
 * 2025-09-10  19:55
 */
public interface IExecuteStrategy {
    /**
     * 执行命令
     * @param executeCommandEntity 执行命令的实体
     */
    String execute(ExecuteCommandEntity executeCommandEntity, ResponseBodyEmitter emitter) throws Exception;
}
