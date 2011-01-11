package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import pxchat.util.Config;
import pxchat.util.Config.Profile;

/**
 * @author Markus Holtermann
 *
 */
public class ConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 651941254209254194L;
	
	private JTabbedPane tabs;
	
	private JComboBox cbLanguages, cbDefaultProfile, cbProfile;
	private JTextField profileHostAddress, profilePortNumber, profileUserName;
	private JPasswordField profilePassWord;
	private JButton button;

	public ConfigurationDialog(ClientMain parent) {
		super(parent, I18n.getInstance().getString("cfdTitle"), true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(10,10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setPreferredSize(new Dimension(300, 300));
		
		tabs = new JTabbedPane();
		
		/**************************************************
		 * Settings Tab
		 **************************************************/
		JPanel panelSettings = new JPanel(new GridLayout(7, 2, 10, 10));
		panelSettings.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/*
		 * Language		
		 */
		panelSettings.add(new JLabel(I18n.getInstance().getString("cfdLanguage")));
		Set<Locale> langs = I18n.getInstance().getLanguages();
		Vector<String> languages = new Vector<String>();
		if (langs.size() > 0) {
			for (Locale lang : langs) {
				languages.add(lang.toString());
			}
		}
		cbLanguages = new JComboBox(languages); 
		cbLanguages.setSelectedItem(languages.contains(Config.get("language"))
				? Config.get("language")
				: Locale.ENGLISH.getDisplayLanguage(I18n.getInstance().getLocale()));
		panelSettings.add(cbLanguages);
		
		/*
		 * Default Profile		
		 */
		panelSettings.add(new JLabel(I18n.getInstance().getString("cfdDefaultProfile")));
		cbDefaultProfile = new JComboBox(Config.getProfiles());
		cbDefaultProfile.setSelectedItem(Config.getDefaultProfile());
		panelSettings.add(cbDefaultProfile);

		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		panelSettings.add(new JPanel());
		
		tabs.addTab(I18n.getInstance().getString("cfdSettings"), panelSettings);
		
		/**************************************************
		 * Profiles Tab
		 **************************************************/
		JPanel panelProfiles = new JPanel(new GridLayout(7, 2, 10, 10));
		panelProfiles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		/**
		 * profile combobox
		 */
		panelProfiles.add(new JLabel(I18n.getInstance().getString("cdProfile")));
		cbProfile = new JComboBox(Config.getProfiles());
		cbProfile.setEditable(true);
		cbProfile.setSelectedItem(Config.getDefaultProfile());
		cbProfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profile profile = (Profile) cbProfile.getSelectedItem();
				profileHostAddress.setText(profile.getHost());
				profilePortNumber.setText(profile.getPort());
				profileUserName.setText(profile.getUserName());
				profilePassWord.setText(profile.getPassword());
			}
		});
		panelProfiles.add(cbProfile);
		
		/**
		 * textfield for host
		 */
		panelProfiles.add(new JLabel(I18n.getInstance().getString("cdHost")));
		profileHostAddress = new JTextField(Config.getDefaultProfile().getHost());
		profileHostAddress.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConfigurationDialog.this.profileHostAddress.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConfigurationDialog.this.profileHostAddress.select(0, 0);
			}
		});
		panelProfiles.add(profileHostAddress);

		/**
		 * textfield for the port
		 */
		panelProfiles.add(new JLabel(I18n.getInstance().getString("cdPort")));
		profilePortNumber = new JTextField(Config.getDefaultProfile().getPort());
		profilePortNumber.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConfigurationDialog.this.profilePortNumber.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConfigurationDialog.this.profilePortNumber.select(0, 0);
			}
		});
		panelProfiles.add(profilePortNumber);

		/**
		 * textfield for the username
		 */
		panelProfiles.add(new JLabel(I18n.getInstance().getString("cdUser")));
		profileUserName = new JTextField(Config.getDefaultProfile().getUserName());
		profileUserName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConfigurationDialog.this.profileUserName.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConfigurationDialog.this.profileUserName.select(0, 0);
			}
		});
		panelProfiles.add(profileUserName);

		/**
		 * password field
		 */
		panelProfiles.add(new JLabel(I18n.getInstance().getString("cdPassWord")));
		profilePassWord = new JPasswordField(Config.getDefaultProfile().getPassword());
		profilePassWord.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && e.isControlDown()) {
					profilePassWord.setText("");
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		profilePassWord.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				ConfigurationDialog.this.profilePassWord.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				ConfigurationDialog.this.profilePassWord.select(0, 0);
			}
		});
		panelProfiles.add(profilePassWord);

		/*
		 * New Profile Button
		 */
		button = new JButton(I18n.getInstance().getString("cfdAddProfile"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		panelProfiles.add(button);
		
		/*
		 * Delete Profile Button
		 */
		button = new JButton(I18n.getInstance().getString("cfdDeleteProfile"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		panelProfiles.add(button);
		
		panelProfiles.add(new JPanel());
		panelProfiles.add(new JPanel());
		tabs.addTab(I18n.getInstance().getString("cfdProfiles"), panelProfiles);

		/*
		 * add tabs to panel
		 */
		panel.add(tabs, BorderLayout.CENTER);

		/**************************************************
		 * Buttons
		 **************************************************/
		JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/*
		 * Save Button
		 */
		button = new JButton(I18n.getInstance().getString("cfdSave"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConfigurationDialog.this.save();
				ConfigurationDialog.this.dispose();
			}
		});
		btnPanel.add(button);
		
		/*
		 * Apply Button
		 */
		button = new JButton(I18n.getInstance().getString("cfdApply"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConfigurationDialog.this.save();
			}
		});
		btnPanel.add(button);
		
		/*
		 * Cancel Button
		 */
		button = new JButton(I18n.getInstance().getString("cfdCancel"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConfigurationDialog.this.dispose();
			}
		});
		btnPanel.add(button);
		
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		this.pack();
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.getParent());
		this.setVisible(true);
		this.setResizable(false);
	}

	private void save() {
		Config.put("language", cbLanguages.getSelectedItem().toString());
		Config.put("defaultProfile", cbDefaultProfile.getSelectedItem().toString());
		Config.save();
		I18n.getInstance().updateComponents();
	}
}
