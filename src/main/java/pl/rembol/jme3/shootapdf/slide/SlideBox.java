package pl.rembol.jme3.shootapdf.slide;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

public class SlideBox extends Node {

    private static Material sideMaterial;

    public SlideBox(Application application, Image image, Vector3f position, float size, float texOffsetX,
                    float texOffsetY,
                    float texSize) {
        Texture2D boardTexture = new Texture2D(image);
        Material textureMaterial = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        textureMaterial.setTexture("ColorMap", boardTexture);

        Quad frontQuad = new Quad(size, size);
        frontQuad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{ texOffsetX, texOffsetY,
                texOffsetX + texSize, texOffsetY,
                texOffsetX + texSize, texOffsetY + texSize,
                texOffsetX, texOffsetY + texSize });
        Geometry front = new Geometry("front", frontQuad);
        front.setLocalTranslation(-size / 2, -size / 2, size / 2);
        front.setMaterial(textureMaterial);
        attachChild(front);

        Quad backQuad = new Quad(size, size);
        backQuad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{ texOffsetX + texSize, texOffsetY,
                texOffsetX, texOffsetY,
                texOffsetX, texOffsetY + texSize,
                texOffsetX + texSize, texOffsetY + texSize });
        Geometry back = new Geometry("back", backQuad);
        back.setLocalTranslation(size / 2, -size / 2, -size / 2);
        back.setLocalScale(new Vector3f(-1, 1, 1));
        back.setMaterial(textureMaterial);
        attachChild(back);

        Quad side = new Quad(size, size);
        Geometry right = new Geometry("right", side);
        right.setLocalTranslation(size / 2, -size / 2, size / 2);
        right.rotate(0, FastMath.HALF_PI, 0);
        right.setMaterial(getSideMaterial(application));
        attachChild(right);

        Geometry left = new Geometry("left", side);
        left.setLocalTranslation(-size / 2, -size / 2, -size / 2);
        left.rotate(0, -FastMath.HALF_PI, 0);
        left.setMaterial(getSideMaterial(application));
        attachChild(left);

        Geometry top = new Geometry("top", side);
        top.setLocalTranslation(-size / 2, size / 2, size / 2);
        top.rotate(-FastMath.HALF_PI, 0, 0);
        top.setMaterial(getSideMaterial(application));
        attachChild(top);

        Geometry bottom = new Geometry("bottom", side);
        bottom.setLocalTranslation(-size / 2, -size / 2, -size / 2);
        bottom.rotate(FastMath.HALF_PI, 0, 0);
        bottom.setMaterial(getSideMaterial(application));
        attachChild(bottom);

        setLocalTranslation(
                position.x + (texOffsetX * Slide.SIZE) * 2,
                position.y + (texOffsetY * Slide.SIZE) * 2,
                position.z);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(
                new BoxCollisionShape(Vector3f.UNIT_XYZ.mult(size / 2)), 1f);
        rigidBodyControl.setKinematic(true);

        addControl(rigidBodyControl);
        application.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rigidBodyControl);
    }

    private Material getSideMaterial(Application application) {
        if (sideMaterial == null) {
            sideMaterial = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            sideMaterial.setColor("Color", ColorRGBA.Brown);
        }
        return sideMaterial;
    }
    
    protected void setKinematic(boolean kinematic) {
        getControl(RigidBodyControl.class).setKinematic(kinematic);
    }

}
