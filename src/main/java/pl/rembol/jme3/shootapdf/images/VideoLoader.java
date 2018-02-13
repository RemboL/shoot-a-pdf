package pl.rembol.jme3.shootapdf.images;

import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JFrame;

import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.media.callback.nonseekable.FileInputStreamMedia;

class VideoLoader implements ImageLoader {

    private BufferedImage image;

    private static final int width = 600;

    private static final int height = 400;

    VideoLoader() {
        new NativeDiscovery().discover();
    }

    private class TutorialRenderCallbackAdapter extends RenderCallbackAdapter {

        private final BufferedImage image;
        private final Texture texture;
        private final AWTLoader awtLoader = new AWTLoader();

        private TutorialRenderCallbackAdapter(BufferedImage image, Texture texture) {
            super(new int[width * height]);
            this.image = image;
            this.texture = texture;
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
            // Simply copy buffer to the image and repaint
            image.setRGB(0, 0, width, height, rgbBuffer, 0, width);
            texture.setImage(awtLoader.load(image, true));
        }
    }

    @Override
    public List<Texture2D> load(File file) {

        JFrame frame = new JFrame("Direct Media Player");
        frame.setBounds(100, 100, width, height);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        image = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(width, height);
        BufferFormatCallback bufferFormatCallback = (sourceWidth, sourceHeight) -> new RV32BufferFormat(width, height);
        Texture2D texture = new Texture2D(new AWTLoader().load(image, true));
        DirectMediaPlayerComponent mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new TutorialRenderCallbackAdapter(image, texture);
            }
        };

        mediaPlayerComponent.getMediaPlayer().playMedia(new FileInputStreamMedia(file));
        mediaPlayerComponent.getMediaPlayer().play();

        frame.setVisible(true);

        return  Collections.singletonList(texture);
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && Stream.of(".flv", ".mpeg").anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }
}
