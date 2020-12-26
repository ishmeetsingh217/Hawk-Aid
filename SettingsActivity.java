package com.c470.studentaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity
{
	// layout vars
	private ImageView backButton;
	private Button guideButton;
	private Button backupButton;
	private Button restoreButton;
	public static final String Courses = "Courses";

	private int REQUEST_EXTERNAL_STORAGE = 1;
	private String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

	private Realm realm;

	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		pref = getApplication().getApplicationContext().getSharedPreferences("MyPref", 0);

		Realm.init(this);
		realm = Realm.getDefaultInstance();

		backButton = findViewById(R.id.settings_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		guideButton = findViewById(R.id.guide_button);
		guideButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
			}
		});

		backupButton = findViewById(R.id.backup_button);
		backupButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new AlertDialog.Builder(SettingsActivity.this, R.style.CustomDialogStyle)
						.setTitle(R.string.backup_title).setMessage(R.string.backup_msg)
						.setNegativeButton(R.string.decision_no, null)
						.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface a, int b)
							{
								backup();
							}
						}).create().show();
			}
		});

		restoreButton = findViewById(R.id.restore_button);
		restoreButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new AlertDialog.Builder(SettingsActivity.this, R.style.CustomDialogStyle)
						.setTitle(R.string.restore_title).setMessage(R.string.restore_msg)
						.setNegativeButton(R.string.decision_no, null)
						.setPositiveButton(R.string.decision_yes, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface a, int b)
							{
								restore();
							}
						}).create().show();
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		setResult(0);
		super.onBackPressed();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			// at least 1 permission granted CONTINUE
		}
		else
		{
			Toast.makeText(SettingsActivity.this, R.string.storage_permission_toast, Toast.LENGTH_SHORT).show();
		}
	}

	public void backup()
	{
		int rwx = ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(rwx != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(SettingsActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
		}
		else
		{
			//new FileUtility().backupData(new File(getFilesDir() + ""));

			final RealmResults<Course> courses = realm.where(Course.class).findAll();
			System.out.println(courses);

			SharedPreferences.Editor editor = pref.edit();
			editor.putString(Courses, courses.asJSON());
			editor.commit();
			Toast.makeText(SettingsActivity.this,"Backup has been created",Toast.LENGTH_LONG).show();

		}
	}

	public void restore()
	{
		int rwx = ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(rwx != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(SettingsActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
		}
		else
		{
			//new FileUtility().resotreData(new File(getFilesDir() + ""));
			String coursesString = pref.getString(Courses, "empty");
			System.out.println(coursesString);

//			Course[] courses = new Gson().fromJson(coursesString, Course[].class);

//			System.out.println(courses);




			realm.beginTransaction();
			realm.createOrUpdateAllFromJson(Course.class, coursesString);
			realm.commitTransaction();
			Toast.makeText(SettingsActivity.this,"Backup has been restored",Toast.LENGTH_LONG).show();
		}
	}
}