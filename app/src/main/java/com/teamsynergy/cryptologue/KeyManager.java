package com.teamsynergy.cryptologue;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Pair;

import com.parse.ParseException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Sean on 4/21/2017.
 */

public class KeyManager {
    private KeyPair  mKeyPair  = null;
    private KeyStore mKeyStore = null;

    private final String mAlias;

    public KeyManager(Context context, String alias) throws KeyGenerationException {
        mAlias = alias;

        //Generate an RSA key pair
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            // Generate the RSA key pairs
            if (!mKeyStore.containsAlias(alias)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                kpg.initialize(spec);

                mKeyPair = kpg.generateKeyPair();
            } else {
                KeyStore.PrivateKeyEntry entry = ((KeyStore.PrivateKeyEntry)mKeyStore.getEntry(alias, null));
                mKeyPair = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
            }
        } catch(Exception e) {
            throw new KeyGenerationException(e.getMessage(), e.getCause());
        }
    }

    public PublicKey getPublicKey() {
        return mKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return mKeyPair.getPrivate();
    }

    public void destroyKey() throws KeyGenerationException {
        try {
            mKeyStore.deleteEntry(mAlias);
        } catch (KeyStoreException e) {
            throw new KeyGenerationException(e.getMessage(), e.getCause());
        }
    }

    public static class KeyGenerationException extends Exception {
        public KeyGenerationException (String msg, Throwable throwable) {
            super(msg, throwable);
        }
    }

    public static PublicKey bytesToPublicKey(byte[] encoded) throws KeyGenerationException {
        try {
            return KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA).generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new KeyGenerationException(e.getMessage(), e.getCause());
        }
    }

    public static PrivateKey bytesToPrivateKey(byte[] encoded) throws KeyGenerationException {
        try {
            return KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new KeyGenerationException(e.getMessage(), e.getCause());
        }
    }

}
