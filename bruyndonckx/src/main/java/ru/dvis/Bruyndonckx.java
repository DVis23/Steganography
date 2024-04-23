package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static ru.dvis.ArrayUtils.*;
import static ru.dvis.BitsUtils.*;
import static ru.dvis.PixelUtils.*;

public class Bruyndonckx {

    public static BufferedImage encodeImage(BufferedImage img, String msg, String key, double alpha, int delta) {
        // Перевод строк в байтовые массивы
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        // Создание сида на основе ключа для ПСПЧ
        int seed = key.hashCode();
        // Генератор ПСПЧ
        Random random = new Random(seed);
        // Добавление ETB-символа
        msgByte = ArrayUtils.add(msgByte, (byte)23);

        int index = 0;
        // Проход по всем блокам
        for (int i = 0; i < img.getWidth() - img.getWidth() % 8; i+=8) {
            for (int j = 0; j < img.getHeight() - img.getHeight() % 8; j+=8) {
                if (index < msgByte.length * 8) {
                    int [] pixelsBrightness = new int[64];
                    boolean [] masks = new boolean[64];
                    // Проход по пикселям блока
                    int pixelIndex = 0;
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            pixelsBrightness[pixelIndex] = getBrightness(img, i+k, j+l);
                            pixelIndex++;
                        }
                    }
                    // Сортируем значение яркостей
                    Arrays.sort(pixelsBrightness);
                    // Поиск точки разделение категорий пикселей
                    int middle = findMaximumSlopePoint(pixelsBrightness);
                    if (calculateSlope(pixelsBrightness, middle) <= alpha) middle = pixelsBrightness.length / 2 - 1;

                    // Заполняем массив масок
                    for (int k = 0; k < 64; k++) {
                        // Генерируем бинарное число
                        int r = random.nextInt(2);
                        // Вносим бинарное число в массив масок
                        masks[k] = r == 1;
                    }

                    // Средние значения групп пикселей
                    double l1A = 0, l2A = 0, l1B = 0, l2B = 0;
                    // Количество пискселей в группе
                    int n1A = 0, n2A = 0, n1B = 0, n2B = 0;
                    // Проход по пикселям блока
                    pixelIndex = 0;
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            // Вычисляем сумму значений яркости для кажой из четырех групп пикселей и
                            // количество пикселей в каждой группе
                            int pixelBrightness = getBrightness(img, i+k, j+l);
                            if (pixelBrightness <= pixelsBrightness[middle]) {
                                if (masks[pixelIndex]) {
                                    l2A+=pixelBrightness;
                                    n2A++;
                                } else {
                                    l2B+=pixelBrightness;
                                    n2B++;
                                }
                            } else {
                                if (masks[pixelIndex]) {
                                    l1A+=pixelBrightness;
                                    n1A++;
                                } else {
                                    l1B+=pixelBrightness;
                                    n1B++;
                                }
                            }
                            pixelIndex++;
                        }
                    }
                    // Нахождение средних значений для каждой группы пикселей
                    l1A /= n1A;
                    l1B /= n1B;
                    l2A /= n2A;
                    l2B /= n2B;

                    double l1 = (l1A * n1A + l1B * n1B) / (n1A + n1B);
                    double l2 = (l2A * n2A + l2B * n2B) / (n2A + n2B);

                    // Вычисляем новое среднее значение
                    l1A = (n1A + n1B) * l1;
                    l1B = (n1A + n1B) * l1;
                    l2A = (n2A + n2B) * l2;
                    l2B = (n2A + n2B) * l2;
                    if (getBit(msgByte, index)) {
                        l1A += n1B * delta;
                        l1B -= n1B * delta;
                        l2A += n2B * delta;
                        l2B -= n2B * delta;
                    } else {
                        l1A -= n1B * delta;
                        l1B += n1B * delta;
                        l2A -= n2B * delta;
                        l2B += n2B * delta;
                    }
                    l1A /= (n1A + n1B);
                    l1B /= (n1A + n1B);
                    l2A /= (n2A + n2B);
                    l2B /= (n2A + n2B);

