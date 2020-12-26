package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import java.io.File;
import java.util.ArrayList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
	// this class hosts a list that displays all user's subjects.
	// NOTIFY ALL BEFORE EDITING!

	// navbar vars
	private ImageView addCourseButton;
	private ImageView settingsLaunchButton;
	private ImageView remindersLaunchButton;

	// subjects list vars
	private RecyclerView coursesList;
	private RealmResults<Course> courses;

	// layout content vars
	private TextView noCoursesNotifier;

	// realm instance
	private Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Realm.init(this);
		//Realm.deleteRealm(Realm.getDefaultConfiguration());
		realm = Realm.getDefaultInstance();


		// preliminary list preparation work
		coursesList = findViewById(R.id.courses_list);
		coursesList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

		settingsLaunchButton = findViewById(R.id.settings_button);
		settingsLaunchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent addSubjectActivity = new Intent(MainActivity.this, SettingsActivity.class);
				startActivityForResult(addSubjectActivity, 0);
			}
		});

		addCourseButton = findViewById(R.id.add_course_button);
		addCourseButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent addSubjectActivity = new Intent(MainActivity.this, AddCourseActivity.class);
				startActivityForResult(addSubjectActivity, 0);
			}
		});

		remindersLaunchButton = findViewById(R.id.reminders_launch_button);
		remindersLaunchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MainActivity.this, RemindersActivity.class));
			}
		});

		noCoursesNotifier = findViewById(R.id.no_courses_notifier);

		getCourses();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getCourses();
	}

	private void getCourses() {
		RealmResults<Course> courses = realm.where(Course.class).findAll();
		this.courses = courses;

		// deciding when to show no subjects alert
		// < 2 because 1 folder is containing crash logs
		// and shouldn't be counted.
		if(courses.size() == 0)
		{
			coursesList.setVisibility(View.GONE);
			noCoursesNotifier.setVisibility(View.VISIBLE);
		}
		else
		{
			coursesList.setVisibility(View.VISIBLE);
			noCoursesNotifier.setVisibility(View.GONE);
		}

		// hooking up to list framework
		coursesList.setAdapter(new RecyclerViewAdapter(courses));
	}

	// related to subjects list
	public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
	{
		private RealmResults<Course> courses;

		public RecyclerViewAdapter(RealmResults<Course> courses)
		{
			this.courses = courses;
		}

		@Override
		public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			LayoutInflater inf = LayoutInflater.from(parent.getContext());
			return new RecyclerViewHolder(inf, parent);
		}

		@Override
		public void onBindViewHolder(RecyclerViewHolder holder, int position)
		{
			Course course = courses.get(position);
			holder.courseCode.setText("Code: " + course.code);
			holder.courseName.setText(getString(R.string.course_name_label) + ": " + course.name);


//			RealmList<Note> notes = courses.get(position).notes;
//			String[] courseGeneralInfo = note.getName().split("_");
//
//			holder.courseCode.setText(getString(R.string.course_code_label) + ": " + courseGeneralInfo[0]);
//			holder.courseName.setText(getString(R.string.course_name_label) + ": " + courseGeneralInfo[1]);
		}

		@Override
		public int getItemCount()
		{
			return courses != null ? courses.size() : 0;
		}
	}

	// related to subjects list
	public class RecyclerViewHolder extends RecyclerView.ViewHolder
	{
		public TextView courseCode;
		public TextView courseName;
		public LinearLayout courseSelector;

		public RecyclerViewHolder(LayoutInflater inf, ViewGroup parent)
		{
			super(inf.inflate(R.layout.course_item, parent, false));
			courseCode = itemView.findViewById(R.id.course_code_input);
			courseName = itemView.findViewById(R.id.course_name_input);
			courseSelector = itemView.findViewById(R.id.course_selector);

			if(courseSelector != null)
			{
				courseSelector.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Intent viewCourseActivity = new Intent(MainActivity.this, ViewCourseActivity.class);
						System.out.println(courses);
						viewCourseActivity.putExtra("courseId", courses.get(getLayoutPosition()).id);
						startActivityForResult(viewCourseActivity, 0);
					}
				});
			}
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

	// refining main activity on user's back press
	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogStyle)
				.setTitle(R.string.dialog_title_app_exit).setMessage(R.string.dialog_message_app_exit)
				.setNegativeButton(R.string.decision_no, null) // no action performed, not calling super
				.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface a, int b)
					{
						// exit app and remove app from recents. refined. complete termination
						// no need to call super method
						finishAndRemoveTask();
					}
				}).create().show();
	}
}