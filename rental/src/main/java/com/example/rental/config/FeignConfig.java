package com.example.rental.config;

import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;

import java.util.Collections;

@Configuration
public class FeignConfig {
    @Bean
    public SpringFormEncoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<>() {
            @Override
            public HttpMessageConverters getObject() {
                FormHttpMessageConverter converter = new FormHttpMessageConverter();
                converter.setSupportedMediaTypes(Collections.singletonList(MediaType.MULTIPART_FORM_DATA));
                return new HttpMessageConverters(converter);
            }
        }));
    }
}