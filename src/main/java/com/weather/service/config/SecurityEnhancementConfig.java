package com.weather.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class SecurityEnhancementConfig {

    // SecurityFilterChain is defined in SecurityConfig
    // This class provides additional security beans

    // @Bean - corsConfigurationSource is defined in SecurityConfig
    public CorsConfigurationSource corsConfigurationSourceEnhanced() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://energyanalytics.com",
            "https://app.energyanalytics.com", 
            "https://*.energyanalytics.com",
            "http://localhost:*" // Development only
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // @Bean - passwordEncoder is defined in SecurityConfig
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder(12); // Higher strength for production
    // }

    // @Bean - DataEncryptionService is already a @Service component
    // public DataEncryptionService dataEncryptionService() {
    //     return new DataEncryptionService();
    // }

    /**
     * Service for encrypting sensitive data at rest
     */
    public static class DataEncryptionService {
        private static final String ALGORITHM = "AES/GCM/NoPadding";
        private static final int GCM_IV_LENGTH = 12;
        private static final int GCM_TAG_LENGTH = 16;
        
        private final SecretKey secretKey;
        private final SecureRandom secureRandom;

        public DataEncryptionService() {
            this.secretKey = generateSecretKey();
            this.secureRandom = new SecureRandom();
        }

        private SecretKey generateSecretKey() {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256);
                return keyGenerator.generateKey();
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate encryption key", e);
            }
        }

        public String encrypt(String plainText) {
            try {
                byte[] iv = new byte[GCM_IV_LENGTH];
                secureRandom.nextBytes(iv);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

                byte[] encryptedData = cipher.doFinal(plainText.getBytes());
                
                // Combine IV and encrypted data
                byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
                System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
                System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);

                return Base64.getEncoder().encodeToString(encryptedWithIv);
            } catch (Exception e) {
                throw new RuntimeException("Encryption failed", e);
            }
        }

        public String decrypt(String encryptedText) {
            try {
                byte[] decodedData = Base64.getDecoder().decode(encryptedText);
                
                // Extract IV and encrypted data
                byte[] iv = new byte[GCM_IV_LENGTH];
                System.arraycopy(decodedData, 0, iv, 0, iv.length);
                
                byte[] encryptedData = new byte[decodedData.length - GCM_IV_LENGTH];
                System.arraycopy(decodedData, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

                byte[] decryptedData = cipher.doFinal(encryptedData);
                return new String(decryptedData);
            } catch (Exception e) {
                throw new RuntimeException("Decryption failed", e);
            }
        }
    }
}