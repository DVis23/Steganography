import ru.dvis.Bruyndonckx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static ru.dvis.ArrayUtils.*;
import static ru.dvis.PixelUtils.*;
public class Test {

    public static void main(String [] args) throws IOException {
        String msg = "I wish to get advice on troubleshooting a USB drive.";

        BufferedImage img = ImageIO.read(new File("image/3.jpg"));
        BufferedImage cjbImg = Bruyndonckx.encodeImage(img, msg, "key", 90, 10);

        try {
            ImageIO.write(cjbImg, "png", new File("image/bruyndockx.png"));
            BufferedImage msgImg = ImageIO.read(new File("image/bruyndockx.png"));
            String decodeMsg = Bruyndonckx.decodeImage(msgImg, "key", 90, 10);
            System.out.println(decodeMsg);
        } catch (IOException ignored) {
        }
    }
}
