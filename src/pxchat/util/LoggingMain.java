package pxchat.util;

public class LoggingMain {
	
	public static void main(String[] args) {
		Logging l = new Logging();
		l.logJoin("Peter");
		l.logJoin("Samir");
		
		l.logMessage("I hate TPS reports!", "Peter");
		l.logMessage("Didn't you get that memo!", "Samir");
		
		l.logInvite("Samir", "Michael");
		l.logJoin("Michael");
		
		l.logMessage("Why don't you go by 'Mike' instead of 'Michael'.", "Samir");
		l.logMessage("Why should I chhange my name. He is the one who sucks.", "Michael");
		
		l.logLeave("Peter");
		
		l.endLog();
	}

}
