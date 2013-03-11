package ca.elman.messageme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<ContactsData> {

	private final Activity context;
	private final ContactsData[] contacts;
	// contacts to whom sms would be sent
	static ArrayList<ContactsData> checkedContacts = new ArrayList<ContactsData>();
	private final List<ContactsData> listContacts;

	class ViewHolder {
		TextView namePerson;
		TextView phonePerson;
		CheckBox checkbox;
	}

	public ContactsAdapter(Activity context, int textViewResourceId,
			List<ContactsData> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.listContacts = objects;
		this.contacts = objects.toArray(new ContactsData[objects.size()]);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vHolder;

		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_of_contacts, null);

			vHolder = new ViewHolder();
			vHolder.namePerson = (TextView) convertView
					.findViewById(R.id.nameOfContact);
			vHolder.phonePerson = (TextView) convertView
					.findViewById(R.id.phone);
			vHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.cBoxContacts);

			vHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isCheckBoxChecked) {
							// TODO Auto-generated method stub
							int getPosition = (Integer) buttonView.getTag();
							listContacts.get(getPosition).setSelected(
									buttonView.isChecked());
						}

					});

			convertView.setTag(vHolder);

			convertView.setTag(R.id.nameOfContact, vHolder.namePerson);
			convertView.setTag(R.id.phone, vHolder.phonePerson);
			convertView.setTag(R.id.cBoxContacts, vHolder.checkbox);

		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		vHolder.checkbox.setTag(position); // This line is important.

		final ContactsData contactPerson = contacts[position];

		vHolder.phonePerson.setText(listContacts.get(position).getPhone());
		vHolder.namePerson.setText(listContacts.get(position).getName());
		vHolder.checkbox.setChecked(listContacts.get(position).isSelected());

		vHolder.checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (vHolder.checkbox.isChecked()) {
					checkedContacts.add(contactPerson);
				} else {
					checkedContacts.remove(contactPerson);
				}
			}
		});

		return convertView;
	}
}
