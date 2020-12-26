package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ViewNotesActivity extends AppCompatActivity
{
	// navbar vars
	private ImageView addNoteButton;
	private ImageView backButton;

	// notes list vars
	private RecyclerView notesList;
	private RealmList<Note> notes = new RealmList<>();

	// layout content vars
	private TextView noNotesNotifier;
	private String courseName;

	private Realm realm;
	private String courseId;

	private Course course;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_notes);

		// get course id
		courseId = getIntent().getStringExtra("courseId");

		Realm.init(this);
		realm = Realm.getDefaultInstance();

		course = realm.where(Course.class).equalTo("id", courseId).findFirst();

		// preliminary list preparation work
		notesList = findViewById(R.id.notes_list);
		notesList.setLayoutManager(new LinearLayoutManager(ViewNotesActivity.this));

		courseName = getIntent().getStringExtra("COURSE_FOLDER");

		backButton = findViewById(R.id.back_to_course_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		addNoteButton = findViewById(R.id.add_note_button);
		addNoteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent addNoteActivity = new Intent(ViewNotesActivity.this, AddNoteActivity.class);
//				addNoteActivity.putExtra("COURSE_FOLDER", courseName);
				addNoteActivity.putExtra("courseId", courseId);
				startActivityForResult(addNoteActivity, 0);
			}
		});

		noNotesNotifier = findViewById(R.id.no_notes_notifier);

		System.out.println(course.getNotes());
		notes = course.getNotes();

		// query subjects directory containing all subject subdirectories
//		File f = new File(getFilesDir().toString() + "/courses/" + courseName);

//		notes = new ArrayList<>();
//		notes.clear();
//
//		// list files contained within main app directory
//		File[] ff = f.listFiles();
//		for(File fff : ff)
//		{
//			notes.add(fff);
//		}

		if(notes == null || notes.size() == 0)
		{
			notesList.setVisibility(View.GONE);
			noNotesNotifier.setVisibility(View.VISIBLE);
		}
		else
		{
			notesList.setVisibility(View.VISIBLE);
			noNotesNotifier.setVisibility(View.GONE);
		}

		// hooking up to list framework
		notesList.setAdapter(new ViewNotesActivity.RecyclerViewAdapter(notes));
	}

	// related to subjects list
	public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewNotesActivity.RecyclerViewHolder>
	{
		private RealmList<Note> noteItems;

		public RecyclerViewAdapter(RealmList<Note> _noteItems)
		{
			noteItems = _noteItems;
		}

		@Override
		public ViewNotesActivity.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			LayoutInflater inf = LayoutInflater.from(parent.getContext());
			return new ViewNotesActivity.RecyclerViewHolder(inf, parent);
		}

		@Override
		public void onBindViewHolder(ViewNotesActivity.RecyclerViewHolder holder, int position)
		{
//			File note = noteItems.get(position);
//			String n = note.getName().substring(36);
			Note note = course.notes.get(position);
			String d = note.lastModified.toString();
			holder.noteName.setText(note.name);
			holder.noteLastModified.setText(getString(R.string.last_edited) + " " + d);
		}

		@Override
		public int getItemCount()
		{
			return noteItems == null ? 0 : noteItems.size();
		}
	}

	// related to subjects list
	public class RecyclerViewHolder extends RecyclerView.ViewHolder
	{
		public TextView noteName;
		public TextView noteLastModified;
		public ImageView noteRemoveButton;
		public LinearLayout noteSelector;

		public RecyclerViewHolder(LayoutInflater inf, ViewGroup parent)
		{
			super(inf.inflate(R.layout.note_item, parent, false));
			noteName = itemView.findViewById(R.id.note_name_disp);
			noteLastModified = itemView.findViewById(R.id.note_date_disp);
			noteRemoveButton = itemView.findViewById(R.id.remove_note_button);
			noteSelector = itemView.findViewById(R.id.note_selector);

			if(noteSelector != null)
			{
				noteSelector.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Intent viewNoteActivity = new Intent(ViewNotesActivity.this, ViewNoteActivity.class);
//						viewNoteActivity.putExtra("NOTE_LOCATION", courseName + "/" + notes.get(getLayoutPosition()).getName());
						viewNoteActivity.putExtra("noteId", notes.get(getLayoutPosition()).id);
						startActivityForResult(viewNoteActivity, 0);
					}
				});
			}

			noteRemoveButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					new AlertDialog.Builder(ViewNotesActivity.this, R.style.CustomDialogStyle)
							.setTitle(R.string.dndn).setMessage(R.string.dndn_msg)
							.setNegativeButton(R.string.decision_no, null)
							.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface a, int b)
								{
									realm.beginTransaction();
									course.getNotes().get(getLayoutPosition()).deleteFromRealm();
									realm.commitTransaction();

//									notes.get(getLayoutPosition()).delete();
									Toast.makeText(ViewNotesActivity.this, R.string.ndnd, Toast.LENGTH_SHORT).show();
									recreate();
								}
							}).create().show();
				}
			});
		}
	}

	// handle result from AddSubjectActivity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 0)
		{
			// result code of 1 means we added a subject and therefore
			// need to update the list to show updated list.
			if(resultCode == 1)
			{
				recreate();
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}
}