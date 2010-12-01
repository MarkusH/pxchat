package pxchat.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SplashScreen extends JFrame {
	public SplashScreen () {
		super("enter pxchat");
		JButton startButton = new JButton("enter chat");
		startButton.setPreferredSize(new Dimension(200,30));
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SplashScreen.this.setVisible(false);
				ClientMain.mainWindow.setVisible(true);
			}
		});
		this.getContentPane().add(startButton);
		this.pack();
	}
}
