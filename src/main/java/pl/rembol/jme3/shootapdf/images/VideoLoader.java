package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.media.callback.nonseekable.FileInputStreamMedia;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class VideoLoader implements ImageLoader {

    DirectMediaPlayerComponent mediaPlayerComponent;

    private Texture2D texture;

    private static final int width = 600;

    private static final int height = 400;

    VideoLoader() {
        new NativeDiscovery().discover();
    }

    @Override
    public List<Texture2D> load(File file) {

        texture = new Texture2D();

        Image image = new Image();
        image.setWidth(width);
        image.setHeight(height);
        image.setFormat(Image.Format.BGRA8);
        image.setData(BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4));
        texture.setImage(image);
        BufferFormatCallback bufferFormatCallback = (sourceWidth, sourceHeight) -> new RV32BufferFormat(width, height);


        mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new JmeRenderCallbackAdapter(texture);
            }
        };

        mediaPlayerComponent.getMediaPlayer().playMedia(new FileInputStreamMedia(file));
        mediaPlayerComponent.getMediaPlayer().play();
        mediaPlayerComponent.getMediaPlayer().setRepeat(true);

        return Collections.singletonList(texture);
    }

    public class JmeRenderCallbackAdapter extends RenderCallbackAdapter {

        private ByteBuffer imageBuffer;
        private Texture2D texture;
        int width;
        int height;

        JmeRenderCallbackAdapter(Texture2D texture) {
            super(new int[texture.getImage().getWidth() * texture.getImage().getHeight()]);
            width = texture.getImage().getWidth();
            height = texture.getImage().getHeight();
            imageBuffer = BufferUtils.clone(texture.getImage().getData(0));
            this.texture = texture;
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
            // Simply copy buffer to the image and repaint
            if (imageBuffer != null) {
                imageBuffer.rewind();
                imageBuffer.clear();
                for (int i : rgbBuffer) {
                    imageBuffer.putInt(i);
                }
                texture.getImage().setData(BufferUtils.clone(imageBuffer));
                texture.getImage().setUpdateNeeded();
            }
        }
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && Stream.of(".flv", ".mpeg", ".mp4").anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }
}
