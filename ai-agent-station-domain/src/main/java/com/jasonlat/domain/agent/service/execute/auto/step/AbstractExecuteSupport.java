package com.jasonlat.domain.agent.service.execute.auto.step;

import com.jasonlat.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.jasonlat.domain.agent.adapter.repository.IAgentRepository;
import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import com.jasonlat.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.jasonlat.domain.agent.service.execute.auto.step.factory.DefaultAutoAgentExecuteStrategyFactory;
import com.jasonlat.types.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author jasonlat
 * 2025-09-10  20:07
 */
public abstract class AbstractExecuteSupport extends AbstractMultiThreadStrategyRouter<ExecuteCommandEntity, DefaultAutoAgentExecuteStrategyFactory.DynamicContext, String> {

    protected final Logger log = LoggerFactory.getLogger(AbstractExecuteSupport.class);

    @Resource
    protected BeanUtils beanUtils;
    @Resource
    protected IAgentRepository repository;

    public static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "chat_memory_conversation_id_";
    public static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "chat_memory_response_size_";


    /**
     * 多线程异步数据加载方法
     * <p>
     * 子类需要实现此方法来定义具体的异步数据加载逻辑。
     * 该方法在业务流程处理之前执行，用于预加载必要的数据。
     * </p>
     *
     * @param requestParameter 请求参数
     * @param dynamicContext   动态上下文
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     * @throws TimeoutException     超时异常
     */
    @Override
    protected void multiThread(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
        // 缺省的方法
    }

    /**
     * 根据客户端ID获取ChatClient实例
     *
     * @param clientId 客户端ID
     * @return ChatClient实例
     */
    protected ChatClient getChatClientByClientId(String clientId) {
        return beanUtils.getBean(AiAgentEnumVO.AI_CLIENT.getBeanName(clientId));
    }


}
