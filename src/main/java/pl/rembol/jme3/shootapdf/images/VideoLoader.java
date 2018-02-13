package pl.rembol.jme3.shootapdf.images;

import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class VideoLoader implements ImageLoader {

    private BufferedImage image;

    private static final int width = 600;

    private static final int height = 400;

    VideoLoader() {
        new NativeDiscovery().discover();
        try {
            SwingUtilities.invokeAndWait(() -> init(new String[0]));
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void init(String[] args) {
//        JFrame frame = new JFrame("Direct Media Player");
//        frame.setBounds(100, 100, width, height);
//        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
        image = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(width, height);
        BufferFormatCallback bufferFormatCallback = (sourceWidth, sourceHeight) -> new RV32BufferFormat(width, height);
        VideoSurfacePanel videoSurface = new VideoSurfacePanel(image);
//        frame.setContentPane(videoSurface);
//        frame.setVisible(true);
        DirectMediaPlayerComponent mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new TutorialRenderCallbackAdapter(image, videoSurface);
            }
        };
        mediaPlayerComponent.getMediaPlayer().playMedia(args[0]);
        mediaPlayerComponent.getMediaPlayer().play();
    }

    private class VideoSurfacePanel extends JPanel {

        private final BufferedImage image;

        private VideoSurfacePanel(BufferedImage image) {
            this.image = image;
            setBackground(Color.black);
            setOpaque(true);
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(image, null, 0, 0);
        }
    }

    private class TutorialRenderCallbackAdapter extends RenderCallbackAdapter {

        private final BufferedImage image;
        private final VideoSurfacePanel videoSurface;

        private TutorialRenderCallbackAdapter(BufferedImage image, VideoSurfacePanel videoSurface) {
            super(new int[width * height]);
            this.image = image;
            this.videoSurface = videoSurface;
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
            // Simply copy buffer to the image and repaint
            image.setRGB(0, 0, width, height, rgbBuffer, 0, width);
            videoSurface.repaint();
        }
    }

    @Override
    public List<Texture2D> load(File file) {
        return  Collections.singletonList(new Texture2D(new AWTLoader().load(image, true)));
    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && Stream.of(".flv", ".mpeg").anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }
}
