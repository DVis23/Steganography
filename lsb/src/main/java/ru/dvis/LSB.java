package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
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
                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
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

    public static void setRGB(BufferedImage img, int i, int j, int[] rgb) {
        img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
    }

    // изменение значение бита в байте на заданное по индексу
    private static int replaceBit(int num, boolean bit, int bitIndex) {
        int mask = 1 << bitIndex;
        if (bit) num |= mask;
        else num &= ~mask;
        return num;
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
        if (bits.size() % 8 != 0 || bits.isEmpty()) return false;
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
