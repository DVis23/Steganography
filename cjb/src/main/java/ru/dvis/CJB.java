package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CJB {

    // Встраивание сообщения в изображение
    public static BufferedImage encodeImage(BufferedImage img, String msg, double h, int o) {
        // Перевод строки в байтовый массив
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        // Добавление ETB-символа
        msgByte = ArrayUtils.add(msgByte, (byte)23);

        int index = 0;
        // Проход по всем пикселям
        for (int i = o; i < img.getWidth() - o; i++) {
            for (int j = o; j < img.getHeight() - o; j++) {
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
                    if (getBit(msgByte, index)) blue +=  (int) (h * brightness);
                    else blue -=  (int) (h * brightness);

                    // Создание нового RGB значения
                    pixel = (pixel & 0xFF00FFFF) | (blue << 16);
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
    public static String decodeImage(BufferedImage img, int o) {
        // Создаем список бит
        ArrayList<Boolean> bits = new ArrayList<Boolean>();
        for (int i = o; i < img.getWidth() - o; i++) {
            for (int j = o; j < img.getHeight() - o; j++) {
                if (!checkETB(bits)) {
                    // Если мы еще не дошли до ETB-символа

                    // Получение значения синего
                    int blue = getBlue(img, i, j);

                    // Получение среденего значения синего по области o
                    int middleBlue = 0;
                    for (int k = 1; k <= o; k++) middleBlue += getBlue(img, i, j - k) + getBlue(img, i - k, j)
                            + getBlue(img, i, j + k) + getBlue(img, i + k, j);
                    middleBlue /= 4 * o;

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

    // извлечение значения синего
    private static int getBlue(BufferedImage img, int i, int j) {
        return img.getRGB(i, j) & 0xff;
    }

    // извлечение значения яркости
    private static int getBrightness(BufferedImage img, int i, int j) {
        // Достаем значения пикселя RGB
        int pixel = img.getRGB(i, j);
        // Массив значений каждого цвета
        int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
        return (int) (0.3 * rgb[0] + 0.59 * rgb[1] + 0.11 * rgb[2]);
    }
    // извлечение бита из массива байт по индексу
    private static boolean getBit(byte[] bytes, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitOffset = bitIndex % 8;

        if (byteIndex >= bytes.length) return false;

        byte targetByte = bytes[byteIndex];
        return ((targetByte >> (7 - bitOffset)) & 1) == 1;
    }

    // Проверка наличия ETB-симола
    private static boolean checkETB(ArrayList<Boolean> bits) {
        if (bits.size() % 8 != 0 || bits.size() == 0) return false;
            // ETB-символ в bin = 00010111
        else return  bits.get(bits.size() - 1) && bits.get(bits.size() - 2) && bits.get(bits.size() - 3) &&
                !bits.get(bits.size() - 4) && bits.get(bits.size() - 5) &&
                !bits.get(bits.size() - 6) && !bits.get(bits.size() - 7) && !bits.get(bits.size() - 8);
    }

    // Конвертация списка бит в байты
    private static byte[] bitsToBytes(ArrayList<Boolean> bits) {
        int byteCount = (int) Math.ceil(bits.size() / 8.0);
        byte[] bytes = new byte[byteCount];

        for (int i = 0; i < byteCount; i++) {
            for (int j = 0; j < 8; j++) {
                if (i*8 + j < bits.size()) {
                    if (bits.get(i*8 + j)) {
                        bytes[i] |= (byte) (1 << (7 - j));
                    }
                } else {
                    break;
                }
            }
        }
        return bytes;
    }
}
