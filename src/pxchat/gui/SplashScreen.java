package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class SplashScreen extends JFrame {
	public SplashScreen () {
		super("pxchat");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JButton startButton = new JButton("pxchat betreten");
		startButton.setPreferredSize(new Dimension(200,30));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SplashScreen.this.dispose();
				ClientMain.mainWindow.setVisible(true);
			}
		});
		JLabel imageLabel = new JLabel("",new ImageIcon("testbild.png"), SwingConstants.LEFT);
		imageLabel.setToolTipText("Prüfen Sie mittels dieses Bildes, ob Ihre Bildschirmeinstellungen stimmen.");
		this.getContentPane().add(imageLabel, BorderLayout.CENTER);
		this.getContentPane().add(startButton, BorderLayout.SOUTH);
		this.pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width/2-this.getWidth()/2, size.height/2-this.getHeight()/2);
		this.setResizable(false);
	}
}
