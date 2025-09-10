package com.jasonlat.domain.agent.service.armory;

import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;

/**
 * @author jasonlat
 * 2025-09-10  21:34
 */
public interface IArmoryStrategy {

    /**
     * 策略执行
     * @param armoryCommandEntity 策略执行参数
     * @throws Exception 策略执行过程中可能抛出的异常
     */
    String armory(ArmoryCommandEntity armoryCommandEntity) throws Exception;
}
