import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. The Document class maintains
 *         the details of the document.
 * */
public class Document implements Serializable {

	File file;
	String fileName;
	String fileCreatorIp;
	String lastSavedIp;
	ArrayList<String> currentUsers = new ArrayList<String>();

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileCreatorIp() {
		return fileCreatorIp;
	}

	public void setFileCreatorIp(String fileCreatorIp) {
		this.fileCreatorIp = fileCreatorIp;
	}

	public String getLastSavedIp() {
		return lastSavedIp;
	}

	public void setLastSavedIp(String lastSavedIp) {
		this.lastSavedIp = lastSavedIp;
	}

	Document(File file, String fileName, String fileCreatedIp) {
		this.file = file;
		this.fileName = fileName;
		this.fileCreatorIp = fileCreatedIp;
		// this.lastSavedIp = lastsavedIp;
	}

	public ArrayList<String> getCurrentUsers() {
		return currentUsers;
	}

	public void setCurrentUsers(String userIp) {
		System.out.println(userIp);
		if (!currentUsers.contains(userIp))
			currentUsers.add(userIp);
	}
}
