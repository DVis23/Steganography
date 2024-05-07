package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import static ru.dvis.BitsUtils.*;
import static ru.dvis.PixelUtils.*;

public class KochZhao {
    private static final int BLOCK_SIZE = 8;
    private static final double EPS = 2;

    // Встраивание сообщения в изображение
    public static BufferedImage encodeImage(BufferedImage img, String msg, String key) {
        // Создание сида на основе ключа для ПСПЧ
        int seed = key.hashCode();
        // Генератор ПСПЧ
        Random random = new Random(seed);
        // Перевод строки в байтовый массив
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        // Добавление ETB-символа
        msgByte = ArrayUtils.add(msgByte, (byte)23);

        int index = 0;
        // Проход по всем блокам
        for (int i = 0; i < img.getWidth() - img.getWidth() % BLOCK_SIZE; i+=BLOCK_SIZE) {
            for (int j = 0; j < img.getHeight() - img.getHeight() % BLOCK_SIZE; j += BLOCK_SIZE) {
                if (index < msgByte.length * BLOCK_SIZE) {
                    int coef1 = random.nextInt(BLOCK_SIZE);
                    int coef2 = random.nextInt(BLOCK_SIZE);

                    double[][][] block = new double[BLOCK_SIZE][BLOCK_SIZE][3];
                    for (int k = 0; k < BLOCK_SIZE; k++) {
                        for (int l = 0; l < BLOCK_SIZE; l++) {
                            // Достаем значения пикселя RGB
                            int pixel = img.getRGB(i+k, j+l);
                            // Массив значений каждого цвета
                            int [] rgb = new int[] {pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF};
                            block[k][l] = RGBToYCbCr(rgb);
                        }
                    }
                    double[][][] blockDct = dct(block);

                    if (getBit(msgByte, index)) {
                        if (Math.abs(blockDct[coef1][coef2][2]) - Math.abs(blockDct[coef2][coef1][2]) >= -EPS) {
                            if (blockDct[coef1][coef2][2] >= 0) {
                                blockDct[coef2][coef1][2] = blockDct[coef1][coef2][2] + EPS;
                            } else {
                                blockDct[coef2][coef1][2] = blockDct[coef1][coef2][2] - EPS;
                            }
                        }
                    } else {
                        if (Math.abs(blockDct[coef1][coef2][2]) - Math.abs(blockDct[coef2][coef1][2]) <= EPS) {
                            if (blockDct[coef2][coef1][2] >= 0) {
                                blockDct[coef1][coef2][2] = blockDct[coef2][coef1][2] + EPS;
                            } else {
                                blockDct[coef1][coef2][2] = blockDct[coef2][coef1][2] - EPS;
                            }
                        }
                    }

                    block = idct(blockDct);

                    for (int k = 0; k < BLOCK_SIZE; k++) {
                        for (int l = 0; l < BLOCK_SIZE; l++) {
                            int [] rgb = YCbCrToRGB(block[k][l]);
                            Color newColor = new Color(rgb[0], rgb[1], rgb[2]);
                            img.setRGB(i+k, j+l, newColor.getRGB());
                        }
                    }
                    index++;
                }
            }
        }
        return img;
    }

    // извлечение сообщения из изображения
    public static String decodeImage(BufferedImage img, String key) {
        // Создание сида на основе ключа для ПСПЧ
        int seed = key.hashCode();
        // Генератор ПСПЧ
        Random random = new Random(seed);
        // Создаем список бит
        ArrayList<Boolean> bits = new ArrayList<Boolean>();

        // Проход по всем блокам
        for (int i = 0; i < img.getWidth() - img.getWidth() % BLOCK_SIZE; i+=BLOCK_SIZE) {
            for (int j = 0; j < img.getHeight() - img.getHeight() % BLOCK_SIZE; j += BLOCK_SIZE) {
                if (!checkETB(bits)) {
                    int coef1 = random.nextInt(BLOCK_SIZE);
                    int coef2 = random.nextInt(BLOCK_SIZE);

                    double[][][] block = new double[BLOCK_SIZE][BLOCK_SIZE][3];
                    for (int k = 0; k < BLOCK_SIZE; k++) {
                        for (int l = 0; l < BLOCK_SIZE; l++) {
                            // Достаем значения пикселя RGB
                            int pixel = img.getRGB(i + k, j + l);
                            // Массив значений каждого цвета
                            int[] rgb = new int[]{pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF};
                            block[k][l] = RGBToYCbCr(rgb);
                        }
                    }

                    double[][][] blockDct = dct(block);
                    if (Math.abs(blockDct[coef1][coef2][2]) > Math.abs(blockDct[coef2][coef1][2])) bits.add(false);
                    else bits.add(true);
                }
            }
        }
        return new String(bitsToBytes(bits));
    }


    public static double[][][] dct(double[][][] block) {
        double[][][] dctResult = new double[BLOCK_SIZE][BLOCK_SIZE][3];

        for (int c = 0; c < 3; c++) {
            for (int u = 0; u < BLOCK_SIZE; u++) {
                for (int v = 0; v < BLOCK_SIZE; v++) {
                    double sum = 0.0;
                    for (int x = 0; x < BLOCK_SIZE; x++) {
                        for (int y = 0; y < BLOCK_SIZE; y++) {
                            sum += block[x][y][c] * Math.cos((2 * x + 1) * u * Math.PI / 16) *
                                    Math.cos((2 * y + 1) * v * Math.PI / 16);
                        }
                    }
                    double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                    double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                    dctResult[u][v][c] = 0.25 * cu * cv * sum;
                }
            }
        }

        return dctResult;
    }

    public static double[][][] idct(double[][][] block) {
        double[][][] idctResult = new double[BLOCK_SIZE][BLOCK_SIZE][3];

        for (int c = 0; c < 3; c++) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                for (int y = 0; y < BLOCK_SIZE; y++) {
                    double sum = 0.0;
                    for (int u = 0; u < BLOCK_SIZE; u++) {
                        for (int v = 0; v < BLOCK_SIZE; v++) {
                            double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                            double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                            sum += cu * cv * block[u][v][c] *
                                    Math.cos(((2 * x + 1) * u * Math.PI) / 16) *
                                    Math.cos(((2 * y + 1) * v * Math.PI) / 16);
                        }
                    }
                    idctResult[x][y][c] = 0.25 * sum;
                }
            }
        }

        return idctResult;
    }
}
