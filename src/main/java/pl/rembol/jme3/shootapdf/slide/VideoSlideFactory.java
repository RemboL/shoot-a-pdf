package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture2D;
import com.jme3x.jfx.media.TextureMovie;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import pl.rembol.jme3.shootapdf.ImageRescaler;

import java.io.File;

public class VideoSlideFactory extends SlideFactory {

    private final ImageRescaler imageRescaler;

    private final File file;

    private MediaPlayer mediaPlayer;

    private TextureMovie textureMovie;

    public VideoSlideFactory(ImageRescaler imageRescaler, File file) {
        this.imageRescaler = imageRescaler;
        this.file = file;
    }

    @Override
    public Slide create(Application application, Vector3f position, Vector2f slideSize) {
        final Media media = new Media(file.toURI().toString());

        media.errorProperty().addListener((observable, oldValue, newValue) -> newValue.printStackTrace());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);

        textureMovie = new TextureMovie(application, mediaPlayer, TextureMovie.LetterboxMode.VALID_LETTERBOX);
        textureMovie.setLetterboxColor(ColorRGBA.Black);

        Texture2D texture2D = imageRescaler.rescale(textureMovie.getTexture());

        return new Slide(application, texture2D, position, slideSize);
    }
}
