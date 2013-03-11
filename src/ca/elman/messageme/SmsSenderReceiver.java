package ca.elman.messageme;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * broadcast receiver, the class which is responsible for sending scheduled messages
 * @author emansim
 *
 */
public class SmsSenderReceiver extends BroadcastReceiver {

	private MessagesDataSource datasource;

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		final Context fContext = context;
		Bundle bundle = intent.getExtras();
		final HashMap<String, String> contactsNamesAndTexts = (HashMap<String, String>) bundle
				.get("contacts_names_phones");
		final String freq = bundle.getString("frequency");
		final String timeAndDate = bundle.getString("time_and_date");
		final int alarm_id = bundle.getInt("last_alarm_id");
		final Boolean isRep = bundle.getBoolean("is_repeating");

		// Delete previously Scheduled message from database with the same id (if they exist)
		if (alarm_id != 0 && isRep == false) {
			datasource = new MessagesDataSource(context.getApplicationContext());
			datasource.open();
			for (MessagesData sch_md : datasource.getScheduledMessages()) {
				if (sch_md.getAlarmId() == alarm_id) {
					datasource.deleteMessage(sch_md);
					break;
				}
			}
		}

		SmsManager smsManager = SmsManager.getDefault();

		for (final String each_contact : contactsNamesAndTexts.keySet()) {
			String phoneNum = null;

			// get the phone number
			if (each_contact.matches("\"[a-zA-Z_0-9'\" ()]+\" [0-9]+")) {
				phoneNum = each_contact.substring(
						each_contact.lastIndexOf('"') + 1).trim();
			} else if (each_contact.matches("[0-9]+")) {
				phoneNum = each_contact;
			}


			String SENT = "SMS_SENT";
			String DELIVERED = "SMS_DELIVERED";

			PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
					new Intent(SENT), PendingIntent.FLAG_UPDATE_CURRENT);

			PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
					new Intent(DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT);

			// ---when the SMS has been sent---
			context.getApplicationContext().registerReceiver(
					new BroadcastReceiver() {
						@Override
						public void onReceive(Context arg0, Intent arg1) {
							datasource = new MessagesDataSource(
									arg0.getApplicationContext());
							datasource.open();
							switch (getResultCode()) {
							case Activity.RESULT_OK:
								// SmsSenderReceiver.sentReceivers.add(each_contact);
								datasource.createMessage(new MessagesData(
										each_contact, contactsNamesAndTexts
												.get(each_contact),
										timeAndDate, freq, "Sent"));
								Toast.makeText(fContext,
										"SMS was sent to " + each_contact,
										Toast.LENGTH_SHORT).show();
								break;
							case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
								// SmsSenderReceiver.notSentReceivers.add(each_contact);
								datasource.createMessage(new MessagesData(
										each_contact, contactsNamesAndTexts
												.get(each_contact),
										timeAndDate, freq, "Draft"));
								Toast.makeText(
										fContext,
										"Generic failure, SMS was NOT sent to "
												+ each_contact,
										Toast.LENGTH_SHORT).show();
								break;
							case SmsManager.RESULT_ERROR_NO_SERVICE:
								// SmsSenderReceiver.notSentReceivers.add(each_contact);
								datasource.createMessage(new MessagesData(
										each_contact, contactsNamesAndTexts
												.get(each_contact),
										timeAndDate, freq, "Draft"));
								Toast.makeText(
										fContext,
										"No service, SMS was NOT sent to "
												+ each_contact,
										Toast.LENGTH_SHORT).show();
								break;
							case SmsManager.RESULT_ERROR_NULL_PDU:
								// SmsSenderReceiver.notSentReceivers.add(each_contact);
								datasource.createMessage(new MessagesData(
										each_contact, contactsNamesAndTexts
												.get(each_contact),
										timeAndDate, freq, "Draft"));
								Toast.makeText(
										fContext,
										"Null PDU, SMS was NOT sent to "
												+ each_contact,
										Toast.LENGTH_SHORT).show();
								break;
							case SmsManager.RESULT_ERROR_RADIO_OFF:
								// SmsSenderReceiver.notSentReceivers.add(each_contact);
								datasource.createMessage(new MessagesData(
										each_contact, contactsNamesAndTexts
												.get(each_contact),
										timeAndDate, freq, "Draft"));
								Toast.makeText(
										fContext,
										"Radio off, SMS was NOT sent to "
												+ each_contact,
										Toast.LENGTH_SHORT).show();
								break;
							}
							datasource.close();
							context.getApplicationContext().unregisterReceiver(this);
						}
					}, new IntentFilter(SENT));

			// ---when the SMS has been delivered---
			context.getApplicationContext().registerReceiver(
					new BroadcastReceiver() {
						@Override
						public void onReceive(Context arg0, Intent arg1) {
							switch (getResultCode()) {
							case Activity.RESULT_OK:
								Toast.makeText(
										fContext,
										each_contact + " received Your message",
										Toast.LENGTH_SHORT).show();
								break;
							case Activity.RESULT_CANCELED:
								Toast.makeText(
										fContext,
										each_contact
												+ " did NOT receive Your message",
										Toast.LENGTH_SHORT).show();
								break;
							}
							context.getApplicationContext().unregisterReceiver(this);
						}
					}, new IntentFilter(DELIVERED));

