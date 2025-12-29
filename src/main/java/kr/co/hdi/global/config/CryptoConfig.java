package kr.co.hdi.global.config;

import kr.co.hdi.global.auth.AesEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    @Value("${crypto.password-key}")
    private String keyBase64;

    @Bean
    public AesEncryptor aesEncryptor() {
        return new AesEncryptor(keyBase64);
    }
}
