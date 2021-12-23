/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myexcel.core.builder;

import com.cntaiping.tplhk.reins.common.excel.core.strategy.WidthStrategy;
import com.cntaiping.tplhk.reins.common.excel.exception.ExcelBuildException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.codec.CharEncoding;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

/**
 * freemarker的excel创建者
 *
 * @author zhouhong
 * @version 1.0
 */
public class FreemarkerExcelBuilder extends AbstractExcelBuilder {

    private static final Configuration CFG;

    static {
        CFG = new Configuration(Configuration.VERSION_2_3_28);
        CFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CFG.setDefaultEncoding(CharEncoding.UTF_8);
        CFG.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(), "/");
    }

    private Template template;

    public FreemarkerExcelBuilder() {
        widthStrategy(WidthStrategy.AUTO_WIDTH);
    }

    /**
     * 设置模板信息
     *
     * @param path 模板路径，相对路径
     */
    @Override
    public ExcelBuilder template(String path) {
        try {
            template = CFG.getTemplate(path);
            return this;
        } catch (IOException e) {
            throw ExcelBuildException.of("Failed to get freemarker template", e);
        }
    }

    @Override
    protected <T> void render(Map<String, T> data, Writer out) {
        Objects.requireNonNull(template, "The template cannot be empty. Please set the template first.");
        try {
            template.process(data, out);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
