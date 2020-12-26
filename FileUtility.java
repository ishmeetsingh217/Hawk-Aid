package com.c470.studentaid;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import androidx.core.app.ActivityCompat;

public class FileUtility
{
	public String saveNote(File note, String noteContent)
	{
		if(noteContent.equals(""))
		{
			noteContent = " ";
		}

		try
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(note), StandardCharsets.UTF_8));
			bw.write(noteContent);
			bw.close();

			return "";
		}
		catch(IOException e)
		{
			return "ERROR @ " + new Date().toString() + ": " + e.getMessage();
		}
	}

	public String loadNote(File note)
	{
		String content = "";

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(note), StandardCharsets.UTF_8));

			char[] buffer = new char[128];
			int reader = br.read(buffer, 0, 128);

			while(reader != -1)
			{
				content += new String(buffer);
				reader = br.read(buffer, 0, 128);
			}
			br.close();

			return content;
		}
		catch(IOException e)
		{
			return "ERROR @ " + new Date().toString() + ": " + e.getMessage();
		}
	}

	public void deleteCourse(File course)
	{
		File[] ff = course.listFiles();
		for(File f : ff)
		{
			f.delete();
		}
		course.delete();
	}

	public void backupData(File data)
	{
		File bp = new File(data.getAbsolutePath() + "/" + "new");
		bp.mkdir();
	}

	public void resotreData(File data)
	{
		File bp = new File(data.getAbsolutePath() + "/" + "new");
		bp.mkdir();
	}



}

