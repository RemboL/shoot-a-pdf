package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class ImageIOLoader implements ImageLoader {

    @Override
    public List<Texture2D> load(String fileLocation) {
        try {
            BufferedImage img = ImageIO.read(new File(fileLocation));
            return Collections.singletonList(new Texture2D(new AWTLoader().load(img, true)));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean canLoad(String fileLocation) {
        return Stream.of(".png", ".jpeg", ".jpg", ".bmp").anyMatch(extension -> fileLocation.toLowerCase().endsWith(extension));
    }
}
