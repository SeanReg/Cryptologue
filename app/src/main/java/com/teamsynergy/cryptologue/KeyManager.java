package com.teamsynergy.cryptologue;

import android.util.Pair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by Sean on 4/21/2017.
 */

public class KeyManager {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int    KEY_SIZE = 2048;

    public static final int KEY_TYPE_SYMMETRIC  = 0;
    public static final int KEY_TYPE_ASYMMETRIC = 1;

    private KeyPairGenerator mKeyPairGen  = null;
    private KeyGenerator     mKeyGen  = null;
    private KeyPair          mKeyPair = null;

    SecretKey mSymmetricKey = null;

    public KeyManager(int keyType) {
        generateKey(keyType);
    }

    public KeyManager(String alias) {

    }

    private void generateKey(int keyType)  {
        switch (keyType) {
            case KEY_TYPE_ASYMMETRIC:
                try {
                    mKeyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
                    mKeyPairGen.initialize(KEY_SIZE);
                    mKeyPair = mKeyPairGen.genKeyPair();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            case KEY_TYPE_SYMMETRIC:
                try {
                    mKeyGen = KeyGenerator.getInstance("AES");
                    mKeyGen.init(128);
                    mSymmetricKey = mKeyGen.generateKey();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public Pair<Byte[], Byte[]> getKeys() {
        if (mKeyPair != null) {
            return new Pair<>(byteArrToByteArr(mKeyPair.getPublic().getEncoded()), byteArrToByteArr(mKeyPair.getPrivate().getEncoded()));
        } else {
            return new Pair<>(byteArrToByteArr(mSymmetricKey.getEncoded()), null);
        }
    }

    public void storeKeys(String alias) {

    }

    private static Byte[] byteArrToByteArr(byte[] byteArr) {
        Byte[] bObjs = new Byte[byteArr.length];
        for (int i = 0; i < byteArr.length; ++i) {
            bObjs[i] = byteArr[i];
        }

        return bObjs;
    }
}
