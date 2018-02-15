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

class VideoLoader implements ImageLoader {

    private MediaPlayer mediaPlayer;
    
    private TextureMovie textureMovie;

    private SimpleApplication application;

    private final ImageRescaler imageRescaler;

    VideoLoader(SimpleApplication application, ImageRescaler imageRescaler) {
        this.application = application;
        this.imageRescaler = imageRescaler;
    }

    @Override
    public List<SlideFactory> load(File file) {
        final Media media = new Media(file.toURI().toString());


        media.errorProperty().addListener((observable, oldValue, newValue) -> newValue.printStackTrace());
        this.mediaPlayer = new MediaPlayer(media);
        this.mediaPlayer.play();

        this.textureMovie = new TextureMovie(application, this.mediaPlayer, TextureMovie.LetterboxMode.VALID_LETTERBOX);
        this.textureMovie.setLetterboxColor(ColorRGBA.Black);

        return Collections.singletonList(
                new SimpleTextureSlideFactory(imageRescaler.rescale(textureMovie.getTexture())));
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && Stream.of(".flv", ".mpeg", ".mp4").anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }
}
