/**
 * 
 */
package pxchat.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pxchat.net.Client;
import pxchat.net.WhiteboardClientListener;
import pxchat.util.Icons;
import pxchat.util.PicFileFilter;
import pxchat.whiteboard.CircleObject;
import pxchat.whiteboard.EllipseObject;
import pxchat.whiteboard.FreeHandObject;
import pxchat.whiteboard.ImageObject;
import pxchat.whiteboard.ImageTable;
import pxchat.whiteboard.LineObject;
import pxchat.whiteboard.PaintObject;
import pxchat.whiteboard.PointObject;
import pxchat.whiteboard.RectObject;

/**
 * @author Florian Bausch
 * @author Markus Holtermann
 * 
 */
public class WhiteBoard extends JFrame {

	private static final long serialVersionUID = -1461725784646478083L;

	public static enum Tool {
		Circle, Ellipse, Eraser, Freehand, Line, Rectangle, Text, Image
	};

	private int sizeX = 1024;
	private int sizeY = 768;

	private PaintBoard paintBoard;
	private JPanel toolbar, drawColorPanel;
	private JToggleButton drawCircle, drawEllipse, drawEraser, drawFreehand, drawLine,
			drawRectangle, drawText, drawImage, lockCanvas;
	private JButton drawColor, saveImage, clearImage, loadBackground,
			loadBackgroundColor;
	private JSlider lineWidthSlider;
	private JLabel lineWidthLabel;

	private Tool tool = Tool.Freehand;
	private Color currentColor = Color.BLACK;
	private Color currentBackgroundColor = Color.WHITE;
	
	private FreeHandObject freeHand;
	private long freeHandStart;
	
	private ImageObject imageObject;
	
