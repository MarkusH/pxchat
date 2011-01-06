/**
 * 
 */
package pxchat.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import pxchat.net.Client;
import pxchat.whiteboard.DrawTextObject;

/**
 * @author florian
 * 
 */
public class TextInputDialog extends JDialog {

	private JTextArea text;
	private JLabel label;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JButton okButton, cancelButton;

	private Point startPoint;
	private Point currentPoint;
	private Color currentColor;
	private float strokeWidth;

	/**
	 * @param parent
	 */
	public TextInputDialog(WhiteBoard parent, Point startPoint, Point currentPoint,
							Color currentColor, float currentStrokeWidth) {
		super(parent, I18n.getInstance().getString("tiTitle"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.startPoint = startPoint;
		this.currentPoint = currentPoint;
		this.currentColor = currentColor;
		this.strokeWidth = currentStrokeWidth;

		label = new JLabel(I18n.getInstance().getString("tiLabel"));
		label.setBounds(10, 10, 300, 10);
		label.setPreferredSize(new Dimension(300, 20));

		text = new JTextArea();

		scrollPane = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(300, 50));

		panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2, 10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		cancelButton = new JButton(I18n.getInstance().getString("tiCancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextInputDialog.this.dispose();
			}
		});

		okButton = new JButton(I18n.getInstance().getString("tiOk"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextInputDialog.this.sendText();
			}
		});

		panel.add(label);
		panel.add(scrollPane);
		panel.add(cancelButton);
		panel.add(okButton);
		this.getContentPane().add(panel);

		this.pack();
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.getParent());
		this.setVisible(true);
	}

	private void sendText() {
		System.out.println("x " + this.startPoint.x + ", y " + this.startPoint.y + ":\n" + text.getText());
		Client.getInstance().sendPaintObject(new DrawTextObject(startPoint, currentPoint, currentColor, 300, text.getText()));
		this.dispose();
	}

}
