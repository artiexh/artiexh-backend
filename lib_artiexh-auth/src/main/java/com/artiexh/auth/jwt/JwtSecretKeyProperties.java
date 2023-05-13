package com.artiexh.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "artiexh.jwt.secret", ignoreUnknownFields = true)
@Data
public class JwtSecretKeyProperties {
    private String key;
    private String salt = "";
    private String info = "";
}
