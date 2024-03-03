package ru.dvis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String [] args) throws IOException {

        String msg = "Hello World!!!";
        System.out.println(msg);
        int msgSize = msg.length() * 8;

        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        BufferedImage img = ImageIO.read(new File("image/2.jpg"));
        BufferedImage lsbImg = LSB.encodeImage(img, msgByte, msgSize);

        try {
            ImageIO.write(lsbImg, "jpg", new File("image/lsb.jpg"));

            BufferedImage msgImg = ImageIO.read(new File("image/lsb.jpg"));
            byte[] decodeMsgByte = LSB.decodeImage(lsbImg, msgSize);
            String decodeMsg = new String(decodeMsgByte);
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
