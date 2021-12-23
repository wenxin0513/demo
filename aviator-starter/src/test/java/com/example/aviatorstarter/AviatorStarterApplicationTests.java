package com.example.aviatorstarter;

import com.googlecode.aviator.AviatorEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Map;

@SpringBootTest(classes = AviatorApplication.class)
class AviatorStarterApplicationTests {

    @Autowired
    AviatorExecutor aviatorExecutor;

    @Test
    public void testDateDiff(){
        Map<String, Object> env1 = AviatorEvaluator.newEnv(
                "issueDate", LocalDate.of(2020,1,1),
                "currentDate", LocalDate.of(2022,5,15),"sums",100);

//        System.out.println(aviatorExecutor.execute(AviatorContext.builder().expression("datedif(d1,d2,dateType)").env(env1).build()));
//

        System.out.println(aviatorExecutor.execute(AviatorContext.builder().expression("1.01/5*min(5,datedif(issueDate,currentDate,'Y')+1)*sums").env(env1).build()));

    }

}
