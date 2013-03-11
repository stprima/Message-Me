package ca.elman.messageme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class NewKeywordActivity extends SherlockActivity {

	private EditText title, text;
	private Button save, cancel;
	private KeywordsDataSource datasource;
	private Bundle extras;
	private CharSequence previousTitle;
	private KeywordsData oldKD = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_keyword);
		
		datasource = new KeywordsDataSource(this);
		datasource.open();

		title = (EditText) findViewById(R.id.editTitleKeyword);
		text = (EditText) findViewById(R.id.editTextKeyword);
		save = (Button) findViewById(R.id.saveKeyword);

		//check if we either need to update keyword or create new one
		extras = getIntent().getExtras();
		if (extras != null) {
			try {
				previousTitle = extras.getCharSequence("keyword_title");
				title.setText(previousTitle);
				text.setText(extras.getCharSequence("keyword_text"));
				save.setText(extras.getCharSequence("button_title"));
				//get pointer to keyword
				for (KeywordsData kd : datasource.getAllKeywords()) {
					if (kd.getTitle().equals(previousTitle)) {
						oldKD = kd;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (title.getText().toString().matches("")) {
					Toast.makeText(getApplicationContext(),
							"Please add a title", Toast.LENGTH_LONG).show();
					return;
				}

				if (title.getText().toString().contains(" ")) {
					Toast.makeText(getApplicationContext(),
							"Spaces are not allowed in Title",
							Toast.LENGTH_LONG).show();
					return;
				}

				//check if possible duplicate already exists
				for (KeywordsData kd : datasource.getAllKeywords()) {
					if (kd.getTitle().matches(title.getText().toString())
							&& ((save.getText().toString().equals("Save")) 
									|| (save.getText().toString().equals("Update") 
											&& !title.getText().toString().equals(previousTitle)))) {
						
						Toast.makeText(getApplicationContext(),
								"You can not duplicate titles of keywords",
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				
				if (text.getText().toString().matches("")) {
					Toast.makeText(getApplicationContext(),
							"Please add a text", Toast.LENGTH_LONG).show();
					return;
				}

				if (save.getText().toString().equals("Save")) {
					KeywordsData newKD = new KeywordsData(title.getText()
							.toString(), text.getText().toString());
					datasource.createKeyword(newKD);
				}

				// update keyword in case Button's name is "Update" 
				else if (save.getText().toString().equals("Update")) {
					datasource.deleteKeyword(oldKD);
					KeywordsData newKD = new KeywordsData();
					newKD.setTitle(title.getText().toString());
					newKD.setText(text.getText().toString());
					datasource.createKeyword(newKD);
				}

				datasource.close();
				Intent in = new Intent();
				setResult(50, in);// Here I am Setting the RequestCode to 50
				finish();
			}

		});

		cancel = (Button) findViewById(R.id.cancelSavingKeyword);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				datasource.close();
				Intent in = new Intent();
				setResult(51, in);// Here I am Setting the RequestCode to 51
				finish();
			}

		});
	}

}
