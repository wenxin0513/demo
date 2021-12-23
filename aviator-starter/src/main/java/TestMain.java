import cn.hutool.core.date.DateUtil;
import com.example.aviatorstarter.AviatorContext;
import com.example.aviatorstarter.AviatorExecutor;
import com.example.aviatorstarter.TESTTEST;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.SimpleFormatter;

public class TestMain
{
    public static void main(String[] args) throws ParseException {


//        String str = "2021/9/30";

//        String str = "1.01/5*min(5,DATEDIF(date1,date2,Y)+1)*sum";
//
        Map<String, Object> env1 = new HashMap<>();
        env1.put("issueDate","2020-10-29");
        env1.put("currentDate","2021-12-08");
        env1.put("sumIns",125800.00000000);
        env1.put("gcv",0);
        env1.put("rate",0);
        env1.put("reinsProportion",0);
        System.out.println(AviatorExecutor.execute(AviatorContext.builder().expression("((1.01/5*min(5,datedif(issueDate,currentDate,'Y')+1)*sumIns)-gcv)*rate*reinsProportion/1000/12").env(env1).build()));
////        System.out.println(AviatorExecutor.execute(AviatorContext.builder().expression("fun2").env(env1).build()));
//        System.out.println(AviatorExecutor.execute(AviatorContext.builder().expression(str).env(env1).build()));
//        List<String> str = new ArrayList<>();
//        str.add("1");
//        str.add("2");
//        System.out.println(String.join(",", str));
//        String str = "www gfgf";
//        System.out.println(str.split(" ")[0]+" "+str.split(" ")[1])
//        SimpleFormatter sf = new SimpleFormatter("yyyy/MM/dd");
//        LocalDate date = LocalDate.parse("2021/9/30".replace("/",""), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//        System.out.println(date);
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse("2021-13-45")));
//        System.out.println(LocalDate.parse("2021/9/30", DateTimeFormatter.ofPattern("yyyy/MM/dd")));
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = null;
//        try {
//            date = sdf.parse("2021-12-45");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(DateUtil.parseLocalDateTime("2021/9/31"));
//        System.out.println((String.format("yyyy-MM-dd",new Date())));
//        Boolean flag = TESTTEST.isValidDate("2021/9/31");
//        final long time = Long.parseLong("50000000");
//        System.out.print(new Date(time));
    }
}
