package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.security.AlgorithmParameterGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddCourseActivity extends AppCompatActivity
{
	// characters that are allowed in the course code and course name
	private final String allowedCharacters = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";

	// navbar vars
	private ImageView discardSubjectButton;

	// layout vars
	private Button saveSubjectButton;
	private EditText subjectName;
	private EditText subjectCode;

//	private DatabaseReference mDatabase;
	private Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_course);

		// config subject name and code
		subjectName = findViewById(R.id.course_name_input);
		subjectCode = findViewById(R.id.course_code_input);

//		// get firebase reference
//		mDatabase = FirebaseDatabase.getInstance().getReference();
//		System.out.println(mDatabase);
		Realm.init(this);
		realm = Realm.getDefaultInstance();

		// config save subject button
		saveSubjectButton = findViewById(R.id.save_subject_button);
		saveSubjectButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new AlertDialog.Builder(AddCourseActivity.this, R.style.CustomDialogStyle)
						.setTitle(R.string.dialog_title_add_subject_confirm).setMessage(R.string.dialog_message_add_subject_confirm)
						.setNegativeButton(R.string.decision_no, null)
						.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface a, int b)
							{
								saveSubjectEntry(subjectCode.getText().toString().toUpperCase().trim(), subjectName.getText().toString().toUpperCase().trim());
							}
						}).create().show();
			}
		});

		// config discard subject button, same action as back button press
		discardSubjectButton = findViewById(R.id.discard_subject_button);
		discardSubjectButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});
	}

	// on back pressed by user, we direct user to either save or discard subject entry.
	// no need to call super method
	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(AddCourseActivity.this, R.style.CustomDialogStyle)
				.setTitle(R.string.dialog_title_discard_subject_confirm).setMessage(R.string.dialog_message_discard_subject_confirm)
				.setNegativeButton(R.string.decision_no, null)
				.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface a, int b)
					{
						setResult(0);
						finish();
					}
				}).create().show();
	}

	// saving subject entry handler
	// checks for entry name validity
	// checks for duplicate entry conflict
	// saves entry upon checks pass
	public void saveSubjectEntry(String entryCode, String entryName)
	{
//		File subjectEntry = new File(getFilesDir() + "/courses/" + entryCode + "_" + entryName);
		Course findCourse = realm.where(Course.class).equalTo("code", entryCode).findFirst();

		if(findCourse != null)
		{
			new AlertDialog.Builder(AddCourseActivity.this, R.style.CustomDialogStyle)
					.setTitle(R.string.dialog_title_add_subject_duplicate).setMessage(R.string.dialog_message_add_subject_duplicate)
					.setNegativeButton(R.string.decision_ok, null).create().show();
		}
		else
		{
			if(entryCode.equals("") || entryName.equals(""))
			{
				new AlertDialog.Builder(AddCourseActivity.this, R.style.CustomDialogStyle)
						.setTitle(R.string.dialog_title_add_subject_empty_fields).setMessage(R.string.dialog_message_add_subject_invalid_fields)
						.setNegativeButton(R.string.decision_ok, null).create().show();
			}
			else
			{
				int invalid = 0;
				String comparisonString = entryCode + entryName;
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
					new AlertDialog.Builder(AddCourseActivity.this, R.style.CustomDialogStyle)
							.setTitle(R.string.dialog_title_add_subject_invalid_chars).setMessage(R.string.dialog_message_add_subject_invalid_chars)
							.setNegativeButton(R.string.decision_ok, null).create().show();
				}
				else
				{
//					subjectEntry.mkdir();
					Toast.makeText(AddCourseActivity.this, entryCode + ": " + entryName + " " + getString(R.string.subject_creation_successful_toast), Toast.LENGTH_SHORT).show();
					setResult(1);

					// save to database
					Realm.init(this);
					Realm realm = Realm.getDefaultInstance();

					realm.beginTransaction();
					Course course = realm.createObject(Course.class, UUID.randomUUID().toString());
					course.name = subjectName.getText().toString();
					course.code = subjectCode.getText().toString();
					realm.commitTransaction();
					realm.close();
					// end saving

					finish();
				}
			}
		}
	}
}