package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ClientMain extends JFrame{
//	private Logging log = new Logging();
	
	private JMenuBar mBar;
	private JMenu mFile, mHelp;
	private JMenuItem mNewChat, mExit, mAbout;
	
	private JTextArea inputArea;
	private JTextPane chatLog;
	private JScrollPane chatLogPane, inputAreaPane, userListPane;
	private JList userList;
	private JButton whiteBoardButton, sendButton;
	
	private WhiteBoard wb = new WhiteBoard();
	
	private static SimpleAttributeSet OWN = new SimpleAttributeSet(), FOREIGN = new SimpleAttributeSet(),
		OWNNAME = new SimpleAttributeSet(), FOREIGNNAME = new SimpleAttributeSet(),
		NOTIFY = new SimpleAttributeSet();
	static {
		StyleConstants.setForeground(OWN, Color.blue);
		StyleConstants.setFontSize(OWN, 12);
		
		StyleConstants.setForeground(FOREIGN, Color.black);
		StyleConstants.setFontSize(FOREIGN, 12);
		
		StyleConstants.setForeground(OWNNAME, Color.blue);
		StyleConstants.setFontSize(OWNNAME, 12);
		StyleConstants.setBold(OWNNAME, true);
		
		StyleConstants.setForeground(FOREIGNNAME, Color.black);
		StyleConstants.setFontSize(FOREIGNNAME, 12);
		StyleConstants.setBold(FOREIGNNAME, true);
		
		StyleConstants.setForeground(NOTIFY, Color.gray);
		StyleConstants.setFontSize(NOTIFY, 12);
		StyleConstants.setItalic(NOTIFY, true);
	}
	
	public ClientMain() {
		super("pxchat");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("./data/img/icon/whiteboard.png"));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		SplashScreen splashScreen = new SplashScreen(this);
		splashScreen.setVisible(true);
		
		//Create Menu Bar
		mBar = new JMenuBar();
		/**
		 * building the pxchat menu
		 */
		mFile = new JMenu("pxchat");
		mNewChat = new JMenuItem(I18n.getInstance().getString("connectToChat"));
		mFile.add(mNewChat);
		mExit = new JMenuItem(I18n.getInstance().getString("quitProgram"), new ImageIcon("./data/img/icon/quit.png"));
		mExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientMain.this.wb.dispose();
				ClientMain.this.dispose();
			}
		});
		mFile.add(mExit);
		mBar.add(mFile);
		/**
		 * building the help menu
		 */
		mHelp = new JMenu(I18n.getInstance().getString("help"));
		mAbout = new JMenuItem(I18n.getInstance().getString("aboutInfo"), new ImageIcon("./data/img/icon/about.png"));
		mAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, I18n.getInstance().getString("aboutText"), I18n.getInstance().getString("aboutInfo"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon("./data/img/icon/about-64.png"));
			}
		});
		mHelp.add(mAbout);
		mBar.add(mHelp);
		this.setJMenuBar(mBar);
		
		//Layout
		getContentPane().setLayout(null);
		
		chatLog = new JTextPane();
		chatLog.setEditable(false);
		chatLogPane = new JScrollPane(chatLog,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLogPane.setBounds(10, 10, 400, 300);
		getContentPane().add(chatLogPane);

		inputArea = new JTextArea("", 3, 30);
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);
		inputArea.setEditable(true);
		inputArea.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(e.isControlDown()) {
						ClientMain.this.inputArea.append("\n");
						return;
					}
					ClientMain.this.sendMessage();
					e.setKeyCode(0);
				}
			}
		});
		inputAreaPane = new JScrollPane(inputArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inputAreaPane.setBounds(10, 320, 400, 100);
		getContentPane().add(inputAreaPane);
		
		userList = new JList(new String[] {"User", "User2"});
		userList.setEnabled(false);
		userListPane = new JScrollPane(userList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		userListPane.setBounds(420, 10, 150, 300);
		getContentPane().add(userListPane);
		
		whiteBoardButton = new JButton(I18n.getInstance().getString("whiteBoardButton"), new ImageIcon("./data/img/icon/whiteboard.png"));
		whiteBoardButton.setBounds(420, 320, 150, 30);
		whiteBoardButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				wb.setVisible(!wb.isVisible());
			}
		});
		getContentPane().add(whiteBoardButton);
		
		sendButton = new JButton(I18n.getInstance().getString("sendButton"), new ImageIcon("./data/img/icon/send.png"));
		sendButton.setBounds(420, 360, 150, 30);
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ClientMain.this.sendMessage();
				
			}
		});
		getContentPane().add(sendButton);
		
		this.setSize(580, 480);
		this.setResizable(false);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width/3-this.getWidth()/2, size.height/2-this.getHeight()/2);
		splashScreen.setReady();
	}
	
	public void writeMessage(String author, String msg, String time) {
		try {
			chatLog.getDocument().insertString(chatLog.getDocument().getLength(), "[" + time + "] " + author, FOREIGNNAME);
			chatLog.getDocument().insertString(chatLog.getDocument().getLength(), msg + "\n", FOREIGN);
		} catch(BadLocationException e) {
      		System.err.println("Could not write to JTextPane \"chatLog\".");
		}
		
//		log.logMessage(msg, author);
	}
	
	public void writeNotification(String msg, String time) {
		try {
			chatLog.getDocument().insertString(chatLog.getDocument().getLength(), "[" + time + "] " + msg + "\n", NOTIFY);
		} catch(BadLocationException e) {
      		System.err.println("Could not write to JTextPane \"chatLog\".");
		}
	}
	
	private void sendMessage() {
		String msg = inputArea.getText();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

		if(!msg.trim().equals("")) {
			try {
				chatLog.getDocument().insertString(chatLog.getDocument().getLength(), "[" + df.format(new Date()) + "] "
						+ I18n.getInstance().getString("you") + ": ", OWNNAME);
				chatLog.getDocument().insertString(chatLog.getDocument().getLength(), msg + "\n", OWN);
				// TODO msg über Netzwerk versenden
			
			} catch(BadLocationException e) {
	      		System.err.println("Could not write to JTextPane \"chatLog\".");
			}
//			log.logMessage(msg, "Sie");
			inputArea.setText("");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		I18n.getInstance().setLocale(new Locale("de", "DE"));
		new ClientMain();
	}

}
