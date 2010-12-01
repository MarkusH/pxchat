package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SplashScreen extends JDialog {
	public SplashScreen (JFrame parent) {
		super(parent,"pxchat", true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JButton startButton = new JButton("pxchat betreten");
		startButton.setPreferredSize(new Dimension(200,30));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SplashScreen.this.dispose();
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
