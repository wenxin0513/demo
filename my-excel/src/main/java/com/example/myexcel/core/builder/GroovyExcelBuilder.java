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

import com.example.myexcel.core.strategy.WidthStrategy;
import com.example.myexcel.exception.ExcelBuildException;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhouhong
 * @version 1.0
 */
public class GroovyExcelBuilder extends AbstractExcelBuilder {

    private static final MarkupTemplateEngine ENGINE;

    static {
        TemplateConfiguration config = new TemplateConfiguration();
        ENGINE = new MarkupTemplateEngine(config);
    }

    private Template template;

    public GroovyExcelBuilder() {
        widthStrategy(WidthStrategy.AUTO_WIDTH);
    }

    @Override
    public ExcelBuilder template(String path) {
        Objects.requireNonNull(path);
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            template = ENGINE.createTemplate(reader);
            return this;
        } catch (ClassNotFoundException | IOException e) {
            throw ExcelBuildException.of("Failed to get groovy template", e);
        }
    }


    @Override
    protected <T> void render(Map<String, T> renderData, Writer out)  {
        Objects.requireNonNull(template, "The template cannot be empty. Please set the template first.");
        Writable output = template.make(renderData);
        try {
            output.writeTo(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
