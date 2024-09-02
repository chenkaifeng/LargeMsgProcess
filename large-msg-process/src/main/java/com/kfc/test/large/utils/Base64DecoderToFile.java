package com.kfc.test.large.utils;

import java.io.*;
import java.util.Base64;

/**
 * 解码base64
 *
 * @author: Chenkf
 * @create: 2024/09/02
 **/
public class Base64DecoderToFile {

    public static void decodeBase64ToFile(String inputFile, String outputFile, int bufferSize) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            Base64.Decoder decoder = Base64.getDecoder();
            char[] buffer = new char[bufferSize];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                // 将字符数组转换为字符串，然后解码
                byte[] decodedBytes = decoder.decode(new String(buffer, 0, charsRead));
                fos.write(decodedBytes);
            }
        }
    }

    /*public static void encodeFileToBase64(String inputFile, String outputFile, int bufferSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                // 编码并写入文件
                writer.write(encoder.encodeToString(buffer, 0, bytesRead));
                writer.newLine(); // 添加换行符以便阅读
            }
        }
    }*/


    public static void main(String[] args) {
        String base64Data = "QWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYTMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMw==";
        String outputFile = "F:\\my\\github-project\\LargeMsgProcess\\test" + System.currentTimeMillis() + ".txt";
        int chunkSize = 1024 * 3;

        try {
            decodeBase64ToFile(base64Data, outputFile, chunkSize);
            System.out.println("解码完成，文件已保存至：" + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
