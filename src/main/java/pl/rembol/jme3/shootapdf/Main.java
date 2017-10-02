package pl.rembol.jme3.shootapdf;

import java.util.List;
import java.util.stream.Collectors;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.ball.Ball;
import pl.rembol.jme3.shootapdf.slide.Slide;
import pl.rembol.jme3.shootapdf.slide.SlideBox;

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

        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
        for (int i = 0; i < 4; ++i) {
            PointLight light = new PointLight();
            light.setPosition(new Vector3f(FastMath.sin(i * FastMath.TWO_PI / 4) * 20f, 20f, FastMath.cos(i * FastMath.TWO_PI / 4) * 20f));
            light.setColor(ColorRGBA.LightGray);
            rootNode.addLight(light);
            final int SHADOWMAP_SIZE=1024;
            PointLightShadowFilter filter = new PointLightShadowFilter(assetManager, SHADOWMAP_SIZE);
            filter.setLight(light);
            filter.setEnabled(true);
            filterPostProcessor.addFilter(filter);
            viewPort.addProcessor(filterPostProcessor);
        }

        AWTLoader awtLoader = new AWTLoader();
        List<Image> images = new PDFLoader().load("jme.pdf").stream().map(
                awtImage -> awtLoader.load(awtImage, true)).collect(Collectors.toList());

        BulletAppState bulletAppState = new BulletAppState();
//        bulletAppState.setDebugEnabled(true);
        getStateManager().attach(bulletAppState);
        getCamera().setLocation(new Vector3f(0f, 10f, 20f));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        for (int i = 0; i < images.size(); ++i) {
            Node node = new Slide(this, images.get(i), new Vector3f(0f, 0.5f, -i * 4f));

//            node.setLocalRotation(new Quaternion().fromAngleAxis(i, Vector3f.UNIT_Y));
            rootNode.attachChild(node);
            Scene scene = new Scene(this);
            rootNode.attachChild(scene);
        }

        getStateManager().getState(BulletAppState.class).getPhysicsSpace().addCollisionListener(new PhysicsCollisionListener() {
            @Override
            public void collision(PhysicsCollisionEvent event) {
                if (event.getObjectA().getUserObject() instanceof SlideBox || event.getObjectB().getUserObject() instanceof SlideBox) {
                    if (event.getObjectA().getUserObject() instanceof Ball || event.getObjectB().getUserObject() instanceof Ball) {
                        setSlidesPhysical();
                    }
                }
            }
        });

        getStateManager().attach(new AbstractAppState() {

            private float tick = 0;

            public void update(float tpf) {
                tick += tpf / 2;
                getCamera().setLocation(new Vector3f(FastMath.sin(tick) * 20f, 10f, FastMath.cos(tick) * 20f));
                getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            }
        });

        inputManager.addMapping("shoot", new MouseButtonTrigger(
                MouseInput.BUTTON_LEFT));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed) {
                    Ball ball = new Ball(Main.this);
                    rootNode.attachChild(ball);
                    ball.getControl(RigidBodyControl.class).setPhysicsLocation(getCamera().getLocation());
                    ball.getControl(RigidBodyControl.class).setLinearVelocity(getCamera().getDirection().normalize().mult(100f));
                }
            }
        }, "shoot");
    }

    public void setSlidesPhysical() {
        rootNode.getChildren().stream()
                .filter(Slide.class::isInstance)
                .map(Slide.class::cast)
                .forEach(slide -> slide.setKinematic(false));
    }
}
