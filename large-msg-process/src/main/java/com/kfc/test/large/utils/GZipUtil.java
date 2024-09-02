package com.kfc.test.large.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip解压缩工具类
 *
 * @author: Chenkf
 * @create: 2024/09/02
 **/
public class GZipUtil {

    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * 压缩文件到GZIP格式
     *
     * @param sourceFile 要压缩的文件路径
     * @param outputFile 压缩后的文件路径
     * @throws IOException 如果读写文件时发生错误
     */
    public static void compressFile(String sourceFile, String outputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             GZIPOutputStream gzOS = new GZIPOutputStream(new FileOutputStream(outputFile))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzOS.write(buffer, 0, len);
            }
            gzOS.finish();
        }
    }

    /**
     * 将文件压缩成GZIP格式的byte数组
     *
     * @param inputFile 源文件路径
     * @return 压缩后的byte数组
     * @throws IOException 如果压缩过程中发生错误
     */
    public static byte[] compressToFileBytes(String inputFile) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FileInputStream fis = new FileInputStream(inputFile);
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }
            gzos.finish(); // 确保所有数据都被写入
            return baos.toByteArray();
        }
    }

    /**
     * 从GZIP格式解压文件
     *
     * @param sourceFile 压缩的文件路径
     * @param outputFile 解压后的文件路径
     * @throws IOException 如果读写文件时发生错误
     */
    public static void decompressFile(String sourceFile, String outputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             GZIPInputStream gzIS = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gzIS.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }


    /**
     * 将GZIP压缩的byte数组解压到文件中
     *
     * @param compressedData 压缩的byte数组
     * @param outputFile     目标文件路径
     * @throws IOException 如果解压过程中发生错误
     */
    public static void decompressToFile(byte[] compressedData, String outputFile) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzis = new GZIPInputStream(bais);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }



    public static void main(String[] args) {
        // 要压缩或解压的文件路径
        String sourceFile = "F:\\my\\github-project\\LargeMsgProcess\\test\\aaa.txt";
        // 压缩后的文件路径
        String compressedFile = sourceFile + ".gz";
        // 解压后的文件路径
        String decompressedFile = compressedFile + ".decompress";

        try {
            // 压缩文件
            GZipUtil.compressFile(sourceFile, compressedFile);
            System.out.println("文件压缩完成");

            // 解压文件
            GZipUtil.decompressFile(compressedFile, decompressedFile);
            System.out.println("文件解压完成");


            // 压缩文件到byte数组
            byte[] compressedData = GZipUtil.compressToFileBytes(sourceFile);
            System.out.println("压缩后字节数大小：" + compressedData.length);
            // 将byte数组解压到文件
            GZipUtil.decompressToFile(compressedData, decompressedFile + ".byte");
            System.out.println("压缩和解压完成");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
