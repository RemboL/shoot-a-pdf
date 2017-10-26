package pl.rembol.jme3.shootapdf.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class Player extends Node {

    public Player(SimpleApplication simpleApplication) {
        super("player");
        setLocalTranslation(0, 0, 10);
        BetterCharacterControl player = new BetterCharacterControl(1f, 2f, 1f);
        player.setJumpForce(new Vector3f(0,5f,0));
        player.setGravity(new Vector3f(0,1f,0));
        addControl(player);
        simpleApplication.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(player);

        PlayerMovementControl playerMovementControl = new PlayerMovementControl(simpleApplication);
        addControl(playerMovementControl);
        new PlayerInputListener(simpleApplication, this);
        simpleApplication.getRootNode().attachChild(this);
    }

}
