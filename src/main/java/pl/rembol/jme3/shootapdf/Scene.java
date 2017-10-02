package pl.rembol.jme3.shootapdf;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Scene extends Node {
    
    public Scene(Application application) {
        Box box = new Box(new Vector3f(-20f, -1f, -20f), new Vector3f(20f, 0f, 20f));
        Material material = new Material(application.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", new ColorRGBA(0f, .5f, 0f, 1f));
        Geometry geometry = new Geometry("floor", box);
        geometry.setMaterial(material);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(0f);
        geometry.addControl(rigidBodyControl);
        application.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rigidBodyControl);
        attachChild(geometry);
        
    }
}
