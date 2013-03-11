package ca.elman.messageme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockActivity;

class DateP {
	private int year, month, day;

	public DateP(int dYear, int dMonth, int dDay) {
		year = dYear;
		month = dMonth;
		day = dDay;
	}

	public String getYear() {
		return Integer.toString(year);
	}

	public int getMonth() {
		return month;
	}

	public String getDay() {
		return Integer.toString(day);
	}
}

class TimeP {
	private int hour, minute;

	public TimeP(int tHour, int tMinute) {
		hour = tHour;
		minute = tMinute;
	}

	public String getHour() {
		return Integer.toString(hour);
	}

	public String getMinute() {
		return Integer.toString(minute);
	}
}

public class SetDateAndTimeActivity extends SherlockActivity {

	private CheckBox sendNow, sendLater;
	private DatePicker dp;
	private TimePicker tm;
	private Button addResult, cancelResult;
	private Boolean now = false;
	public static DateP finalDP;
	public static TimeP finalTP;

	private void stopActivity(int reqCode) {
		Intent in = new Intent();
		setResult(reqCode, in);// Here I am Setting the RequestCode to reqCode
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_date_and_time);

		dp = (DatePicker) findViewById(R.id.datePicker1);
		tm = (TimePicker) findViewById(R.id.timePicker1);

		sendNow = (CheckBox) findViewById(R.id.checkBoxSendNow);
		sendLater = (CheckBox) findViewById(R.id.checkBoxSendAtSpecificTime);
		//add button at the bottom
		addResult = (Button) findViewById(R.id.addTimeToSend);

		addResult.setEnabled(false);

		sendNow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (sendNow.isChecked()) {
					addResult.setEnabled(true);
					now = true;
					sendLater.setChecked(false);
					dp.setEnabled(false);
					tm.setEnabled(false);

				} else {
					addResult.setEnabled(false);
				}
			}
		});

		sendLater.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (sendLater.isChecked()) {
					addResult.setEnabled(true);
					now = false;
					sendNow.setChecked(false);
					dp.setEnabled(true);
					tm.setEnabled(true);
				} else {
					addResult.setEnabled(false);
				}
			}
		});

		cancelResult = (Button) findViewById(R.id.cancelTimeToSend);

		addResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (now == true) {
					stopActivity(3); // setting reqCode to 3
				} else if (now == false) {

					finalDP = new DateP(dp.getYear(), dp.getMonth(), dp
							.getDayOfMonth());
					finalTP = new TimeP(tm.getCurrentHour(), tm
							.getCurrentMinute());
					stopActivity(4); // setting reqCode to 4
				}
			}

		});

		cancelResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopActivity(5); // setting reqCode to 5
			}

		});
	}

}
