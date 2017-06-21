package generics;

public class BuggyHunk {
	
	private String bugFixRev = "";
	private String preRev = ""; // the revision before bug fix revision
	private String file = "";
	private int startLine = -1; // start line of buggy hunk in preRev 
	private int endLine = -1; // end line of buggy hunk in preRev
	private boolean isDelOperation = false;
	public String getBugFixRev() {
		return bugFixRev;
	}
	public void setBugFixRev(String bugFixRev) {
		this.bugFixRev = bugFixRev;
	}
	public String getPreRev() {
		return preRev;
	}
	public void setPreRev(String preRev) {
		this.preRev = preRev;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public boolean isDelOperation() {
		return isDelOperation;
	}
	public void setDelOperation(boolean isDelOperation) {
		this.isDelOperation = isDelOperation;
	}	
}
