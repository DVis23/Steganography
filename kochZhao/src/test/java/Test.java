import ru.dvis.KochZhao;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String msg = "I wish to get advice on troubleshooting a USB drive." ;

        BufferedImage image = ImageIO.read(new File("image/2.jpg"));
        BufferedImage kochZhaoImg = KochZhao.encodeImage(image, msg, "key");
        try {
            ImageIO.write(kochZhaoImg, "JPEG", new File("image/kochZhao.jpg"));
            BufferedImage msgImg = ImageIO.read(new File("image/kochZhao.jpg"));
            String decodeMsg = KochZhao.decodeImage(msgImg, "key");
            System.out.println(decodeMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
