//database for messages
package ca.elman.messageme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MessagesMySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "messages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_RECEIVERS = "receivers";
	public static final String COLUMN_TEXT = "allText";
	public static final String COLUMN_TIME_AND_DATE = "timeAndDate";	
	public static final String COLUMN_FREQUENCY = "frequency";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_ALARM_ID = "alarmId";

	private static final String DATABASE_NAME = "messages.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_RECEIVERS
			+ " text, " + COLUMN_TIME_AND_DATE + " text, " + COLUMN_FREQUENCY + " text, " + COLUMN_TEXT + " text not null, " + COLUMN_TYPE + " text, " +  COLUMN_ALARM_ID + " integer );";

	public MessagesMySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MessagesMySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		onCreate(db);
	}

}