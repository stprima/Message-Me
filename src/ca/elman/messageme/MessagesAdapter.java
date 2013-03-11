package ca.elman.messageme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesAdapter extends ArrayAdapter<MessagesData> {

	private final Activity act;
	private final List<MessagesData> listMessages;
	public static List<MessagesData> checkedMessages;
	
	class ViewHolder {
		TextView receiversMessage;
		TextView textMessage;
		TextView timeMessage;
		CheckBox checkbox;
		ImageView icon;
	}
	
	public MessagesAdapter(Activity context, int textViewResourceId,
			List<MessagesData> objects) {
		super(context, textViewResourceId, objects);
		act = context;
		listMessages = objects;
		checkedMessages = new ArrayList<MessagesData>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder vHolder;
		
		if (convertView == null) {
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_of_all_messages, null);

			vHolder = new ViewHolder();
			vHolder.receiversMessage = (TextView) convertView.findViewById(R.id.receiversMessage);
			vHolder.textMessage = (TextView) convertView.findViewById(R.id.textMessage);
			vHolder.timeMessage = (TextView) convertView.findViewById(R.id.dayAndTimeMessage);
			vHolder.checkbox = (CheckBox) convertView.findViewById(R.id.cBoxAllMessages);
			vHolder.icon = (ImageView) convertView.findViewById(R.id.iconOfMessage1);

			vHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isCheckBoxChecked) {
							// TODO Auto-generated method stub
							int getPosition = (Integer) buttonView.getTag();
							listMessages.get(getPosition).setSelected(
									buttonView.isChecked());
						}

					});

			convertView.setTag(vHolder);

			convertView.setTag(R.id.receiversMessage, vHolder.receiversMessage);
			convertView.setTag(R.id.textMessage, vHolder.textMessage);
			convertView.setTag(R.id.dayAndTimeMessage, vHolder.timeMessage);
			convertView.setTag(R.id.cBoxAllMessages, vHolder.checkbox);
			convertView.setTag(R.id.iconOfMessage1, vHolder.icon);

		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		vHolder.checkbox.setTag(position); // This line is important.

		vHolder.receiversMessage.setText("To: " + listMessages.get(position).getReceivers());
		vHolder.textMessage.setText(listMessages.get(position).getText());
		vHolder.timeMessage.setText("At " + listMessages.get(position).getTimeAndDate() + " " + listMessages.get(position).getFrequency());
		vHolder.checkbox.setChecked(listMessages.get(position).getSelected());
		
		if (listMessages.get(position).getType().equals("Sent")) {
			vHolder.icon.setImageResource(R.drawable.sent_icon);
		}
		else if (listMessages.get(position).getType().equals("Draft")) {
			vHolder.icon.setImageResource(R.drawable.drafts_icon);
		}
		else if (listMessages.get(position).getType().equals("Scheduled")) {
			vHolder.icon.setImageResource(R.drawable.scheduled_icon);
		}

		final MessagesData messageD = listMessages.get(position);

		//remeber the items which were checked, when user scrolls down
		vHolder.checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (vHolder.checkbox.isChecked()) {
					checkedMessages.add(messageD);
				} else {
					checkedMessages.remove(messageD);
				}
			}
		});

		return convertView;
	}
	
}