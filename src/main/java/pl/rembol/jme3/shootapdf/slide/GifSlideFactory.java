package pl.rembol.jme3.shootapdf.slide;

import at.dhyan.open_imaging.GifDecoder;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.ImageRescaler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifSlideFactory extends SlideFactory {

    private final ImageRescaler imageRescaler;

    private final File file;

    private List<GifFrame> frames;

    public GifSlideFactory(ImageRescaler imageRescaler, File file) {
        this.imageRescaler = imageRescaler;
        this.file = file;
    }

    @Override
    public Slide create(Application application, Vector3f position, Vector2f slideSize) {
        Slide slide = new Slide(application, new Texture2D(1, 1, Image.Format.RGBA8), position, slideSize);
        try {
            final GifDecoder.GifImage gif = GifDecoder.read(new FileInputStream(file));
            final int frameCount = gif.getFrameCount();
            List<GifFrame> frames = new ArrayList<>();
            AWTLoader awtLoader = new AWTLoader();
            for (int i = 0; i < frameCount; i++) {
                final BufferedImage img = gif.getFrame(i);
                final int delay = gif.getDelay(i);
                frames.add(new GifFrame(awtLoader.load(img, true), delay));
            }

            Texture2D originalTexture = new Texture2D(frames.get(0).image.getWidth(), frames.get(0).image.getHeight(), Image.Format.RGBA8);
            Texture2D rescaledTexture = imageRescaler.rescale(originalTexture);
            slide = new Slide(application, rescaledTexture, position, slideSize);
            slide.addControl(new GifAnimControl(application, originalTexture, frames));
        } catch (IOException e) {
            System.out.println("Exception caught while processing "+file.getName()+": "+e.getMessage());
        }
        return slide;
    }

    private static class GifFrame {
        final Image image;

        final int delay;

        GifFrame(Image image, int delay) {
            this.image = image;
            this.delay = delay;
        }
    }

    private static class GifAnimControl extends AbstractControl {

        private final List<GifFrame> frames;

        private final List<Geometry> frameQuads;

        private final Node scene;

        private float timeToSwitchFrames = 0f;

        private int currentFrame = -1;

        GifAnimControl(Application application, Texture2D texture, List<GifFrame> frames) {
            this.frames = frames;
            frameQuads = new ArrayList<>();

            for (int i = 0; i < frames.size(); ++i) {
                Geometry geometry = new Geometry("slide", new Quad(1, 1));
                geometry.setLocalTranslation(-.5f, -.5f, -frames.size() + i);
                Material material = new Material(application.getAssetManager(),
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
            ViewPort modelView = application.getRenderManager().createPreView("playerShipBarsView", camera);
            modelView.setClearFlags(true, true, true);
            FrameBuffer offBuffer = new FrameBuffer(frames.get(0).image.getWidth(), frames.get(0).image.getHeight(), 1);
            offBuffer.setDepthBuffer(Image.Format.Depth);
            offBuffer.setColorTexture(texture);

            modelView.setOutputFrameBuffer(offBuffer);
            scene = new Node("scene");

            scene.updateGeometricState();
            modelView.attachScene(scene);

        }

        @Override
        protected void controlUpdate(float tpf) {
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

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {

        }
    }

}
