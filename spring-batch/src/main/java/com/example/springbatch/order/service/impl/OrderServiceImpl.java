package com.example.springbatch.order.service.impl;

import com.example.springbatch.order.service.OrderService;
import org.springframework.stereotype.Service;

@Service

public class OrderServiceImpl implements OrderService {

    public String orderList() {
       System.out.println("======订单查询中=======");
       return "订单查询中";
    }

    @Override
    public String orderInsert() {
        System.out.println("======添加订单=======");
        return "添加订单";
    }
}
