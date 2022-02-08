package com.example.myexcel.core.converter;
import com.example.myexcel.core.container.Pair;
import com.example.myexcel.core.converter.reader.*;
import com.example.myexcel.core.converter.writer.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhouhong
 * @version 1.0
 * @title: ConverterProxy
 * @date 2020/1/2 18:20
 */
@Component
public class ConverterProxy implements ReadConverter<String, Object>, WriteConverter,ExcelRepository {

    private static final List<WriteConverter> WRITE_CONVERTER_CONTAINER = Lists.newArrayList();
    private static final Map<Class<?>, ReadConverter<String, ?>> READ_CONVERTERS = Maps.newHashMap();

    static {
        WRITE_CONVERTER_CONTAINER.add(new DateTimeWriteConverter());
        WRITE_CONVERTER_CONTAINER.add(new StringWriteConverter());
        WRITE_CONVERTER_CONTAINER.add(new BigDecimalWriteConverter());
        WRITE_CONVERTER_CONTAINER.add(new DropDownListWriteConverter());
        WRITE_CONVERTER_CONTAINER.add(new LinkWriteConverter());
        WRITE_CONVERTER_CONTAINER.add(new MappingWriteConverter());
        WRITE_CONVERTER_CONTAINER.add(new ImageWriteConverter());

        BoolReadConverter boolReadConverter = new BoolReadConverter();
        READ_CONVERTERS.put(Boolean.class, boolReadConverter);
        READ_CONVERTERS.put(boolean.class, boolReadConverter);

        READ_CONVERTERS.put(Date.class, new DateReadConverter());
        READ_CONVERTERS.put(LocalDate.class, new LocalDateReadConverter());
        READ_CONVERTERS.put(LocalDateTime.class, new LocalDateTimeReadConverter());

        NumberReadConverter<Double> doubleReadConverter = NumberReadConverter.of(Double::valueOf);
        READ_CONVERTERS.put(Double.class, doubleReadConverter);
        READ_CONVERTERS.put(double.class, doubleReadConverter);

        NumberReadConverter<Float> floatReadConverter = NumberReadConverter.of(Float::valueOf);
        READ_CONVERTERS.put(Float.class, floatReadConverter);
        READ_CONVERTERS.put(float.class, floatReadConverter);

        NumberReadConverter<Long> longReadConverter = NumberReadConverter.of(Long::valueOf, true);
        READ_CONVERTERS.put(Long.class, longReadConverter);
        READ_CONVERTERS.put(long.class, longReadConverter);

        NumberReadConverter<Integer> integerReadConverter = NumberReadConverter.of(Integer::valueOf, true);
        READ_CONVERTERS.put(Integer.class, integerReadConverter);
        READ_CONVERTERS.put(int.class, integerReadConverter);

        NumberReadConverter<Short> shortReadConverter = NumberReadConverter.of(Short::valueOf, true);
        READ_CONVERTERS.put(Short.class, shortReadConverter);
        READ_CONVERTERS.put(short.class, shortReadConverter);

        NumberReadConverter<Byte> byteReadConverter = NumberReadConverter.of(Byte::valueOf, true);
        READ_CONVERTERS.put(Byte.class, byteReadConverter);
        READ_CONVERTERS.put(byte.class, byteReadConverter);

        READ_CONVERTERS.put(BigDecimal.class, new BigDecimalReadConverter());
        READ_CONVERTERS.put(String.class, new StringReadConverter());

        READ_CONVERTERS.put(Timestamp.class, new TimestampReadConverter());

        NumberReadConverter<BigInteger> bigIntegerReadConverter = NumberReadConverter.of(BigInteger::new, true);
        READ_CONVERTERS.put(BigInteger.class, bigIntegerReadConverter);
    }

    @Override
    public Object readConvert(Field field, String obj) {
        ReadConverter<String, ?> converter = READ_CONVERTERS.get(field.getType());
        if (converter == null) {
            throw new IllegalStateException("No suitable type converter was found.");
        }
        return converter.readConvert(field, obj);
    }

    @Override
    public Pair<Class, Object> writeConvert(Field field, Object fieldVal) {
        Optional<WriteConverter> writeConverterOptional = WRITE_CONVERTER_CONTAINER.stream()
                .filter(writeConverter -> writeConverter.support(field, fieldVal))
                .findFirst();
        return writeConverterOptional.isPresent() ?
                writeConverterOptional.get().writeConvert(field, fieldVal) :
                Pair.of(field.getType(), fieldVal);
    }

    @Override
    public boolean support(Field field, Object fieldVal) {
        return true;
    }










    @Override
    public String[] resource() {
        return new String[0];
    }
}
