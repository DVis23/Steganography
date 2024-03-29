import ru.dvis.CJB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String [] args) throws IOException {

        String msg = "I wish to get advice on troubleshooting a USB drive. A problem that have nerdsniped me.\n" +
                "\n" +
                "Background: A client has asked for an image of a VM to be put on a USB 3.0 drive they have provided. " +
                "\n" +
                "It has an exfat FS and sufficient space for the copy. " +
                "\n" +
                "The disk fragmentation state is unknown. fsck find no error with the FS.\n" +
                "\n" +
                "Whats been done: A copy of the VM has been successfully made on it's host " +
                "\n" +
                "(Ubuntu Server 20.04) to an LVM drive. The copy transfer to the USB drive was " +
                "\n" +
                "scheduled to start yesterday evening using dd with the expectation that it would " +
                "\n" +
                "be finished throughout the night. The first couple of hundred GiB was transferred " +
                "\n" +
                "roughly at the speed I expected after a few transfers of test files.";

        BufferedImage img = ImageIO.read(new File("image/2.jpg"));
        BufferedImage cjbImg = CJB.encodeImage(img, msg, 0.1, 3);

        try {
            ImageIO.write(cjbImg, "png", new File("image/cjb.png"));
            BufferedImage msgImg = ImageIO.read(new File("image/cjb.png"));
            String decodeMsg = CJB.decodeImage(msgImg, 3);
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

