package bankManagement.AttachmentType;

import java.io.File;
import java.util.HashMap;

/**
 * Wrapper for Excel, PDF and Word class
 * 
 * @author Abhinav
 * @version 25/06/25
 */
public abstract class AttachmentType {
	protected File file;
	protected String name;
	protected String content;
	protected HashMap<String, Object> detailsMap;

	/**
	 * Constructs AttachmentType object
	 * 
	 * @param name       the name of file
	 * @param content    the contents of the file
	 * @param detailsMap contains the metadata of the conteent
	 */
	public AttachmentType(String name, String content, HashMap<String, Object> detailsMap) {
		this.name = name;
		this.content = content;
		this.detailsMap = detailsMap;
	}

	/**
	 * Sends the attachment
	 * 
	 * @return File file which contains the attachment
	 */
	public File sendAttachment() {
		return this.file;
	};
}
