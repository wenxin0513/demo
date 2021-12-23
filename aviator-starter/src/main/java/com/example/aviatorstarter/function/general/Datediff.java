package com.example.aviatorstarter.function.general;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class Datediff  extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        LocalDate targetDay1 = (LocalDate)arg1.getValue(env);
        LocalDate targetDay2 = (LocalDate)arg2.getValue(env);
        String dateType = (String)arg3.getValue(env);
        if(targetDay1.isAfter(targetDay2)){
            throw new RuntimeException("d1 must before d2 ");
        }
        if("Y".equalsIgnoreCase(dateType)){
            return AviatorLong.valueOf(ChronoUnit.YEARS.between(targetDay1, targetDay2));
        }else if("M".equalsIgnoreCase(dateType)){
            return AviatorLong.valueOf(ChronoUnit.MONTHS.between(targetDay1, targetDay2));
        }else if("D".equalsIgnoreCase(dateType)){
            return AviatorLong.valueOf(ChronoUnit.DAYS.between(targetDay1, targetDay2));
        }else {
            throw new RuntimeException("dateType error, must be Y/M/D ");
        }
    }

    @Override
    public String getName() {
        return "datedif";
    }

}
