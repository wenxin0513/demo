package com.example.basics.proxy.impl;

import com.example.basics.proxy.interfa.Movie;

public class RealMovie implements Movie {
    @Override
    public void play() {
        System.out.println("您正在观看电影 《肖申克的救赎》");
    }
}
