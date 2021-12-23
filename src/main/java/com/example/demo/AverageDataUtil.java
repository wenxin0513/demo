package com.example.demo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.istack.internal.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AverageDataUtil {
    /**
     * 定义存储待分配数据集合
     */
    private static List<String> list =   Lists.newArrayList();

    /**
     * 定义存储分组数据的结构，Map去除泛型，适配多种数据类型格式，使用时需注意
     */
    private static List<Map> los = Lists.newArrayList();

    /**
     * 供外部调用的平均分配方法
     *
     * @param visitorIds 客户列表
     * @param sellerIds  员工列表
     * @return List<Map>
     */
    public static List<Map> averageData(List<String> visitorIds, List<String> sellerIds) {
        initCollections(visitorIds, sellerIds);
        if (visitorIds.size() >= sellerIds.size()) {
            groupByData(los.size());
            return getMaps();
        } else {
            groupByData(list.size());
            return getMaps();
        }
    }

    /**
     * 返回数据，清空静态缓存
     *
     * @return List<Map>
     */
    @NotNull
    private static List<Map> getMaps() {
        List<Map> listMap = Lists.newArrayList();
        listMap.addAll(los);
        //清空静态数据
        los = Lists.newArrayList();
        list = Lists.newArrayList();
        return listMap;
    }

    /**
     * 分配数据
     *
     * @param size       分组大小
     */
    private static void groupByData(int size) {
        List<String> augmented = list;

        List<List<String>> lists = chunk2(augmented, size);

        for (int i = 0; i < size; i++) {
            Map map = los.get(i);
            Iterator iterator = map.keySet().iterator();
            if (iterator.hasNext()) {
                String next = (String) iterator.next();
                map.put(next, lists.get(i));
            }
        }
    }

    /**
     * 初始化集合数据
     *
     * @param visitorIds 待分配数据
     * @param sellerIds  分配目标
     */
    private static void initCollections(List<String> visitorIds, List<String> sellerIds) {
        //每次调用前清空数据
        if (list.size() > 0) {
            list = Lists.newArrayList();
        }
        if (los.size() > 0) {
            los = Lists.newArrayList();
        }
        list.addAll(visitorIds);
        List<Map<String, List<String>>> list1 = new ArrayList<>();
        for (String sellerId : sellerIds) {
            Map<String, List<String>> map = new HashMap<>(16);
            List<String> list = new ArrayList<>();
            map.put(sellerId, list);
            list1.add(map);
        }
        los.addAll(list1);
    }

    /**
     * 分组数据-核心算法，勿动
     *
     * @param list  需分配数据
     * @param group 分组大小
     * @param <T>   分组数据泛型
     * @return 分组结果
     */
    private static <T> List<List<T>> chunk2(List<T> list, int group) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        List<List<T>> result = Lists.newArrayList();
        Map<Integer, Set<T>> temp = Maps.newHashMap();
        for (int i = 0; i < list.size(); i++) {
            if (temp.containsKey(i % group)) {
                Set<T> ts = temp.get(i % group);
                ts.add(list.get(i));
                temp.put(i % group, ts);
            } else {
                Set<T> ts = Sets.newHashSet();
                ts.add(list.get(i));
                temp.put(i % group, ts);
            }
        }
        for (Set<T> ts : temp.values()) {
            result.add(Lists.newArrayList(ts));
        }
        return result;
    }

    public static void main(String[] args) {
        List<String> visitorIds = new ArrayList<>();
        visitorIds.add("aa");
        visitorIds.add("bb");
        visitorIds.add("cc");
        visitorIds.add("dd");
        visitorIds.add("ee");
        visitorIds.add("ff");
        visitorIds.add("gg");
        visitorIds.add("hh");
        visitorIds.add("ii");
        visitorIds.add("jj");
        visitorIds.add("kk");
        List<String> sellerIds = new ArrayList<>();
        sellerIds.add("11");
        sellerIds.add("22");
        sellerIds.add("33");
        sellerIds.add("44");
        sellerIds.add("55");
        sellerIds.add("66");
        sellerIds.add("77");
        sellerIds.add("88");
        sellerIds.add("99");
        sellerIds.add("1010");
        sellerIds.add("1111");
        List<Map> maps = averageData(visitorIds, sellerIds);
        System.out.println(maps);
    }

}
