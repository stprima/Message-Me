package ca.elman.messageme;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

// main contacts activity
public class ContactsListActivity extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ContactsAdapter(ContactsListActivity.this, 0, getContactsFromPhone()));

	}

	
	public ArrayList<ContactsData> getContactsFromPhone() {
		ContentResolver cr = getContentResolver();
		ArrayList<ContactsData> contacts = new ArrayList<ContactsData>();
		Cursor cdCur = cr
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] {
								ContactsContract.CommonDataKinds.Phone._ID,
								ContactsContract.CommonDataKinds.Phone.NUMBER,
								ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
								ContactsContract.CommonDataKinds.Phone.PHOTO_FILE_ID },
						ContactsContract.CommonDataKinds.Phone.NUMBER
								+ " IS NOT NULL",
						null,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
		
		while (cdCur.moveToNext()) {
			String phone = cdCur
					.getString(cdCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			String id = cdCur
					.getString(cdCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
			String name = cdCur
					.getString(cdCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
			String picture = cdCur
					.getString(cdCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_FILE_ID));

			ContactsData personInfo = new ContactsData(id, name, phone, picture);
			contacts.add(personInfo);
		}
		cdCur.close();
		return contacts;

	}

	public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, id);
		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(cr, uri);
		if (input == null) {
			return null;
		}
		return BitmapFactory.decodeStream(input);
	}
	
}
