/**
 * 
 */
package pxchat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import pxchat.net.Client;

/**
 * @author Florian Bausch
 * 
 */
public class ConnectionDialog extends JDialog {

	/**
	 * 
	 */

	private JButton connectButton, abortButton;
	private JTextField hostAddress, portNumber, userName;
	private JPasswordField passWord;
	private JLabel hostAddressLabel, portNumberLabel, userNameLabel, passWordLabel;

	private KeyListener returnKeyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				ConnectionDialog.this.connect();
			} else
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					ConnectionDialog.this.abort();
				}
		}
	};

	public ConnectionDialog(ClientMain parent) {
		super(parent, I18n.getInstance().getString("cdTitle"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(null);

		connectButton = new JButton(I18n.getInstance().getString("cdConnect"));
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.connect();
			}
		});
		connectButton.setBounds(35, 150, 100, 30);
		connectButton.addKeyListener(returnKeyListener);

		abortButton = new JButton(I18n.getInstance().getString("cdAbort"));
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.abort();
			}
		});
		abortButton.setBounds(145, 150, 100, 30);

		hostAddress = new JTextField(I18n.getInstance().getString("cdHost"));
		hostAddress.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.hostAddress.select(0, 0);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.hostAddress.selectAll();
			}
		});
		hostAddress.setBounds(120, 10, 150, 25);
		hostAddress.addKeyListener(returnKeyListener);

		hostAddressLabel = new JLabel(I18n.getInstance().getString("cdHost"));
		hostAddressLabel.setBounds(10, 10, 100, 25);

		portNumber = new JTextField(I18n.getInstance().getString("cdPort"));
		portNumber.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.portNumber.select(0, 0);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.portNumber.selectAll();
			}
		});
		portNumber.setBounds(120, 45, 150, 25);
		portNumber.addKeyListener(returnKeyListener);

		portNumberLabel = new JLabel(I18n.getInstance().getString("cdPort"));
		portNumberLabel.setBounds(10, 45, 100, 25);

		userName = new JTextField(I18n.getInstance().getString("cdUser"));
		userName.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.userName.select(0, 0);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.userName.selectAll();
			}
		});
		userName.setBounds(120, 80, 150, 25);
		userName.addKeyListener(returnKeyListener);

		userNameLabel = new JLabel(I18n.getInstance().getString("cdUser"));
		userNameLabel.setBounds(10, 80, 100, 25);

		passWord = new JPasswordField();
		passWord.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.passWord.select(0, 0);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.passWord.selectAll();
			}
		});
		passWord.setBounds(120, 115, 150, 25);
		passWord.addKeyListener(returnKeyListener);

		passWordLabel = new JLabel(I18n.getInstance().getString("cdPassWord"));
		passWordLabel.setBounds(10, 115, 100, 25);

		getContentPane().add(hostAddress);
		getContentPane().add(hostAddressLabel);
		getContentPane().add(portNumber);
		getContentPane().add(portNumberLabel);
		getContentPane().add(userName);
		getContentPane().add(userNameLabel);
		getContentPane().add(passWord);
		getContentPane().add(passWordLabel);
		getContentPane().add(connectButton);
		getContentPane().add(abortButton);

		this.setSize(280, 220);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.getParent());
		this.setVisible(true);
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

		try {
			Client.getInstance().connect(host, port, user, pass);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, I18n.getInstance().getString("cdConnectFail"));
			return;
		}

		this.dispose();
	}

	private void abort() {
		this.dispose();
	}
}
