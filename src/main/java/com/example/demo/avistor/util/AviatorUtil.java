package com.example.demo.avistor.util;

import cn.hutool.core.util.NumberUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class AviatorUtil {

    /**
     * @param formula :公式
     * @param map ：变量
     * @param roundDecimal：保留几位小数
     * @return
     */
    @Deprecated
    public static String excute(String formula, Map map, Integer roundDecimal){
        Object result = AviatorEvaluator.execute(formula, map);
        BigDecimal value = NumberUtil.round(new BigDecimal(result+""),roundDecimal);
        return value.toString();
    }


    public static String readerToString(Reader reader) throws IOException {
        BufferedReader r = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        try
        {
            while((line=r.readLine())!=null)
            {
                sb.append(line);
                sb.append("\r\n");
            }
        }
        catch (IOException e)
        {
            log.error("readerToString error", e);
            throw e;
        }
        return sb.toString();
    }




}
