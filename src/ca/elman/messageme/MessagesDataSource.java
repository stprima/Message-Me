package ca.elman.messageme;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MessagesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MessagesMySQLiteHelper dbHelper;
	private String[] allColumns = { MessagesMySQLiteHelper.COLUMN_ID,
			MessagesMySQLiteHelper.COLUMN_RECEIVERS, MessagesMySQLiteHelper.COLUMN_TIME_AND_DATE, MessagesMySQLiteHelper.COLUMN_FREQUENCY, MessagesMySQLiteHelper.COLUMN_TEXT, MessagesMySQLiteHelper.COLUMN_TYPE , MessagesMySQLiteHelper.COLUMN_ALARM_ID };

	public MessagesDataSource(Context context) {
		dbHelper = new MessagesMySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public MessagesData createMessage(MessagesData message) {
		ContentValues values = new ContentValues();
		values.put(MessagesMySQLiteHelper.COLUMN_RECEIVERS, message.getReceivers());
		values.put(MessagesMySQLiteHelper.COLUMN_TIME_AND_DATE, message.getTimeAndDate());
		values.put(MessagesMySQLiteHelper.COLUMN_FREQUENCY, message.getFrequency());
		values.put(MessagesMySQLiteHelper.COLUMN_TEXT, message.getText());
		values.put(MessagesMySQLiteHelper.COLUMN_TYPE, message.getType());
		values.put(MessagesMySQLiteHelper.COLUMN_ALARM_ID, message.getAlarmId());

		
		long insertId = database.insert(MessagesMySQLiteHelper.TABLE_MESSAGES, null,
				values);
		Cursor cursor = database.query(MessagesMySQLiteHelper.TABLE_MESSAGES,
				allColumns, MessagesMySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		MessagesData newMessage = cursorToMessage(cursor);
		cursor.close();
		return newMessage;
	}

	public void deleteMessage(MessagesData message) {
		long id = message.getId();
		database.delete(MessagesMySQLiteHelper.TABLE_MESSAGES, MessagesMySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	// Updating single keyword
	public int updateMessage(MessagesData message) {

		ContentValues values = new ContentValues();
		values.put(MessagesMySQLiteHelper.COLUMN_RECEIVERS, message.getReceivers());
		values.put(MessagesMySQLiteHelper.COLUMN_TIME_AND_DATE, message.getTimeAndDate());
		values.put(MessagesMySQLiteHelper.COLUMN_FREQUENCY, message.getFrequency());
		values.put(MessagesMySQLiteHelper.COLUMN_TEXT, message.getText());
		values.put(MessagesMySQLiteHelper.COLUMN_TYPE, message.getType());
		values.put(MessagesMySQLiteHelper.COLUMN_ALARM_ID, message.getAlarmId());

		// updating row
		return database.update(MessagesMySQLiteHelper.TABLE_MESSAGES, values, MessagesMySQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(message.getId()) });
	}

	public List<MessagesData> getAllMessages() {
		List<MessagesData> messages = new ArrayList<MessagesData>();

		Cursor cursor = database.query(MessagesMySQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MessagesData message = cursorToMessage(cursor);
			messages.add(message);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return messages;
	}
	
	public List<MessagesData> getScheduledMessages() {
		List<MessagesData> messages = new ArrayList<MessagesData>();

		Cursor cursor = database.query(MessagesMySQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MessagesData message = cursorToMessage(cursor);
			if (message.getType().equals("Scheduled")) {
				messages.add(message);
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return messages;
	}
	
	public List<MessagesData> getSentMessages() {
		List<MessagesData> messages = new ArrayList<MessagesData>();

		Cursor cursor = database.query(MessagesMySQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MessagesData message = cursorToMessage(cursor);
			if (message.getType().equals("Sent")) {
				messages.add(message);
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return messages;
	}

	public List<MessagesData> getDraftMessages() {
		List<MessagesData> messages = new ArrayList<MessagesData>();

		Cursor cursor = database.query(MessagesMySQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MessagesData message = cursorToMessage(cursor);
			if (message.getType().equals("Draft")) {
				messages.add(message);
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return messages;
	}
	
	private MessagesData cursorToMessage(Cursor cursor) {
		MessagesData message = new MessagesData();
		message.setId(cursor.getLong(0));
		message.setReceivers(cursor.getString(1));
		message.setTimeAndDate(cursor.getString(2));
		message.setFrequency(cursor.getString(3));
		message.setText(cursor.getString(4));
		message.setType(cursor.getString(5));
		message.setAlarmId(cursor.getInt(6));
		return message;
	}
	
}