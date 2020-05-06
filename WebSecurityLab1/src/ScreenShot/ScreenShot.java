package ScreenShot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
 
public class ScreenShot {
	private String fileName;
	private String defaultName="GuiCamera";
	static int serialNum=0;
	private String imageFormat;//Image file format
	private String defaultImageFormat="jpg";
	Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
	
	public  ScreenShot(){
		fileName=defaultName;
		imageFormat=defaultImageFormat;
	}
	
	public ScreenShot(String s,String format) {
		fileName=s;
		imageFormat=format;
	}
	/**
	 * Take a picture of the screen照
	 * 
	 * **/
	public void snapshot(){
		try {
			//Copy screen to a BufferedImage object screenshot
			BufferedImage screenshot=(new Robot()).createScreenCapture(
					new Rectangle(0,0,(int)d.getWidth(),(int)d.getHeight()));
			serialNum++;
			//Automatically generate file names based on file prefix variables and file format variables
			String name=fileName+String.valueOf(serialNum)+"."+imageFormat;
			System.out.println(name);
			File f=new File(name);
			System.out.println("Save File-"+name);
			//Write a screenshot object to an image file
			ImageIO.write(screenshot, imageFormat, f);
			System.out.println("..Finished");
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void SavaScreen(){
		ScreenShot cam=new ScreenShot(".\\picture\\screen","png");
		cam.snapshot();
	}

	public static File GetScreen(){
        File originalFile = new File(".\\picture\\screen"+serialNum+".png");//指定要读取的图片
        return originalFile;
	}

}