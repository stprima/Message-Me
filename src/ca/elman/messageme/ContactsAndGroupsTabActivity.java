package ca.elman.messageme;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;


public class ContactsAndGroupsTabActivity extends SherlockActivity {

	private ListView listView;
	private EditText etSearch;
	private ArrayList<ContactsData> sort_contacts = new ArrayList<ContactsData>();
	private List<ContactsData> contacts;
	private Button addCont, canclCont;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.contacts_and_groups);

		listView = (ListView) findViewById(R.id.contacts_list);
		contacts = getContactsFromPhone();
		ArrayAdapter<ContactsData> adapter = new ContactsAdapter(this, 0,
				contacts);
		listView.setAdapter(adapter);

		etSearch = (EditText) findViewById(R.id.editTextSearch);

		//search field
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				int textlength = etSearch.getText().length();
				sort_contacts.clear();
				for (int i = 0; i < contacts.size(); i++) {
					if (textlength <= contacts.get(i).getName().length()) {

						if (etSearch
								.getText()
								.toString()
								.equalsIgnoreCase(
										(String) contacts.get(i).getName()
												.subSequence(0, textlength))) {
							sort_contacts.add(contacts.get(i));
						}
					}
				}

				listView.setAdapter(new ContactsAdapter(
						ContactsAndGroupsTabActivity.this, 0, sort_contacts));

			}

		});

		addCont = (Button) findViewById(R.id.addContacts);

		addCont.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent in = new Intent();
				setResult(1, in);// Here I am Setting the RequestCode to 1
				finish();
			}
		});

		canclCont = (Button) findViewById(R.id.cancelAllContacts);

		canclCont.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent();
				setResult(2, in);// Here I am Setting the RequestCode to 2
				finish();
			}

		});
	}


	private List<ContactsData> getContactsFromPhone() {
		ContentResolver cr = getContentResolver();
		ArrayList<ContactsData> contacts = new ArrayList<ContactsData>();
		Cursor cdCur = cr
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] {
								ContactsContract.CommonDataKinds.Phone._ID,
								ContactsContract.CommonDataKinds.Phone.NUMBER,
								ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY },
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


			ContactsData personInfo = new ContactsData(id, name, phone);
			contacts.add(personInfo);
		}
		cdCur.close();
		return contacts;

	}

}
