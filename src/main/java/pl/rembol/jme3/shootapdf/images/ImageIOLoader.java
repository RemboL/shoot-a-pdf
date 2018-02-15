package pl.rembol.jme3.shootapdf.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.ImageRescaler;
import pl.rembol.jme3.shootapdf.slide.SimpleTextureSlideFactory;
import pl.rembol.jme3.shootapdf.slide.SlideFactory;

class ImageIOLoader implements ImageLoader {

    private final ImageRescaler imageRescaler;

    ImageIOLoader(ImageRescaler imageRescaler) {
        this.imageRescaler = imageRescaler;
    }

    @Override
    public List<SlideFactory> load(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            return Collections.singletonList(new SimpleTextureSlideFactory(
                    imageRescaler.rescale(new Texture2D(new AWTLoader().load(img, true)))));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && Stream.of(".png", ".jpeg", ".jpg", ".bmp").anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }
}
