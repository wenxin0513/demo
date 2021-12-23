package com.example.aviatorstarter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: Aviator 上下文
 * @Date : 2018/9/7 下午3:17
 * @Author : 石冬冬-Seig Heil(shiyongxin2010@163.com)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviatorContext implements Serializable {
    /**
     * 表达式
     */
    private String expression;
    /**
     * 表达式参数
     */
    private Map<String, Object> env;
    /**
     * 是否缓存
     */
    private boolean cached;
}
