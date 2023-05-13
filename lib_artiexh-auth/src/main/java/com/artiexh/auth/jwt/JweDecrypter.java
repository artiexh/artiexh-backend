package com.artiexh.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;

import java.text.ParseException;

public interface JweDecrypter {

    JWEDecrypter getDecrypter();

    EncryptedJWT decrypt(String jweString) throws JOSEException, ParseException;

}
