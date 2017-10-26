package pl.rembol.jme3.shootapdf;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;
import pl.rembol.jme3.shootapdf.mode.ModeManager;
import pl.rembol.jme3.shootapdf.player.Player;
import pl.rembol.jme3.shootapdf.slide.SlideManager;

import java.util.List;
import java.util.stream.Collectors;


public class Main extends SimpleApplication {

    private Player player;

    private SlideManager slideManager;

    public Main() {
        super(new AppState[0]/*new StatsAppState(), new FlyCamAppState(), new AudioListenerState()*/);
    }

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

        initLights();

        AWTLoader awtLoader = new AWTLoader();
        List<Image> images = new PDFLoader().load("jme.pdf").stream().map(
                awtImage -> awtLoader.load(awtImage, true)).collect(Collectors.toList());

        BulletAppState bulletAppState = new BulletAppState();
//        bulletAppState.setDebugEnabled(true);
        getStateManager().attach(bulletAppState);
        getCamera().setLocation(new Vector3f(0f, 10f, 20f));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        slideManager = new SlideManager(this, images);

        Scene scene = new Scene(this);
        rootNode.attachChild(scene);

        getStateManager().getState(BulletAppState.class).getPhysicsSpace().addCollisionListener(new SetSlidesPhysicalOnBallHitCollisionListener());

        player = new Player(this);
        rootNode.attachChild(player);

        new ModeManager(this, player, slideManager);
    }

    private void initLights() {
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
    }


}
