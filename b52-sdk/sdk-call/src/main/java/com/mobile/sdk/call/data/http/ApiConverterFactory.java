package com.mobile.sdk.call.data.http;

import com.mobile.guava.jvm.domain.SourceException;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ApiConverterFactory extends Converter.Factory {

    private ApiConverterFactory() {
        super();
    }

    public static ApiConverterFactory create() {
        return new ApiConverterFactory();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type,
            Annotation[] annotations,
            Retrofit retrofit
    ) {
        Converter<ResponseBody, ApiResponse<?>> delegate = retrofit.nextResponseBodyConverter(
                this,
                Types.newParameterizedType(ApiResponse.class, type),
                annotations
        );
        return new Converter<ResponseBody, Object>() {
            @Nullable
            @Override
            public Object convert(ResponseBody value) throws IOException {
                ApiResponse apiResponse = delegate.convert(value);
                if (apiResponse.success()) {
                    return apiResponse.getData();
                }
                throw new SourceException(apiResponse.getMsg(), apiResponse.getCode());
            }
        };
    }
}
