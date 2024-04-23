import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import static ru.dvis.ArrayUtils.*;
import static ru.dvis.PixelUtils.*;
public class Test {

    public static void main(String [] args) throws IOException {
        BufferedImage img = ImageIO.read(new File("image/23.png"));
        System.out.println(getBrightness(img, 100, 100));
        System.out.println(getRed(img, 100, 100) + ", "
                + getGreen(img, 100, 100) + ", "
                + getBlue(img, 100, 100));
        setBrightness(img, 100, 100, 180);
        System.out.println(getBrightness(img, 100, 100));
        System.out.println(getRed(img, 100, 100) + ", "
                + getGreen(img, 100, 100) + ", "
                + getBlue(img, 100, 100));
    }
}
