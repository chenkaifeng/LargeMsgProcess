package com.kfc.test.large.utils.sign;

import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.util.Base64;

/**
 * @author: Chenkf
 * @create: 2024/09/10
 **/
public class RSAUtil {

    /**
     * 签名算法名称
     */
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 244;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * 文件加核签时的buffer大小
     */
    public static final int FILE_BUFFER_SIZE = 4 * 1024;

    /**
     * 用私钥对信息生成数字签名
     *
     * @param privateKey 私钥
     * @param data       数据
     * @return 签名结果
     * @throws Exception
     */
    public static byte[] sign(PrivateKey privateKey, byte[] data)
            throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 校验数字签名
     *
     * @param data      数据
     * @param publicKey 公钥
     * @param sign      签名内容
     * @return
     * @throws Exception
     */
    public static boolean verify(PublicKey publicKey, byte[] data, byte[] sign)
            throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }


    /**
     * 私钥解密
     *
     * @param privateKey    私钥
     * @param encryptedData 已加密数据
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKey privateKey,
                                             byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher
                        .doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher
                        .doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }


    /**
     * 公钥加密
     *
     * @param publicKey 公钥
     * @param data      源数据
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encrypt(PublicKey publicKey, byte[] data)
            throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    public static void encryptFile(String inputFile, String outputFile, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[MAX_ENCRYPT_BLOCK];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] cipherBlock = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(cipherBlock);
            }
        }
    }

    public static void decryptFile(String inputFile, String outputFile, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[MAX_DECRYPT_BLOCK];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                //byte[] cipherBlock = Base64.getDecoder().decode(buffer, 0, bytesRead);
                byte[] plainBlock = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(plainBlock);
            }
        }
    }

    public static byte[] signByFile(String filePath, PrivateKey privateKey) throws Exception {
        Signature s = Signature.getInstance(SIGNATURE_ALGORITHM);
        s.initSign(privateKey);
        try (InputStream in = new FileInputStream(filePath)) {
            int n;
            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            while (-1 != (n = in.read(buffer))) {
                s.update(buffer, 0, n);
            }
        }
        return s.sign();
    }


    public static boolean verifyByFile(String filePath, PublicKey key, byte[] signature) throws Exception {
        Signature s = Signature.getInstance(SIGNATURE_ALGORITHM);
        s.initVerify(key);

        try (InputStream in = new FileInputStream(filePath)) {
            int n;
            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            while (-1 != (n = in.read(buffer))) {
                s.update(buffer, 0, n);
            }
        }
        return s.verify(signature);
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // 密钥长度，2048位是常用的长度
        KeyPair keyPair =  keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("Public Key: " + publicKey);
        System.out.println("Private Key: " + privateKey);


        String sourceFilePath = "F:\\my\\github-project\\LargeMsgProcess\\test\\aaa.txt";
        File sourceFile = new File(sourceFilePath);
        byte[] sourceByte = FileUtils.readFileToByteArray(sourceFile);

    }
}
