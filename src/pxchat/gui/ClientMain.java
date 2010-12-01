package pxchat.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class ClientMain extends JFrame{
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
		
		new SplashScreen(this).setVisible(true);
	}

	/**
	 * @param args
	 * @throws InvocationTargetException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		
		//new SplashScreen().setVisible(true);
		//SwingUtilities.invokeAndWait(new Runnable() {
		//	public void run() {
		//		new SplashScreen().setVisible(true);
		//	}
		//});
		new ClientMain().setVisible(true);
	}

}