			// depending of length of the message, send either one or several messages
			if (contactsNamesAndTexts.get(each_contact).length() <= 160) {
				smsManager.sendTextMessage(phoneNum, null,
						contactsNamesAndTexts.get(each_contact), sentPI,
						deliveredPI);
			} else {
				ArrayList<PendingIntent> listOfSentIntents = new ArrayList<PendingIntent>(
						0);
				ArrayList<PendingIntent> listOfDeliveredIntents = new ArrayList<PendingIntent>(
						0);
				listOfSentIntents.add(sentPI);
				listOfDeliveredIntents.add(deliveredPI);
				smsManager.sendMultipartTextMessage(phoneNum, null,
						smsManager.divideMessage(contactsNamesAndTexts
								.get(each_contact)), listOfSentIntents,
						listOfDeliveredIntents);
			}


		}

	}

	public void SetAlarm(Context context,
			HashMap<String, String> contactsNamesAndText, long time,
			String contacts, String frequencyString, String timeAndDateString,
			String text) {
		
		Boolean wasSch = false;
		MessagesData schMD = null;
		datasource = new MessagesDataSource(context.getApplicationContext());
		datasource.open();
		int last_alarm_id = 0;
		
		if (!timeAndDateString.equals("NOW")) {
			for (MessagesData sch_md : datasource.getScheduledMessages()) {
				last_alarm_id = sch_md.getAlarmId();
			}
			last_alarm_id++;
			schMD = new MessagesData(contacts, text, timeAndDateString,
					frequencyString, "Scheduled", last_alarm_id);
			datasource.createMessage(schMD);
			wasSch = true;
		}

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = newIntent(context, contactsNamesAndText,
				frequencyString, timeAndDateString, text, last_alarm_id, false);
		PendingIntent pIntent = null;
		if (wasSch == false) {
			pIntent = PendingIntent.getBroadcast(
					NewMessageActivity.getAppContext(), -1, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			pIntent = PendingIntent.getBroadcast(
					NewMessageActivity.getAppContext(), schMD.getAlarmId(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		
		am.set(AlarmManager.RTC_WAKEUP, time, pIntent);
	}

	public void SetRepeatingAlarm(Context context,
			HashMap<String, String> contactsNamesAndText, long time,
			long repeatingTime, String contacts, String frequencyString,
			String timeAndDateString, String text) {
		
		Boolean wasSch = false;
		MessagesData schMD = null;
		datasource = new MessagesDataSource(context.getApplicationContext());
		datasource.open();
		
		int last_alarm_id = 0;
		for (MessagesData sch_md : datasource.getScheduledMessages()) {
			last_alarm_id = sch_md.getAlarmId();
		}
		
		last_alarm_id++;
		schMD = new MessagesData(contacts, text, timeAndDateString,
				frequencyString, "Scheduled", last_alarm_id);
		datasource.createMessage(schMD);
		wasSch = true;

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = newIntent(context, contactsNamesAndText,
				frequencyString, timeAndDateString, text, last_alarm_id, true);
		PendingIntent pIntent = null;
		
		if (wasSch == false) {
			pIntent = PendingIntent.getBroadcast(
					NewMessageActivity.getAppContext(), -1, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			pIntent = PendingIntent.getBroadcast(
					NewMessageActivity.getAppContext(), schMD.getAlarmId(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		am.setRepeating(AlarmManager.RTC_WAKEUP, time, repeatingTime, pIntent); // Millisec
																				// *
																				// Second
																				// *
																				// Minute

	}

	// cancel the alarm <==> cancel the scheduled message
	public void CancelAlarm(Context context, int alarm_id,
			HashMap<String, String> contactsNamesAndText,
			String timeAndDateString, String frequencyString, String text,
			Boolean isRep) {
		
		Intent intent = newIntent(context, contactsNamesAndText,
				frequencyString, timeAndDateString, text, alarm_id, isRep);
		PendingIntent sender = PendingIntent.getBroadcast(context, alarm_id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		sender.cancel();
	}

	private Intent newIntent(Context context,
			HashMap<String, String> contactsNamesAndText,
			String frequencyString, String timeAndDateString, String text,
			int lastAlarmId, Boolean isRep) {
		Intent intent = new Intent(context, SmsSenderReceiver.class);
		intent.putExtra("contacts_names_phones", contactsNamesAndText);
		intent.putExtra("frequency", frequencyString);
		intent.putExtra("time_and_date", timeAndDateString);
		intent.putExtra("general_text", text);
		intent.putExtra("last_alarm_id", lastAlarmId);
		intent.putExtra("is_repeating", isRep);
		return intent;
	}

}
