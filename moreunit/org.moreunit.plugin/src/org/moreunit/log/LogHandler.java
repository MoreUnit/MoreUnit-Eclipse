package org.moreunit.log;

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
		System.out.println("moreunit: (EXC) "+ exception.getMessage());
		exception.printStackTrace();
	}
	
	public void handleInfoLog(String infoMessage) {
		System.out.println("moreunit: (INFO) "+infoMessage);
	}
	
	public void handleWarnLog(String warning) {
		System.out.println("moreunit (WARN) :"+warning);
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.4  2006/01/31 21:35:04  gianasista
// Small changes of the log string.
//
// Revision 1.3  2006/01/31 19:36:46  gianasista
// Add logging for warnings.
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//