package pl.rembol.jme3.shootapdf.mode;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

class SwitchingToPresentationAppState extends SwitchingAppState {

    SwitchingToPresentationAppState(SimpleApplication simpleApplication, ModeManager modeManager) {
        super(simpleApplication, modeManager,
                modeManager.player.getCameraPosition(), simpleApplication.getCamera().getDirection().clone(),
                modeManager.slideManager::getViewPosition, Vector3f.UNIT_Z::negate,
                "SWITCHING TO PRESENTATION MODE");
    }


    @Override
    void fixLights(float time) {
        modeManager.setLightDistribution(1 - time, time);
    }

    @Override
    void finish() {
        modeManager.switchToPresentation();
    }
}
