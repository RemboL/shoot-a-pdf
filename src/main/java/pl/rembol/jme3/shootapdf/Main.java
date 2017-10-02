package pl.rembol.jme3.shootapdf;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import java.util.List;
import java.util.stream.Collectors;

public class Main extends SimpleApplication {

    private Main(AppState[] appStates) {
        super(appStates);
    }

    public static void main(String[] args) {
        Main app = new Main(new AppState[0]);
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Shoot-a-PDF");
        settings.put("VSync", true);
        settings.put("Samples", 4);
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        AWTLoader awtLoader = new AWTLoader();
        List<Image> images = new PDFLoader().load("jme.pdf").stream().map(awtImage -> awtLoader.load(awtImage, true)).collect(Collectors.toList());


        for (int i =0; i < images.size(); ++i) {
        Texture2D boardTexture = new Texture2D(images.get(i));
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setTexture("ColorMap", boardTexture);

            Quad boardShape = new Quad(4.0f, 4.0f);

            Geometry board = new Geometry("Board", boardShape);

            Node node = new Node();
            node.attachChild(board);
            board.setLocalTranslation(-2f, -2f, 0f);

            board.setMaterial(material);
            node.addControl(new AbstractControl() {
                @Override
                protected void controlUpdate(float tpf) {
                    getSpatial().rotate(0, tpf, 0);
                }

                @Override
                protected void controlRender(RenderManager rm, ViewPort vp) {

                }
            });

            node.setLocalTranslation(0, -2+4*i, 0);
            node.setLocalRotation(new Quaternion().fromAngleAxis(i, Vector3f.UNIT_Y));
            rootNode.attachChild(node);
        }

    }
}
