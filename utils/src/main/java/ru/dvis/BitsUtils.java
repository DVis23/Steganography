package ru.dvis;

import java.util.ArrayList;

public class BitsUtils {

    // изменение значение бита в байте на заданное по индексу
    public static int replaceBit(int num, boolean bit, int bitIndex) {
        int mask = 1 << bitIndex;
        if (bit) num |= mask;
        else num &= ~mask;
        return num;
    }

    // извлечение бита из массива байт по индексу
    public static boolean getBit(byte[] bytes, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitOffset = bitIndex % 8;

        if (byteIndex >= bytes.length) return false;

        byte targetByte = bytes[byteIndex];
        return ((targetByte >> (7 - bitOffset)) & 1) == 1;
    }

    // Проверка наличия ETB-симола
    public static boolean checkETB(ArrayList<Boolean> bits) {
        if (bits.size() % 8 != 0 || bits.isEmpty()) return false;
            // ETB-символ в bin = 00010111
        else return  bits.get(bits.size() - 1) && bits.get(bits.size() - 2) && bits.get(bits.size() - 3) &&
                !bits.get(bits.size() - 4) && bits.get(bits.size() - 5) &&
                !bits.get(bits.size() - 6) && !bits.get(bits.size() - 7) && !bits.get(bits.size() - 8);
    }

    // Конвертация списка бит в байты
    public static byte[] bitsToBytes(ArrayList<Boolean> bits) {
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

    private static void printByte(byte [] msgByte) {
        for (byte b : msgByte) {
            System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            System.out.print(" ");
        }
        System.out.println();
    }


}
