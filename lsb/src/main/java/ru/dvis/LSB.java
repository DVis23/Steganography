package ru.dvis;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class LSB {

    // Внедрение сообщения
    public static BufferedImage encodeImage(BufferedImage img, byte[] msg, int msgSize) {
        int width = img.getWidth();
        int height = img.getHeight();
        int index = 0;

        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                int pixel = img.getRGB(i, j);

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msgSize) {
                        rgb[k] = replaceBit(rgb[k], getBit(msg, index), 0);
                        index++;
                        byte b = (byte)rgb[k];
                        //System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
                        //System.out.print(" ");
                    } else {
                        img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
                        //System.out.println();
                        return img;
                    }
                }
                img.setRGB(i, j, (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
            }
        }
        return img;
    }

    // Извлечение сообщения
    public static byte[] decodeImage(BufferedImage img, int msgSize) {
        int width = img.getWidth();
        int height = img.getHeight();

        int index = 0;
        BitSet bitsetMsg = new BitSet();
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {

                int pixel = img.getRGB(i, j);
                int newPixel;

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msgSize) {
                        index++;
                        byte b = (byte)rgb[k];
                        System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
                        System.out.print(" ");
                    }
                }
                System.out.println();
                //int pixel = img.getRGB(i, j);
                //int[] rgb = new int[]{(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};

                for (int k = 0; k < 3; k++) {
                    if (index < msgSize) {
                        rgb[k] = (rgb[k] & 1);
                        if (rgb[k] == 1) bitsetMsg.set(index);
                        index++;
                    } else {
                        //System.out.println(bitsetMsg);
                        return bitsetMsg.toByteArray();
                    }
                }

            }
        }
        return bitsetMsg.toByteArray();
    }

    private static int replaceBit(int num, boolean bit, int position) {
        int mask = 1 << position;
        // Если новый бит равен 1, то устанавливаем его
        if (bit) {
            num |= mask;
        } else {
            // Если новый бит равен 0, то сбрасываем его
            num &= ~mask;
        }
        return num;
    }

    private static void printBitSet(BitSet bi) {
        StringBuilder s = new StringBuilder();
        for( int i = 0; i < bi.length();  i++ )
        {
            s.append( bi.get(i) ? 1: 0 );
            if ((i + 1) % 8 == 0) s.append(" ");
        }

        System.out.println( s );
    }

    private static byte [] reverse(byte [] bytes) {
        byte [] bytes1 = new byte[bytes.length];
        int j = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes1[j] = bytes[i];
            j++;
        }
        return bytes1;
    }

    public static boolean getBit(byte[] array, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitOffset = bitIndex % 8;

        if (byteIndex >= array.length) return false;

        byte targetByte = array[byteIndex];
        return ((targetByte >> (7 - bitOffset)) & 1) == 1;
    }



    /*
    // Внедрение сообщения
    public static BufferedImage encodeImage(BufferedImage img, byte[] msg, int msgSize) {
        int width = img.getWidth();
        int height = img.getHeight();

        int index = 0;
        BitSet bitsetMsg = BitSet.valueOf(msg);
        BitSet bitsetMsg2 = BitSet.valueOf(reverse(msg));
        printBitSet(bitsetMsg);
        printBitSet(bitsetMsg2);
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                int pixel = img.getRGB(i, j);
                int newPixel;

                int [] rgb = new int[] {(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msgSize) {
                        rgb[k] = replaceBit(rgb[k], bitsetMsg.get(index), 0);
                        index++;
                    } else {
                        newPixel = (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff);
                        img.setRGB(i, j, newPixel);
                        return img;
                    }
                }

                newPixel = (rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff);
                img.setRGB(i, j, newPixel);
            }
        }
        //System.out.println(Arrays.toString(bitsetMsg.toByteArray()));
        return img;
    }

    // Извлечение сообщения
    public static byte[] decodeImage(BufferedImage img, int msgSize) {
        int width = img.getWidth();
        int height = img.getHeight();

        int index = 0;
        BitSet bitsetMsg = new BitSet();
        for (int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                int pixel = img.getRGB(i, j);

                int[] rgb = new int[]{(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
                for (int k = 0; k < 3; k++) {
                    if (index < msgSize) {
                        rgb[k] = (rgb[k] & 1);
                        if (rgb[k] == 1) bitsetMsg.set(index);
                        index++;
                    } else {
                        //System.out.println(bitsetMsg);
                        return bitsetMsg.toByteArray();
                    }
                }
            }
        }
        return bitsetMsg.toByteArray();
    }

    private static int replaceBit(int num, boolean bit, int position) {
        int mask = 1 << position;
        // Если новый бит равен 1, то устанавливаем его
        if (bit) {
            num |= mask;
        } else {
            // Если новый бит равен 0, то сбрасываем его
            num &= ~mask;
        }
        return num;
    }

    private static void printBitSet(BitSet bi) {
        StringBuilder s = new StringBuilder();
        for( int i = 0; i < bi.length();  i++ )
        {
            s.append( bi.get(i) ? 1: 0 );
            if ((i + 1) % 8 == 0) s.append(" ");
        }

        System.out.println( s );
    }

    private static byte [] reverse(byte [] bytes) {
        byte [] bytes1 = new byte[bytes.length];
        int j = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes1[j] = bytes[i];
            j++;
        }
        return bytes1;
    }
     */

}
