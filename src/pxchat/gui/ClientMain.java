package pxchat.gui;

import java.awt.Dimension;
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
		JMenu mFile = new JMenu("pxchat");
		JMenuItem mNewChat = new JMenuItem("Mit Chat verbinden…");
		mFile.add(mNewChat);
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
		getContentPane().setLayout(null);
		JTextArea chatLog = new JTextArea("Log", 1, 30);
		chatLog.setLineWrap(true);
		chatLog.setWrapStyleWord(true);
		chatLog.setEditable(false);
		JScrollPane chatLogPane = new JScrollPane(chatLog,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLogPane.setBounds(10, 10, 400, 300);
		getContentPane().add(chatLogPane);

		JTextArea inputArea = new JTextArea("Input", 3, 30);
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);
		inputArea.setEditable(true);
		JScrollPane inputAreaPane = new JScrollPane(inputArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inputAreaPane.setBounds(10, 320, 400, 100);
		getContentPane().add(inputAreaPane);
		
		JList userList = new JList(new String[] {"User", "User2"});
		userList.setEnabled(false);
		JScrollPane userListPane = new JScrollPane(userList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		userListPane.setBounds(420, 10, 150, 300);
		getContentPane().add(userListPane);
		
		JButton whiteBoardButton = new JButton("Whiteboard");
		whiteBoardButton.setBounds(420, 320, 150, 30);
		whiteBoardButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new WhiteBoard().setVisible(true);
			}
		});
		getContentPane().add(whiteBoardButton);
		
		JButton sendButton = new JButton("Senden");
		sendButton.setBounds(420, 360, 150, 30);
		getContentPane().add(sendButton);
		
		this.setSize(580, 480);
		this.setResizable(false);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width/3-this.getWidth()/2, size.height/2-this.getHeight()/2);
		
		new SplashScreen(this).setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClientMain().setVisible(true);
	}

}
