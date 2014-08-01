import java.util.List;

public class Movies extends AbstractDBObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2927626779981126230L;
	private String id;
	private String title;
	private List<Download> download;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Download> getDownload() {
		return download;
	}

	public void setDownload(List<Download> download) {
		this.download = download;
	}

}
