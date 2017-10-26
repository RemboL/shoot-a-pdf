package pl.rembol.jme3.shootapdf;

import java.util.List;
import java.util.stream.Collectors;

import com.jme3.app.SimpleApplication;
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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.ball.Ball;
import pl.rembol.jme3.shootapdf.player.Player;
import pl.rembol.jme3.shootapdf.player.PlayerInputListener;
import pl.rembol.jme3.shootapdf.player.PlayerMovementControl;
import pl.rembol.jme3.shootapdf.slide.Slide;
import pl.rembol.jme3.shootapdf.slide.SlideBox;

public class Main extends SimpleApplication {


    public static void main(String[] args) {
        Main app = new Main();
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
            final int SHADOWMAP_SIZE = 1024;
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

            rootNode.attachChild(node);
            Scene scene = new Scene(this);
            rootNode.attachChild(scene);
        }

        getStateManager().getState(BulletAppState.class).getPhysicsSpace().addCollisionListener(event -> {
            SlideBox slideBox = null;
            Ball ball = null;
            if (event.getObjectA().getUserObject() instanceof SlideBox) {
                slideBox = (SlideBox) event.getObjectA().getUserObject();
            }
            if (event.getObjectB().getUserObject() instanceof SlideBox) {
                slideBox = (SlideBox) event.getObjectB().getUserObject();
            }
            if (event.getObjectA().getUserObject() instanceof Ball) {
                ball = (Ball) event.getObjectA().getUserObject();
            }
            if (event.getObjectB().getUserObject() instanceof Ball) {
                ball = (Ball) event.getObjectB().getUserObject();
            }

            if (slideBox != null && ball != null) {
                slideBox.setSlidePhysical();
            }
        });

        inputManager.addMapping("shoot", new MouseButtonTrigger(
                MouseInput.BUTTON_LEFT));
        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (isPressed) {
                shootBall();
            }
        }, "shoot");


        Player player = new Player(this);
        rootNode.attachChild(player);
    }

    private void shootBall() {
        Ball ball = new Ball(Main.this);
        rootNode.attachChild(ball);
        ball.getControl(RigidBodyControl.class).setPhysicsLocation(getCamera().getLocation().add(getCamera().getDirection().normalize().mult(2f)));
        ball.getControl(RigidBodyControl.class).setLinearVelocity(getCamera().getDirection().normalize().mult(100f));
    }
}
