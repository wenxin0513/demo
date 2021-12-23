package com.example.aviatorstarter;

import com.googlecode.aviator.FunctionMissing;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Arrays;
import java.util.Map;

public class FunctionMissingCustomize implements FunctionMissing{

    @Override
    public AviatorObject onFunctionMissing(final String name, final Map<String, Object> env,
        final AviatorObject... args) {
      System.out.println(
          "Function not found, name=" + name + ", env=" + env + ", args=" + Arrays.toString(args));
      return FunctionUtils.wrapReturn(AviatorNil.NIL);
    }
}
