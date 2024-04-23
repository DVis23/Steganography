package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.dvis.BitsUtils.*;
import static ru.dvis.PixelUtils.*;

public class LSB {

    // Встраивание сообщения в изображение
    public static BufferedImage encodeImage(BufferedImage img, String msg) {
        // Перевод строки в байтовый массив
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        // Добавление ETB-символа
        msgByte = ArrayUtils.add(msgByte, (byte)23);

        int index = 0;
        // Проход по всем пикселям
        for (int i = img.getWidth() - 1; i >= 0; i--) {
            for (int j = img.getHeight() - 1; j >= 0; j--) {
                // Достаем значения пикселя RGB
                int pixel = img.getRGB(i, j);
                // Массив значений каждого цвета
                int [] rgb = new int[] {pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF};
                for (int k = 0; k < 3; k++) {
                    if (index < msgByte.length * 8) {
                        // Если сообщение еще полностью не встроено
                        // Встраевываем один бит из сообщения в индекс 0 байта каждого цвета пикселя
                        rgb[k] = replaceBit(rgb[k], getBit(msgByte, index), 0);
                        index++;
                    } else {
                        setRGB(img, i, j, rgb);
                        return img;
                    }
                }
                setRGB(img, i, j, rgb);
            }
        }
        return img;
    }

    // извлечение сообщения из изображения
    public static String decodeImage(BufferedImage img) {
        // Создаем список бит
        ArrayList<Boolean> bits = new ArrayList<Boolean>();
        for (int i = img.getWidth() - 1; i >= 0; i--) {
            for (int j = img.getHeight() - 1; j >= 0; j--) {
                // Достаем значения пикселя RGB
                int pixel = img.getRGB(i, j);
                // Массив значений каждого цвета
                int [] rgb = new int[] {pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF};
                for (int k = 0; k < 3; k++) {
                    if (!checkETB(bits)) {
                        // Если мы еще не дошли до ETB-символа
                        // Достаем бит из индекса 0 байта кажого цвета пикселя
                        rgb[k] = (rgb[k] & 1);
                        // Записываем значение бита в список
                        if (rgb[k] == 1) bits.add(true);
                        else bits.add(false);
                    } else {
                        return new String(bitsToBytes(bits));
                    }
                }
            }
        }
        return new String(bitsToBytes(bits));
    }
}
