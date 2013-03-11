package ca.elman.messageme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class NewMessageActivity extends SherlockActivity {
	
	private class KeywordTextAndNewPositionOfIndex {
		public String title;
		public int new_position;
		public Boolean wasFound;

		KeywordTextAndNewPositionOfIndex(String title, int pos, Boolean bool) {
			this.title = title;
			this.new_position = pos;
			wasFound = bool;
		}

	}

	/**
	private class UniqueTextsAndNewPositionOfIndex {
		public HashMap<String, String> uniqueTexts;
		public int new_position;

		UniqueTextsAndNewPositionOfIndex(HashMap<String, String> texts, int pos) {
			uniqueTexts = texts;
			new_position = pos;
		}
	}
	**/
	
	int reqCode;
	private final int DIALOG_FREQUENCY_ID = 100000, DIALOG_KEYWORDS_ID = 100001;
	private final int MENU_HELP = 10000;
	private ImageButton getContactsButton;
	private RelativeLayout setDateButton, setFrequencyButton;
	private EditText contactsToSend, textToSend;
	private TextView tViewChangeTime, tViewChangeHeadingTime, tViewFrequency;
	private String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	final CharSequence[] choicesFrequency = { "Once", "Every 5 minutes",
			"Every 15 minutes", "Every 30 minutes", "Every 1 hour",
			"Every 2 hours", "Every 6 hours", "Every 12 hours", "Every day",
			"Every week" };
	private Button hidePreviewButton, sendSmsButton, previewSmsButton,
			addKeywordsButton, saveAsDraftButton;
	private KeywordsDataSource keywordsdata;
	private MessagesDataSource messagesdata;
	private MessagesData oldMD = null;
	private LinearLayout llView;
	private HashMap<String, String> allTexts;
	private int numberOfIdOfPreviewTexts = 0, itemFrequency = 0;
	private long repeatingMinutes = 0;
	private static Context appContext;
	private static SmsSenderReceiver ssr;
	private Bundle extras;

	//a new dialog appears when user either sets frequency or adds a  newkeyword 
	protected Dialog onCreateDialog(int id) {
		AlertDialog adlog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
		
		case DIALOG_FREQUENCY_ID:
			builder.setTitle("Pick Frequency");
			builder.setSingleChoiceItems(choicesFrequency, -1,
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int item) {
							tViewFrequency.setText(choicesFrequency[item]
									.toString().toUpperCase());
							dialog.dismiss();
							
							//depending on which item was selected, it converts it to minutes
							if (item >= 1 && item <= 3) {
								repeatingMinutes = Integer
										.parseInt(choicesFrequency[item]
												.toString().split(" ")[1]);
							} else if (item > 3 && item < 8) {
								repeatingMinutes = Integer
										.parseInt(choicesFrequency[item]
												.toString().split(" ")[1]) * 60;
							} else if (item == 8) {
								repeatingMinutes = 24 * 60;
							} else if (item == choicesFrequency.length - 1) {
								repeatingMinutes = 7 * 24 * 60;
							}
							itemFrequency = item;
						}
					});
			break;

		case DIALOG_KEYWORDS_ID:
			final List<KeywordsData> selectedKeywords = new ArrayList<KeywordsData>();
			final List<KeywordsData> allKeywords = keywordsdata.getAllKeywords();
			final String[] choices2 = new String[allKeywords.size()];
			int i = 0;
			
			for (KeywordsData kd : allKeywords) {
				choices2[i] = "#" + kd.getTitle();
				i++;
			}
			
			builder.setTitle("Pick Keywords");
			builder.setMultiChoiceItems(choices2, null,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int pos,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								selectedKeywords.add(allKeywords.get(pos));
							} else {
								selectedKeywords.remove(allKeywords.get(pos));
							}
						}
					});

			// insert text
			builder.setPositiveButton("Add Keyword(s)",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							int startOfCursor = textToSend.getSelectionStart();
							if (!selectedKeywords.isEmpty()) {
								for (KeywordsData each_choice : selectedKeywords) {
									textToSend.getText().insert(startOfCursor,
											"#" + each_choice.getTitle() + " ");
								}
							}

							selectedKeywords.clear();
							dialog.dismiss();
						}
					});

			break;

		}

		adlog = builder.create();
		return adlog;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_HELP, 0, "Help")
				.setIcon(R.drawable.ic_menu_help)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return result;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case MENU_HELP:
			Intent intent = new Intent(NewMessageActivity.this,
					NewMessageHelpActivity.class);
			startActivityForResult(intent, reqCode);
			return true;
		}

		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_message);

		NewMessageActivity.appContext = getApplicationContext();
		
		//open databases
		keywordsdata = new KeywordsDataSource(this);
		keywordsdata.open();

		messagesdata = new MessagesDataSource(this);
		messagesdata.open();

		contactsToSend = (EditText) findViewById(R.id.whereToSend);
		textToSend = (EditText) findViewById(R.id.whatToSend);
		tViewChangeTime = (TextView) findViewById(R.id.tvDateTime);
		tViewChangeHeadingTime = (TextView) findViewById(R.id.tvChangeHeadingDateTime);
		tViewFrequency = (TextView) findViewById(R.id.tvFrequency);

		// check if there are some extras i.e. need to modify draft message
		extras = getIntent().getExtras();
		if (extras != null) {
			try {
				String receivers = (String) extras
						.getCharSequence("message_receivers");
				String timeAndDate = (String) extras
						.getCharSequence("message_time_and_date");
				String freq = (String) extras
						.getCharSequence("message_frequency");
				String text = (String) extras.getCharSequence("message_text");
				String type = (String) extras.getCharSequence("message_type");

				contactsToSend.setText(receivers);
				tViewChangeTime.setText(timeAndDate);
				tViewFrequency.setText(freq);
				textToSend.setText(text);
				for (MessagesData md : messagesdata.getAllMessages()) {
					if (md.getReceivers().equals(receivers)
							&& md.getTimeAndDate().equals(timeAndDate)
							&& md.getFrequency().equals(freq)
							&& md.getText().equals(text)
							&& md.getType().equals(type)) {
						oldMD = md;
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		llView = (LinearLayout) findViewById(R.id.linearLayoutNewMessage);

		getContactsButton = (ImageButton) findViewById(R.id.ContactsButton);
		getContactsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent contacts_intent = new Intent(NewMessageActivity.this,
						ContactsAndGroupsTabActivity.class);
				startActivityForResult(contacts_intent, reqCode);
			}
		});

		setDateButton = (RelativeLayout) findViewById(R.id.setDate);
		setDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent date_and_time_intent = new Intent(
						NewMessageActivity.this, SetDateAndTimeActivity.class);
				startActivityForResult(date_and_time_intent, reqCode);
			}
		});

		setFrequencyButton = (RelativeLayout) findViewById(R.id.setFrequency);
		setFrequencyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showDialog(DIALOG_FREQUENCY_ID);
			}
		});

		saveAsDraftButton = (Button) findViewById(R.id.buttonSaveAsDraft);

		saveAsDraftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (oldMD != null) {
					if (oldMD.getType().equals("Scheduled")) {
						Boolean isRep = true;
						if (oldMD.getFrequency().equals("ONCE")) {
							isRep = false;
						}
						ssr.CancelAlarm(
								NewMessageActivity.getAppContext(),
								oldMD.getAlarmId(),
								parseText(oldMD.getText(), oldMD
										.getReceivers().split(",")), oldMD
										.getTimeAndDate(),
								oldMD.getFrequency(), oldMD.getText(), isRep);
					}
					
					messagesdata.deleteMessage(oldMD);
					MessagesData newMD = new MessagesData();
					newMD.setReceivers(contactsToSend.getText().toString());
					newMD.setText(textToSend.getText().toString());
					newMD.setTimeAndDate(tViewChangeTime.getText().toString());
					newMD.setFrequency(tViewFrequency.getText().toString());
					newMD.setType("Draft");
					newMD.setAlarmId(-1);
					messagesdata.createMessage(newMD);
				} else {
					MessagesData newMD = new MessagesData(contactsToSend
							.getText().toString(), textToSend.getText()
							.toString(), tViewChangeTime.getText().toString(),
							tViewFrequency.getText().toString(), "Draft");
					messagesdata.createMessage(newMD);
				}
				messagesdata.close();
				keywordsdata.close();
				Intent foldersIn = new Intent(getApplicationContext(),
						FoldersActivity.class);
				startActivity(foldersIn);
				finish();
			}
		});

		sendSmsButton = (Button) findViewById(R.id.buttonSendSms);

		sendSmsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Boolean sendSms = true;
				HashMap<String, String> contactsNamesAndTexts = parseText(
						textToSend.getText().toString(), contactsToSend
								.getText().toString().split(","));

				if (contactsToSend.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please add recievers", Toast.LENGTH_SHORT).show();
					sendSms = false;
				}

				if (textToSend.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(), "Please add text",
							Toast.LENGTH_SHORT).show();
					sendSms = false;
				}

				if (oldMD != null && oldMD.getType().equals("Sent")) {
					Toast.makeText(getApplicationContext(),
							"You can not modify Sent Messages",
							Toast.LENGTH_SHORT).show();
					sendSms = false;
				}

				for (final String each_contact : contactsNamesAndTexts.keySet()) {

					if ((!each_contact
							.matches("\"[a-zA-Z_0-9'\" ()]+\" [0-9]+"))
							&& (!each_contact.matches("[0-9]+"))) {

						sendSms = false;
						Toast.makeText(
								getApplicationContext(),
								each_contact
										+ " contains an invalid character. Please read help for more info",
								Toast.LENGTH_LONG).show();
					}
				}

				// if all above if statements are true then send sms messages
				if (sendSms == true) {
					long timeToSend;
					Boolean correctlySetTime = true;
					if (tViewChangeTime.getText().toString().equals("NOW")) {
						timeToSend = System.currentTimeMillis();
					} else {
						String[] time = tViewChangeTime.getText().toString()
								.split(" ");
						Calendar setCal = Calendar.getInstance();
						setCal.setTimeInMillis(System.currentTimeMillis() + 150);
						setCal.clear();
						setCal.set(Integer.parseInt(time[2]),
								Arrays.asList(MONTHS).indexOf(time[1]),
								Integer.parseInt(time[0]),
								Integer.parseInt(time[4]),
								Integer.parseInt(time[6]));
						//convert time to milliseconds
						timeToSend = setCal.getTimeInMillis();
						if (setCal.getTimeInMillis() < System
								.currentTimeMillis()) {
							Toast.makeText(getApplicationContext(),
									"Time was incorrectly set",
									Toast.LENGTH_SHORT).show();
							correctlySetTime = false;
						}
					}

					if (correctlySetTime == true) {
						ssr = new SmsSenderReceiver();
						// if this message was scheduled before then replace it with new one
						if (oldMD != null) {
							if (oldMD.getType().equals("Scheduled")) {
								Boolean isRep = true;
								if (oldMD.getFrequency().equals("ONCE")) {
									isRep = false;
								}
								ssr.CancelAlarm(
										NewMessageActivity.getAppContext(),
										oldMD.getAlarmId(),
										parseText(oldMD.getText(), oldMD
												.getReceivers().split(",")),
										oldMD.getTimeAndDate(), oldMD
												.getFrequency(), oldMD
												.getText(), isRep);
							}
							messagesdata.deleteMessage(oldMD);
						}

						if (repeatingMinutes == 0) {
							ssr.SetAlarm(
									getApplicationContext(),
									parseText(textToSend.getText().toString(),
												contactsToSend.getText()
													.toString().split(",")),
									timeToSend, contactsToSend.getText()
											.toString(),
									choicesFrequency[itemFrequency].toString(),
									tViewChangeTime.getText().toString(),
									textToSend.getText().toString());
						} else {
							ssr.SetRepeatingAlarm(
									getApplicationContext(),
									parseText(textToSend.getText().toString(),
												contactsToSend.getText()
													.toString().split(",")),
									timeToSend, repeatingMinutes * 60000,
									contactsToSend.getText().toString(),
									choicesFrequency[itemFrequency].toString(),
									tViewChangeTime.getText().toString(),
									textToSend.getText().toString());
						}
						
						keywordsdata.close();
						messagesdata.close();
						Intent folders_intent = new Intent(
								NewMessageActivity.this, FoldersActivity.class);
						startActivity(folders_intent);
						finish();
					}
				}
			}
		});

		previewSmsButton = (Button) findViewById(R.id.buttonPreviewSms);

		previewSmsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (contactsToSend.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please add receivers", Toast.LENGTH_SHORT).show();
				}

				else if (previewSmsButton.getText().equals("Preview")) {

					previewSmsButton.setText("Update Preview");
					
					allTexts = parseText(textToSend.getText().toString(),
							contactsToSend.getText().toString().split(","));

					//dynamically create TextView for each receiver
					TextView previewText = setTextView("Preview", 14, 0);
					previewText.setGravity(Gravity.CENTER_HORIZONTAL);
					previewText.setBackgroundColor(Color.parseColor("#E0FFFF"));
					previewText.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
					llView.addView(previewText);

					numberOfIdOfPreviewTexts = 1;
					
					for (String each_contact : allTexts.keySet()) {
						
						TextView contactsTextView = setTextView("To: "
								+ each_contact, 18, numberOfIdOfPreviewTexts);
						contactsTextView.setGravity(Gravity.CENTER_HORIZONTAL);
						llView.addView(contactsTextView);

						numberOfIdOfPreviewTexts += 1;
						
						TextView messageTextView = setTextView(
								allTexts.get(each_contact), 16,
								numberOfIdOfPreviewTexts);
						llView.addView(messageTextView);

						numberOfIdOfPreviewTexts += 1;
					}
				}

				else if (previewSmsButton.getText().equals("Update Preview")) {

					int j = 1;
					TextView tViewContact = new TextView(
							getApplicationContext());
					TextView tViewMessage = new TextView(
							getApplicationContext());

					allTexts = parseText(textToSend.getText().toString(),
							contactsToSend.getText().toString().split(","));

					TextView tViewPreview = new TextView(
							getApplicationContext());
					tViewPreview = (TextView) findViewById(0);
					tViewPreview.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));

					for (String each_contact : allTexts.keySet()) {
						//depending on number of receivers either update old textViews or dynamically add new ones
						if (j >= numberOfIdOfPreviewTexts) {
							TextView contactsTextView = setTextView("To: "
									+ each_contact, 18, j);
							contactsTextView
									.setGravity(Gravity.CENTER_HORIZONTAL);
							llView.addView(contactsTextView);

							j += 1;
							TextView messageTextView = setTextView(
									allTexts.get(each_contact), 16, j);
							llView.addView(messageTextView);

							numberOfIdOfPreviewTexts += 2;
						} else {
							tViewContact = (TextView) findViewById(j);
							j += 1;
							tViewMessage = (TextView) findViewById(j);
							// set layout params to wrap_content because it may
							// be hidden (params set to 0)
							tViewContact
									.setLayoutParams(new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,
											LinearLayout.LayoutParams.WRAP_CONTENT));
							tViewMessage
									.setLayoutParams(new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,
											LinearLayout.LayoutParams.WRAP_CONTENT));
							tViewContact.setText("To: " + each_contact);
							tViewMessage.setText(allTexts.get(each_contact));
						}

						j += 1;

					}

					// Delete all other not used textViews if there are less
					// contacts in editText field than in previous case
					int count = 0;
					while (j < numberOfIdOfPreviewTexts) {
						TextView tView1 = new TextView(getApplicationContext());
						tView1 = (TextView) findViewById(j);
						tView1.setLayoutParams(new LinearLayout.LayoutParams(0,
								0));
						++j;
						++count;
					}
					numberOfIdOfPreviewTexts -= count;
				}

			}

		});

		hidePreviewButton = (Button) findViewById(R.id.buttonHidePreview);

		hidePreviewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (numberOfIdOfPreviewTexts != 0) {
					int i = 0;
					while (i < numberOfIdOfPreviewTexts) {
						TextView tView1 = new TextView(getApplicationContext());
						tView1 = (TextView) findViewById(i);
						tView1.setLayoutParams(new LinearLayout.LayoutParams(0,
								0));
						++i;
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Preview was not set yet", Toast.LENGTH_SHORT)
							.show();
				}

			}

		});
		
		addKeywordsButton = (Button) findViewById(R.id.buttonAddKeywords);

		addKeywordsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_KEYWORDS_ID);
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// Add button Contacts
		if (resultCode == 1) {
			for (ContactsData cData : ContactsAdapter.checkedContacts) {
				if (contactsToSend.getText().toString().matches("")) {
					contactsToSend.setText('"' + cData.getName() + '"' + ' '
							+ cData.getPhone());
				}

				//add contacts to contacts text field
				else {
					if (!contactsToSend.getText().toString()
							.contains(cData.getPhone())) {
						contactsToSend.setText(contactsToSend.getText()
								.toString()
								+ ','
								+ ' '
								+ '"'
								+ cData.getName()
								+ '"' + ' ' + cData.getPhone());
					}
				}

			}
			ContactsAdapter.checkedContacts.clear();
		}

		// Send messages now
		if (resultCode == 3) {
			tViewChangeTime.setText("NOW");
			tViewChangeHeadingTime
					.setText("Click here to change Date and Time");
		}

		// Send messages later
		if (resultCode == 4) {
			String hour = SetDateAndTimeActivity.finalTP.getHour();
			String minute = SetDateAndTimeActivity.finalTP.getMinute();

			if (Integer.parseInt(hour) < 10) {
				hour = '0' + hour;
			}
			if (Integer.parseInt(minute) < 10) {
				minute = '0' + minute;
			}

			String text = SetDateAndTimeActivity.finalDP.getDay() + ' '
					+ MONTHS[SetDateAndTimeActivity.finalDP.getMonth()] + ' '
					+ SetDateAndTimeActivity.finalDP.getYear() + " ; " + hour
					+ " : " + minute;
			tViewChangeTime.setText(text);
			tViewChangeHeadingTime
					.setText("Click here to change Date and Time");
		}
	}

	/**
	 * Main method which parses text
	 * @param text
	 * @param receivers
	 * @return HashMap , where key is the receiver and its value is text that would be sent to him/her
	 */
	public HashMap<String, String> parseText(String text, String[] receivers) {
		int i = 0;
		HashMap<String, String> individualTexts = new HashMap<String, String>();

		for (String e_contact : receivers) {
			individualTexts.put(e_contact.trim(), "");
		}

		while (i < text.length()) {
			char ch = text.charAt(i);

			if (ch == '#') {
				KeywordTextAndNewPositionOfIndex ktanpof = returnKeywordText(text
						.substring(i));

				// if keyword was found
				if (ktanpof.wasFound == true) {

					// recursively parse the keyword because it may contain another keywords inside
					HashMap<String, String> recursivelyParsedTexts = parseText(ktanpof.title, receivers);
					
					for (String each_contact : recursivelyParsedTexts.keySet()) {
						String cont_text = recursivelyParsedTexts.get(each_contact);

						// if the keyword is First_Name replace it with real first name of receiver
						if (cont_text.contains("First_Name")) {
							if (each_contact.charAt(0) == '"') {
								cont_text = cont_text.replaceAll(
										"First_Name",
										each_contact.substring(1,
												each_contact.lastIndexOf('"'))
												.split(" ")[0]);
							} else {
								cont_text = cont_text.replaceAll("First_Name",
										each_contact);
							}
						}

						// update each text for each receiver
						individualTexts.put(each_contact,
								individualTexts.get(each_contact) + cont_text);
					}
				}

				// if keyword (text after #) was not found, just change the index 
				else {
					for (String each_contact : individualTexts.keySet()) {
						individualTexts.put(each_contact,
								individualTexts.get(each_contact)
										+ ktanpof.title);
					}
				}

				i += ktanpof.new_position;
			}
			
			
			else {
				for (String each_contact : individualTexts.keySet()) {
					individualTexts.put(each_contact,
							individualTexts.get(each_contact) + ch);
				}
				i++;
			}
		}
		return individualTexts;
	}

	
	/**
	 * Returns the keyword text and the position where index will be moved in main text
	 * @param substring 
	 * @return KeywordTextAndNewPositionOfIndex see above
	 */
	private KeywordTextAndNewPositionOfIndex returnKeywordText(String substring) {
		// TODO Auto-generated method stub
		
		if (substring.length() == 1) {
			// only	one char in substring which is # and obviously it is not a keyword
			return new KeywordTextAndNewPositionOfIndex("#", 1, false); 
			
		} else {
			int i = 1;
			Boolean wasFound = false;
			String expectedText = "";
			
			// find what is the title of the keyword, so then we can check if that title exists
			// in keywords database
			while (i < substring.length() && substring.charAt(i) != ' ') {
				expectedText += substring.charAt(i);
				++i;
			}

			// if title is First_Name do not do anything here , it would be
			// changed in parseText method
			// otherwise find the keyword from keywords database
			if (expectedText.equals("First_Name")) {
				wasFound = true;
			} else {
				List<KeywordsData> kDataList = keywordsdata.getAllKeywords();
				for (KeywordsData kd : kDataList) {
					if (kd.getTitle().equals(expectedText)) {
						// text of the keyword
						expectedText = kd.getText();
						wasFound = true;
						break;
					}
				}
			}

			if (wasFound == false) {
				expectedText = "#" + expectedText;
			}

			return new KeywordTextAndNewPositionOfIndex(expectedText, i, wasFound);
		}
	}


	@Override
	protected void onResume() {
		keywordsdata.open();
		messagesdata.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		keywordsdata.close();
		messagesdata.close();
		super.onPause();
	}

	// dynamically create new TextView and set new text, size and id
	private TextView setTextView(String text, float size, int id) {
		TextView tView = new TextView(getApplicationContext());
		tView.setText(text);
		tView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		tView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tView.setId(id);
		tView.setTextColor(Color.BLACK);
		return tView;
	}

	public static Context getAppContext() {
		return NewMessageActivity.appContext;
	}

}
