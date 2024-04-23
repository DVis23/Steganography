package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.dvis.BitsUtils.*;
import static ru.dvis.PixelUtils.*;

public class KJB {

    // Встраивание сообщения в изображение
    public static BufferedImage encodeImage(BufferedImage img, String msg, double lambda, int sigma) {
        // Перевод строки в байтовый массив
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        // Добавление ETB-символа
        msgByte = ArrayUtils.add(msgByte, (byte)23);

        int index = 0;
        // Проход по всем пикселям
        for (int i = sigma; i < img.getWidth() - sigma; i++) {
            for (int j = sigma; j < img.getHeight() - sigma; j++) {
                if (index < msgByte.length * 8) {
                    // Если сообщение еще полностью не встроено
                    // Встраевываем один бит из сообщения в индекс 0 байта каждого цвета пикселя

                    // Достаем значения пикселя RGB
                    int pixel = img.getRGB(i, j);
                    // Получение значения синего
                    int blue = getBlue(img, i, j);
                    // Получение яркости пикселя
                    int brightness = getBrightness(img, i, j);

                    // Замена значения пикселя blue
                    if (getBit(msgByte, index)) blue += (int) (lambda * brightness);
                    else blue -= (int) (lambda * brightness);
                    // Контролируем выход значения за область
                    blue = Math.max(0, Math.min(255, blue));
                    // Создание нового RGB значения
                    pixel = (pixel & 0xFFFFFF00) | (blue);
                    // Установка нового значения пикселя
                    img.setRGB(i, j, pixel);
                    index++;
                } else {
                    return img;
                }
            }
        }
        return img;
    }

    // извлечение сообщения из изображения
    public static String decodeImage(BufferedImage img, int sigma) {
        // Создаем список бит
        ArrayList<Boolean> bits = new ArrayList<Boolean>();
        for (int i = sigma; i < img.getWidth() - sigma; i++) {
            for (int j = sigma; j < img.getHeight() - sigma; j++) {
                if (!checkETB(bits)) {
                    // Если мы еще не дошли до ETB-символа

                    // Получение значения синего
                    int blue = getBlue(img, i, j);

                    // Получение среденего значения синего по области sigma
                    int middleBlue = 0;
                    for (int k = 1; k <= sigma; k++) middleBlue += getBlue(img, i, j - k) + getBlue(img, i - k, j)
                            + getBlue(img, i, j + k) + getBlue(img, i + k, j);
                    middleBlue /= 4 * sigma;

                    // Записываем значение бита в список
                    if (blue > middleBlue) bits.add(true);
                    else bits.add(false);
                } else {
                    return new String(bitsToBytes(bits));
                }
            }
        }
        return new String(bitsToBytes(bits));
    }

}
