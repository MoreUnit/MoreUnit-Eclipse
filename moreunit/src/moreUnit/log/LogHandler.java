package moreUnit.log;

public class LogHandler {
	
	private static LogHandler instance;
	
	public static LogHandler getInstance() {
		if(instance == null)
			instance = new LogHandler();
		
		return instance;
	}
	
	private LogHandler() {
	}

	public void handleExceptionLog(Exception exception) {
		System.out.println("moreunit: "+ exception.getMessage());
		exception.printStackTrace();
	}
	
	public void handleInfoLog(String infoMessage) {
		System.out.println("moreunit: "+infoMessage);
	}
}

// $Log: not supported by cvs2svn $