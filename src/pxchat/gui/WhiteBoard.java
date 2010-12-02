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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author florian
 *
 */
public class WhiteBoard extends JFrame {
	
	public static enum Tool { Pencil, Rectangle, Circle };

	private int sizeX = 800;
	private int sizeY = 600;

	private JPanel board;
	private JPanel toolbar;

	private Tool tool = Tool.Pencil;

	public WhiteBoard() {
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
		setLocation((d.width - getSize().width) / 2, 
				(d.height - getSize().height) / 2);


		setLayout(new BorderLayout());
		
		board = new JPanel();
		board.setPreferredSize(new Dimension(sizeX, sizeY));
		board.setBackground(Color.white);
		
		toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
		toolbar.setPreferredSize(new Dimension(100, sizeY));

		ButtonGroup tools = new ButtonGroup();

		JRadioButton pencil = new JRadioButton("Pencil", true);
		pencil.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tool = Tool.Pencil;
			}
		});

		JRadioButton rectangle = new JRadioButton("Rectangle");
		rectangle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tool = Tool.Rectangle;
			}
		});

		JRadioButton circle = new JRadioButton("Circle");
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

		this.getContentPane().add(board, BorderLayout.CENTER);
		this.getContentPane().add(toolbar, BorderLayout.WEST);
		this.pack();
	}

}
