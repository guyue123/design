package com.eteks.sweethome3d.game;

public class LightAttribute {
    
    /**
     * 光源名称
     */
    private String name;
    
    /**
     * 灯光尺寸：宽度, 单位：厘米
     */
    private float width;

    /**
     * 灯光尺寸：深度, 单位：厘米
     */
    private float deepth;
    
    /**
     * 灯光尺寸：高度, 单位：厘米
     */
    private float height;
    
    /**
     * 平面X坐标, 单位：厘米
     */
    private float px;
    
    /**
     * 平面Y坐标, 单位：厘米
     */
    private float py;
    
    /**
     * 悬空高度（添加上楼层高度）, 单位：厘米
     */
    private float hangHeight;
    
    /**
     * 角度
     */
    private float angle;
    
    /**
     * 亮度
     */
    private float power;
    
    /**
     * RGB颜色
     */
    private int rgbColor;
    
    /**
     * 是否可见
     */
    private boolean display;
    
    /**
     * 楼层
     */
    private int floor = 1;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getDeepth() {
        return deepth;
    }

    public void setDeepth(float deepth) {
        this.deepth = deepth;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getPx() {
        return px;
    }

    public void setPx(float px) {
        this.px = px;
    }

    public float getPy() {
        return py;
    }

    public void setPy(float py) {
        this.py = py;
    }

    public float getHangHeight() {
        return hangHeight;
    }

    public void setHangHeight(float hangHeight) {
        this.hangHeight = hangHeight;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public int getRgbColor() {
        return rgbColor;
    }

    public void setRgbColor(int rgbColor) {
        this.rgbColor = rgbColor;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "LightAttribute [name=" + name + ", width=" + width + ", deepth=" + deepth + ", height=" + height
                + ", px=" + px + ", py=" + py + ", hangHeight=" + hangHeight + ", angle=" + angle + ", power=" + power
                + ", rgbColor=" + rgbColor + ", display=" + display + ", floor=" + floor + "]";
    }
    
    /**
     * 是否是球形光源
     * @return
     */
    public boolean isSphere() {
      if (this.height == this.deepth && this.height == this.width) {
        return true;
      }
      
      return false;
    }

    
}
