package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import io.realm.Realm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

public class ViewNoteActivity extends AppCompatActivity
{
	// navbar vars
	private TextView noteTitle;
	private ImageView noteBackButton;
	private ImageView noteEditButton;
	private ImageView optionsButton;

	// layout vars
	private ScrollView viewingLayout;
	private ScrollView editingLayout;
	private TextView noteViewSpace;
	private EditText noteEditSpace;
	private String notePath;
	private File noteFile;
	private String noteName;
	private String viewContent;
	private String originContent;
	private int editing = 0;

	private Realm realm;
	private String noteId;
	private Note note;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_note);

		// get note id
		noteId = getIntent().getStringExtra("noteId");

		Realm.init(this);
		realm = Realm.getDefaultInstance();

//		Course course = realm.where(Course.class).equalTo("id", courseId).findFirst();
//		Note note = course.notes.get(0);

		note = realm.where(Note.class).equalTo("id", noteId).findFirst();

//		notePath = getIntent().getStringExtra("NOTE_LOCATION");
//		noteName = notePath.split("/")[1].substring(36);
//		noteFile = new File(FilegetFilesDir().toString() + "/courses/" + notePath);

		noteTitle = findViewById(R.id.note_view_title_disp);
		noteTitle.setText(note.name);

		optionsButton = findViewById(R.id.note_view_menu_button);
		noteBackButton = findViewById(R.id.note_view_back_button);
		viewingLayout = findViewById(R.id.view_mode_layout);
		editingLayout = findViewById(R.id.edit_mode_layout);
		noteViewSpace = findViewById(R.id.note_view_space);
		noteEditSpace = findViewById(R.id.note_edit_space);

		viewContent = originContent = note.noteValue.trim();//new FileUtility().loadNote(noteFile).trim();

		noteViewSpace.setText(note.noteValue);

		noteEditButton = findViewById(R.id.note_edit_button);
		noteEditButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(editing == 0)
				{
					editing = 1;
				}
				else
				{
					viewContent = noteEditSpace.getText().toString().trim().replaceAll("\t", "    ");
					editing = 0;
				}
				updateView();
			}
		});

		noteBackButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		optionsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				PopupMenu menu = new PopupMenu(ViewNoteActivity.this, v);

				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(MenuItem item)
					{
						int selection = noteEditSpace.getSelectionStart();
						String nc = noteEditSpace.getText().toString();
						String ncc = "";
						int offs = 0;

						if(item.getItemId() == R.id.insert_one)
						{
							ncc = nc.substring(0, selection) + "    ";
							offs = ncc.length();
							ncc += nc.substring(selection);
							noteEditSpace.setText(ncc);
							noteEditSpace.setSelection(offs);
						}
						if(item.getItemId() == R.id.insert_two)
						{
							ncc = nc.substring(0, selection) + "§";
							offs = ncc.length();
							ncc += nc.substring(selection);
							noteEditSpace.setText(ncc);
							noteEditSpace.setSelection(offs);
						}
						if(item.getItemId() == R.id.insert_three)
						{
							ncc = nc.substring(0, selection) + "•";
							offs = ncc.length();
							ncc += nc.substring(selection);
							noteEditSpace.setText(ncc);
							noteEditSpace.setSelection(offs);
						}
						if(item.getItemId() == R.id.insert_four)
						{
							ncc = nc.substring(0, selection) + "»";
							offs = ncc.length();
							ncc += nc.substring(selection);
							noteEditSpace.setText(ncc);
							noteEditSpace.setSelection(offs);
						}

						return false;
					}
				});

				MenuInflater menuInflater = menu.getMenuInflater();
				menuInflater.inflate(R.menu.note_menu, menu.getMenu());
				menu.show();
			}
		});
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	public void updateView()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		if(editing == 0)
		{
			noteBackButton.setImageDrawable(getResources().getDrawable(R.drawable.back_icon, null));
			noteEditButton.setImageDrawable(getResources().getDrawable(R.drawable.edit_icon, null));
			optionsButton.setVisibility(View.GONE);
			editingLayout.setVisibility(View.GONE);
			if(!(viewContent.equals(noteViewSpace.getText().toString().trim())))
			{
				noteViewSpace.setText(viewContent);



				Toast.makeText(ViewNoteActivity.this, R.string.edits_saved, Toast.LENGTH_SHORT).show();
			}
			viewingLayout.setVisibility(View.VISIBLE);
			imm.hideSoftInputFromWindow(noteEditSpace.getWindowToken(), 0);
		}
		else
		{
			noteBackButton.setImageDrawable(getResources().getDrawable(R.drawable.cancel_icon, null));
			noteEditButton.setImageDrawable(getResources().getDrawable(R.drawable.save_icon, null));
			optionsButton.setVisibility(View.VISIBLE);
			viewingLayout.setVisibility(View.GONE);
			editingLayout.setVisibility(View.VISIBLE);
			noteEditSpace.setText(viewContent);
			noteEditSpace.requestFocus();
			imm.showSoftInput(noteEditSpace, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	@Override
	public void onBackPressed()
	{
		if((!(originContent.equals(viewContent))) && editing == 0)
		{
			realm.beginTransaction();
			note.noteValue = viewContent;
			note.lastModified = new Date().toString();
			realm.commitTransaction();

			setResult(1);
			finish();
		}
		else if(editing == 1)
		{
			new AlertDialog.Builder(ViewNoteActivity.this, R.style.CustomDialogStyle)
					.setTitle(R.string.discard_edits_dialog_title).setMessage(R.string.discard_edits_dialog_message)
					.setNegativeButton(R.string.decision_no, null)
					.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface a, int b)
						{
							viewContent = noteViewSpace.getText().toString().trim();
							editing = 0;
							Toast.makeText(ViewNoteActivity.this, R.string.edits_discarded, Toast.LENGTH_SHORT).show();
							updateView();
						}
					}).create().show();
		}
		else if(editing == 0)
		{
			super.onBackPressed();
		}
	}
}