// keywords object itself
package ca.elman.messageme;

public class KeywordsData {

	private long id;
	private String title, text;
	private Boolean selected = false;

	public KeywordsData(String kTitle, String kText) {
		title = kTitle;
		text = kText;
	}
	
	public KeywordsData(long l, String kTitle, String kText) {
		id = l;
		title = kTitle;
		text = kText;
	}
	
	public KeywordsData() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean getSelected() {
		return selected;
	}
}
