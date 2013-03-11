// main activity
package ca.elman.messageme;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class MessageMeActivity extends SherlockActivity {

	private ImageButton new_message, folders, keywords, help;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new_message = (ImageButton) findViewById(R.id.new_mes);
		new_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent new_message_intent = new Intent(MessageMeActivity.this,
						NewMessageActivity.class);
				startActivity(new_message_intent);
			}
		});
		
		folders = (ImageButton) findViewById(R.id.folders);
		folders.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent folders_intent = new Intent(MessageMeActivity.this,
						FoldersActivity.class);
				startActivity(folders_intent);
			}
		});
		
		keywords = (ImageButton) findViewById(R.id.keywords);
		keywords.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent keywords_intent = new Intent(MessageMeActivity.this,
						KeywordsActivity.class);
				startActivity(keywords_intent);
			}
		});
		
		help = (ImageButton) findViewById(R.id.help);
		help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent help_intent = new Intent(MessageMeActivity.this,
						NewMessageHelpActivity.class);
				startActivity(help_intent);
			}
		});
		
	}
}