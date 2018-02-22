package pl.rembol.jme3.shootapdf.images;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3x.jfx.media.TextureMovie;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import pl.rembol.jme3.shootapdf.ImageRescaler;
import pl.rembol.jme3.shootapdf.slide.SimpleTextureSlideFactory;
import pl.rembol.jme3.shootapdf.slide.SlideFactory;
import pl.rembol.jme3.shootapdf.slide.VideoSlideFactory;

class VideoLoader implements ImageLoader {

    private final ImageRescaler imageRescaler;

    VideoLoader(ImageRescaler imageRescaler) {
        this.imageRescaler = imageRescaler;
    }

    @Override
    public List<SlideFactory> load(File file) {
        return Collections.singletonList(new VideoSlideFactory(imageRescaler, file));
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && Stream.of(".flv", ".mpeg", ".mp4").anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }
}
