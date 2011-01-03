/**
 * 
 */
package pxchat.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import pxchat.net.Client;
import pxchat.util.Config;
import pxchat.util.Config.Profile;

/**
 * @author Florian Bausch
 * @author Markus Holtermann
 * 
 */
public class ConnectionDialog extends JDialog {

	private static final long serialVersionUID = -3599228840090316782L;
	
	private JButton connectButton, abortButton;
	private JTextField hostAddress, portNumber, userName;
	private JPasswordField passWord;
	private JLabel profileLabel, hostAddressLabel, portNumberLabel, userNameLabel, passWordLabel;
	private JComboBox profileComboBox;

	private KeyListener returnKeyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (e.getSource() != abortButton) {
					ConnectionDialog.this.connect();
				} else {
					ConnectionDialog.this.abort();
				}
			} else
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					ConnectionDialog.this.abort();
				} 
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	};

	public ConnectionDialog(ClientMain parent) {
		super(parent, I18n.getInstance().getString("cdTitle"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2, 10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/**
		 * profile combobox
		 */
		profileLabel = new JLabel(I18n.getInstance().getString("cdProfile"));
		profileLabel.setLabelFor(profileComboBox);
		profileComboBox = new JComboBox(Config.getProfiles());
		profileComboBox.setSelectedItem(Config.getDefaultProfile());
		profileComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profile profile = (Profile) profileComboBox.getSelectedItem();
				hostAddress.setText(profile.getHost());
				portNumber.setText(profile.getPort());
				userName.setText(profile.getUserName());
				passWord.setText(profile.getPassword());
			}
		});

		/**
		 * connect button
		 */
		connectButton = new JButton(I18n.getInstance().getString("cdConnect"));
		connectButton.addKeyListener(returnKeyListener);
		connectButton.setPreferredSize(new Dimension(150, 30));
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionDialog.this.connect();
			}
		});

		/**
		 * abort button
		 */
		abortButton = new JButton(I18n.getInstance().getString("cdAbort"));
		abortButton.addKeyListener(returnKeyListener);
		abortButton.setPreferredSize(new Dimension(150, 30));
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionDialog.this.abort();
			}
		});

		/**
		 * textfield for host
		 */
		hostAddressLabel = new JLabel(I18n.getInstance().getString("cdHost"));
		hostAddressLabel.setLabelFor(hostAddress);
		hostAddress = new JTextField(Config.getDefaultProfile().getHost());
		hostAddress.addKeyListener(returnKeyListener);
		hostAddress.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConnectionDialog.this.hostAddress.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConnectionDialog.this.hostAddress.select(0, 0);
			}
		});

		/**
		 * textfield for the port
		 */
		portNumberLabel = new JLabel(I18n.getInstance().getString("cdPort"));
		portNumberLabel.setLabelFor(portNumber);
		portNumber = new JTextField(Config.getDefaultProfile().getPort());
		portNumber.addKeyListener(returnKeyListener);
		portNumber.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConnectionDialog.this.portNumber.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConnectionDialog.this.portNumber.select(0, 0);
			}
		});

		/**
		 * textfield for the username
		 */
		userNameLabel = new JLabel(I18n.getInstance().getString("cdUser"));
		userNameLabel.setLabelFor(userName);
		userName = new JTextField(Config.getDefaultProfile().getUserName());
		userName.addKeyListener(returnKeyListener);
		userName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConnectionDialog.this.userName.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConnectionDialog.this.userName.select(0, 0);
			}
		});

		/**
		 * password field
		 */
		passWordLabel = new JLabel(I18n.getInstance().getString("cdPassWord"));
		passWordLabel.setLabelFor(passWord);
		passWord = new JPasswordField(Config.getDefaultProfile().getPassword());
		passWord.addKeyListener(returnKeyListener);
		passWord.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && e.isControlDown()) {
					passWord.setText("");
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		passWord.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConnectionDialog.this.passWord.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConnectionDialog.this.passWord.select(0, 0);
			}
		});

		panel.add(profileLabel);
		panel.add(profileComboBox);
		panel.add(hostAddressLabel);
		panel.add(hostAddress);
		panel.add(portNumberLabel);
		panel.add(portNumber);
		panel.add(userNameLabel);
		panel.add(userName);
		panel.add(passWordLabel);
		panel.add(passWord);
		panel.add(connectButton);
		panel.add(abortButton);
		
		this.getContentPane().add(panel);

		this.pack();
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.getParent());
		this.setVisible(true);
	}

	private void abort() {
		this.dispose();
	}

	private void connect() {
		String host, user, pass;
		int port;
		host = hostAddress.getText();
		try {
			port = Integer.valueOf(portNumber.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, I18n.getInstance().getString("cdPortFail"));
			return;
		}
		user = userName.getText();
		pass = String.valueOf(passWord.getPassword());
		
		if (host.equals("") || user.equals("") || pass.equals("")) {
			JOptionPane.showMessageDialog(this, I18n.getInstance().getString("cdInputFail"));
			return;
		}

		try {
			Client.getInstance().connect(host, port, user, pass);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, I18n.getInstance().getString("cdConnectFail"));
			return;
		}

		this.dispose();
	}
}
