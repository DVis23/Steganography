package ru.dvis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {

    public static void main(String [] args) throws IOException {

        String msg = "Hello World!";
        byte[] msgByte = msg.getBytes(StandardCharsets.US_ASCII);
        File img = new File("image/2.jpg");
        byte[] imgByte = Files.readAllBytes(img.toPath());
        byte[] lsbImg = LSB.encode(imgByte, msgByte);

        try {
            FileOutputStream outputStream = new FileOutputStream("image/lsb.jpg");
            outputStream.write(lsbImg);
            outputStream.close();

            File msgImg = new File("image/lsb.jpg");
            byte[] msgImgByte = Files.readAllBytes(msgImg.toPath());
            byte[] decodeMsgByte = LSB.decode(msgImgByte);
            String decodeMsg = new String(decodeMsgByte);
            System.out.println(decodeMsg);
        } catch (IOException ignored) {
        }

    }
}
