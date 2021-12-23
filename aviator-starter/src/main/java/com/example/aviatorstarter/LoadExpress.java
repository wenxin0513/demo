package com.example.aviatorstarter;

import com.googlecode.aviator.ExpressCustomize;
import com.googlecode.aviator.ILoadExpressCustomize;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : LoadExpress
 * @Description : TODO
 * @Author : taiping
 * @Date: 2021/9/3 18:12
 **/
public class LoadExpress implements ILoadExpressCustomize {

    @Override
    public List<ExpressCustomize> loadExpressCustomize() {
        ExpressCustomize e1 = ExpressCustomize.build().setName("fun1").setExpress("1+2");
        ExpressCustomize e2 = ExpressCustomize.build().setName("fun2").setExpress("min(x,y,z,d)");
        ExpressCustomize e3 = ExpressCustomize.build().setName("fun3").setExpress("fun1+fun2");
        ExpressCustomize e4 = ExpressCustomize.build().setName("fun4").setExpress("if(fun3>3) {return 100;}else{return 200;}");
        ExpressCustomize e5 = ExpressCustomize.build().setName("min").setExpress("100");

        List<ExpressCustomize> list = new LinkedList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        return list;

    }
}
