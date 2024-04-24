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

        BufferedImage img = ImageIO.read(new File("image/23.png"));
        BufferedImage bruyndockxImg = Bruyndonckx.encodeImage(img, msg, "key", 80, 10);

        try {
            ImageIO.write(bruyndockxImg, "png", new File("image/bruyndockx.png"));
            BufferedImage msgImg = ImageIO.read(new File("image/bruyndockx.png"));
            String decodeMsg = Bruyndonckx.decodeImage(msgImg, "key", 80, 10);
            System.out.println(decodeMsg);
        } catch (IOException ignored) {
        }
    }
}
