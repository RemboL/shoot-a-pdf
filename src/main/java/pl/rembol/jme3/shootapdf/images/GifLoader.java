package pl.rembol.jme3.shootapdf.images;

import pl.rembol.jme3.shootapdf.ImageRescaler;
import pl.rembol.jme3.shootapdf.slide.GifSlideFactory;
import pl.rembol.jme3.shootapdf.slide.SlideFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class GifLoader implements ImageLoader {

    private final ImageRescaler imageRescaler;

    GifLoader(ImageRescaler imageRescaler) {
        this.imageRescaler = imageRescaler;
    }

    @Override
    public List<SlideFactory> load(File file) {
        return Collections.singletonList(new GifSlideFactory(imageRescaler, file));
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".gif");
    }
}
