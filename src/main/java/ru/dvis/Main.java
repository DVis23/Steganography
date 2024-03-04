package ru.dvis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String [] args) throws IOException {

        String msg = "The Byte class wraps a value of primitive type byte in an object. \n" +
                "In addition, this class provides several methods for converting a byte to a String and a String to a byte, " +
                "as well as other constants and methods useful when dealing with a byte.\n" +
                "\n" +
                "For example, in a future release, synchronization may fail.";

        BufferedImage img = ImageIO.read(new File("image/2.jpg"));
        BufferedImage lsbImg = LSB.encodeImage(img, msg);

        try {
            ImageIO.write(lsbImg, "png", new File("image/lsb.png"));
            BufferedImage msgImg = ImageIO.read(new File("image/lsb.png"));
            String decodeMsg = LSB.decodeImage(msgImg);
            System.out.println(decodeMsg);
        } catch (IOException ignored) {
        }

    }

    private static void printByte(byte [] msgByte) {
        for (byte b : msgByte) {
            System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            System.out.print(" ");
        }
        System.out.println();
    }
}
