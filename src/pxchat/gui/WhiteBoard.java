/**
 * 
 */
package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import pxchat.gui.whiteboard.PaintBoard;

/**
 * @author Markus H.
 * 
 */
public class WhiteBoard extends JFrame {

	public static enum Tool {
		Circle, Ellipse, Eraser, Freehand, Line, Rectangle, Text
	};

	private int sizeX = 800;
	private int sizeY = 600;

	private PaintBoard paintBoard;
	private JPanel toolbar;

	private Tool tool = Tool.Freehand;

	public WhiteBoard() {
		super(I18n.getInstance().getString("wbTitle"));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"./data/img/icon/whiteboard.png"));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new BorderLayout());

		paintBoard = new PaintBoard();
		// paintBoard.setOpaque(false);
		paintBoard.setBackground(Color.white);
		paintBoard.setPreferredSize(new Dimension(sizeX, sizeY));

		final JPopupMenu popup = new JPopupMenu();
		JMenuItem backgroundMenuItem = new JMenuItem(I18n.getInstance()
				.getString("wbBackground"));
		backgroundMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadBackgroundImage();

			}
		});
		popup.add(backgroundMenuItem);
		popup.add(new JMenuItem(I18n.getInstance().getString("wbSaveToFile")));
		popup.add(new JMenuItem(I18n.getInstance().getString("wbClear")));

		paintBoard.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
		toolbar.setPreferredSize(new Dimension(100, sizeY));

		JToggleButton drawCircle = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-circle.png"));
		drawCircle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Circle;

			}
		});
		JButton drawColor = new JButton("", new ImageIcon(
				"./data/img/icon/draw-color.png"));
		drawColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		JToggleButton drawEllipse = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-ellipse.png"));
		drawEllipse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Ellipse;

			}
		});
		JToggleButton drawEraser = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-eraser.png"));
		drawEraser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Eraser;

			}
		});
		JToggleButton drawFreehand = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-freehand.png"));
		drawFreehand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Freehand;

			}
		});
		JToggleButton drawLine = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-line.png"));
		drawLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Line;

			}
		});
		JToggleButton drawRectangle = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-rectangle.png"));
		drawRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Rectangle;

			}
		});
		JToggleButton drawText = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-text.png"));
		drawText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Text;

			}
		});
		JButton loadImage = new JButton("", new ImageIcon(
				"./data/img/icon/load-image.png"));
		loadImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadBackgroundImage();
			}
		});
		toolbar.add(drawCircle);
		toolbar.add(drawColor);
		toolbar.add(drawEllipse);
		toolbar.add(drawEraser);
		toolbar.add(drawFreehand);
		toolbar.add(drawLine);
		toolbar.add(drawRectangle);
		toolbar.add(drawText);
		toolbar.add(loadImage);
		
		ButtonGroup g = new ButtonGroup();
		g.add(drawCircle);
		g.add(drawEllipse);
		g.add(drawEraser);
		g.add(drawFreehand);
		g.add(drawLine);
		g.add(drawRectangle);
		g.add(drawText);

		this.getContentPane().add(paintBoard, BorderLayout.CENTER);
		this.getContentPane().add(toolbar, BorderLayout.WEST);
		this.pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - getSize().width) / 2,
				(d.height - getSize().height) / 2);
	}

	private void loadBackgroundImage() {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			paintBoard.loadBackground(fc.getSelectedFile());
			paintBoard.repaint();
		}
	}
}
