package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Florian Bausch
 * @author Markus Holtermann
 * 
 */
public class SplashScreen extends JDialog {
	private JButton startButton;
	private JLabel imageLabel;
	private JFrame parent;

	public SplashScreen(JFrame parent) {
		super(parent, "pxchat", false);
		this.parent = parent;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(10,10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		startButton = new JButton(I18n.getInstance().getString("ssEnterChat"));
		startButton.setEnabled(false);
		startButton.setPreferredSize(new Dimension(200, 30));

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SplashScreen.this.dispose();
				SplashScreen.this.parent.setVisible(true);
			}
		});

		imageLabel = new JLabel(new ImageIcon("data/img/test-pattern.png"),
				SwingConstants.LEFT);
		imageLabel.setToolTipText(I18n.getInstance().getString("ssInfoText"));
		panel.add(imageLabel, BorderLayout.CENTER);
		panel.add(startButton, BorderLayout.SOUTH);
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width / 2 - this.getWidth() / 2,
				size.height / 2 - this.getHeight() / 2);
		this.setResizable(false);
	}

	public void setReady() {
		startButton.setEnabled(true);
	}
}
