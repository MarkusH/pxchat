package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import pxchat.net.Client;
import pxchat.net.ClientListener;
import pxchat.net.protocol.frames.NotificationFrame;
import pxchat.util.Icons;
import pxchat.util.Logging;
import pxchat.util.XMLUtil;
import pxchat.whiteboard.ImageTable;

/**
 * @author Florian Bausch
 * @author Markus Holtermann
 * 
 */
public class ClientMain extends JFrame {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// I18n.getInstance().setLocale(new Locale("de", "DE"));
		Icons.setFolder("./data/img/icon/");
		new ClientMain();
	}

	private Logging log;

	private JMenuBar mBar;
	private JMenu mFile, mHelp;
	private JMenuItem mNewChat, mCloseChat, mExit, mAbout;

	private JTextArea inputArea;
	private JTextPane chatLog;
	private JScrollPane chatLogPane, inputAreaPane, userListPane;
	private JList userList;

	private JButton whiteBoardButton, sendButton;
	
	private HashMap<String, String> config;
	private HashMap<String, HashMap<String, String>> profiles;

	private WhiteBoard wb = new WhiteBoard();

	private static final SimpleAttributeSet OWN = new SimpleAttributeSet(),
			FOREIGN = new SimpleAttributeSet(), OWNNAME = new SimpleAttributeSet(),
			FOREIGNNAME = new SimpleAttributeSet(), NOTIFY = new SimpleAttributeSet();

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

		/**************************************************
		 * Load the configuration file
		 **************************************************/
		config = new HashMap<String, String>();
		profiles = new HashMap<String, HashMap<String,String>>();
		/* TODO put default values here
		 * the  
		 */
		config.put("defaultProfile", "");
		
		File file = new File("data/config/client.xml");
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setErrorHandler(new ErrorHandler() {

				@Override
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					System.out.println("Fatal error validating the config file:");
					e.printStackTrace();
					System.exit(0);
				}

				@Override
				public void warning(SAXParseException e) throws SAXException {
					System.out.println("Warning validating the config file:");
					e.printStackTrace();
				}
			});
			doc = builder.parse(file);
			Node node = doc.getDocumentElement();
			
			Node configNode = XMLUtil.getChildByName(node, "config");
			NodeList listConfig = configNode.getChildNodes();
			if (listConfig != null) {
				for (int i = 0; i < listConfig.getLength(); i++) {
					config.put(XMLUtil.getAttributeValue(listConfig.item(i), "key"),
							   XMLUtil.getAttributeValue(listConfig.item(i), "value"));
				}
			}
			Node profilesNode = XMLUtil.getChildByName(node, "profiles");
			NodeList listProfiles = profilesNode.getChildNodes();
			HashMap<String, String> profile;
			if (listProfiles != null) {
				for (int i = 0; i < listProfiles.getLength(); i++) {
					if (listProfiles.item(i).getNodeName().equals("profile")) {
						profile = new HashMap<String, String>();
						profile.put("host", XMLUtil.getAttributeValue(listProfiles.item(i), "host"));
						profile.put("port", XMLUtil.getAttributeValue(listProfiles.item(i), "port"));
						profile.put("username", XMLUtil.getAttributeValue(listProfiles.item(i), "username"));
						profile.put("password", XMLUtil.getAttributeValue(listProfiles.item(i), "password"));
						profiles.put(XMLUtil.getAttributeValue(listProfiles.item(i), "name"), profile);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("An error ocurred loading the config file");
			e.printStackTrace();
			return;
		}
		
		
		/**************************************************
		 * Create Menu Bar
		 **************************************************/
		mBar = new JMenuBar();
		/**
		 * building the pxchat menu
		 */
		mFile = new JMenu("pxchat");
		mFile.setMnemonic('x');
		mNewChat = new JMenuItem(I18n.getInstance().getString("connectToChat"));
		mNewChat.setMnemonic('n');
		mNewChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConnectionDialog(ClientMain.this);
			}
		});
		mFile.add(mNewChat);

		mCloseChat = new JMenuItem(I18n.getInstance().getString("closeChat"));
		mCloseChat.setMnemonic('d');
		mCloseChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Client.getInstance().disconnect();
			}
		});
		mCloseChat.setEnabled(false);
		mFile.add(mCloseChat);

		mExit = new JMenuItem(I18n.getInstance().getString("quitProgram"), Icons.get("quit.png"));
		mExit.setMnemonic('q');
		mExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ClientMain.this.wb.dispose();
				ClientMain.this.dispose();
			}
		});
		mFile.add(mExit);

		mBar.add(mFile);
		
		/**
		 * Create language menu
		 */
		Set<Locale> langs = I18n.getInstance().getLanguages();
		if (langs.size() > 0) {
			JMenu mLang = new JMenu(I18n.getInstance().getString("language"));
			for (Locale lang : langs) {
				JMenuItem itm = new JMenuItem(lang.getDisplayLanguage(lang));
				itm.putClientProperty("locale", lang);
				itm.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						I18n.getInstance().setLocale((Locale) 
								((JComponent) e.getSource()).getClientProperty("locale"));
					}
				});
				mLang.add(itm);
			}
			mBar.add(mLang);
		}
		
		
		/**
		 * building the help menu
		 */
		mHelp = new JMenu(I18n.getInstance().getString("help"));
		mHelp.setMnemonic('h');
		mAbout = new JMenuItem(I18n.getInstance().getString("aboutInfo"), Icons.get("about.png"));
		mAbout.setMnemonic('b');
		mAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, I18n.getInstance().getString("aboutText"), I18n
						.getInstance().getString("aboutInfo"), JOptionPane.INFORMATION_MESSAGE,
					Icons.get("about-64.png"));
			}
		});
		mHelp.add(mAbout);
		mBar.add(mHelp);
		
		this.setJMenuBar(mBar);

		/**************************************************
		 * Layout
		 **************************************************/

		/**
		 * We need some panels for grouping
		 */
		JPanel panel = new JPanel(), panell = new JPanel(), panelr = new JPanel(), panelButtons = new JPanel();
		panel.setLayout(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panell.setLayout(new BorderLayout(10, 10));
		panelr.setLayout(new BorderLayout(10, 10));
		panelButtons.setLayout(new GridLayout(2, 1, 10, 10));

		/**
		 * the chat log
		 */
		chatLog = new JTextPane();
		chatLog.setEditable(false);
		chatLogPane = new JScrollPane(chatLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatLogPane.setPreferredSize(new Dimension(400, 300));

		/**
		 * the input area
		 */
		inputArea = new JTextArea("");
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);
		inputArea.setEditable(true);
		inputArea.setEnabled(false);
		inputAreaPane = new JScrollPane(inputArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		inputAreaPane.setPreferredSize(new Dimension(400, 50));
		inputArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (e.isControlDown()) {
						ClientMain.this.inputArea.append("\n");
						return;
					}
					ClientMain.this.sendMessage();
					e.setKeyCode(0);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		/**
		 * the userlist
		 */
		userList = new JList();
		userList.setEnabled(false);
		userListPane = new JScrollPane(userList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		userListPane.setPreferredSize(new Dimension(150, 300));

		/**
		 * the button to access the whiteboard
		 */
		whiteBoardButton = new JButton(I18n.getInstance().getString("whiteBoardButton"), Icons
				.get("whiteboard.png"));
		whiteBoardButton.setMnemonic('w');
		whiteBoardButton.setPreferredSize(new Dimension(150, 30));
		whiteBoardButton.setEnabled(false);
		whiteBoardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				wb.setVisible(!wb.isVisible());
			}
		});

		/**
		 * the button to send a message
		 */
		sendButton = new JButton(I18n.getInstance().getString("sendButton"), Icons.get("send.png"));
		sendButton.setMnemonic('s');
		sendButton.setPreferredSize(new Dimension(150, 30));
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ClientMain.this.sendMessage();

			}
		});

		/**
		 * finally add all panels to the program
		 */
		panell.add(inputAreaPane, BorderLayout.SOUTH);
		panell.add(chatLogPane, BorderLayout.CENTER);

		panelButtons.add(whiteBoardButton);
		panelButtons.add(sendButton);

		panelr.add(userListPane, BorderLayout.CENTER);
		panelr.add(panelButtons, BorderLayout.SOUTH);

		panel.add(panell, BorderLayout.CENTER);
		panel.add(panelr, BorderLayout.EAST);

		this.getContentPane().add(panel);

		this.pack();
		this.setResizable(false);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(size.width / 3 - this.getWidth() / 2,
			size.height / 2 - this.getHeight() / 2);

		/**
		 * now we register a client listener
		 */
		Client.getInstance().registerListener(new ClientListener() {

			@Override
			public void clientConnect(String remoteAddress) {
				chatLog.setText("");
				mCloseChat.setEnabled(true);
				mNewChat.setEnabled(false);
				whiteBoardButton.setEnabled(true);
				sendButton.setEnabled(true);
				inputArea.setEnabled(true);
				writeNotification(I18n.getInstance().getString("connectedToServer") + " " + remoteAddress);
				log = new Logging();
			}

			@Override
			public void clientDisconnect() {
				mNewChat.setEnabled(true);
				mCloseChat.setEnabled(false);
				whiteBoardButton.setEnabled(false);
				sendButton.setEnabled(false);
				inputArea.setEnabled(false);
				writeNotification(I18n.getInstance().getString("disconnectedFromServer"));
				userList.setListData(new Object[0]);
				log.endLog();
				wb.dispose();
				wb = new WhiteBoard();
				ImageTable.getInstance().clear();
			}

			@Override
			public void messageReceived(String author, String message) {
				writeMessage(author, message);
			}

			@Override
			public void notification(int type) {
				switch (type) {
					case NotificationFrame.AUTH_FAIL:
						writeNotification(I18n.getInstance().getString("authenticationFail"));
						break;
					case NotificationFrame.TIMEOUT:
						writeNotification(I18n.getInstance().getString("timeoutFail"));
						break;
					case NotificationFrame.VERSION_FAIL:
						writeNotification(I18n.getInstance().getString("versionFail"));
						break;
				}
			}

			@Override
			public void notification(int type, String username) {
				switch (type) {
					case NotificationFrame.JOIN:
						writeNotification(username + " " + I18n.getInstance().getString(
							"joinMessage"));
						log.logJoin(username);
						break;
					case NotificationFrame.LEAVE:
						writeNotification(username + " " + I18n.getInstance().getString(
							"leaveMessage"));
						log.logLeave(username);
						break;
				}
			}

			@Override
			public void userListChanged(HashMap<Integer, String> newUserList) {
				ClientMain.this.userList.setListData(newUserList.values().toArray(
					new String[newUserList.size()]));
				log.logParticipants(newUserList.values().toArray(new String[newUserList.size()]));
			}

		});

		/**
		 * and send a notifcation to the splashscreen that we are ready ;-)
		 */
		splashScreen.setReady();
	}
	
	public String getConfig(String key) {
		return config.get(key);
	}
	
	public HashMap<String, String> getProfile(String name) {
		if (profiles.get(name) != null) {
			return profiles.get(name);
		}
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("host", "localhost");
		hm.put("port", "12345");
		hm.put("username", "");
		hm.put("password", "");
		return hm;
	}
	
	public Object[] getProfileNames() {
		if (profiles.keySet() != null) {
			return profiles.keySet().toArray();
		}
		return new String[0];
	}

	private void scrollChatLog() {
		chatLog.scrollRectToVisible(new Rectangle(chatLog.getWidth() - chatLogPane.getWidth(),
				chatLog.getHeight() - chatLogPane.getHeight(), chatLogPane.getWidth(), chatLogPane
						.getHeight()));
		chatLog.scrollRectToVisible(new Rectangle(chatLog.getWidth() - chatLogPane.getWidth(),
				chatLog.getHeight() - chatLogPane.getHeight(), chatLogPane.getWidth(), chatLogPane
						.getHeight()));
	}

	private void sendMessage() {
		String msg = inputArea.getText();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

		if (!msg.trim().equals("")) {
			try {
				chatLog.getDocument()
						.insertString(
							chatLog.getDocument().getLength(),
							"[" + df.format(new Date()) + "] " + I18n.getInstance()
									.getString("you") + ": ", OWNNAME);

				chatLog.getDocument().insertString(chatLog.getDocument().getLength(), msg + "\n",
					OWN);
				this.scrollChatLog();
				Client.getInstance().sendMessage(msg);
				log.logMessage(msg, I18n.getInstance().getString("you"));

			} catch (BadLocationException e) {
				System.err.println("Could not write to JTextPane \"chatLog\".");
			}
			inputArea.setText("");
		}
	}

	public void writeMessage(String author, String msg) {
		try {
			chatLog.getDocument().insertString(chatLog.getDocument().getLength(),
				"[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + author + ": ",
				FOREIGNNAME);
			chatLog.getDocument().insertString(chatLog.getDocument().getLength(), msg + "\n",
				FOREIGN);
			this.scrollChatLog();
		} catch (BadLocationException e) {
			System.err.println("Could not write to JTextPane \"chatLog\".");
		}

		log.logMessage(msg, author);
	}

	public void writeNotification(String msg) {
		try {
			chatLog.getDocument().insertString(chatLog.getDocument().getLength(),
				"[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + msg + "\n",
				NOTIFY);
			this.scrollChatLog();
		} catch (BadLocationException e) {
			System.err.println("Could not write to JTextPane \"chatLog\".");
		}
	}
}
