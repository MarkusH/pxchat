/**
 * 
 */
package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import pxchat.gui.whiteboard.CircleObject;
import pxchat.gui.whiteboard.EllipseObject;
import pxchat.gui.whiteboard.LineObject;
import pxchat.gui.whiteboard.PaintBoard;
import pxchat.gui.whiteboard.RectObject;
import pxchat.util.Icons;
import pxchat.util.PicFileFilter;

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
	private JToggleButton drawCircle, drawEllipse, drawEraser, drawFreehand,
			drawLine, drawRectangle, drawText, lockCanvas;
	private JButton drawColor, loadImage, saveImage, clearImage,
			loadBackground;

	private Tool tool = Tool.Freehand;
	private Color currentColor = Color.BLACK;
	private Boolean lock = false;

	/**
	 * The coordinates of the starting point of a new paint object. It is
	 * usually set in mousePressed()
	 */
	private Point startPoint;

	/**
	 * The coordinates of the current point of a new paint object. It is usually
	 * set in mouseMoved()
	 */
	private Point currentPoint;

	/**
	 * The current width of the stroke.
	 */
	private float currentStrokeWidth = 1.0f;

	public WhiteBoard() {
		super(I18n.getInstance().getString("wbTitle"));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"./data/img/icon/whiteboard.png"));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);

		paintBoard = new PaintBoard();
		// paintBoard.setOpaque(false);
		paintBoard.setBackground(Color.white);
		paintBoard.setPreferredSize(new Dimension(sizeX, sizeY));

		final JPopupMenu popup = new JPopupMenu();
		JMenuItem backgroundMenuItem = new JMenuItem(I18n.getInstance()
				.getString("wbBackground"), Icons.get("load-background-16.png"));
		backgroundMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadBackground();
			}
		});
		JMenuItem saveMenuItem = new JMenuItem(I18n.getInstance().getString(
				"wbSaveToFile"), Icons.get("save-16.png"));
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();

			}
		});

		JMenuItem clearMenuItem = new JMenuItem(I18n.getInstance().getString(
				"wbClear"), Icons.get("clear-16.png"));
		clearMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearImage();

			}
		});

		popup.add(backgroundMenuItem);
		popup.add(saveMenuItem);
		popup.add(clearMenuItem);

		paintBoard.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);

				startPoint = currentPoint = new Point(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);

				switch (tool) {
					case Circle:
						paintBoard.getPreviewObjects().clear();
						paintBoard.repaint();
						break;
					case Ellipse:
						paintBoard.getPreviewObjects().clear();
						paintBoard.repaint();
						break;
					case Line:
						paintBoard.getPreviewObjects().clear();
						paintBoard.repaint();
						break;
					case Rectangle:
						paintBoard.getPreviewObjects().clear();
						paintBoard.repaint();
						break;
				}
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		paintBoard.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				switch (tool) {
					case Circle:
						currentPoint = new Point(e.getX(), e.getY());
						paintBoard.getPreviewObjects().clear();
						paintBoard.getPreviewObjects().add(
								new CircleObject(startPoint, currentPoint,
										currentColor, currentStrokeWidth));
						paintBoard.repaint();
						break;
					case Ellipse:
						currentPoint = new Point(e.getX(), e.getY());
						paintBoard.getPreviewObjects().clear();
						paintBoard.getPreviewObjects().add(
								new EllipseObject(startPoint, currentPoint,
										currentColor, currentStrokeWidth));
						paintBoard.repaint();
						break;
					case Line:
						currentPoint = new Point(e.getX(), e.getY());
						paintBoard.getPreviewObjects().clear();
						paintBoard.getPreviewObjects().add(
								new LineObject(startPoint, currentPoint,
										currentColor, currentStrokeWidth));
						paintBoard.repaint();
						break;
					case Rectangle:
						currentPoint = new Point(e.getX(), e.getY());
						paintBoard.getPreviewObjects().clear();
						paintBoard.getPreviewObjects().add(
								new RectObject(startPoint, currentPoint,
										currentColor, currentStrokeWidth));
						paintBoard.repaint();
						break;
				}
			}
		});

		toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
		toolbar.setPreferredSize(new Dimension(100, sizeY));

		drawCircle = new JToggleButton("", Icons.get("draw-circle.png"));
		drawCircle.setToolTipText(I18n.getInstance().getString("wbCircle"));
		drawCircle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Circle;

			}
		});
		drawColor = new JButton("", Icons.get("draw-color.png"));
		drawColor.setToolTipText(I18n.getInstance().getString("wbChangeColor"));
		drawColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(WhiteBoard.this, I18n
						.getInstance().getString("ccDialog"), currentColor);
				if (newColor != null) {
					currentColor = newColor;
				}
			}
		});
		drawEllipse = new JToggleButton("", Icons.get("draw-ellipse.png"));
		drawEllipse.setToolTipText(I18n.getInstance().getString("wbEllipse"));
		drawEllipse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Ellipse;

			}
		});
		drawEraser = new JToggleButton("", Icons.get("draw-eraser.png"));
		drawEraser.setToolTipText(I18n.getInstance().getString("wbEraser"));
		drawEraser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Eraser;

			}
		});
		drawFreehand = new JToggleButton("", Icons.get("draw-freehand.png"));
		drawFreehand.setToolTipText(I18n.getInstance().getString("wbPencil"));
		drawFreehand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Freehand;

			}
		});
		drawLine = new JToggleButton("", Icons.get("draw-line.png"));
		drawLine.setToolTipText(I18n.getInstance().getString("wbLine"));
		drawLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Line;

			}
		});

		drawRectangle = new JToggleButton("", Icons.get("draw-rectangle.png"));
		drawRectangle.setToolTipText(I18n.getInstance()
				.getString("wbRectangle"));
		drawRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Rectangle;

			}
		});
		drawText = new JToggleButton("", Icons.get("draw-text.png"));
		drawText.setToolTipText(I18n.getInstance().getString("wbText"));
		drawText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Text;

			}
		});
		lockCanvas = new JToggleButton("", Icons.get("lock.png"));
		lockCanvas.setToolTipText(I18n.getInstance().getString("wbLockCanvas"));
		lockCanvas.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lockCanvas.isSelected()) {
					lockCanvas.setToolTipText(I18n.getInstance().getString(
							"wbUnlockCanvas"));
					lockCanvas.setIcon(Icons.get("unlock.png"));
				} else {
					lockCanvas.setToolTipText(I18n.getInstance().getString(
							"wbLockCanvas"));
					lockCanvas.setIcon(Icons.get("lock.png"));
				}
			}
		});
		loadImage = new JButton("", Icons.get("load-image.png"));
		loadImage.setToolTipText(I18n.getInstance().getString("wbInsertImage"));
		loadImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				insertImage();
			}
		});
		saveImage = new JButton("", Icons.get("save.png"));
		saveImage.setToolTipText(I18n.getInstance().getString("wbSaveToFile"));
		saveImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();
			}
		});

		clearImage = new JButton("", Icons.get("clear.png"));
		clearImage.setToolTipText(I18n.getInstance().getString("wbClear"));
		clearImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearImage();

			}
		});

		loadBackground = new JButton("", Icons.get("load-background.png"));
		loadBackground.setToolTipText(I18n.getInstance().getString(
				"wbBackground"));
		loadBackground.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadBackground();
			}
		});
		toolbar.add(drawColor);
		toolbar.add(drawFreehand);
		toolbar.add(drawLine);
		toolbar.add(drawRectangle);
		toolbar.add(drawCircle);
		toolbar.add(drawEllipse);
		toolbar.add(drawText);
		toolbar.add(drawEraser);
		toolbar.add(lockCanvas);
		toolbar.add(loadImage);
		toolbar.add(saveImage);
		toolbar.add(clearImage);
		toolbar.add(loadBackground);

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

	private void clearImage() {
		paintBoard.clearBoard();
	}

	private void insertImage() {
		// TODO insert image
	}

	private void saveImage() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PicFileFilter());
		if (fc.showSaveDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			String format = "png";
			String name = fc.getSelectedFile().getName();
			if (name.contains(".")) {
				format = name.substring(name.lastIndexOf(".") + 1);
			}
			try {
				ImageIO.write(paintBoard.saveImage(), format,
						fc.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WhiteBoard.this, e.getMessage());
			}
		}
	}

	private void loadBackground() {
		// TODO need some option to set the background to a specific color
		// 1) Display a dialog to choose "Image" or "Color"
		// 2) Add an additional button to the toolbar
		// 3) Something else
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PicFileFilter());
		if (fc.showOpenDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			paintBoard.loadBackground(fc.getSelectedFile());
			paintBoard.repaint();
		}
	}

	public void lockControls(Boolean lock) {
		this.lock = lock;
		drawCircle.setEnabled(!lock);
		drawEllipse.setEnabled(!lock);
		drawEraser.setEnabled(!lock);
		drawFreehand.setEnabled(!lock);
		drawLine.setEnabled(!lock);
		drawRectangle.setEnabled(!lock);
		drawText.setEnabled(!lock);
		lockCanvas.setEnabled(!lock);
		drawColor.setEnabled(!lock);
		loadImage.setEnabled(!lock);
		clearImage.setEnabled(!lock);
		loadBackground.setEnabled(!lock);
	}

	public void lockControls() {
		lockControls(true);
	}

	public void unlockControls() {
		lockControls(false);
	}
}
