package ru.dvis;

import org.apache.commons.lang3.ArrayUtils;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class LSB {

    // Внедрение сообщения
    public static BufferedImage encodeImage(BufferedImage img, byte[] msg) {
        // Получение ширины и высоты изображение
        int width = img.getWidth();
        int height = img.getHeight();

        // Добавление в сообщение символа окончания блока
        msg = ArrayUtils.add(msg, (byte)23);

        int index = 0;
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                // Получение значение пикселя в RGB
                int pixel = img.getRGB(i, j);

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msg.length * 8) {
                        rgb[k] = replaceBit(rgb[k], getBit(msg, index), 0);
                        index++;
                    }
                }
                img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
            }
        }
        return img;
    }

    // Внедрение сообщения
    public static BufferedImage encodeImage(BufferedImage img, String msg) {
        // Получение ширины и высоты изображение
        int width = img.getWidth();
        int height = img.getHeight();

        // Перевод строки в массив байтов
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        // Добавление в сообщение символа окончания блока
        msgByte = ArrayUtils.add(msgByte, (byte)23);

        int index = 0;
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                // Получение значение пикселя в RGB
                int pixel = img.getRGB(i, j);

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msgByte.length * 8) {
                        rgb[k] = replaceBit(rgb[k], getBit(msgByte, index), 0);
                        index++;
                    }
                }
                img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
            }
        }
        return img;
    }

    // Извлечение сообщения
    public static String decodeImage(BufferedImage img) {
        // Получение ширины и высоты изображение
        int width = img.getWidth();
        int height = img.getHeight();

        // Множество бит извлекаемого сообщения
        ArrayList<Boolean> bits = new ArrayList<Boolean>();
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                // Получение значение пикселя в RGB
                int pixel = img.getRGB(i, j);

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (!checkETB(bits)) {
                        rgb[k] = (rgb[k] & 1);
                        if (rgb[k] == 1) bits.add(true);
                        else bits.add(false);
                    }
                }
            }
        }
        return new String(bitsToBytes(bits));
    }

    // Замена бита в байте по индексу
    private static int replaceBit(int num, boolean bit, int bitIndex) {
        int mask = 1 << bitIndex;
        // Если новый бит равен 1, то устанавливаем его
        if (bit) {
            num |= mask;
        } else {
            // Если новый бит равен 0, то сбрасываем его
            num &= ~mask;
        }
        return num;
    }

    // Получение бита из массива байтов по индексу
    private static boolean getBit(byte[] bytes, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitOffset = bitIndex % 8;

        if (byteIndex >= bytes.length) return false;

        byte targetByte = bytes[byteIndex];
        return ((targetByte >> (7 - bitOffset)) & 1) == 1;
    }

    // Проверка на символ окончания блока
    private static boolean checkETB(ArrayList<Boolean> bits) {
        if (bits.size() % 8 != 0 || bits.size() == 0) return false;
        // ETB-символ 00010111
        else return  bits.get(bits.size() - 1) && bits.get(bits.size() - 2) && bits.get(bits.size() - 3) &&
                !bits.get(bits.size() - 4) && bits.get(bits.size() - 5) &&
                !bits.get(bits.size() - 6) && !bits.get(bits.size() - 7) && !bits.get(bits.size() - 8);
    }

    private static byte[] bitsToBytes(ArrayList<Boolean> bits) {
        byte [] bytes = new byte[bits.size() / 8 - 1];
        for (int i = 0; i < bits.size() - 8; i+=8) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                if (bits.get(i + j)) sb.append("1");
                else sb.append("0");
            }
            bytes[i / 8] = Byte.parseByte(sb.toString(), 2);
        }
        return bytes;
    }
}
