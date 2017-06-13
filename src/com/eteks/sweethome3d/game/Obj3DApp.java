/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.eteks.sweethome3d.game;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import org.lwjgl.opengl.Display;

import com.eteks.sweethome3d.model.HomeLight;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.LightSource;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;

/** Sample 3 - how to load an OBJ model, and OgreXML model, 
 * a material/texture, or text. */
public class Obj3DApp extends SimpleApplication implements ActionListener {
  
    private static final int SHADOWMAP_SIZE = 512;
    /**
     * 模型路径
     */
    private String objFilePath;
    
    /**
     * 模型灯光信息
     */
    private List<HomeLight> homeLights;
    
    private AppSettings appSettings;
    
    // 上下左右
    private boolean left=false,right=false,up=false,down=false;
    
    /**
     * 游戏者
     */
    private PhysicsCharacter player;
    
    /**
     * 游戏者位置
     */
    private float playerPosx = 23.4f;
    private float playerPosy = 20f;
    private float playerPosz = 40f;
    
    /**
     *  地面
     */
    private Geometry ground;
    private Material matGroundU;
    private Material matGroundL;
    
    private BulletAppState bulletAppState;
    
    // 模型
    private Spatial objModel;
    
    /**
     * 模型缩放比例
     */
    private float localScale = 0.1f;
    
    private int flyCamMoveSpeed = 20;
    
    /**
     * 窗口标题
     */
    private String appTitle = "3D";
    
    private Vector3f walkDirection = new Vector3f();
    
    private ChaseCamera chaserCamara;
    
    public Obj3DApp() {
      
    }
    
    public Obj3DApp(String objFilePath) {
      this.objFilePath = objFilePath;
    }
    
    public Obj3DApp(String objFilePath, List<HomeLight> homeLights) {
      this.objFilePath = objFilePath;
      this.homeLights = homeLights;
    }
    
    public void start() {
      super.start();
    }
    
    public void stop() {
      super.stop(true);
    }
    
    /**
     * 初始化模型
     */
    private void initModel() {
        File f = new File(objFilePath);
        assetManager.registerLocator(f.getParent(), FileLocator.class);
        objModel = assetManager.loadModel(f.getName());
        objModel.setLocalScale(localScale);
        objModel.setLocalTranslation(0, 2.1f, 0);
        objModel.setShadowMode(ShadowMode.CastAndReceive);
        objModel.addControl(new RigidBodyControl(0));
        rootNode.attachChild(objModel);
    }
    
    /**
     * 初始化参观者
     */
    private void initPlayer() {
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.1f, 17f, 1);
        player = new CharacterControl(capsuleShape, 0.5f);
        
        /*player = new PhysicsCharacter(new SphereCollisionShape(0.1f), .01f);*/
        player.setJumpSpeed(20);
        player.setFallSpeed(20);
        player.setGravity(30);

        player.setPhysicsLocation(new Vector3f(playerPosx, playerPosy, playerPosz));
    }
    
    /**
     * 运行
     */
    public void init() {
        this.setShowSettings(false);
        if (appSettings == null) {
            appSettings = initAppSettings();
        }
        this.setSettings(appSettings);
    }

    /**
     * 初始化设置
     * @return
     */
    public AppSettings initAppSettings() {
        appSettings = new AppSettings(true);
        appSettings.setSamples(4);
        appSettings.setTitle(appTitle);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        appSettings.setWidth(screenSize.width);
        appSettings.setHeight(screenSize.height);
        return appSettings;
    }
    
    /**
     *  初始化设置
     * @param width 宽度
     * @param height 高度
     * @param samples 抗锯齿度
     * @return
     */
    public AppSettings initAppSettings(int width, int height, int samples) {
        appSettings = new AppSettings(true);
        appSettings.setSamples(samples);
        appSettings.setWidth(width);
        appSettings.setHeight(height);
        appSettings.setTitle(appTitle);
      return appSettings;
    }

