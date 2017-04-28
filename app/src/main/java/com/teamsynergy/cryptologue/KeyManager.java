package com.teamsynergy.cryptologue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Pair;

import com.parse.ParseException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
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
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Sean on 4/21/2017.
 */

public class KeyManager {
    private static final String RSA_MODE =  "RSA/ECB/PKCS1Padding";
    private static final String AES_MODE = "AES/ECB/PKCS7Padding";

    private KeyPair  mKeyPair  = null;
    private KeyStore mKeyStore = null;

    private final String mAlias;

    private final Context mContext;

    public KeyManager(Context context, String alias) throws KeyGenerationException {
        mAlias = alias;
        mContext = context;

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

    public byte[] getSymmetricKey(String alias, boolean generate) throws KeyGenerationException {
        SharedPreferences pref = mContext.getSharedPreferences(alias, Context.MODE_PRIVATE);
        String enryptedKeyB64 = pref.getString(mAlias + alias, null);
        if (enryptedKeyB64 == null) {
            if (!generate)
                return null;

            KeyGenerator keyGen = null;
            try {
                keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES);
                Key key = keyGen.generateKey();

                byte[] encKey = key.getEncoded();
                persistSymmetricKey(alias, encKey);

                return encKey;
            } catch (Exception e) {
                throw new KeyGenerationException(e.getMessage(), e.getCause());
            }
        } else {
            try {
                return rsaDecrypt(Base64.decode(enryptedKeyB64, 0));
            } catch (Exception e) {
                throw new KeyGenerationException(e.getMessage(), e.getCause());
            }
        }
    }

    public void persistSymmetricKey(String alias, byte[] key) throws KeyGenerationException {
        try {
            SharedPreferences pref = mContext.getSharedPreferences(alias, Context.MODE_PRIVATE);
            byte[] encryptedKey = rsaEncrypt(key);
            String enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(mAlias + alias, enryptedKeyB64);
            edit.commit();
        } catch (Exception e) {
            throw new KeyGenerationException(e.getMessage(), e.getCause());
        }
    }

    public byte[] symmetricEncrypt(String alias, byte[] encrypt) throws KeyGenerationException {
        byte[] symKey = getSymmetricKey(alias, false);

        if (symKey != null) {
            try {
                Cipher c = Cipher.getInstance(AES_MODE, "BC");
                c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(symKey, KeyProperties.KEY_ALGORITHM_AES));
                return c.doFinal(encrypt);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    public byte[] symmetricDecrypt(String alias, byte[] decrypt) throws KeyGenerationException {
        byte[] symKey = getSymmetricKey(alias, false);

        if (symKey != null) {
            try {
                Cipher c = Cipher.getInstance(AES_MODE, "BC");
                c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(symKey, KeyProperties.KEY_ALGORITHM_AES));
                return c.doFinal(decrypt);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
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

    public byte[] rsaEncrypt(byte[] secret) throws Exception {
        return rsaEncrypt(mKeyPair.getPublic(), secret);
    }

    public byte[] rsaDecrypt(byte[] secret) throws Exception {
        return rsaDecrypt(mKeyPair.getPrivate(), secret);
    }

    public static byte[] rsaEncrypt(PublicKey publicKey, byte[] secret) throws Exception{
        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        inputCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return vals;
    }

    public static byte[]  rsaDecrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
        Cipher output = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        }
        else { // android m and above
            output = Cipher.getInstance(RSA_MODE, "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }
        output.init(Cipher.DECRYPT_MODE, privateKey);
        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte)nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i).byteValue();
        }
        return bytes;
    }

    public static PrivateKey bytesToPrivateKey(byte[] encoded) throws KeyGenerationException {
        try {
            return KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new KeyGenerationException(e.getMessage(), e.getCause());
        }
    }

}
