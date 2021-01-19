/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashAlgorithmNotFoundException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashEncodeException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashProviderNotFoundException;
import com.h2tcoin.takamakachain.saturn.SatUtils;
import com.h2tcoin.takamakachain.saturn.exceptions.SaturnException;
import com.h2tcoin.takamakachain.utils.FileHelper;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.lang3.RandomStringUtils;
import sun.security.rsa.RSAUtil;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class CryptoHelper {

    private static final String WEB_SESSIONS_PASSWORD_KEYSTORE_ID = "WebSessionsPassword";
    private static final String KEY_STORE_PASSWORD_ID_FRAGMENT = "KeyStorePassword";

    public static String getWEB_SESSIONS_PASSWORD_KEYSTORE_ID() {
        return WEB_SESSIONS_PASSWORD_KEYSTORE_ID;
    }

    public static final String getSaltString() {
        return RandomStringUtils.randomAlphanumeric(30, 30);
    }

    public static final IvParameterSpec generateNewIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static final SecretKey getNewAesSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey sk = keyGenerator.generateKey();
        return sk;
    }

    public static final String ivToHex(IvParameterSpec iv) {
        return TkmSignUtils.fromByteArrayToHexString(iv.getIV());
    }

    public static final IvParameterSpec hexToIv(String hex) {
        return new IvParameterSpec(TkmSignUtils.fromHexToByteArray(hex));
    }

    public static final KeyStore getKeyStoreOrNew(Path keystore) throws KeyStoreException, IOException, HashEncodeException, HashAlgorithmNotFoundException, HashProviderNotFoundException, NoSuchAlgorithmException, CertificateException {
        //check if exists
        FileInputStream fis;
        final char[] webSessionPassword = ProjectHelper.getPassword(WEB_SESSIONS_PASSWORD_KEYSTORE_ID).toCharArray();
        final char[] keyStorePassword = ProjectHelper.getPassword(KEY_STORE_PASSWORD_ID_FRAGMENT).toCharArray();
        if (!FileHelper.fileExists(keystore)) {
            fis = null; //new keystore
        } else {
            fis = new FileInputStream(keystore.toString());//open keystore
        }
        KeyStore ks = KeyStore.getInstance("pkcs12");
        ks.load(fis, keyStorePassword);
        if (ks.size() <= 0) {
            //load new key
            SecretKeyEntry se = new KeyStore.SecretKeyEntry(getNewAesSecretKey());
            ProtectionParameter pp = new KeyStore.PasswordProtection(webSessionPassword);
            ks.setEntry(WEB_SESSIONS_PASSWORD_KEYSTORE_ID, se, pp);
            FileOutputStream fos = new FileOutputStream(keystore.toString());
            ks.store(fos, keyStorePassword);
        }
        return ks;
    }

    public static final SecretKey getWebSessionSecret(KeyStore ks) throws IOException, HashEncodeException, HashAlgorithmNotFoundException, HashProviderNotFoundException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Key key = ks.getKey(WEB_SESSIONS_PASSWORD_KEYSTORE_ID, ProjectHelper.getPassword(WEB_SESSIONS_PASSWORD_KEYSTORE_ID).toCharArray());
        if (key instanceof SecretKey) {
            return (SecretKey) key;
        }
        throw new UnrecoverableKeyException("The type of key saved in the alias does not match a secret key aes");
    }

    public static final String encryptPasswordHEX(String plainText, IvParameterSpec iv, SecretKey sk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sk, iv);
        byte[] encBytes = cipher.doFinal(plainText.getBytes());
        return TkmSignUtils.fromByteArrayToHexString(encBytes);
    }

    public static final String decryptPasswordHEX(String hexCipher, IvParameterSpec iv, SecretKey sk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] byteCipher = TkmSignUtils.fromHexToByteArray(hexCipher);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sk, iv);
        byte[] plainTextBytes = cipher.doFinal(byteCipher);
        return new String(plainTextBytes);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, IOException, HashEncodeException, HashAlgorithmNotFoundException, HashProviderNotFoundException, CertificateException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        //KeyStore keyStore = getKeyStoreOrNew(getNewAesSecretKey(), Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "test.jks"));
        KeyStore keyStore = getKeyStoreOrNew(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "test.pkcs12"));
        System.out.println("kssize " + keyStore.size());//keyStore.size()
        System.out.println("kstype " + keyStore.getType());//keyStore.size()
        SecretKey webSessionPassword = getWebSessionSecret(keyStore);
        System.out.println("password: " + Arrays.toString(webSessionPassword.getEncoded()));
        IvParameterSpec ivParameterSpec = ProjectHelper.getIVParameterSpec("trollo");
        String encryptPasswordHEX = encryptPasswordHEX("trollo", ivParameterSpec, webSessionPassword);
        System.out.println("ENC PASS: " + encryptPasswordHEX);
        String decryptPasswordHEX = decryptPasswordHEX(encryptPasswordHEX, ivParameterSpec, webSessionPassword);
        System.out.println("DEC PASS: " + decryptPasswordHEX);
    }

}
