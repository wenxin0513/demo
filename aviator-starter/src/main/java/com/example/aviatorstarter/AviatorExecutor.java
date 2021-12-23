package com.example.aviatorstarter;

import com.example.aviatorstarter.function.collection.Exsit;
import com.example.aviatorstarter.function.collection.NotExsit;
import com.example.aviatorstarter.function.general.*;
import com.example.aviatorstarter.function.number.*;
import com.example.aviatorstarter.function.object.Nvl;
import com.example.aviatorstarter.function.object.ToNumber;
import com.example.aviatorstarter.function.object.ToString;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;


/**
 * @description: Aviator引擎执行器
 * @Date : 2021/8/31
 * @Author : xujunqiang
 */
@Slf4j
@Service
public final class AviatorExecutor {

    private static Map expressHashMap = new HashMap();

    private AviatorExecutor(){}

    static {
        loadFunctionCustomize();
        loadExpressCustomize();
    }

    private static void loadFunctionCustomize() {
        log.info("init AviatorEvaluator......");
        AviatorEvaluator.setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL , true);
        AviatorEvaluator.setOption(Options.MATH_CONTEXT, MathContext.DECIMAL128);
        AviatorEvaluator.getInstance().setCachedExpressionByDefault(true);
        AviatorEvaluator.setFunctionMissing(new FunctionMissingCustomize());
        AviatorEvaluator.getInstance().setLoadExpressCustomize(new LoadExpress());
        AviatorEvaluator.addFunction(new Nvl());
        AviatorEvaluator.addFunction(new Sum());
        AviatorEvaluator.addFunction(new Min());
        AviatorEvaluator.addFunction(new Max());
        AviatorEvaluator.addFunction(new Round());
        AviatorEvaluator.addFunction(new Ceil());
        AviatorEvaluator.addFunction(new Floor());
        AviatorEvaluator.addFunction(new Scale());
        AviatorEvaluator.addFunction(new Exsit());
        AviatorEvaluator.addFunction(new NotExsit());
        AviatorEvaluator.addFunction(new DiffDays());
        AviatorEvaluator.addFunction(new Age());
        AviatorEvaluator.addFunction(new ChineseNumberUpper());
        AviatorEvaluator.addFunction(new StringNumbersSum());
        AviatorEvaluator.addFunction(new ToNumber());
        AviatorEvaluator.addFunction(new ToString());
        AviatorEvaluator.addFunction(new Datediff());




        AviatorEvaluator.setOption(Options.TRACE_EVAL, true);

        try {
            AviatorEvaluator.setTraceOutputStream(new FileOutputStream(new File("aviator.log")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void loadExpressCustomize(){
        AviatorExecutor.expressHashMap.put("haha", "1+2");
    }

    /**
     * 执行结果
     * @param context 上下文对象
     * @return
     */
    public static Object execute(AviatorContext context){
        Object result = AviatorEvaluator.execute(context.getExpression(), context.getEnv(), context.isCached());
        log.info("Aviator执行器context={},result={}", context,result);
        return result;
    }

    public static Object executeCache(AviatorContext context){
        Object result = AviatorEvaluator.execute(context.getExpression(), context.getEnv(), AviatorEvaluator.getInstance().isCachedExpressionByDefault());
        log.info("Aviator执行器context={},result={}", context,result);
        return result;
    }

}
