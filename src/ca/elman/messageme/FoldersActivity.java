package ca.elman.messageme;

import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class FoldersActivity extends SherlockActivity implements ActionBar.TabListener {

	int reqCode;
    private final int MENU_NEW = 1;
    private final int MENU_HELP = 2;
    private ActionBar abar;
	private ListView listView;
	private MessagesDataSource datasource;
	private ArrayAdapter<MessagesData> adapter;
	private Button deleteMessage;
	final CharSequence[] tabs_titles = {"All", "Scheduled", "Sent", "Drafts"};

    // icons on right top of the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

    	boolean result = super.onCreateOptionsMenu(menu);
		
    	menu.add(0, MENU_NEW , 0, "New")
            .setIcon(R.drawable.ic_menu_compose)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
                    MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
    	menu.add(0, MENU_HELP , 0, "Help")
        	.setIcon(R.drawable.ic_menu_help)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
                MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return result;
       
    }

    // icons on right top of the screen
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		    case MENU_NEW:
				Intent intent = new Intent(FoldersActivity.this, NewMessageActivity.class);
		        startActivity(intent);
		        finish();
		        return true;
			case MENU_HELP:
				Intent intent2 = new Intent(FoldersActivity.this,
						NewMessageHelpActivity.class);
				startActivityForResult(intent2, reqCode);
				return true;
		    }
		   
		   return false;
	}
 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folders);
        
        datasource = new MessagesDataSource(this);
        datasource.open();
       
        abar = getSupportActionBar();
        abar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        for (CharSequence title : tabs_titles) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(title);
            tab.setTabListener(this);
            abar.addTab(tab);
        }
        
        deleteMessage = (Button) findViewById(R.id.deleteCancelMessage);
        
        deleteMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				for (MessagesData md : MessagesAdapter.checkedMessages) {
					if (md.getType().equals("Scheduled")) {
						SmsSenderReceiver ssr = new SmsSenderReceiver();
						NewMessageActivity nma = new NewMessageActivity();
						Boolean isRep = true;
						if (md.getFrequency().equals("ONCE")) {
							isRep = false;
						}
						ssr.CancelAlarm(NewMessageActivity.getAppContext(), md.getAlarmId(), nma.parseText(md.getText(),
								md.getReceivers().split(",")), md.getTimeAndDate(), md.getFrequency(), md.getText(), isRep);
					}
					datasource.deleteMessage(md);
				}
					
				MessagesAdapter.checkedMessages.clear();
				startActivity(getIntent());
				finish();
			}
        });
        		
    }

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		String typeOfMessage = tab.getText().toString();
		List<MessagesData> md = null;
		
		if (typeOfMessage.equals("All")) {
			md = datasource.getAllMessages();
		}
		else if (typeOfMessage.equals("Scheduled")) {
			md = datasource.getScheduledMessages();
		}
		else if (typeOfMessage.equals("Sent")) {
			md = datasource.getSentMessages();
		}
		else if (typeOfMessage.equals("Drafts")) {
			md = datasource.getDraftMessages();
		}

		listView = (ListView) findViewById(R.id.messages_list);
		adapter = new MessagesAdapter(this, 0, md);

		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);
		
		listView.setEmptyView(findViewById(R.id.empty_message));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View childView,
					int position, long id) {
				// TODO Auto-generated method stub
				MessagesData message = datasource.getAllMessages().get(position);
				Intent update_message_intent = new Intent(
						FoldersActivity.this, NewMessageActivity.class);
				update_message_intent.putExtra("message_receivers",
						(CharSequence) message.getReceivers());
				update_message_intent.putExtra("message_time_and_date",
						(CharSequence) message.getTimeAndDate());
				update_message_intent.putExtra("message_frequency",
						(CharSequence) message.getFrequency());
				update_message_intent.putExtra("message_text",
						(CharSequence) message.getText());
				update_message_intent.putExtra("message_type", (CharSequence) message.getType());
				datasource.close();
				startActivityForResult(update_message_intent, reqCode);
			}
		});
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
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