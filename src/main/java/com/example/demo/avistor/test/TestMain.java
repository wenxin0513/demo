package com.example.demo.avistor.test;

import com.example.demo.avistor.util.AviatorUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TestMain {
    public static void main(String[] args) {
//     String formual = "(sumIns-gcv)*0.25*rate*reinsProportion/1000/12";
//     Map map = new HashMap<String, BigDecimal>();
//     map.put("sumIns",Integer.valueOf("500000000"));
//     map.put("gcv",new BigDecimal("10.66"));
//     map.put("rate",new BigDecimal("0.59"));
//     map.put("reinsProportion",new BigDecimal("0.99"));

//        map.put("rate",new BigDecimal("0.58999999999999996891375531049561686813831329345703125"));
//        map.put("reinsProportion",new BigDecimal("0.9879999989637699127278517574311979"));
//      System.out.println("结果"+ AviatorUtil.excute(formual,map,2));
//        AviatorEvaluator.addFunction(new AviatorUtil.IF_Function());
//
//        List<String> list = new ArrayList();
//        list.add("a");
//        list.add("b");
//        String ss = String.join(",", list);
//        System.out.println("ceshi01"+ss);
//        String str = "01,02,03";
//        List<String> listString = Arrays.asList(str);
////        String[] strings = str.split(",");
//        System.out.println("ceshi02"+listString);
//        Integer SRT1 = String.valueOf(listString)

        System.out.println(LocalDate.of(2022,10,8).isEqual(LocalDate.now()));

//        ParamInfo paramInfo = new ParamInfo();
//        paramInfo.setParamSign("cheshi02");
//        paramInfo.setId("cheshi01");
//        QueryParam queryParam = new QueryParam();
//        queryParam.setParamId("cheshi03");
//        paramInfo.setQueryParam(queryParam);
//        Map<String,Object> jsonObject = JSONUtil.parseObj(paramInfo);
////        System.out.println(jsonObject);
//        BigDecimal sumRiskInsured = BigDecimal.ZERO;
//        BigDecimal sumRetention = BigDecimal.ZERO;
//        List<BigDecimal> bigDecimals = new ArrayList<>();
//        bigDecimals.add(new BigDecimal("1"));
//        bigDecimals.add(new BigDecimal("3"));
//        for (BigDecimal item : bigDecimals) {
//            sumRetention = sumRetention.add(item);
//        }
//        System.out.println(sumRetention);
    }
}
