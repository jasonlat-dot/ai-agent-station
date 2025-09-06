package com.jasonlat.infrastructure.dao.po.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class BasePoSupport {

    /**
     * 状态(0:禁用,1:启用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Getter
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Getter
    private LocalDateTime updateTime;

    /**
     * 查看是否可用
     */
    public boolean isAvailable() {
        return this.status == 1;
    }
}
