package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import pxchat.util.Config;

/**
 * @author Markus Holtermann
 *
 */
public class ConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 651941254209254194L;
	
	private JTabbedPane tabs;
	
	private JComboBox cbLanguages, cbDefaultProfile;

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
				languages.add(lang.getLanguage() +
						(lang.getCountry() != ""
							? "_" + lang.getCountry()
							: ""));
			}
		}
		cbLanguages = new JComboBox(languages); 
		cbLanguages.setSelectedItem(languages.contains(Config.get("defaultLanguage"))
				? Config.get("defaultLanguage")
				: "English");
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
		JPanel panelProfiles = new JPanel();
		tabs.addTab(I18n.getInstance().getString("cfdProfiles"), panelProfiles);
		
		panel.add(tabs, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton button;
		
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
		Config.put("defaultLanguage", cbLanguages.getSelectedItem().toString());
		Config.put("defaultProfile", cbDefaultProfile.getSelectedItem().toString());
		Config.save();
	}
}
