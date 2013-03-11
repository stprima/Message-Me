package ca.elman.messageme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class KeywordsActivity extends SherlockActivity {

	
	private ListView listView;
	private Button addNewKeyword, deleteKeywords;
	private int reqCode;
	private KeywordsDataSource datasource;
	private ArrayAdapter<KeywordsData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.keywords);
		
		datasource = new KeywordsDataSource(this);
		datasource.open();

		listView = (ListView) findViewById(R.id.keywords_list);

		adapter = new KeywordsAdapter(this, 0, datasource.getAllKeywords());

		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View childView,
					int position, long id) {
				// TODO Auto-generated method stub

				if (position == 0) { 
					Toast.makeText(getApplicationContext(),
							"You can not modify First_Name keyword",
							Toast.LENGTH_SHORT).show(); 
				} 
				else {
					Intent update_keyword_intent = new Intent(
						KeywordsActivity.this, NewKeywordActivity.class);
					update_keyword_intent.putExtra("keyword_title",
							(CharSequence) datasource.getAllKeywords()
							.get(position).getTitle());
					update_keyword_intent.putExtra("keyword_text",
							(CharSequence) datasource.getAllKeywords()
							.get(position).getText());
					update_keyword_intent.putExtra("button_title", "Update");
					startActivityForResult(update_keyword_intent, reqCode);
				}
			}

		});

		addNewKeyword = (Button) findViewById(R.id.addKeyword);

		addNewKeyword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent contacts_intent = new Intent(KeywordsActivity.this,
						NewKeywordActivity.class);
				startActivityForResult(contacts_intent, reqCode);
			}

		});

		deleteKeywords = (Button) findViewById(R.id.deleteKeyword);

		deleteKeywords.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (KeywordsData kd : KeywordsAdapter.checkedKeywords) {
					if (kd.getTitle().equals("First_Name")) {
						Toast.makeText(getApplicationContext(),
								"You can not delete First_Name keyword",
								Toast.LENGTH_SHORT).show();
						kd.setSelected(false);
					} else {
						datasource.deleteKeyword(kd);
					}
				}
				KeywordsAdapter.checkedKeywords.clear();
				startActivity(getIntent());
				finish();
			}

		});
		datasource.close();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		adapter.notifyDataSetChanged();
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}
