package pl.rembol.jme3.shootapdf.slide;

import java.io.File;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture2D;
import com.jme3x.jfx.media.TextureMovie;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import pl.rembol.jme3.shootapdf.ImageRescaler;

public class VideoSlideFactory extends SlideFactory {

    private final ImageRescaler imageRescaler;

    private final File file;

    private TextureMovie textureMovie;

    public VideoSlideFactory(ImageRescaler imageRescaler, File file) {
        this.imageRescaler = imageRescaler;
        this.file = file;
    }

    @Override
    public Slide create(Application application, Vector3f position, Vector2f slideSize) {
        final Media media = new Media(file.toURI().toString());

        media.errorProperty().addListener((observable, oldValue, newValue) -> newValue.printStackTrace());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);

        textureMovie = new TextureMovie(application, mediaPlayer, TextureMovie.LetterboxMode.VALID_SQUARE);
        textureMovie.setLetterboxColor(ColorRGBA.Black);

        return new VideoSlide(application, imageRescaler, textureMovie.getTexture(), position, slideSize, mediaPlayer);
    }

    private class VideoSlide extends Slide {

        private final MediaPlayer mediaPlayer;

        private VideoSlide(Application application, ImageRescaler imageRescaler, Texture2D texture, Vector3f position, Vector2f slideSize, MediaPlayer mediaPlayer) {
            super(application, imageRescaler, texture, position, slideSize);
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void remove() {
            super.remove();
            System.out.println("disposing video "+file.getName());

            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

    }
}
