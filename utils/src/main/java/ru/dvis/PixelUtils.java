package ru.dvis;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PixelUtils {

    public static void setRGB(BufferedImage img, int i, int j, int[] rgb) {
        img.setRGB(i, j, (rgb[0] & 0xFF) << 16 | (rgb[1] & 0xFF) << 8 | (rgb[2] & 0xFF));
    }

    // извлечение значения синего
    public static int getBlue(BufferedImage img, int i, int j) {
        return img.getRGB(i, j) & 0xff;
    }
    // извлечение значения синего
    public static int getGreen(BufferedImage img, int i, int j) {return (img.getRGB(i, j) >> 8) & 0xff; }
    // извлечение значения синего
    public static int getRed(BufferedImage img, int i, int j) {
        return (img.getRGB(i, j) >> 16 ) & 0xff;
    }

    // извлечение значения яркости
    public static int getBrightness(BufferedImage img, int i, int j) {
        // Достаем значения пикселя RGB
        int pixel = img.getRGB(i, j);
        // Массив значений каждого цвета
        int [] rgb = new int[] {pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF};
        return (int) (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
    }

    // замена значения яркости
    public static void setBrightness(BufferedImage img, int i, int j, int value) {
        // Достаем значения пикселя RGB
        int pixel = img.getRGB(i, j);
        // Массив значений каждого цвета
        int [] rgb = new int[] {pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF};
        double currentBrightness = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];

        if (currentBrightness != 0) {
            rgb[0] += (int) (value - currentBrightness);
            rgb[1] += (int) (value - currentBrightness);
            rgb[2] += (int) (value - currentBrightness);
        } else {
            rgb[0] = value;
            rgb[1] = value;
            rgb[2] = value;
        }

        rgb[0] = Math.min(Math.max(rgb[0], 0), 255);
        rgb[1] = Math.min(Math.max(rgb[1], 0), 255);
        rgb[2] = Math.min(Math.max(rgb[2], 0), 255);

        Color newColor = new Color(rgb[0], rgb[1], rgb[2]);
        img.setRGB(i, j, newColor.getRGB());
    }

    public static double[] RGBToYCbCr(int[] pixel) {
        int r = pixel[0];
        int g = pixel[1];
        int b = pixel[2];
        double y = 0.299 * r + 0.587 * g + 0.114 * b;
        double cb = -0.168736 * r - 0.331264 * g + 0.5 * b + 128;
        double cr = 0.5 * r - 0.418688 * g - 0.081312 * b + 128;
        return new double[]{y, cb, cr};
    }

    public static int[] YCbCrToRGB(double[] pixel) {
        double y = pixel[0];
        double cb = pixel[1];
        double cr = pixel[2];
        int r = (int) (y + 1.402 * (cr - 128));
        int g = (int) (y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128));
        int b = (int) (y + 1.772 * (cb - 128));
        return new int[]{r, g, b};
    }
}
