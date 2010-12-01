package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClientMain extends JFrame{
	public ClientMain() {
		super("pxchat");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Create Menu Bar
		JMenuBar mBar = new JMenuBar();
		JMenu mFile = new JMenu("Datei");
		JMenuItem mExit = new JMenuItem("Beenden");
		mExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientMain.this.dispose();
			}
		});
		mFile.add(mExit);
		mBar.add(mFile);
		this.setJMenuBar(mBar);
		
		//Layout
		getContentPane().setLayout(new BorderLayout(5, 5));
		JTextArea chatLog = new JTextArea("Log", 1, 30);
		chatLog.setLineWrap(true);
		chatLog.setEditable(false);
		JScrollPane chatLogPane = new JScrollPane(chatLog,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(chatLogPane, BorderLayout.WEST);

		JTextArea inputArea = new JTextArea("Input", 3, 30);
		inputArea.setSize(50, 200);
		chatLog.setLineWrap(true);
		chatLog.setEditable(true);
		JScrollPane inputAreaPane = new JScrollPane(inputArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(inputAreaPane, BorderLayout.SOUTH);
		
		JList userList = new JList(new String[] {"User", "User2"});
		chatLog.setLineWrap(true);
		chatLog.setEditable(false);
		JScrollPane userListPane = new JScrollPane(userList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(userListPane, BorderLayout.EAST);
		
		JButton sendButton = new JButton("Senden");
		sendButton.setSize(200, 30);
		getContentPane().add(sendButton, BorderLayout.CENTER);
		
		JButton whiteBoardButton = new JButton("Whiteboard");
		whiteBoardButton.setSize(200, 30);
		getContentPane().add(whiteBoardButton, BorderLayout.NORTH);
		
		this.setSize(700, 450);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width/2-this.getWidth()/2, size.height/2-this.getHeight()/2);
		
		new SplashScreen(this).setVisible(true);
	}

	/**
	 * @param args
	 * @throws InvocationTargetException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		
		//new SplashScreen().setVisible(true);
		//SwingUtilities.invokeAndWait(new Runnable() {
		//	public void run() {
		//		new SplashScreen().setVisible(true);
		//	}
		//});
		new ClientMain().setVisible(true);
	}

}
