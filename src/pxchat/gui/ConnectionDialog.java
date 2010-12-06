/**
 * 
 */
package pxchat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author florian
 * 
 */
public class ConnectionDialog extends JDialog {

	/**
	 * 
	 */
	private JFrame parent;

	private JButton connectButton, abortButton;
	private JTextField hostAddress, portNumber, userName;
	private JPasswordField passWord;

	public ConnectionDialog(JFrame parent) {
		super(parent, I18n.getInstance().getString("cdTitle"));
		this.parent = parent;
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
		connectButton.setBounds(10, 170, 100, 30);

		abortButton = new JButton(I18n.getInstance().getString("cdAbort"));
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.abort();
			}
		});
		abortButton.setBounds(120, 170, 100, 30);

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
		hostAddress.setBounds(10, 10, 210, 30);

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
		portNumber.setBounds(10, 50, 210, 30);

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
		userName.setBounds(10, 90, 210, 30);

		passWord = new JPasswordField(I18n.getInstance()
				.getString("cdPassWord"));
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
		passWord.setBounds(10, 130, 210, 30);

		this.add(hostAddress);
		this.add(portNumber);
		this.add(userName);
		this.add(passWord);
		this.add(connectButton);
		this.add(abortButton);

		this.setSize(230, 240);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.parent);
		this.setVisible(true);
	}

	private void connect() {
		this.dispose();
	}

	private void abort() {
		this.dispose();
	}
}
