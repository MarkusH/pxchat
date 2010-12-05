package pxchat.gui;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {

	private Locale locale;
	private ResourceBundle bundle;

	private I18n() {
		setLocale(Locale.getDefault());
	}

	private static class Holder {
		public static final I18n INSTANCE = new I18n();
	}

	public static I18n getInstance() {
		return Holder.INSTANCE;
	}

	public String getString(String key) {
		try {
			return (bundle == null) ? "!!" + key + "!!" : bundle.getString(key);
		} catch (Exception e) {
			return "!!" + key + "!!";
		}

	}

	public void setLocale(Locale locale) {
		if (locale == null)
			locale = Locale.getDefault();
		this.locale = locale;
		try {
			this.bundle = ResourceBundle.getBundle("Messages", this.locale,
					new URLClassLoader(new URL[] { new File("./data/lang/")
							.toURI().toURL() }));
		} catch (Exception e) {
			this.bundle = null;
			this.locale = null;
		}
	}
}
