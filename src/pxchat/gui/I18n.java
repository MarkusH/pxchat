package pxchat.gui;

import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.JComponent;

import pxchat.util.Config;
import pxchat.util.ExtendedControl;

/**
 * This class provides the application with translated content.
 * 
 * @author Florian Bausch
 */
public final class I18n {

	private static class Holder {
		public static final I18n INSTANCE = new I18n();
	}

	public static I18n getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * The current locale of the application.
	 */
	private Locale locale;

	/**
	 * The resource bundle associated with the locale.
	 */
	private ResourceBundle bundle;

	/**
	 * A list of components that have a text. It will be set by calling
	 * {@link AbstractButton#setText(String)}.
	 */
	private HashMap<AbstractButton, String> textComps = new HashMap<AbstractButton, String>();

	/**
	 * A list of components that have a tooltip. It will be set by calling
	 * {@link JComponent#setToolTipText(String)}.
	 */
	private HashMap<JComponent, String> toolComps = new HashMap<JComponent, String>();

	/**
	 * A list of components that have a title. It will be set by calling
	 * {@link Frame#setTitle(String)}.
	 */
	private HashMap<Frame, String> titleComps = new HashMap<Frame, String>();

	/**
	 * Constructs a new I18n object.
	 */
	private I18n() {
		setLocale(getLocaleFromString(Config.get("language")), false);
	}

	/**
	 * Returns the value associated with the specified key. If no value is
	 * available, "<code>!!key!!</code>" is returned.
	 * 
	 * @param key The key to search the value for
	 * @return The value associated with the key
	 */
	public String getString(String key) {
		try {
			return bundle.getString(key);
		} catch (Exception e) {
			return "!!" + key + "!!";
		}

	}
	
	/**
	 * @return The current locale
	 */
	public Locale getLocale() {
		return this.locale;
	}
	
	/**
	 * Sets a new locale for the application and loads the appropriate resource
	 * bundle.
	 * 
	 * @param locale The new locale
	 */
	public void setLocale(Locale locale) {
		setLocale(locale, true);
	}

	/**
	 * Sets a new locale for the application and loads the appropriate resource
	 * bundle. Updates the config, if updateConfig is <code>true</code>.
	 * 
	 * @param locale The new locale
	 * @param updateConfig <code>true</code> if the config should be updated.
	 */
	public void setLocale(Locale locale, boolean updateConfig) {
		if (locale == null)
			locale = Locale.getDefault();
		this.locale = locale;
		try {
			this.bundle = ResourceBundle.getBundle("Messages", this.locale, new URLClassLoader(
					new URL[] { new File("./data/lang/").toURI().toURL() }), new ExtendedControl());
		} catch (Exception e) {
			this.bundle = null;
			this.locale = null;
		}
		updateComponents();
		if (updateConfig) {
			Config.put("language", locale.toString());
			Config.save();
		}
	}

	/**
	 * Sets the text content of all registered components to the current locales
	 * values.
	 */
	public void updateComponents() {
		for (AbstractButton button : textComps.keySet()) {
			button.setText(getString(textComps.get(button)));
		}
		for (JComponent component : toolComps.keySet()) {
			component.setToolTipText(getString(toolComps.get(component)));
		}
		for (Frame button : titleComps.keySet()) {
			button.setTitle(getString(titleComps.get(button)));
		}
	}

	/**
	 * @return the textComps
	 */
	public HashMap<AbstractButton, String> getTextComps() {
		return textComps;
	}

	/**
	 * @return the toolComps
	 */
	public HashMap<JComponent, String> getToolComps() {
		return toolComps;
	}

	/**
	 * @return the titleComps
	 */
	public HashMap<Frame, String> getTitleComps() {
		return titleComps;
	}

	/**
	 * Retrieves the languages for which there exists a property file in
	 * /data/lang/.
	 * 
	 * @return A mapping of the locale object to its language string
	 */
	public Set<Locale> getLanguages() {
		File[] files = new File("./data/lang/").listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches("^Messages_\\w{2}(_\\w{2})?\\.properties\\.xml$");
			}
		});

		Set<Locale> languages = new HashSet<Locale>();
		for (File file : files) {
			Locale l = getLocaleFromString(file.getName().replaceAll(
					"^" + "Messages" + "(_)?|\\.properties\\.xml$", ""));
			languages.add(l);
		}
		return languages;
	}

	/**********************************************************************
	 * The following method is taken from:
	 * http://www.java2s.com/Code/Java/Network-Protocol/GetLocaleFromString.htm
	 * 
	 * Copyright (c) 2003 Erik Bengtson and others. All rights reserved.
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may
	 * not use this file except in compliance with the License. You may obtain a
	 * copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 * 
	 * Contributors: ...
	 **********************************************************************/
	/**
	 * Convert a string based locale into a Locale Object. Assumes the string
	 * has form "{language}_{country}_{variant}". Examples: "en", "de_DE",
	 * "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"
	 * 
	 * @param localeString The String
	 * @return the Locale
	 */
	private static Locale getLocaleFromString(String localeString) {
		if (localeString == null) {
			return null;
		}
		localeString = localeString.trim();
		if (localeString.toLowerCase().equals("default")) {
			return Locale.getDefault();
		}

		// Extract language
		int languageIndex = localeString.indexOf('_');
		String language = null;
		if (languageIndex == -1) {
			// No further "_" so is "{language}" only
			return new Locale(localeString, "");
		} else {
			language = localeString.substring(0, languageIndex);
		}

		// Extract country
		int countryIndex = localeString.indexOf('_', languageIndex + 1);
		String country = null;
		if (countryIndex == -1) {
			// No further "_" so is "{language}_{country}"
			country = localeString.substring(languageIndex + 1);
			return new Locale(language, country);
		} else {
			// Assume all remaining is the variant so is
			// "{language}_{country}_{variant}"
			country = localeString.substring(languageIndex + 1, countryIndex);
			String variant = localeString.substring(countryIndex + 1);
			return new Locale(language, country, variant);
		}
	}
}
