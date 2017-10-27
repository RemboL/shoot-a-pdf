package pl.rembol.jme3.shootapdf.mode;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import java.util.function.Supplier;

abstract class SwitchingAppState extends AbstractAppState {

    private final float TIME_TO_LIVE = 2f;

    private final SimpleApplication simpleApplication;

    final ModeManager modeManager;

    private float time = 0;

    private final Vector3f startingPosition;

    private final Vector3f startingDirection;

    private final Supplier<Vector3f> finalPosition;

    private final Supplier<Vector3f> finalDirection;

    private final BitmapText bitmapText;

    SwitchingAppState(SimpleApplication simpleApplication, ModeManager modeManager,
                      Vector3f startingPosition, Vector3f startingDirection,
                      Supplier<Vector3f> finalPosition, Supplier<Vector3f> finalDirection,
                      String text) {
        this.simpleApplication = simpleApplication;
        this.modeManager = modeManager;
        this.startingPosition = startingPosition;
        this.startingDirection = startingDirection;
        this.finalPosition = finalPosition;
        this.finalDirection = finalDirection;
        bitmapText = new BitmapText(simpleApplication.getAssetManager().loadFont("Interface/Fonts/Default.fnt"));
        bitmapText.setColor(new ColorRGBA(1f, 1f, 1f, 1f));
        bitmapText.setLocalScale(3);
        bitmapText.setText(text);
        bitmapText.setLocalTranslation(simpleApplication.getCamera().getWidth() / 2 - bitmapText.getLineWidth() / 2 * 3, simpleApplication.getCamera().getHeight() / 2 + bitmapText.getHeight() / 2 * 3, 1);
        simpleApplication.getGuiNode().attachChild(bitmapText);

    }

    public void update(float tpf) {

        time += tpf;
        bitmapText.setAlpha(Math.abs(FastMath.sin(time * 8)));

        if (time < TIME_TO_LIVE) {
            updateCamera(time / TIME_TO_LIVE);
            fixLights(time / TIME_TO_LIVE);
        } else {
            finish();
            simpleApplication.getGuiNode().detachChild(bitmapText);
            simpleApplication.getStateManager().detach(this);
        }
    }

    abstract void fixLights(float time);

    abstract void finish();

    private void updateCamera(float time) {

        simpleApplication.getCamera().setLocation(startingPosition.mult(1 - time).add(finalPosition.get().mult(time)));
        simpleApplication.getCamera().lookAtDirection(startingDirection.mult(1 - time).add(finalDirection.get().mult(time)).normalize(), Vector3f.UNIT_Y);
    }
}
