package com.example.demo.avistor.util;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplaceJstlFunction extends AbstractFunction {

    public final static Pattern pattern = Pattern.compile("\\$\\{([^}]+?)\\}");


    @Override
    public String getName() {
        return "string.jstl";
    }

    /**
     * 替换参数
     * @param env
     * @param str
     * @param defaultResult
     * @return
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject str, AviatorObject defaultResult) {
        String text = FunctionUtils.getStringValue(str, env);

        // 正则找到文案中包含的参数
        List<String> params = new ArrayList<>();
        Matcher m = pattern.matcher(text);
        while(m.find()){
            params.add(m.group(1));
        }

        // 替换参数
        for (String paramKey : params) {
            String param = "${" + paramKey + "}";
            if (text.contains(param) && env.containsKey(paramKey)) {
                text = text.replace(param, String.valueOf(env.get(paramKey)));
            }
        }

        if (pattern.matcher(text).find()){
            return defaultResult;
        } else {
            return new AviatorString(text);
        }

    }

    public static void main(String[] args) {
        String text = "文案${abc}tihuan";
        String defaultText = "文案def";
        AviatorEvaluator.addFunction(new StringReplaceJstlFunction());
        String expression = "string.jstl(_text_,_default_)";
        Expression compiledExp = AviatorEvaluator.compile(expression);
        Map<String, Object> params = new HashMap<>();
        params.put("_text_", text);
        params.put("_default_", defaultText);
        params.put("abc", "123");
        System.out.println(compiledExp.execute(params));



    }
}
