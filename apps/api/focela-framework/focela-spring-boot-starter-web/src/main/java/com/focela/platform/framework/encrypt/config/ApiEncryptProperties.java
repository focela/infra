package com.focela.platform.framework.encrypt.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * HTTP API encryption/decryption configuration.
 */
@ConfigurationProperties(prefix = "focela.api-encrypt")
@Validated
@Data
public class ApiEncryptProperties {

    /**
     * Whether enabled.
     */
    @NotNull(message = "enabled flag must not be blank")
    private Boolean enable;

    /**
     * Request header (response header) name.
     *
     * 1. If this request header is non-empty, the request parameters are encrypted by the "frontend" and need to be decrypted by the "backend".
     * 2. If this response header is non-empty, the response is encrypted by the "backend" and needs to be decrypted by the "frontend".
     */
    @NotEmpty(message = "request header (response header) name must not be blank")
    private String header = "X-Api-Encrypt";

    /**
     * Symmetric encryption algorithm used for request/response encryption and decryption.
     *
     * Currently supported:
     * [Symmetric encryption]:
     *      1. {@link cn.hutool.crypto.symmetric.SymmetricAlgorithm#AES}
     *      2. {@link cn.hutool.crypto.symmetric.SM4#ALGORITHM_NAME} (requires custom development; low cost)
     * [Asymmetric encryption]:
     *      1. {@link cn.hutool.crypto.asymmetric.AsymmetricAlgorithm#RSA}
     *      2. {@link cn.hutool.crypto.asymmetric.SM2} (requires custom development; low cost)
     *
     * @see <a href="https://help.aliyun.com/zh/ssl-certificate/what-are-a-public-key-and-a-private-key">What are a public key and a private key?</a>
     */
    @NotEmpty(message = "symmetric encryption algorithm must not be blank")
    private String algorithm;

    /**
     * Decryption key for requests.
     *
     * Note:
     * 1. For [symmetric encryption], on the "backend" side it is the "secret key"; correspondingly on the "frontend" side it is also the "secret key".
     * 2. For [asymmetric encryption], on the "backend" side it is the "private key"; correspondingly on the "frontend" side it is the "public key". (Important!)
     */
    @NotEmpty(message = "request decrypt secret must not be blank")
    private String requestKey;

    /**
     * Encryption key for responses.
     *
     * Note:
     * 1. For [symmetric encryption], on the "backend" side it is the "secret key"; correspondingly on the "frontend" side it is also the "secret key".
     * 2. For [asymmetric encryption], on the "backend" side it is the "public key"; correspondingly on the "frontend" side it is the "private key". (Important!)
     */
    @NotEmpty(message = "response encrypt secret must not be blank")
    private String responseKey;

}
