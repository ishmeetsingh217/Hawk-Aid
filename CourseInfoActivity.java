package com.c470.studentaid;

import  androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CourseInfoActivity extends AppCompatActivity {
	private ImageView addSyllabus;
	private TextView courseInfoTitle;
	private ImageView backButton;
	private String courseTitle;
	private String courseId;
	private Realm realm;
	private String name = "Course";
	private DownloadManager mManager;
	File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	Uri file = Uri.fromFile(path);

	WebView webview;
	ProgressBar progressbar;

	// Request code for selecting a PDF document.
	private static final int PICK_PDF_FILE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_info);

		courseId = getIntent().getStringExtra("courseId");

		Realm.init(this);
		realm = Realm.getDefaultInstance();

		Course course = realm.where(Course.class).equalTo("id", courseId).findFirst();

		courseInfoTitle = findViewById(R.id.course_info_title);
		courseInfoTitle.setText(course.name);

		backButton = findViewById(R.id.course_info_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// Adding Syllabus Function Method Here.
		addSyllabus = findViewById(R.id.add_syllabus_button);
		addSyllabus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openFile(file);
//				progressbar.setVisibility(View.GONE);
//
//				String filename = "https://s25.q4cdn.com/967830246/files/doc_downloads/test.pdf";
//				webview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + filename);
//				webview.setWebViewClient(new WebViewClient() {
//
//					public void onPageFinished(WebView view, String url) {
//						// do your stuff here
//						progressbar.setVisibility(View.GONE);
//					}
//				});

			}

		});

	}

	private void openFile(Uri pickerInitialUri) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("application/pdf");
		webview = (WebView) findViewById(R.id.webView);
		progressbar = (ProgressBar) findViewById(R.id.progressBar);

		PdfViewPager pdfViewPager = new PDFViewPager(this, "sample.pdf");
		setContentView(pdfViewPager);

		webview.getSettings().setJavaScriptEnabled(true);
				String filename = intent.toString();
				webview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + filename);
				webview.setWebViewClient(new WebViewClient() {
					public void onPageFinished(WebView view, String url) {
						// do your stuff here
						progressbar.setVisibility(View.GONE);
					}
				});

		intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

		startActivityForResult(intent, PICK_PDF_FILE);
	}

//	public void down() {
//		mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//		Uri downloadUri = Uri.parse("https://s25.q4cdn.com/967830246/files/doc_downloads/test.pdf");
//		DownloadManager.Request request = new DownloadManager.Request(
//				downloadUri)
//				.setAllowedOverRoaming(false)
//				.setTitle("Downloading")
//				.setDestinationInExternalPublicDir(
//						Environment.DIRECTORY_DOWNLOADS, name + "CV.pdf")
//				.setDescription("Download in progress").setMimeType("pdf");
//	}
//	@Override
//	protected void onResume() {
//		super.onResume();
//		IntentFilter intentFilter = new IntentFilter(
//				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//		registerReceiver(broadcast, intentFilter);
//	}
//
//	public void showPdf() {
//		try {
//			File file = new File(Environment.getExternalStorageDirectory()
//					+ "/Download/" + name + "Syllabus.pdf");//name here is the name of any string you want to pass to the method
//			if (!file.isDirectory())
//				file.mkdir();
//			Intent testIntent = new Intent("com.adobe.reader");
//			testIntent.setType("application/pdf");
//			testIntent.setAction(Intent.ACTION_VIEW);
//			Uri uri = Uri.fromFile(file);
//			testIntent.setDataAndType(uri, "application/pdf");
//			startActivity(testIntent);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	BroadcastReceiver broadcast = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			showPdf();
//		}
//	};
}


