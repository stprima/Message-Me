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
import android.widget.TextView;

public class KeywordsAdapter extends ArrayAdapter<KeywordsData> {

	private final Activity act;
	private final List<KeywordsData> listKeywords;
	public static List<KeywordsData> checkedKeywords;
	
	class ViewHolder {
		TextView titleKeyword;
		TextView textKeyword;
		CheckBox checkbox;
	}
	
	public KeywordsAdapter(Activity context, int textViewResourceId,
			List<KeywordsData> objects) {
		super(context, textViewResourceId, objects);
		act = context;
		listKeywords = objects;
		checkedKeywords = new ArrayList<KeywordsData>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder vHolder;
		
		if (convertView == null) {
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_of_keywords, null);

			vHolder = new ViewHolder();
			vHolder.titleKeyword = (TextView) convertView
					.findViewById(R.id.titleOfKeyword);
			vHolder.textKeyword = (TextView) convertView
					.findViewById(R.id.textOfKeyword);
			vHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.cBoxKeywords);

			vHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isCheckBoxChecked) {
							// TODO Auto-generated method stub
							int getPosition = (Integer) buttonView.getTag();
							listKeywords.get(getPosition).setSelected(
									buttonView.isChecked());
						}

					});

			convertView.setTag(vHolder);

			convertView.setTag(R.id.titleOfKeyword, vHolder.titleKeyword);
			convertView.setTag(R.id.textOfKeyword, vHolder.textKeyword);
			convertView.setTag(R.id.cBoxKeywords, vHolder.checkbox);

		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		vHolder.checkbox.setTag(position); // This line is important.

		vHolder.titleKeyword.setText(listKeywords.get(position).getTitle());
		vHolder.textKeyword.setText(listKeywords.get(position).getText());
		vHolder.checkbox.setChecked(listKeywords.get(position).getSelected());
		
		final KeywordsData keywordD = listKeywords.get(position);

		vHolder.checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (vHolder.checkbox.isChecked()) {
					checkedKeywords.add(keywordD);
				} else {
					checkedKeywords.remove(keywordD);
				}
			}
		});

		return convertView;
	}
	
}
