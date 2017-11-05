package pl.rembol.jme3.shootapdf.mode;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

class SwitchingToFpsAppState extends SwitchingAppState {

    SwitchingToFpsAppState(SimpleApplication simpleApplication, ModeManager modeManager) {
        super(simpleApplication, modeManager,
                modeManager.slideManager.getViewPosition(), Vector3f.UNIT_Z.negate(),
                modeManager.player::getCameraPosition, () -> modeManager.player.getWorldRotation().mult(Vector3f.UNIT_Z),
                "SWITCHING TO FPS MODE");
    }

    @Override
    void fixLights(float time) {
        modeManager.setLightDistribution(time, 1 - time);
    }


    @Override
    void finish() {
        modeManager.switchToFPS();
    }
}
