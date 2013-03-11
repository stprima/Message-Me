// database for keywords object
package ca.elman.messageme;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class KeywordsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private KeywordsMySQLiteHelper dbHelper;
	private String[] allColumns = { KeywordsMySQLiteHelper.COLUMN_ID,
			KeywordsMySQLiteHelper.COLUMN_TITLE, KeywordsMySQLiteHelper.COLUMN_TEXT };

	public KeywordsDataSource(Context context) {
		dbHelper = new KeywordsMySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public KeywordsData createKeyword(KeywordsData keyword) {
		ContentValues values = new ContentValues();
		values.put(KeywordsMySQLiteHelper.COLUMN_TITLE, keyword.getTitle());
		values.put(KeywordsMySQLiteHelper.COLUMN_TEXT, keyword.getText());
		long insertId = database.insert(KeywordsMySQLiteHelper.TABLE_KEYWORDS, null,
				values);
		
		Cursor cursor = database.query(KeywordsMySQLiteHelper.TABLE_KEYWORDS,
				allColumns, KeywordsMySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		KeywordsData newKeyword = cursorToKeyword(cursor);
		cursor.close();
		return newKeyword;
	}

	public void deleteKeyword(KeywordsData keyword) {
		long id = keyword.getId();
		database.delete(KeywordsMySQLiteHelper.TABLE_KEYWORDS, KeywordsMySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	// Updating single keyword
	public int updateKeyword(KeywordsData keyword) {

		ContentValues values = new ContentValues();
		values.put(KeywordsMySQLiteHelper.COLUMN_TITLE, keyword.getTitle());
		values.put(KeywordsMySQLiteHelper.COLUMN_TEXT, keyword.getText());

		// updating row
		return database.update(KeywordsMySQLiteHelper.TABLE_KEYWORDS, values, KeywordsMySQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(keyword.getId()) });
	}

	public List<KeywordsData> getAllKeywords() {
		List<KeywordsData> keywords = new ArrayList<KeywordsData>();

		Cursor cursor = database.query(KeywordsMySQLiteHelper.TABLE_KEYWORDS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			KeywordsData keyword = cursorToKeyword(cursor);
			keywords.add(keyword);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return keywords;
	}

	private KeywordsData cursorToKeyword(Cursor cursor) {
		KeywordsData keyword = new KeywordsData();
		keyword.setId(cursor.getLong(0));
		keyword.setTitle(cursor.getString(1));
		keyword.setText(cursor.getString(2));
		return keyword;
	}
	
}
