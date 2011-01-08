/**
 * 
 */
package pxchat.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import pxchat.net.Client;
import pxchat.whiteboard.DrawTextObject;

/**
 * @author Florian Bausch
 */
public class TextInputDialog extends JDialog {

	private static final long serialVersionUID = -432298055900854843L;
	
	private JTextArea text;
	private JLabel label;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JButton okButton, cancelButton;
	private JComboBox selectFont, selectFontSize;

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
		panel.setLayout(new GridLayout(3, 2, 10, 10));
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

		selectFont = new JComboBox(new String[] { "SansSerif", "Serif", "Monospaced" });

		selectFontSize = new JComboBox(
				new Integer[] { 10, 12, 16, 20, 24, 30, 36, 42, 48, 54, 60, 66, 72 });
		selectFontSize.setSelectedIndex(3);

		panel.add(label);
		panel.add(scrollPane);
		panel.add(selectFont);
		panel.add(selectFontSize);
		panel.add(okButton);
		panel.add(cancelButton);
		this.getContentPane().add(panel);

		this.pack();
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.getParent());
		this.setVisible(true);
	}

	private void sendText() {
		if (text.getText().trim().equals("")) {
			this.dispose();
			return;
		}

		int i, y, fontsize;
		y = this.startPoint.y;
		String[] newText = text.getText().split("\n");
		Font f = new Font((String) selectFont.getSelectedItem(), Font.PLAIN,
				(Integer) selectFontSize.getSelectedItem());

		FontMetrics fm = new FontMetrics(f) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5412937013584910480L;};
		fontsize = fm.getHeight();

		for (i = 0; i < newText.length; i++) {
			y += fontsize;
			Client.getInstance().sendPaintObject(
				new DrawTextObject(new Point(this.startPoint.x, y), new Point(this.currentPoint.x,
						y), currentColor, strokeWidth, newText[i], f));
		}
		this.dispose();
	}

}
