package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
 */
public class SplashScreen extends JDialog {
	private static final long serialVersionUID = 8187513629570194009L;

	private JButton startButton;
	private JLabel imageLabel;
	/**
	 * This WindowAdapter enables quitting the program via the window's close
	 * button. It is activated when constructing the SplashScreen window and
	 * removed in {@link setReady}
	 */
	private WindowAdapter closeAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		};
	};

	public SplashScreen(JFrame parent) {
		super(parent, "pxchat", false);

		/**
		 * Adds the closing window listener to SplashScreen. Will be removed in
		 * {@link setReady}.
		 */
		this.addWindowListener(closeAdapter);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		startButton = new JButton(I18n.getInstance().getString("ssEnterChat"));
		startButton.setEnabled(false);
		startButton.setPreferredSize(new Dimension(200, 30));

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SplashScreen.this.dispose();
				SplashScreen.this.getParent().setVisible(true);
			}
		});

		imageLabel = new JLabel(new ImageIcon("data/img/test-pattern.png"), SwingConstants.LEFT);
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

	/**
	 * setReady enables the startButton so that (after loading the complete
	 * class {@link ClientMain}) the user can enter the main window of the
	 * program. It also removes the window listener so that the SplashScreen can
	 * only be leaved via the startButton. This approach gives the opportunity
	 * that the user can leave the program if the configuration data (./data)
	 * cannot be found and therefore the {@link ClientMain} class stops running.
	 */
	public void setReady() {
		startButton.setEnabled(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.removeWindowListener(closeAdapter);
	}
}
