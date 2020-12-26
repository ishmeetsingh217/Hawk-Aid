package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class AddNoteActivity extends AppCompatActivity
{
	private EditText noteTitle;
	private ImageView noteSaveButton;
	private ImageView noteDiscardButton;

	private EditText noteContent;

	private String courseFolder;

	private DatabaseReference mDatabase;

	private Realm realm;
	private String courseId;
	public Course course = new Course();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note);

		// get course id
		courseId = getIntent().getStringExtra("courseId");

		Realm.init(this);
		realm = Realm.getDefaultInstance();
		course = realm.where(Course.class).equalTo("id", courseId).findFirst();

		noteTitle = findViewById(R.id.note_title_input);

		noteContent = findViewById(R.id.note_content);

		courseFolder = getIntent().getStringExtra("COURSE_FOLDER");

		noteSaveButton = findViewById(R.id.save_note_button);
		noteSaveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				saveNote();
			}
		});

		noteDiscardButton = findViewById(R.id.discard_note_button);
		noteDiscardButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		// get firebase reference
//		mDatabase = FirebaseDatabase.getInstance().getReference();
	}

	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(AddNoteActivity.this, R.style.CustomDialogStyle)
				.setTitle(R.string.d_note).setMessage(R.string.r_u_sure_d_note)
				.setNegativeButton(R.string.decision_no, null)
				.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface a, int b)
					{
						Toast.makeText(AddNoteActivity.this, R.string.note_note_added, Toast.LENGTH_SHORT).show();
						finish();
					}
				}).create().show();
	}

	public void saveNote()
	{
		final String title = noteTitle.getText().toString().trim();
		if(title.equals(""))
		{
			new AlertDialog.Builder(AddNoteActivity.this, R.style.CustomDialogStyle)
					.setTitle(R.string.ms_n).setMessage(R.string.ms_n_msg)
					.setNegativeButton(R.string.decision_ok, null).create().show();
		}
		else
		{
			new AlertDialog.Builder(AddNoteActivity.this, R.style.CustomDialogStyle)
					.setTitle(R.string.save_n).setMessage(R.string.save_n_msg)
					.setNegativeButton(R.string.decision_no, null)
					.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface a, int b)
						{
							String content = noteContent.getText().toString().trim().replaceAll("\t", "    ");

//							Note note = new Note(noteTitle.getText().toString(), content);

							realm.beginTransaction();
							Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
							note.name = noteTitle.getText().toString();
							note.noteValue = content;
							note.lastModified = new Date().toString();

							course.notes.add(note);
							realm.commitTransaction();

							System.out.println(course.notes.size());
							System.out.println(note);


//							File f = new File(getFilesDir().toString() + "/courses/" + courseFolder + "/" + UUID.randomUUID().toString() + title);
//							String s = new FileUtility().saveNote(f, content);
//							if(s.equals(""))
//							{
								Toast.makeText(AddNoteActivity.this, R.string.n_saved, Toast.LENGTH_SHORT).show();
								setResult(1);
								finish();
//
//								//
//								Note newNote = new Note(noteTitle.getText().toString(), noteContent.getText().toString());
//								String noteKey = mDatabase.child("note").push().getKey();
//								mDatabase.child("note").child(noteKey).setValue(newNote);
//							}
//							else
//							{
//								// crash log
//							}
						}
					}).create().show();
		}
	}
}