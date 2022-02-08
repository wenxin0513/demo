package com.example.basics.hash;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class hashTestMain {
    public static void main(String[] args) {
//        log.info("数组长度不-1：{}", 16 & "郭德纲".hashCode());
//        log.info("数组长度不-1：{}", 16 & "彭于晏".hashCode());
//        log.info("数组长度不-1：{}", 16 & "李小龙".hashCode());
//        log.info("数组长度不-1：{}", 16 & "蔡徐鸡".hashCode());
//        log.info("数组长度不-1：{}", 16 & "唱跳rap篮球鸡叫".hashCode());
//
//        log.info("数组长度-1但是不进行异或和>>>16运算：{}", 15 & "郭德纲".hashCode());
//        log.info("数组长度-1但是不进行异或和>>>16运算：{}", 15 & "彭于晏".hashCode());
//        log.info("数组长度-1但是不进行异或和>>>16运算：{}", 15 & "李小龙".hashCode());
//        log.info("数组长度-1但是不进行异或和>>>16运算：{}", 15 & "蔡徐鸡".hashCode());
//        log.info("数组长度-1但是不进行异或和>>>16运算：{}", 15 & "唱跳rap篮球鸡叫".hashCode());
//
//        log.info("数组长度-1并且进行异或和>>>16运算：{}", 15 & ("郭德纲".hashCode()^("郭德纲".hashCode()>>>16)));
//        log.info("数组长度-1并且进行异或和>>>16运算：{}", 15 & ("彭于晏".hashCode()^("彭于晏".hashCode()>>>16)));
//        log.info("数组长度-1并且进行异或和>>>16运算：{}", 15 & ("李小龙".hashCode()^("李小龙".hashCode()>>>16)));
//        log.info("数组长度-1并且进行异或和>>>16运算：{}", 15 & ("蔡徐鸡".hashCode()^("蔡徐鸡".hashCode()>>>16)));
//        log.info("数组长度-1并且进行异或和>>>16运算：{}", 15 & ("唱跳rap篮球鸡叫".hashCode()^("唱跳rap篮球鸡叫".hashCode()>>>16)));

        /*log.info("16的二进制码：{}",Integer.toBinaryString(16));*/
//        log.info("key的二进制码：{}",Integer.toBinaryString("郭德纲".hashCode()));


        log.info("key的二进制码：{}","郭德纲".hashCode()>>>16);
    }
}