                    // Проход по пикселям блока
                    pixelIndex = 0;
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            int pixelBrightness = getBrightness(img, i+k, j+l);
                            if (pixelBrightness <= pixelsBrightness[middle]) {
                                if (masks[pixelIndex]) {
                                    // l2A
                                    setBrightness(img, i+k, j+l, (int) l2A);
                                } else {
                                    // l2B
                                    setBrightness(img, i+k, j+l, (int) l2B);
                                }
                            } else {
                                if (masks[pixelIndex]) {
                                    // l1A
                                    setBrightness(img, i+k, j+l, (int) l1A);
                                } else {
                                    // l1B
                                    setBrightness(img, i+k, j+l, (int) l1B);
                                }
                            }
                            pixelIndex++;
                        }
                    }
                    index++;
                } else {
                    return img;
                }
            }
        }
        return img;
    }

    public static String decodeImage(BufferedImage img, String key, double alpha, int delta) {
        // Создание сида на основе ключа для ПСПЧ
        int seed = key.hashCode();
        // Генератор ПСПЧ
        Random random = new Random(seed);
        // Создаем список бит
        ArrayList<Boolean> bits = new ArrayList<Boolean>();

        // Проход по всем блокам
        for (int i = 0; i < img.getWidth() - img.getWidth() % 8; i+=8) {
            for (int j = 0; j < img.getHeight() - img.getHeight() % 8; j+=8) {
                if (!checkETB(bits)) {
                    int [] pixelsBrightness = new int[64];
                    boolean [] masks = new boolean[64];
                    // Проход по пикселям блока
                    int pixelIndex = 0;
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            pixelsBrightness[pixelIndex] = getBrightness(img, i+k, j+l);
                            pixelIndex++;
                        }
                    }
                    // Сортируем значение яркостей
                    Arrays.sort(pixelsBrightness);
                    // Поиск точки разделение категорий пикселей
                    int middle = findMaximumSlopePoint(pixelsBrightness);
                    if (calculateSlope(pixelsBrightness, middle) <= alpha) middle = pixelsBrightness.length / 2 - 1;

                    // Заполняем массив масок
                    for (int k = 0; k < 64; k++) {
                        // Генерируем бинарное число
                        int r = random.nextInt(2);
                        // Вносим бинарное число в массив масок
                        masks[k] = r == 1;
                    }

                    // Средние значения групп пикселей
                    double l1A = 0, l2A = 0, l1B = 0, l2B = 0;
                    // Количество пискселей в группе
                    int n1A = 0, n2A = 0, n1B = 0, n2B = 0;
                    // Проход по пикселям блока
                    pixelIndex = 0;
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            // Вычисляем сумму значений яркости для кажой из четырех групп пикселей и
                            // количество пикселей в каждой группе
                            int pixelBrightness = getBrightness(img, i+k, j+l);
                            if (pixelBrightness <= pixelsBrightness[middle]) {
                                if (masks[pixelIndex]) {
                                    l2A+=pixelBrightness;
                                    n2A++;
                                } else {
                                    l2B+=pixelBrightness;
                                    n2B++;
                                }
                            } else {
                                if (masks[pixelIndex]) {
                                    l1A+=pixelBrightness;
                                    n1A++;
                                } else {
                                    l1B+=pixelBrightness;
                                    n1B++;
                                }
                            }
                            pixelIndex++;
                        }
                    }
                    // Нахождение средних значений для каждой группы пикселей
                    l1A /= n1A;
                    l1B /= n1B;
                    l2A /= n2A;
                    l2B /= n2B;

                    if ((l1A - l1B) < 0 && (l2A - l2B) < 0) {
                        bits.add(false);
                    } else if ((l1A - l1B) > 0 && (l2A - l2B) > 0){
                        bits.add(true);
                    }
                } else {
                    return new String(bitsToBytes(bits));
                }
            }
        }
        return new String(bitsToBytes(bits));
    }

}


