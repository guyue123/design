
package com.eteks.sweethome3d.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BeyeImagePanel extends BeyeColorGradientPanel {

	private String filePath;
	private int posX;
	private int posY;

	private BufferedImage backgroundImage;
	private boolean isVisible;

	public BeyeImagePanel() {
		this.backgroundImage = null;
		this.posX = 0;
		this.posY = 0;
	}

	public BeyeImagePanel(String filePath) {
		this(filePath, 0, 0);
	}

	public BeyeImagePanel(String filePath, int posX, int posY) {
		this.filePath = filePath;
		this.posX = posX;
		this.posY = posY;
	}

	public BeyeImagePanel(BeyeImagePanel other) {
		this(other.getImageFilePath(), other.getImagePosX(), other.getImagePosY());
		this.isVisible = other.isImageVisible();
	}

	public void setImageFilePath(String filePath) {
		this.filePath = filePath;
		this.backgroundImage = null;
	}

	public void setImagePosX(int posX) {
		this.posX = posX;
	}

	public void setImagePosY(int posY) {
		this.posY = posY;
	}

	public void setImageVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String getImageFilePath() {
		return this.filePath;
	}

	public int getImagePosX() {
		return this.posX;
	}

	public int getImagePosY() {
		return this.posY;
	}

	public boolean isImageVisible() {
		return this.isVisible;
	}

	public void setImagePosition(int posX, int posY) {
		setImagePosX(posX);
		setImagePosY(posY);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(!isVisible) {
			return;
		}
		
		if(backgroundImage == null) {
			try {
				backgroundImage = ImageIO.read(new File(filePath));
			}
			catch(IOException ex) {
				ex.printStackTrace(System.err);
				return;
			}
		}
		
		Graphics2D g2D = (Graphics2D) g.create();
		g2D.drawImage(backgroundImage, posX, posY, null);
		g2D.dispose();

	}

}
