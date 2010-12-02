package pxchat.gui;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Internationalization {

	private Locale locale;
	private ResourceBundle bundle;

	private Internationalization() {
		setLocale(Locale.getDefault());
	}

	private static class Holder { 
		public static final Internationalization INSTANCE = new Internationalization();
	}

	public static Internationalization getInstance() {
		return Holder.INSTANCE;
	}

	public String getString(String key) {
		return (bundle == null) ? "!!" + key + "!!" : bundle.getString(key);
	}
	
	public void setLocale(Locale locale) {
		if (locale == null)
			locale = Locale.getDefault();
		this.locale = locale;
		try {
			this.bundle = ResourceBundle.getBundle("Messages", this.locale,
				new URLClassLoader(new URL[] { new File("./languages/").toURI().toURL() }));
		} catch (Exception e) {
			this.bundle = null;
			this.locale = null;
		}
	}
}
