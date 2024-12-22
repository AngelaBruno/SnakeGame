import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	
	private BufferedImage image;
	private int width;
	private int height;
	private int x;
	private int y;
	
	public Sprite(String imageFilePath) {
		try {
			this.image = ImageIO.read(new File(imageFilePath));
		} catch(IOException e) {
			System.out.println("Unable to load image at " + imageFilePath);
			e.printStackTrace();
			System.exit(1);
		}
		width = image.getWidth();
		height = image.getHeight();
		x = 0;
		y = 0;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void paint(Graphics2D brush) {
		brush.drawImage(image, x, y, width, height, null);
	}
}