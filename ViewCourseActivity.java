package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmResults;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ViewCourseActivity extends AppCompatActivity
{
	// navbar vars
	private TextView courseTitle;
	private ImageView backButton;

	// layout content vars
	private String fileName;
	private TextView courseTitleLarge;
	private Button viewInfoButton;
	private Button viewNotesButton;
	private Button deleteCourseButton;

	private String courseId;
	private Realm realm;

	private EditText deletionActivator;
	private LinearLayout dialogInputContainerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_course);

		// get course id
		courseId = getIntent().getStringExtra("courseId");

		Realm.init(this);
		realm = Realm.getDefaultInstance();

		final Course course = realm.where(Course.class).equalTo("id", courseId).findFirst();

		// getting directory name passed from calling activity as string
//		fileName = getIntent().getStringExtra("COURSE");

		// set navbar title as course code
		courseTitle = findViewById(R.id.course_title_display);
		courseTitle.setText(course.name);

		// back button calls onBackPressed method
		backButton = findViewById(R.id.view_course_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		// set layout course description to courseCode: courseName
		courseTitleLarge = findViewById(R.id.course_title_display_large);
		courseTitleLarge.setText(course.name);

		viewInfoButton = findViewById(R.id.course_info_button);
		viewInfoButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent viewInfoActivity = new Intent(ViewCourseActivity.this, CourseInfoActivity.class);
				viewInfoActivity.putExtra("courseId", courseId);
				startActivity(viewInfoActivity);
			}
		});

		viewNotesButton = findViewById(R.id.course_notes_button);
		viewNotesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent viewNotesActivity = new Intent(ViewCourseActivity.this, ViewNotesActivity.class);
				viewNotesActivity.putExtra("courseId", courseId);
				startActivity(viewNotesActivity);
			}
		});

		// delete button, for safety, only activates on long click
		// goes to FileUtility for specialized directory deletion
		deleteCourseButton = findViewById(R.id.delete_course_button);
		deleteCourseButton.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				dialogInputContainerLayout = new LinearLayout(getApplicationContext());
				dialogInputContainerLayout.setOrientation(LinearLayout.VERTICAL);
				LinearLayout.LayoutParams params = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				params.setMargins(120, 0, 120, 0);
				deletionActivator = new EditText(getApplicationContext());
				// set input to 1 line
				deletionActivator.setMaxLines(1);
				// set input to be a maximum of 6 characters long
				deletionActivator.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
				dialogInputContainerLayout.addView(deletionActivator, params);

				new AlertDialog.Builder(ViewCourseActivity.this, R.style.CustomDialogStyle)
						.setTitle(R.string.ccd).setMessage(getString(R.string.ccd_msg) + "\n\nDELETE")
						.setView(dialogInputContainerLayout)
						.setNegativeButton(R.string.decision_no, null)
						.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface a, int b)
							{
								String delete = deletionActivator.getText().toString().trim();
								if(delete.equals("DELETE"))
								{
//									RealmResults<Course> course = realm.where(Course.class).and().equalTo('name', )

//									File course = new File(getFilesDir().toString() + "/courses/" + fileName);
//									new FileUtility().deleteCourse(course);

									realm.beginTransaction();
									if (course.getNotes().size() > 0) {
										course.getNotes().deleteAllFromRealm();
									}
									course.deleteFromRealm();
									realm.commitTransaction();
									realm.close();

									Toast.makeText(ViewCourseActivity.this, R.string.course_deleted_toast, Toast.LENGTH_SHORT).show();
									setResult(1);
									finish();
								}
								else
								{
									Toast.makeText(ViewCourseActivity.this, R.string.course_deletion_failed_toast, Toast.LENGTH_SHORT).show();
								}
							}
						}).create().show();
				return false;
			}
		});
	}

	// setting result to 0, then MainActivity will know no subjects were deleted
	// and will not unnecessary call recreate to update the unchanged list
	@Override
	public void onBackPressed()
	{
		setResult(0);
		super.onBackPressed();
	}
}