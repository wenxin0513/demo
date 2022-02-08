package com.example.myexcel.core.parser;
import com.example.myexcel.utils.ApplicationUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author zhouhong
 * @version 1.0
 * @title: Title
 * @description: Excel 标题传输对象
 * @date 2020/1/3 9:06
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Title {

    /**
     * 索引
     */
    int index;
    /**
     * 标题名称
     */
    String name;

    /**
     * 备注
     */
    String comment;
    /**
     * 下拉框数据
     */
    String[] resource;


    public static Title of(int index, String name, String comment, String[] resource,boolean required) {
        StringBuilder sb=new StringBuilder(ApplicationUtil.getMSA().getMessage(name, name));
        if (required){
            sb.append("[*]");
        }
        return Title.builder()
                .index(index)
                .name(sb.toString())
                .comment(comment)
                .resource(resource)
                .build();
    }
}
