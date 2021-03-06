package pl.rembol.jme3.shootapdf.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class PlayerMovementControl extends AbstractControl {

    private SimpleApplication simpleApplication;

    public PlayerMovementControl(SimpleApplication simpleApplication) {
        this.simpleApplication = simpleApplication;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (getSpatial() == null && !(getSpatial() instanceof Player)) {
            return;
        }
        Player player = (Player) getSpatial();
        if (player.isControlEnabled()) {
            player.getControl(BetterCharacterControl.class).setViewDirection(simpleApplication.getCamera().getDirection());
            player.getControl(BetterCharacterControl.class).getWalkDirection().multLocal(.9f);

            simpleApplication.getCamera().setLocation(player.getCameraPosition());
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
