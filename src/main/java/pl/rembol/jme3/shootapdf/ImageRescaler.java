package pl.rembol.jme3.shootapdf;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

import java.util.List;
import java.util.stream.Collectors;

public class ImageRescaler {

    private final SimpleApplication application;

    public ImageRescaler(SimpleApplication application) {
        this.application = application;
    }

    public List<Texture2D> rescale(List<Texture2D> images) {

        return images.stream().map(this::rescale).collect(Collectors.toList());
    }

    private Texture2D rescale(Texture2D texture) {
        Camera camera = new Camera(application.getCamera().getWidth(), application.getCamera().getHeight());
        camera.setLocation(new Vector3f(0, 0, 1));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        ViewPort modelView = application.getRenderManager().createPreView("playerShipBarsView", camera);
        modelView.setClearFlags(true, true, true);
        Texture2D offTexture = new Texture2D(application.getCamera().getWidth(), application.getCamera().getHeight(), Image.Format.RGBA8);
        FrameBuffer offBuffer = new FrameBuffer(application.getCamera().getWidth(), application.getCamera().getHeight(), 1);
        offBuffer.setDepthBuffer(Image.Format.Depth);
        offBuffer.setColorTexture(offTexture);

        modelView.setOutputFrameBuffer(offBuffer);
        Node scene = new Node("scene");

        Quad quad;

        if (offTexture.getImage().getHeight() * camera.getWidth() < camera.getHeight() * offTexture.getImage().getWidth()) {
            quad = new Quad(1f, (offTexture.getImage().getHeight() * camera.getWidth()) / (1f * offTexture.getImage().getWidth() * camera.getHeight()));
        } else {
            quad = new Quad((camera.getHeight() * offTexture.getImage().getWidth()) / (1f * offTexture.getImage().getHeight() * camera.getWidth()), 1f);
        }

        Geometry geometry = new Geometry("slide", quad);
        geometry.setLocalTranslation(-quad.getWidth() / 2, -quad.getHeight() / 2, -1);
        Material material = new Material(application.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", texture);
        geometry.setMaterial(material);
        scene.attachChild(geometry);

        scene.updateGeometricState();
        modelView.attachScene(scene);
        return offTexture;
    }
}