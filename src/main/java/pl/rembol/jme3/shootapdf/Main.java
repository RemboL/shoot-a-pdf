package pl.rembol.jme3.shootapdf;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import pl.rembol.jme3.shootapdf.images.ImagesLoader;
import pl.rembol.jme3.shootapdf.mode.ModeManager;
import pl.rembol.jme3.shootapdf.player.Player;
import pl.rembol.jme3.shootapdf.slide.SlideManager;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class Main extends SimpleApplication {

    private List<String> files;

    private Player player;

    private SlideManager slideManager;

    public Main(List<String> files) {
        super(new AppState[0]);
        this.files = files;

        // for debug
        if (files.isEmpty()) {
//            this.files = Arrays.asList("jme.pdf");
            this.files = Arrays.asList("source.gif", "source2.gif");
        }
    }

    public static void main(String[] args) {

        Main app = new Main(Arrays.asList(args));

        app.setShowSettings(false);

        boolean fullScreen = false;

        if (fullScreen) {
            DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
            AppSettings settings = new AppSettings(true);
            settings.put("Fullscreen", true);
            settings.put("Width", displayMode.getWidth());
            settings.put("Height", displayMode.getHeight());
            settings.put("Title", "Shoot-a-PDF");
            app.setSettings(settings);
        } else {
            AppSettings settings = new AppSettings(true);
            settings.put("Width", 1280);
            settings.put("Height", 720);
            settings.put("Title", "Shoot-a-PDF");
            app.setSettings(settings);
        }
        app.setPauseOnLostFocus(false);

        app.start();
    }

    @Override
    public void simpleInitApp() {

        List<Texture2D> images = new ImagesLoader(this).loadImages(files);
        List<Texture2D> rescaledImages = new ImageRescaler(this).rescale(images);

        BulletAppState bulletAppState = new BulletAppState();
//        bulletAppState.setDebugEnabled(true);
        getStateManager().attach(bulletAppState);
        getCamera().setLocation(new Vector3f(0f, 10f, 20f));

        new SkyBox(this);
        player = new Player(this);
        rootNode.attachChild(player);

        slideManager = new SlideManager(this, rescaledImages, player);

        Scene scene = new Scene(this, images.size() * 4);
        rootNode.attachChild(scene);

        getStateManager().getState(BulletAppState.class).getPhysicsSpace().addCollisionListener(new SetSlidesPhysicalOnBallHitCollisionListener());

        ModeManager modeManager = new ModeManager(this, player, slideManager);
        modeManager.switchToPresentation();
    }


}
