package com.jasonlat.domain.agent.service.armory;

import com.jasonlat.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Objects;


public abstract class AbstractArmorySupport extends AbstractMultiThreadStrategyRouter<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> {

    private static final Logger log = LoggerFactory.getLogger(AbstractArmorySupport.class);


    @Autowired
    protected ApplicationContext applicationContext;

    @Override
    protected void multiThread(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext)  {
        // 缺省的方法, 子类按需实现
    }

    protected String beanName(String id) {
        return null;
    }

    protected String dynamicDataKey() {
        return null;
    }

    /**
     * 通用的Bean注册方法
     *
     * @param beanName  Bean名称
     * @param beanClass Bean类型
     * @param <T>       Bean类型
     */
    protected synchronized <T> void registerBean(String beanName, Class<T> beanClass, T beanInstance) {
        // 参数校验
        if (!StringUtils.hasLength(beanName) || !StringUtils.hasLength(beanName.trim())) {
            throw new IllegalArgumentException("Bean名称不能为空");
        }
        Objects.requireNonNull(beanClass, "Bean类型不能为null");
        Objects.requireNonNull(beanInstance, "Bean实例不能为null");

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        // 注册Bean
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass, () -> beanInstance);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        // 如果Bean已存在，先移除
        if (beanFactory.containsBeanDefinition(beanName)) {
            beanFactory.removeBeanDefinition(beanName);
        }

        // 注册新的Bean
        beanFactory.registerBeanDefinition(beanName, beanDefinition);

        log.info("成功注册Bean: {}", beanName);
    }

    /**
     * 获取Bean
     *
     * @param beanName Bean名称
     * @param <T>      Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    protected <T> T getBean(String beanName) {
        if (!StringUtils.hasLength(beanName) || !StringUtils.hasLength(beanName.trim())) {
            throw new IllegalArgumentException("Bean名称不能为空");
        }
        try {
            if (!applicationContext.containsBean(beanName)) {
                log.warn("Bean不存在: {}", beanName);
                return null;
            }
            T bean = (T) applicationContext.getBean(beanName);
            log.debug("成功获取Bean: {}", beanName);
            return bean;
        } catch (ClassCastException e) {
            log.error("Bean类型转换失败 [名称={}]", beanName, e);
            throw new RuntimeException("Bean类型转换失败: " + beanName, e);
        } catch (BeansException e) {
            log.error("获取Bean失败 [名称={}]", beanName, e);
            throw new RuntimeException("获取Bean失败: " + beanName, e);
        }
    }

    /**
     * 带类型的Bean获取方法，更安全的类型转换
     *
     * @param beanName  Bean名称
     * @param beanClass Bean类型
     * @param <T>       Bean类型
     * @return Bean实例
     */
    protected <T> T getBean(String beanName, Class<T> beanClass) {
        if (!StringUtils.hasLength(beanName) || !StringUtils.hasLength(beanName.trim())) {
            throw new IllegalArgumentException("Bean名称不能为空");
        }
        Objects.requireNonNull(beanClass, "Bean类型不能为null");

        try {
            T bean = applicationContext.getBean(beanName, beanClass);
            log.debug("成功获取Bean: {}({})", beanName, beanClass.getName());
            return bean;
        } catch (BeansException e) {
            log.error("获取Bean失败 [名称={}, 类型={}]", beanName, beanClass.getName(), e);
            throw new RuntimeException("获取Bean失败: " + beanName, e);
        }
    }



}
