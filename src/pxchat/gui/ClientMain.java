package pxchat.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ClientMain extends JFrame{
	public static ClientMain mainWindow = null;
	public ClientMain() {
		super("pxchat");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JMenuBar mBar = new JMenuBar();
		JMenu mFile = new JMenu("Datei");
		JMenuItem mExit = new JMenuItem("Beenden");
		mExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientMain.this.dispose();
			}
		});
		mFile.add(mExit);
		mBar.add(mFile);
		this.setJMenuBar(mBar);
		this.setSize(500, 320);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width/2-this.getWidth()/2, size.height/2-this.getHeight()/2);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		mainWindow = new ClientMain();
		new SplashScreen().setVisible(true);
		
	}

}
