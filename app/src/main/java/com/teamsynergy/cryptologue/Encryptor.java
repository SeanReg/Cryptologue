package com.teamsynergy.cryptologue;

import android.util.Base64;

import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Sean on 4/22/2017.
 */

public class Encryptor {
    private final Byte[] mEncryptionKey;

    public Encryptor(Byte[] key) {
        mEncryptionKey = key;
    }
//
//    public String encrypt(String text) {
//
//    }
}
