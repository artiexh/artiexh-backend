package com.artiexh.auth.jwt;

import com.google.crypto.tink.subtle.Hkdf;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;

@Component
public class DirectAESJweDecrypter implements JweDecrypter {

    private final JWEDecrypter decrypter;

    @Autowired
    public DirectAESJweDecrypter(JwtSecretKeyProperties properties) throws KeyLengthException, GeneralSecurityException {
        var secret = Hkdf.computeHkdf("HMACSHA256", getBytes(properties.getKey()), getBytes(properties.getSalt()), getBytes(properties.getInfo()), 32);
        var secretKey = new SecretKeySpec(secret, "AES");
        decrypter = new DirectDecrypter(secretKey);
    }

    private byte[] getBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public JWEDecrypter getDecrypter() {
        return decrypter;
    }

    @Override
    public EncryptedJWT decrypt(String jweString) throws JOSEException, ParseException {
        var jwe = EncryptedJWT.parse(jweString);
        jwe.decrypt(decrypter);
        return jwe;
    }
}
