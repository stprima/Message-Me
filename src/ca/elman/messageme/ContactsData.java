package ca.elman.messageme;

import android.graphics.Bitmap;

public class ContactsData {

	private String picture, id, phone, name;
	private Bitmap photo;
	private Boolean selected = false;

	public ContactsData(String cId, String cName, String cPhone, String cPicture, Bitmap cPhoto) {
		id = cId;
		name = cName;
		phone = cPhone;
		picture = cPicture;
		photo = cPhoto;
	}
	
	public ContactsData(String cId, String cName, String cPhone, String cPicture) {
		id = cId;
		name = cName;
		phone = cPhone;
		picture = cPicture;
	}

	public ContactsData(String cId, String cName, String cPhone) {
		id = cId;
		name = cName;
		phone = cPhone;
	}
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getPictureId() {
		return picture;
	}
	
	public Bitmap getPicture() {
		return photo;
	}
	
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
