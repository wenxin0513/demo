package com.example.springbatch.goods.service.impl;

import com.example.springbatch.goods.service.GoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Override
    public String GoodsList() {
        System.out.println("======商品查询中=======");
        return "商品查询中";
    }
}
