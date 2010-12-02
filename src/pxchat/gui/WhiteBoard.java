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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import pxchat.gui.whiteboard.PaintBoard;

/**
 * @author florian
 *
 */
public class WhiteBoard extends JFrame {
	
	public static enum Tool { Pencil, Rectangle, Circle };

	private int sizeX = 800;
	private int sizeY = 600;

	private PaintBoard paintBoard;
	private JPanel toolbar;

	private Tool tool = Tool.Pencil;

	public WhiteBoard() {
		super(I18n.getInstance().getString("wbTitle"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		

		paintBoard = new PaintBoard();
//		paintBoard.setOpaque(false);
		paintBoard.setBackground(Color.white);
		paintBoard.setPreferredSize(new Dimension(sizeX, sizeY));
		
		final JPopupMenu popup = new JPopupMenu();
		JMenuItem backgroundMenuItem = new JMenuItem(I18n.getInstance().getString("wbBackground"));
		backgroundMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
					paintBoard.loadBackground(fc.getSelectedFile());
					paintBoard.repaint();
				}
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
		            popup.show(e.getComponent(),
		                       e.getX(), e.getY());
		        }
		    }
		});

		
		
		toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
		toolbar.setPreferredSize(new Dimension(100, sizeY));

		ButtonGroup tools = new ButtonGroup();

		JRadioButton pencil = new JRadioButton(I18n.getInstance().getString("wbPencil"), true);
		pencil.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tool = Tool.Pencil;
			}
		});

		JRadioButton rectangle = new JRadioButton(I18n.getInstance().getString("wbRectangle"));
		rectangle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tool = Tool.Rectangle;
			}
		});

		JRadioButton circle = new JRadioButton(I18n.getInstance().getString("wbCircle"));
		circle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tool = Tool.Circle;
			}
		});
		

		tools.add(pencil);
		tools.add(rectangle);
		tools.add(circle);

		toolbar.add(pencil);
		toolbar.add(rectangle);
		toolbar.add(circle);

		this.getContentPane().add(paintBoard, BorderLayout.CENTER);
		this.getContentPane().add(toolbar, BorderLayout.WEST);
		this.pack();
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
		setLocation((d.width - getSize().width) / 2, 
				(d.height - getSize().height) / 2);
	}

}
