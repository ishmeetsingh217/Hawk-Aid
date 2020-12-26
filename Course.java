package com.c470.studentaid;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Course extends RealmObject {

	public String name;
	public String code;
	@PrimaryKey
	public String id;
	public RealmList<Note> notes;
	public Course() {}

	public Course(String name, String code) {
		this.name = name;
		this.code = code;
		this.notes = new RealmList<>();
	}

	public RealmList<Note> getNotes() {
		return
				notes == null || notes.size() == 0
				? new RealmList<Note>()
				: notes;
	}

}