	private boolean isLeftButton = false;
	private boolean lock = false;
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
		super();
		I18n.getInstance().getTitleComps().put(this, "wbTitle");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("./data/img/icon/whiteboard.png"));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setResizable(false);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		paintBoard = new PaintBoard();
		paintBoard.setBackground(Color.white);
		paintBoard.setPreferredSize(new Dimension(sizeX, sizeY));

		final JPopupMenu popup = new JPopupMenu();
		final JMenuItem backgroundMenuItem = new JMenuItem(Icons.get("load-background-16.png"));
		I18n.getInstance().getTextComps().put(backgroundMenuItem, "wbBackground");
		backgroundMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadBackground();
			}
		});
		final JMenuItem saveMenuItem = new JMenuItem(Icons.get("save-16.png"));
		I18n.getInstance().getTextComps().put(saveMenuItem, "wbSaveToFile");
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();

			}
		});

		final JMenuItem clearMenuItem = new JMenuItem(Icons.get("clear-16.png"));
		I18n.getInstance().getTextComps().put(clearMenuItem, "wbClear");
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

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
				
				// Only react to left clicks
				if (e.getButton() != MouseEvent.BUTTON1) {
					isLeftButton = false;
					return;
				}
				
				isLeftButton = true;

				if (!lock) {
					startPoint = currentPoint = new Point(e.getX(), e.getY());
					switch (tool) {
						case Eraser:
						case Freehand:
							Color color = (tool == Tool.Eraser) ? new Color(0, 0, 0, 0) : currentColor;
							float strokeWidth = (tool == Tool.Eraser) ? currentStrokeWidth * 3f : currentStrokeWidth;
							Client.getInstance().sendPaintObject(new PointObject(startPoint, color, strokeWidth));
							freeHandStart = System.currentTimeMillis();
							freeHand = new FreeHandObject(new GeneralPath(), color, strokeWidth);
							freeHand.getPath().moveTo(e.getX(), e.getY());
							paintBoard.setPreviewObject(freeHand);
							break;
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
				
				// Only react to left clicks
				if (e.getButton() != MouseEvent.BUTTON1)
					return;

				if (!lock) {
					PaintObject tmp = null;
					switch (tool) {
						case Eraser:
						case Freehand:
							if (tool == Tool.Freehand)
								paintBoard.getCache().add(freeHand);
							Client.getInstance().sendPaintObject(freeHand);
							freeHand = null;
							paintBoard.setPreviewObject(null);
							paintBoard.repaint();
							break;
						case Circle:
							tmp = new CircleObject(startPoint, currentPoint, currentColor,
									currentStrokeWidth);
							paintBoard.getCache().add(tmp);
							Client.getInstance().sendPaintObject(tmp);
							paintBoard.setPreviewObject(null);
							paintBoard.repaint();
							break;
						case Ellipse:
							tmp = new EllipseObject(startPoint, currentPoint, currentColor,
									currentStrokeWidth);
							paintBoard.getCache().add(tmp);
							Client.getInstance().sendPaintObject(tmp);
							paintBoard.setPreviewObject(null);
							paintBoard.repaint();
							break;
						case Line:
							tmp = new LineObject(startPoint, currentPoint, currentColor,
									currentStrokeWidth);
							paintBoard.getCache().add(tmp);
							Client.getInstance().sendPaintObject(tmp);
							paintBoard.setPreviewObject(null);
							paintBoard.repaint();
							break;
						case Rectangle:
							tmp = new RectObject(startPoint, currentPoint, currentColor,
									currentStrokeWidth);
							paintBoard.getCache().add(tmp);
							Client.getInstance().sendPaintObject(tmp);
							paintBoard.setPreviewObject(null);
							paintBoard.repaint();
							break;
						case Image:
							currentPoint = new Point(e.getX(), e.getY());
							paintBoard.setPreviewObject(null);
							BufferedImage img = imageObject.getResizedImage();
							int id = Client.getInstance().getNextImageID();
							ImageTable.getInstance().put(id, img);
							tmp = new ImageObject(startPoint, currentPoint, id);
							paintBoard.getCache().add(tmp);
							Client.getInstance().sendPaintObject(tmp);
							Client.getInstance().sendImage(id);
							paintBoard.repaint();
							break;
					}
				}
				isLeftButton = false;
			}
		});

		paintBoard.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (!isLeftButton)
					return;
				
				if (!lock) {
					switch (tool) {
						case Eraser:
						case Freehand:
							Point newPoint = new Point(e.getX(), e.getY());
							if (newPoint.distance(currentPoint) > 10.0) {
//								Client.getInstance().sendPaintObject(new LineObject(currentPoint, 
//										newPoint, currentColor, currentStrokeWidth));
								freeHand.getPath().lineTo(newPoint.x, newPoint.y);
								if (System.currentTimeMillis() - freeHandStart > 100) {
									paintBoard.setPreviewObject(null);
									if (tool == Tool.Freehand)
										paintBoard.getCache().add(freeHand);
									Client.getInstance().sendPaintObject(freeHand);
									freeHandStart = System.currentTimeMillis();
									Color color = (tool == Tool.Eraser) ? new Color(0, 0, 0, 0) : currentColor;
									float strokeWidth = (tool == Tool.Eraser) ? currentStrokeWidth * 3f : currentStrokeWidth;
									freeHand = new FreeHandObject(new GeneralPath(), color, strokeWidth);
									freeHand.getPath().moveTo(newPoint.x, newPoint.y);		
									paintBoard.setPreviewObject(freeHand);
								}
								paintBoard.repaint();
								currentPoint = new Point(e.getX(), e.getY());
							}
							break;
						case Circle:
							currentPoint = new Point(e.getX(), e.getY());
							paintBoard.setPreviewObject(
									new CircleObject(startPoint, currentPoint, currentColor,
											currentStrokeWidth));
							paintBoard.repaint();
							break;
						case Ellipse:
							currentPoint = new Point(e.getX(), e.getY());
							paintBoard.setPreviewObject(null);
							paintBoard.setPreviewObject(
									new EllipseObject(startPoint, currentPoint, currentColor,
											currentStrokeWidth));
							paintBoard.repaint();
							break;
						case Line:
							currentPoint = new Point(e.getX(), e.getY());
							paintBoard.setPreviewObject(null);
							paintBoard.setPreviewObject(
									new LineObject(startPoint, currentPoint, currentColor,
											currentStrokeWidth));
							paintBoard.repaint();
							break;
						case Rectangle:
							currentPoint = new Point(e.getX(), e.getY());
							paintBoard.setPreviewObject(null);
							paintBoard.setPreviewObject(
									new RectObject(startPoint, currentPoint, currentColor,
											currentStrokeWidth));
							paintBoard.repaint();
							break;
						case Image:
							currentPoint = new Point(e.getX(), e.getY());
							imageObject = new ImageObject(startPoint, currentPoint, imageObject.getImage());
							paintBoard.setPreviewObject(imageObject);
							paintBoard.repaint();
							break;
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosed(WindowEvent e) {
				I18n.getInstance().getTitleComps().remove(this);
				I18n.getInstance().getToolComps().remove(drawCircle);
				I18n.getInstance().getToolComps().remove(drawEllipse);
				I18n.getInstance().getToolComps().remove(drawEraser);
				I18n.getInstance().getToolComps().remove(drawFreehand);
				I18n.getInstance().getToolComps().remove(drawLine);
				I18n.getInstance().getToolComps().remove(drawRectangle);
				I18n.getInstance().getToolComps().remove(drawText);
				I18n.getInstance().getToolComps().remove(drawImage);
				I18n.getInstance().getToolComps().remove(lockCanvas);
				
				I18n.getInstance().getToolComps().remove(drawColor);
				I18n.getInstance().getToolComps().remove(saveImage);
				I18n.getInstance().getToolComps().remove(clearImage);
				I18n.getInstance().getToolComps().remove(loadBackground);
				I18n.getInstance().getToolComps().remove(loadBackgroundColor);
				I18n.getInstance().getToolComps().remove(lineWidthSlider);
				
				I18n.getInstance().getTextComps().remove(backgroundMenuItem);
				I18n.getInstance().getTextComps().remove(saveMenuItem);
				I18n.getInstance().getTextComps().remove(clearMenuItem);
			}
		});

		toolbar = new JPanel();
		Dimension tbDimension = new Dimension(74, 358);
		toolbar.setMinimumSize(tbDimension);
		toolbar.setPreferredSize(tbDimension);
		toolbar.setMaximumSize(tbDimension);
		toolbar.setLayout(new GridLayout(9, 2, 10, 10));
		toolbar.setAlignmentY(TOP_ALIGNMENT);

		drawCircle = new JToggleButton("", Icons.get("draw-circle.png"));
		I18n.getInstance().getToolComps().put(drawCircle, "wbCircle");
		drawCircle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Circle;
			}
		});

		drawColor = new JButton("", Icons.get("draw-color.png"));
		I18n.getInstance().getToolComps().put(drawColor, "wbChangeColor");
		drawColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(WhiteBoard.this, I18n.getInstance().getString("ccDialog"), currentColor);
				if (newColor != null) {
					currentColor = newColor;
				}
				drawColorPanel.setBackground(currentColor);
			}
		});

		drawEllipse = new JToggleButton("", Icons.get("draw-ellipse.png"));
		I18n.getInstance().getToolComps().put(drawEllipse, "wbEllipse");
		drawEllipse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Ellipse;
			}
		});

		drawEraser = new JToggleButton("", Icons.get("draw-eraser.png"));
		I18n.getInstance().getToolComps().put(drawEraser, "wbEraser");
		drawEraser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Eraser;
			}
		});

		drawFreehand = new JToggleButton("", Icons.get("draw-freehand.png"));
		I18n.getInstance().getToolComps().put(drawFreehand, "wbPencil");
		drawFreehand.setSelected(true);
		drawFreehand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Freehand;
			}
		});

		drawLine = new JToggleButton("", Icons.get("draw-line.png"));
		I18n.getInstance().getToolComps().put(drawLine, "wbLine");
		drawLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Line;
			}
		});

		drawRectangle = new JToggleButton("", Icons.get("draw-rectangle.png"));
		I18n.getInstance().getToolComps().put(drawRectangle, "wbRectangle");
		drawRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Rectangle;
			}
		});

		drawText = new JToggleButton("", Icons.get("draw-text.png"));
		I18n.getInstance().getToolComps().put(drawText, "wbText");
		drawText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Text;
			}
		});
		
		drawImage = new JToggleButton("", Icons.get("load-image.png"));
		I18n.getInstance().getToolComps().put(drawImage, "wbInsertImage");
		drawImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setFileFilter(new PicFileFilter());
					if (fc.showOpenDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
						imageObject = new ImageObject(new Point(), new Point(1, 1), ImageIO.read(fc.getSelectedFile()));
						if (imageObject == null)
							throw new Exception();
						tool = Tool.Image;
					} else {
						throw new Exception();
					}
				} catch (Exception e) {
					drawFreehand.setSelected(true);
					tool = Tool.Freehand;
				}
			}
		});

		lockCanvas = new JToggleButton("", Icons.get("lock.png"));
		I18n.getInstance().getToolComps().put(lockCanvas, "wbLockCanvas");
		lockCanvas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lockCanvas.isSelected()) {
					lockCanvas.setToolTipText(I18n.getInstance().getString("wbUnlockCanvas"));
					I18n.getInstance().getToolComps().put(lockCanvas, "wbUnlockCanvas");
					lockCanvas.setIcon(Icons.get("unlock.png"));
					Client.getInstance().sendWhiteboardControlsLock(true);
				} else {
					lockCanvas.setToolTipText(I18n.getInstance().getString("wbLockCanvas"));
					I18n.getInstance().getToolComps().put(lockCanvas, "wbLockCanvas");
					lockCanvas.setIcon(Icons.get("lock.png"));
					Client.getInstance().sendWhiteboardControlsLock(false);
				}
			}
		});

		saveImage = new JButton("", Icons.get("save.png"));
		I18n.getInstance().getToolComps().put(saveImage, "wbSaveToFile");
		saveImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();
			}
		});

		clearImage = new JButton("", Icons.get("clear.png"));
		I18n.getInstance().getToolComps().put(clearImage, "wbClear");
		clearImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearImage();

			}
		});

		loadBackground = new JButton("", Icons.get("load-background.png"));
		I18n.getInstance().getToolComps().put(loadBackground, "wbBackground");
		loadBackground.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadBackground();
			}
		});

		loadBackgroundColor = new JButton("", Icons.get("background-color.png"));
		I18n.getInstance().getToolComps().put(loadBackgroundColor, "wbBackgroundColor");
		loadBackgroundColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(WhiteBoard.this, I18n.getInstance()
						.getString("ccDialog"), currentBackgroundColor);
				if (newColor != null) {
					currentBackgroundColor = newColor;
					// paintBoard.loadBackground(newColor);
					Client.getInstance().sendChangeBackground(newColor);
				}
			}
		});

		drawColorPanel = new JPanel();
		drawColorPanel.setMaximumSize(new Dimension(16, 16));
		drawColorPanel.setBackground(currentColor);

		lineWidthSlider = new JSlider(1, 10, 1);
		I18n.getInstance().getToolComps().put(lineWidthSlider, "wbLineWidth");
		lineWidthSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				currentStrokeWidth = lineWidthSlider.getValue();
				lineWidthLabel.setText("" + (int) currentStrokeWidth);
			}
		});

		lineWidthLabel = new JLabel("" + (int) currentStrokeWidth, SwingConstants.CENTER);
		lineWidthLabel.setSize(new Dimension(32, 32));
		lineWidthLabel.setVerticalAlignment(SwingConstants.CENTER);

		toolbar.add(drawColor);
		toolbar.add(drawColorPanel);

		toolbar.add(drawFreehand);
		toolbar.add(drawLine);

		toolbar.add(drawCircle);
		toolbar.add(drawEllipse);

		toolbar.add(drawRectangle);
		toolbar.add(drawText);

		toolbar.add(drawEraser);
		toolbar.add(lockCanvas);

		toolbar.add(drawImage);
		toolbar.add(saveImage);

		toolbar.add(clearImage);
		toolbar.add(new JPanel());

		toolbar.add(loadBackground);
		toolbar.add(loadBackgroundColor);

		toolbar.add(lineWidthSlider);
		toolbar.add(lineWidthLabel);

		ButtonGroup g = new ButtonGroup();
		g.add(drawCircle);
		g.add(drawEllipse);
		g.add(drawEraser);
		g.add(drawFreehand);
		g.add(drawLine);
		g.add(drawRectangle);
		g.add(drawText);
		g.add(drawImage);

		panel.add(toolbar);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(paintBoard);

		Client.getInstance().registerListener(new WhiteboardClientListener() {

			@Override
			public void backgroundChanged(Color color) {
				paintBoard.loadBackground(color);
			}

			@Override
			public void backgroundChanged(int imageID) {
				paintBoard.loadBackground(imageID);
			}

			@Override
			public void changeControlsLock(boolean lock) {
				WhiteBoard.this.lockControls(lock);
			}

			@Override
			public void paintObjectReceived(PaintObject object) {
				synchronized (paintBoard) {
					paintBoard.add(object);
				}
			}

			@Override
			public void paintRequest(boolean complete) {
//				synchronized (paintBoard) {
					paintBoard.repaint(complete);
//				}
			}

			@Override
			public void imageCleared() {
				paintBoard.clearBoard(true);
			}

		});

		this.getContentPane().add(panel);
		this.pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - getSize().width) / 2, (d.height - getSize().height) / 2);
	}

	/**
	 * Sends the command to clear the image, but does not clear the image itself.
	 */
	private void clearImage() {
		Client.getInstance().sendImageClear();
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

	public void lockControls() {
		lockControls(true);
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
		drawImage.setEnabled(!lock);
		clearImage.setEnabled(!lock);
		loadBackground.setEnabled(!lock);
		loadBackgroundColor.setEnabled(!lock);
	}

	private void saveImage() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PicFileFilter());
		if (fc.showSaveDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			String format = "png";
			String name = fc.getSelectedFile().getName();
			if (name.contains(".")) {
				format = name.substring(name.lastIndexOf(".") + 1);
			} else {
				fc.setSelectedFile(new File(fc.getSelectedFile().getAbsolutePath() + ".png"));
			}
			try {
				ImageIO.write(paintBoard.saveImage(), format, fc.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WhiteBoard.this, e.getMessage());
			}
		}
	}

	public void unlockControls() {
		lockControls(false);
	}
}
