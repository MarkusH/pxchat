package pxchat.gui;

import javax.swing.JFrame;

public class ClientMain extends JFrame{
	public static ClientMain mainWindow = null;
	public ClientMain() {
		super("pxchat");
		this.setSize(500, 320);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello World!");
		mainWindow = new ClientMain();
		new SplashScreen().setVisible(true);
		
	}

}
