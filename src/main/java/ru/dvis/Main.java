package ru.dvis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {

    public static void main(String [] args) throws IOException {

        String msg = "Hello World!";
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        BufferedImage img = ImageIO.read(new File("image/2.jpg"));
        BufferedImage lsbImg = LSB.encodeImage(img, msgByte);

        try {
            ImageIO.write(lsbImg, "jpg", new File("image/lsb.jpg"));

            BufferedImage msgImg = ImageIO.read(new File("image/lsb.jpg"));
            byte[] decodeMsgByte = LSB.decodeImage(msgImg);


            String decodeMsg = new String(decodeMsgByte);
            System.out.println(decodeMsg);
        } catch (IOException ignored) {
        }

    }
}
