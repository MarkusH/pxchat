/**
 * 
 */
package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * @author florian
 * 
 */
public class ConnectionDialog extends JDialog {

	/**
	 * 
	 */
	private JFrame parent;

	private JButton connectButton;

	public ConnectionDialog(JFrame parent) {
		super(parent, I18n.getInstance().getString("cdTitle"));
		this.parent = parent;
		this.parent.setEnabled(false);
		this.parent.setFocusableWindowState(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		connectButton = new JButton(I18n.getInstance().getString("cdConnect"));
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ConnectionDialog.this.connect();
			}
		});
		this.add(connectButton, BorderLayout.SOUTH);

		this.pack();
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.parent);
		this.setVisible(true);
	}

	public void connect() {
		this.parent.setEnabled(true);
		this.parent.setFocusableWindowState(true);
		this.dispose();
	}

}
