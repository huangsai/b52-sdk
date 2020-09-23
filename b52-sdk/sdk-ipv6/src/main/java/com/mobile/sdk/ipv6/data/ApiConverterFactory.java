package com.mobile.sdk.ipv6.data;

import com.mobile.guava.jvm.domain.SourceException;
import com.mobile.sdk.ipv6.data.api.ApiResponse;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ApiConverterFactory extends Converter.Factory {

    public static ApiConverterFactory create() {
        return new ApiConverterFactory();
    }

    private ApiConverterFactory() {
        super();
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
                if (apiResponse.getCode() != 0) {
                    throw new SourceException(apiResponse.getMsg(), apiResponse.getCode());
                }
                return apiResponse.getData();
            }
        };
    }
}
