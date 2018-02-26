package pl.rembol.jme3.shootapdf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

public class ImageRescaler {

    private final SimpleApplication application;
    
    private final Map<Texture2D, ViewPort> viewPorts = new HashMap<>();

    public ImageRescaler(SimpleApplication application) {
        this.application = application;
    }

    public List<Texture2D> rescale(List<Texture2D> images) {

        return images.stream().map(this::rescale).collect(Collectors.toList());
    }

    public Texture2D rescale(Texture2D texture) {
        Camera camera = new Camera(application.getCamera().getWidth(), application.getCamera().getHeight());
        camera.setLocation(new Vector3f(0, 0, 1));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        ViewPort modelView = application.getRenderManager().createPreView("rescalerView", camera);
        modelView.setClearFlags(true, true, true);
        Texture2D offTexture = new Texture2D(application.getCamera().getWidth(), application.getCamera().getHeight(), Image.Format.RGBA8);
        FrameBuffer offBuffer = new FrameBuffer(application.getCamera().getWidth(), application.getCamera().getHeight(), 1);
        offBuffer.setDepthBuffer(Image.Format.Depth);
        offBuffer.setColorTexture(offTexture);

        modelView.setOutputFrameBuffer(offBuffer);
        Node scene = new Node("scene");

        Quad quad = new Quad(1, 1);
        Geometry geometry = new Geometry("slide", quad);

        resize(quad, geometry, texture, camera);

        Node textureNode = new Node();
        textureNode.attachChild(geometry);
        textureNode.setLocalTranslation(0, 0, -1);
        geometry.addControl(new ResizeQuadControl(quad, geometry, texture, camera));
        Material material = new Material(application.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", texture);
        geometry.setMaterial(material);
        scene.attachChild(textureNode);

        scene.updateGeometricState();
        modelView.attachScene(scene);
        
        viewPorts.put(offTexture, modelView);
        
        return offTexture;
    }

    private static class ResizeQuadControl extends AbstractControl {

        private final Quad quad;

        private final Geometry geometry;

        private final Texture2D texture2D;

        private final Camera camera;

        private ResizeQuadControl(Quad quad, Geometry geometry, Texture2D texture2D, Camera camera) {
            this.quad = quad;
            this.geometry = geometry;
            this.texture2D = texture2D;
            this.camera = camera;
        }

        @Override
        protected void controlUpdate(float tpf) {
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            float quadRatio = quad.getWidth() / quad.getHeight();
            float cameraRatio = 1f * camera.getWidth() / camera.getHeight();
            float textureRatio = 1f * texture2D.getImage().getWidth() / texture2D.getImage().getHeight();
            if (quadRatio != textureRatio / cameraRatio) {
                resize(quad, geometry, texture2D, camera);
            }

        }

    }

    private static void resize(Quad quad, Geometry geometry, Texture2D texture2D, Camera camera) {
        float cameraRatio = 1f * camera.getWidth() / camera.getHeight();
        float textureRatio = 1f * texture2D.getImage().getWidth() / texture2D.getImage().getHeight();

        if (cameraRatio / textureRatio < 1f) {
            quad.updateGeometry(1f, cameraRatio / textureRatio);
        } else {
            quad.updateGeometry(textureRatio / cameraRatio, 1f);
        }
        geometry.setLocalTranslation(-quad.getWidth() / 2, -quad.getHeight() / 2, 0);
        getRoot(geometry).updateGeometricState();
    }

    private static Spatial getRoot(Spatial element) {
        if (element.getParent() == null) {
            return element;
        } else {
            return getRoot(element.getParent());
        }
    }
    
    public void dropViewPort(Texture2D texture2D) {
    
        if (viewPorts.containsKey(texture2D)) {
            application.getRenderManager().removePreView(viewPorts.get(texture2D));
            viewPorts.remove(texture2D);
        }
    }
}

