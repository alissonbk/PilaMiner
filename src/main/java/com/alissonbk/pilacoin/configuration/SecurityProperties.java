package com.alissonbk.pilacoin.configuration;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;

@Value
public class SecurityProperties {

    @NotNull
    Jwt jwt;

    @Value
    public static class Jwt {
        @NotBlank
        String secretKey;
        Duration duration;
    }
}

