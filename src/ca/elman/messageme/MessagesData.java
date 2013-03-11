// messages class itself

package ca.elman.messageme;

public class MessagesData {

	private long id;
	private int alarmID;
	private String receivers, text, timeAndDate, frequency, type;
	private Boolean selected = false;

	public MessagesData(long mId, String mReceivers, String mText, String mTimeAndDate, String mFrequency, String mType) {
		id = mId;
		receivers = mReceivers;
		text = mText;
		timeAndDate = mTimeAndDate;
		frequency = mFrequency;
		type = mType;
		alarmID = -1;
	}
	
	public MessagesData(long mId, String mReceivers, String mText, String mTimeAndDate, String mFrequency, String mType, int alarm_id) {
		id = mId;
		receivers = mReceivers;
		text = mText;
		timeAndDate = mTimeAndDate;
		frequency = mFrequency;
		type = mType;
		alarmID = alarm_id;
	}
	
	public MessagesData(String mReceivers, String mText, String mTimeAndDate, String mFrequency, String mType) {
		receivers = mReceivers;
		text = mText;
		timeAndDate = mTimeAndDate;
		frequency = mFrequency;
		type = mType;
		alarmID = -1;
	}
	
	public MessagesData(String mReceivers, String mText, String mTimeAndDate, String mFrequency, String mType, int alarm_id) {
		receivers = mReceivers;
		text = mText;
		timeAndDate = mTimeAndDate;
		frequency = mFrequency;
		type = mType;
		alarmID = alarm_id;
	}
	
	public MessagesData() {
		
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setReceivers(String rec) {
		receivers = rec;
	}
	
	public String getReceivers() {
		return receivers;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setTimeAndDate(String stad) {
		timeAndDate = stad;
	}
	
	public String getTimeAndDate() {
		return timeAndDate;
	}
	
	public void setFrequency(String freq) {
		frequency = freq;
	}
	
	public String getFrequency() {
		return frequency;
	}
	
	public void setType(String tp) {
		type = tp;
	}
	
	public String getType() {
		return type;
	}
	
	public void setAlarmId(int id) {
		this.alarmID = id;
	}
	
	public int getAlarmId() {
		return alarmID;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean getSelected() {
		return selected;
	}
}
