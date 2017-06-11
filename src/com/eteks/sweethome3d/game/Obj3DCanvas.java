package com.eteks.sweethome3d.game;

import java.awt.Canvas;
import java.util.concurrent.Callable;

import com.jme3.app.LegacyApplication;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class Obj3DCanvas {
  
    /**
     * 
     */
    private Canvas canvas;

    /**
     * app对象
     */
    private LegacyApplication app;
    
    /**
     * 运行参数
     */
    private AppSettings appSettings;
    
    public Obj3DCanvas(LegacyApplication app, AppSettings appSettings) {
      this.app = app;
      this.appSettings = appSettings;
    }
    
    /**
     * 创建Canvas
     */
    public Canvas createCanvas(){
        app.setPauseOnLostFocus(false);
        app.setSettings(appSettings);
        app.createCanvas();
        app.startCanvas();

        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(appSettings.getWidth(), appSettings.getHeight());
        
        return canvas;
    }

    /**
     * 启动App
     */
    public void startApp(){
        app.startCanvas();
        app.enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof SimpleApplication){
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    simpleApp.getFlyByCamera().setDragToRotate(true);
                }
                return null;
            }
        });
    }
    
    /**
     * 停止App
     */
    public void stopApp() {
      app.stop();
    }

    public Canvas getCanvas() {
      return this.canvas;
    }

    public void setCanvas(Canvas canvas) {
      this.canvas = canvas;
    }
    
    

}
