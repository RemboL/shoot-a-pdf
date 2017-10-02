package pl.rembol.jme3.shootapdf;

import java.util.List;
import java.util.stream.Collectors;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.slide.Slide;

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
        List<Image> images = new PDFLoader().load("jme.pdf").stream().map(
                awtImage -> awtLoader.load(awtImage, true)).collect(Collectors.toList());

        BulletAppState bulletAppState = new BulletAppState();
//        bulletAppState.setDebugEnabled(true);
        getStateManager().attach(bulletAppState);
        getCamera().setLocation(new Vector3f(0f, 10f, 20f));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        for (int i = 0; i < images.size(); ++i) {
            Node node = new Slide(this, images.get(i), new Vector3f(0f, 1f, - i * 4f));

//            node.setLocalRotation(new Quaternion().fromAngleAxis(i, Vector3f.UNIT_Y));
            rootNode.attachChild(node);
            Scene scene = new Scene(this);
            rootNode.attachChild(scene);
        }
        
        getStateManager().attach(new AbstractAppState() {

            private float tick = 0;

            public void update(float tpf) {
                tick += tpf;
                getCamera().setLocation(new Vector3f(FastMath.sin(tick) * 20f, 10f, FastMath.cos(tick) * 20f));
                getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            }
        });

        getStateManager().attach(new AbstractAppState() {

            private float tick = 0;
            
            private boolean done = false;

            public void update(float tpf) {
                tick += tpf;
                if (tick > 3f && !done) {
                    rootNode.getChildren().stream()
                            .filter(Slide.class::isInstance)
                            .map(Slide.class::cast)
                            .forEach(slide -> slide.setKinematic(true));
                    done = true;
                }
            }
        });

        

    }
}
