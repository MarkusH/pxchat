package pxchat.gui;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Internationalization {

	private Locale locale;
	private ResourceBundle bundle;

	private Internationalization() {
		locale = Locale.getDefault();
		try {
			bundle = ResourceBundle.getBundle("Messages", locale);
		} catch (Exception e) {
			bundle = null;
			locale = null;
		}
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
	
}
