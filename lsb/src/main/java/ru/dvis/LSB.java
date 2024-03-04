package ru.dvis;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class LSB {

    // ��������� ���������
    public static BufferedImage encodeImage(BufferedImage img, byte[] msg) {
        // ��������� ������ � ������ �����������
        int width = img.getWidth();
        int height = img.getHeight();

        int index = 0;

        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                int pixel = img.getRGB(i, j);

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msg.length * 8) {
                        rgb[k] = replaceBit(rgb[k], getBit(msg, index), 0);
                        index++;
                    } else {
                        img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
                        return img;
                    }
                }
                img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
            }
        }
        return img;
    }

    // ���������� ���������
    public static byte[] decodeImage(BufferedImage img, int msgSize) {
        // ��������� ������ � ������ �����������
        int width = img.getWidth();
        int height = img.getHeight();

        int index = 0;
        BitSet bitsetMsg = new BitSet();
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                int pixel = img.getRGB(i, j);

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};

                for (int k = 0; k < 3; k++) {
                    if (index < msgSize) {
                        rgb[k] = (rgb[k] & 1);
                        if (rgb[k] == 1) bitsetMsg.set(index);
                        index++;
                    } else return reverse(bitsetMsg.toByteArray());
                }

            }
        }
        return reverse(bitsetMsg.toByteArray());
    }

    private static int replaceBit(int num, boolean bit, int position) {
        int mask = 1 << position;
        // ���� ����� ��� ����� 1, �� ������������� ���
        if (bit) {
            num |= mask;
        } else {
            // ���� ����� ��� ����� 0, �� ���������� ���
            num &= ~mask;
        }
        return num;
    }

    private static byte[] reverse(byte [] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Integer.reverse(bytes[i]) >>> 24);
        }
        return bytes;
    }

    public static boolean getBit(byte[] array, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitOffset = bitIndex % 8;

        if (byteIndex >= array.length) return false;

        byte targetByte = array[byteIndex];
        return ((targetByte >> (7 - bitOffset)) & 1) == 1;
    }
}
