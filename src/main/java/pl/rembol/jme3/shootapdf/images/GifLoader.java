package pl.rembol.jme3.shootapdf.images;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GifLoader implements ImageLoader {

    private static class GifFrame {
        final Image image;

        final int delay;

        GifFrame(Image image, int delay) {
            this.image = image;
            this.delay = delay;
        }
    }

    private final SimpleApplication simpleApplication;

    GifLoader(SimpleApplication simpleApplication) {
        this.simpleApplication = simpleApplication;
    }

    private class GifAnimAppState extends AbstractAppState {

        private final List<GifFrame> frames;

        private final List<Geometry> frameQuads;

        private final Node scene;

        private float timeToSwitchFrames = 0f;

        private int currentFrame = 0;

        GifAnimAppState(Texture2D texture, List<GifFrame> frames) {
            this.frames = frames;
            frameQuads = new ArrayList<>();

            for (int i = 0; i < frames.size(); ++i) {
                Geometry geometry = new Geometry("slide", new Quad(1, 1));
                geometry.setLocalTranslation(-.5f, -.5f, -frames.size() + i);
                Material material = new Material(simpleApplication.getAssetManager(),
                        "Common/MatDefs/Misc/Unshaded.j3md");
                material.setTexture("ColorMap", new Texture2D(frames.get(i).image));
                material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
                geometry.setMaterial(material);
                frameQuads.add(geometry);
            }

            Camera camera = new Camera(frames.get(0).image.getWidth(), frames.get(0).image.getHeight());
            camera.setFrustumFar(frames.size() + 2);
            camera.setLocation(new Vector3f(0, 0, 1));
            camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            ViewPort modelView = simpleApplication.getRenderManager().createPreView("playerShipBarsView", camera);
            modelView.setClearFlags(true, true, true);
            FrameBuffer offBuffer = new FrameBuffer(frames.get(0).image.getWidth(), frames.get(0).image.getHeight(), 1);
            offBuffer.setDepthBuffer(Image.Format.Depth);
            offBuffer.setColorTexture(texture);

            modelView.setOutputFrameBuffer(offBuffer);
            scene = new Node("scene");

            scene.updateGeometricState();
            modelView.attachScene(scene);

        }

        public void update(float tpf) {
            timeToSwitchFrames -= tpf;

            while (timeToSwitchFrames <= 0) {
                currentFrame++;
                if (currentFrame >= frames.size()) {
                    currentFrame = 0;
                    scene.detachAllChildren();
                }
                scene.attachChild(frameQuads.get(currentFrame));
                scene.updateGeometricState();

                timeToSwitchFrames += frames.get(currentFrame).delay * 0.01f;
            }
        }
    }

    @Override
    public List<Texture2D> load(File file) {
        try {
            List<GifFrame> frames = new ArrayList<>();
            GIFImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
            ir.setInput(ImageIO.createImageInputStream(file));

            AWTLoader awtLoader = new AWTLoader();
            for (int i = 0; i < ir.getNumImages(true); i++) {
                frames.add(new GifFrame(
                        awtLoader.load(ir.read(i), true),
                        ((GIFImageMetadata) ir.getImageMetadata(i)).delayTime));
            }

            Texture2D texture = new Texture2D(frames.get(0).image.getWidth(), frames.get(0).image.getHeight(), Image.Format.RGBA8);
            simpleApplication.getStateManager().attach(new GifAnimAppState(texture, frames));
            return Collections.singletonList(texture);
        } catch (IOException exception) {
            return Collections.emptyList();
        }

    }

    @Override
    public boolean canLoad(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".gif");
    }
}
