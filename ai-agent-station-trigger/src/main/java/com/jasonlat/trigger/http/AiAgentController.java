package com.jasonlat.trigger.http;

import com.alibaba.fastjson.JSON;
import com.jasonlat.api.IAiAgentService;
import com.jasonlat.api.dto.AutoAgentRequestDTO;
import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import com.jasonlat.domain.agent.service.execute.IExecuteStrategy;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jasonlat
 * 2025-09-12  21:25
 */
@Slf4j
@RestController
@RequestMapping("/agent")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AiAgentController implements IAiAgentService {

    @Resource(name = "autoAgentExecuteStrategy")
    private IExecuteStrategy autoAgentExecuteStrategy;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 自动问答 - 流式返回
     *
     * @param request  请求参数
     * @param response http 响应
     */
    @Override
    @RequestMapping(value = "auto_agent", method = RequestMethod.POST)
    public ResponseBodyEmitter autoAgent(@RequestBody AutoAgentRequestDTO request, HttpServletResponse response) {
        log.info("AutoAgent流式执行请求开始，请求信息：{}", JSON.toJSONString(request));

        try {
            // 设置SSE响应头
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");

            // 1. 创建流式输出对象
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(Long.MAX_VALUE);
            // 2. 构建执行命令实体
            ExecuteCommandEntity executeCommandEntity = ExecuteCommandEntity.builder()
                    .aiAgentId(request.getAiAgentId())
                    .message(request.getMessage())
                    .sessionId(request.getSessionId())
                    .maxStep(request.getMaxStep())
                    .build();

            // 3. 异步执行AutoAgent
            threadPoolExecutor.execute(() -> {
                try {
                    autoAgentExecuteStrategy.execute(executeCommandEntity, emitter);
                } catch (Exception e) {
                    log.error("AutoAgent执行异常：{}", e.getMessage(), e);
                    try {
                        emitter.send("执行异常：" + e.getMessage());
                    } catch (Exception ex) {
                        log.error("发送异常信息失败：{}", ex.getMessage(), ex);
                    }
                } finally {
                    try {
                        emitter.complete();
                    } catch (Exception e) {
                        log.error("完成流式输出失败：{}", e.getMessage(), e);
                    }
                }
            });

            return emitter;
        } catch (Exception e) {
            log.error("AutoAgent请求处理异常：{}", e.getMessage(), e);
            ResponseBodyEmitter errorEmitter = new ResponseBodyEmitter();
            try {
                errorEmitter.send("请求处理异常：" + e.getMessage());
                errorEmitter.complete();
            } catch (Exception ex) {
                log.error("发送错误信息失败：{}", ex.getMessage(), ex);
            }
            return errorEmitter;
        }

    }
}
