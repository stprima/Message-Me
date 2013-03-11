package ca.elman.messageme;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KeywordsMySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_KEYWORDS = "keywords";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TEXT = "text";

	private static final String DATABASE_NAME = "keywords.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_KEYWORDS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_TITLE
			+ " text not null , " + COLUMN_TEXT + " text not null );";

	public KeywordsMySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		
		ContentValues values1 = new ContentValues();
		values1.put(KeywordsMySQLiteHelper.COLUMN_TITLE, "First_Name");
		values1.put(KeywordsMySQLiteHelper.COLUMN_TEXT, "");
		database.insert(TABLE_KEYWORDS, null, values1);
		
		ContentValues values2 = new ContentValues();
		values2.put(KeywordsMySQLiteHelper.COLUMN_TITLE, "Birthday");
		values2.put(KeywordsMySQLiteHelper.COLUMN_TEXT, "Happy Birthday #First_Name");
		database.insert(TABLE_KEYWORDS, null, values2);
		
		ContentValues values3 = new ContentValues();
		values3.put(KeywordsMySQLiteHelper.COLUMN_TITLE, "New_Year");
		values3.put(KeywordsMySQLiteHelper.COLUMN_TEXT, "Happy New Year #First_Name");
		database.insert(TABLE_KEYWORDS, null, values3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(KeywordsMySQLiteHelper.class.getName(),
			"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEYWORDS);
		onCreate(db);
	}

}