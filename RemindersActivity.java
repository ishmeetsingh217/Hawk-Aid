package com.c470.studentaid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RemindersActivity extends AppCompatActivity
{

	private final String allowedCharacters = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";

	private ImageView remindersBackButton;
	private ImageView addReminderButton;

	private DatePicker remDate;
	private TimePicker remTime;
	private GregorianCalendar gc;

	private DatePicker.OnDateChangedListener dc;

	private int reminderDay;
	private int reminderMonth;
	private int reminderYear;

	private EditText reminderTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminders);
		gc = new GregorianCalendar();

		remindersBackButton = findViewById(R.id.reminders_back_button);
		remindersBackButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		//reminderTitle = findViewById(R.id.reminder_title_input);

		addReminderButton = findViewById(R.id.add_reminder_button);
		addReminderButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				final View v = getLayoutInflater().inflate(R.layout.reminder_dialog, null);
				remDate = findViewById(R.id.reminder_date);
				remDate = new DatePicker(RemindersActivity.this);
				remDate.init(2020, 12, 2, new DatePicker.OnDateChangedListener()
				{
					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						gc.set(year, monthOfYear, view.getDayOfMonth());
					}
				});

				remTime = findViewById(R.id.reminder_time);
				remTime = new TimePicker(RemindersActivity.this);

				new AlertDialog.Builder(RemindersActivity.this)
						.setView(v)
						.setNegativeButton(R.string.decision_cancel, null)
						.setPositiveButton(R.string.decision_set, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface a, int b)
							{
								//saveReminderEntry(reminderTitle.getText().toString().toUpperCase().trim(), remDate);
								Toast.makeText(RemindersActivity.this, "Reminder Set", Toast.LENGTH_SHORT).show();
							}
						}).create().show();
			}
		});
	}
	public void saveReminderEntry(String reminderTitle, DatePicker remDate)
	{
		File reminderEntry = new File(getFilesDir() + "/reminders/" + reminderTitle + "_" + remDate.getMonth() + "-" + remDate.getDayOfMonth() + "-" + remDate.getYear());

		if(reminderEntry.exists())
		{
			new AlertDialog.Builder(RemindersActivity.this, R.style.CustomDialogStyle)
					.setTitle(R.string.dialog_title_add_reminder_duplicate).setMessage(R.string.dialog_message_add_reminder_duplicate)
					.setNegativeButton(R.string.decision_ok, null).create().show();
		}
		else
		{
			if(reminderTitle.equals(""))
			{
				new AlertDialog.Builder(RemindersActivity.this, R.style.CustomDialogStyle)
						.setTitle(R.string.dialog_title_add_reminder_empty_fields).setMessage(R.string.dialog_message_add_reminder_invalid_fields)
						.setNegativeButton(R.string.decision_ok, null).create().show();
			}
			else
			{
				int invalid = 0;
				String comparisonString = reminderTitle;
				int l = comparisonString.length();

				for(int i = 0; i < l; ++i)
				{
					if(!(allowedCharacters.contains(comparisonString.charAt(i) + "")))
					{
						invalid = 1;
						break;
					}
				}

				if(invalid == 1)
				{
					new AlertDialog.Builder(RemindersActivity.this, R.style.CustomDialogStyle)
							.setTitle(R.string.dialog_title_add_reminder_empty_fields).setMessage(R.string.dialog_message_add_reminder_invalid_fields)
							.setNegativeButton(R.string.decision_ok, null).create().show();
				}
				else
				{
					reminderEntry.mkdir();
					Toast.makeText(RemindersActivity.this, reminderTitle + " " + getString(R.string.subject_creation_successful_toast), Toast.LENGTH_SHORT).show();
					setResult(1);
					finish();
				}
			}
		}
	}
}