package invengo.javaapi.handle;

public class LogChangeEventArgs extends EventArgs {

	public LogChangeEventArgs(String logContent, String logTime,
			String logManagedThreadId, String logFlag) {
		this.logContent = logContent;
		this.logTime = logTime;
		this.logManagedThreadId = logManagedThreadId;
		this.logFlag = logFlag;
		
	}

	private String logContent;

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	private String logTime;

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	private String logManagedThreadId;

	public String getLogManagedThreadId() {
		return logManagedThreadId;
	}

	public void setLogManagedThreadId(String logManagedThreadId) {
		this.logManagedThreadId = logManagedThreadId;
	}

	private String logFlag;

	public String getLogFlag() {
		return logFlag;
	}

	public void setLogFlag(String logFlag) {
		this.logFlag = logFlag;
	}

}
