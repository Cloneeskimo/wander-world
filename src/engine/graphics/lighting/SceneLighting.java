package engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * Models all the lighting in a scene by serving as bundle of multiple lights
 */
public class SceneLighting {

    //Data
    private Vector3f ambientLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;
    private DirectionalLight directionalLight;

    //Accessors
    public boolean hasDirectionalLighting() { return this.directionalLight != null; }
    public Vector3f getAmbientLight() { return this.ambientLight; }
    public PointLight[] getPointLights() { return this.pointLights; }
    public SpotLight[] getSpotLights() { return spotLights; }
    public DirectionalLight getDirectionalLight() { return this.directionalLight; }

    //Mutators
    public void setAmbientLight(Vector3f ambientLight) { this.ambientLight = ambientLight; }
    public void setPointLights(PointLight[] pointLights) { this.pointLights = pointLights; }
    public void setSpotLights(SpotLight[] spotLights) { this.spotLights = spotLights; }
    public void setDirectionalLight(DirectionalLight directionalLight) { this.directionalLight = directionalLight; }
    public void setLights(Vector3f ambientLight, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        this.ambientLight = ambientLight;
        this.pointLights = pointLights;
        this.spotLights = spotLights;
        this.directionalLight = directionalLight;
    }
}