/*    @Override
    public void update() {
        super.update();
        if (Display.wasResized()) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int newWidth = Math.max(Display.getWidth(), screenSize.width);
            int newHeight = Math.max(Display.getHeight(), screenSize.height);
            reshape(newWidth, newHeight);
        }
    }*/
    
    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //Display.setResizable(true);
        initChaseCameraAppState();
        
        flyCam.setEnabled(false);
        // 移动速度
        //flyCam.setMoveSpeed(flyCamMoveSpeed);
        
        
        // 初始化环境
        envInit();
        
        // 设置快捷键
        setupKeys();
        
        // 初始化模型
        initModel();
        
        rootNode.attachChild(objModel);
        // 实体化模型
        getPhysicsSpace().add(objModel);
        
        // 参观者对象
        initPlayer();
        // 实体化参观者
        getPhysicsSpace().add(player);
        
        // 灯光
        addLight();
        
        // 环境光+环境光
        addLight2();
        
        
    }
    
    /**
     * 触发鼠标旋转
     */
    private void initChaseCameraAppState() {
        final Node camTarget = new Node("CamTarget");
        rootNode.attachChild(camTarget);

        ChaseCameraAppState chaser = new ChaseCameraAppState();
        chaser.setTarget(camTarget);
        chaser.setMaxDistance(150);
        chaser.setDefaultDistance(70);
        chaser.setDefaultHorizontalRotation(FastMath.HALF_PI);
        chaser.setMinVerticalRotation(-FastMath.PI);
        chaser.setMaxVerticalRotation(FastMath.PI * 2);
        chaser.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        stateManager.attach(chaser);
        
        chaserCamara = new ChaseCamera(cam, inputManager);
        chaserCamara.registerWithInput(inputManager);
        chaserCamara.setSmoothMotion(true);
        chaserCamara.setMaxDistance(50);
        chaserCamara.setDefaultDistance(50);
    }

    /**
     * 添加灯光
     */
    private void addLight() {
       if (homeLights == null || homeLights.isEmpty()) {
         return;
       }
       
       for (HomeLight light : homeLights) {
         float lightPower = light.getPower();
         Level level = light.getLevel();
         if (light.isVisible()
             && lightPower > 0f
             && (level == null
                 || level.isViewableAndVisible())) {
           float angle = light.getAngle();
           float cos = (float)Math.cos(angle);
           float sin = (float)Math.sin(angle);
           for (LightSource lightSource : ((HomeLight)light).getLightSources()) {
             float lightRadius = lightSource.getDiameter() != null 
                     ? lightSource.getDiameter() * light.getWidth() / 2 
                     : 3.25f; // Default radius compatible with most lights available before version 3.0
             float power = 5 * lightPower * lightPower / (lightRadius * lightRadius);
             int lightColor = lightSource.getColor();
             
             
             LightAttribute la = new LightAttribute();
             la.setAngle(angle);
             
             la.setDeepth(light.getDepth());
             la.setWidth(light.getWidth());
             la.setHeight(light.getHeight());
             
             la.setDisplay(true);
             la.setFloor(1);
             
             la.setName(light.getName());
             la.setPower(power*10);
             
             la.setPx(light.getX());
             la.setPy(light.getY());
             la.setHangHeight(light.getElevation() + light.getLevel().getElevation());
             
             la.setRgbColor(lightSource.getColor());
             
             

             PointLight pl = new PointLight();
             ColorRGBA rgba = new ColorRGBA();
             
             pl.setColor(rgba.fromIntARGB(la.getRgbColor()).mult(la.getPower()));
             pl.setRadius(20f);
             pl.setPosition(new Vector3f(la.getPx() * localScale, la.getHangHeight() * localScale +  2f, la.getPy() * localScale));
             rootNode.addLight(pl);
             
             Geometry lightMdl = new Geometry("Light", new Sphere(5, 5, la.getDeepth() * localScale / 2f));
             lightMdl.setMaterial(assetManager.loadMaterial("Common/Materials/WhiteColor.j3m"));
             lightMdl.getMesh().setStatic();
             lightMdl.setLocalTranslation(pl.getPosition());
             rootNode.attachChild(lightMdl);
             
             PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
             plsr.setLight(pl);
             plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
             plsr.setShadowIntensity(0.05f);
             plsr.setEdgesThickness(3);
             viewPort.addProcessor(plsr);
         
           }
         }
       }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
      Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
      Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
      walkDirection.set(0,0,0);
      if(left)
          walkDirection.addLocal(camLeft);
      if(right)
          walkDirection.addLocal(camLeft.negate());
      if(up)
          walkDirection.addLocal(camDir);
      if(down)
          walkDirection.addLocal(camDir.negate());
      player.setWalkDirection(walkDirection);
      cam.setLocation(player.getPhysicsLocation());
    }
    
    /**
     * 设置快捷键
     */
    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
    }
   
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if(value)
                left=true;
            else
                left=false;
        } else if (binding.equals("Rights")) {
            if(value)
                right=true;
            else
                right=false;
        } else if (binding.equals("Ups")) {
            if(value)
                up=true;
            else
                up=false;
        } else if (binding.equals("Downs")) {
            if(value)
                down=true;
            else
                down=false;
        } else if (binding.equals("Space")) {
            player.jump();
        }
    }
    
    /**
     * 初始化场景
     */
    private void envInit() {
        Box b = new Box(1000, 2, 1000);
        b.scaleTextureCoordinates(new Vector2f(10, 10));
        ground = new Geometry("soil", b);
        ground.setLocalTranslation(0, 0, 0);
        matGroundU = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matGroundU.setColor("Color", ColorRGBA.Green);

        matGroundL = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture grass = assetManager.loadTexture("resources/Textures/Terrain/splat/grass.jpeg");
        grass.setWrap(WrapMode.Repeat);
        matGroundL.setTexture("DiffuseMap", grass);

        ground.setMaterial(matGroundL);

        ground.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(ground);
        
        // 实体化地面
        getPhysicsSpace().add(ground);
        ground.addControl(new RigidBodyControl(0));

        Spatial sky = SkyFactory.createSky(assetManager, "resources/Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);
        rootNode.attachChild(sky);

    }
    
    
    /**
     * 光线和阴影
     */
    private void addLight2() {
        // 太阳光
        DirectionalLight l = new DirectionalLight();
        l.setDirection(new Vector3f(-1, -2, -1));
        
        // 太阳光阴影
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 512 * 2, 4);
        dlsr.setLight(l);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);

        rootNode.addLight(l);

        // 环境光
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.4f));

        rootNode.addLight(al);
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    public String getObjFilePath() {
      return this.objFilePath;
    }

    public void setObjFilePath(String objFilePath) {
      this.objFilePath = objFilePath;
    }

    public AppSettings getAppSettings() {
      return this.appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
      this.appSettings = appSettings;
    }

    public float getLocalScale() {
        return localScale;
    }

    public void setLocalScale(float localScale) {
        this.localScale = localScale;
    }
    
    public void setPlayerPosition(float x, float y, float z) {
        this.playerPosx = x;
        this.playerPosy = y;
        this.playerPosz = z;
    }

    public float getPlayerPosx() {
        return playerPosx;
    }

    public void setPlayerPosx(float playerPosx) {
        this.playerPosx = playerPosx;
    }

    public float getPlayerPosy() {
        return playerPosy;
    }

    public void setPlayerPosy(float playerPosy) {
        this.playerPosy = playerPosy;
    }

    public float getPlayerPosz() {
        return playerPosz;
    }

    public void setPlayerPosz(float playerPosz) {
        this.playerPosz = playerPosz;
    }

    public int getFlyCamMoveSpeed() {
        return flyCamMoveSpeed;
    }

    public void setFlyCamMoveSpeed(int flyCamMoveSpeed) {
        this.flyCamMoveSpeed = flyCamMoveSpeed;
    }

    public List<HomeLight> getHomeLights() {
      return this.homeLights;
    }

    public void setHomeLights(List<HomeLight> homeLights) {
      this.homeLights = homeLights;
    }

    public String getAppTitle() {
      return this.appTitle;
    }

    public void setAppTitle(String appTitle) {
      this.appTitle = appTitle;
    }

}
