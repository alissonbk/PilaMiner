package com.alissonbk.pilacoin.configuration;


import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;


@Value
@ConstructorBinding
@ConfigurationProperties("pilacoin-api")
public class ApiConfiguration {
    @NotNull
    SecurityProperties security;

}
