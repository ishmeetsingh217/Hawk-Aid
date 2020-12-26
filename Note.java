package com.c470.studentaid;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Note extends RealmObject {

	public String name;
	public String noteValue;
	public String lastModified;

	@PrimaryKey
	public String id;

	public Note() {
		this.id = UUID.randomUUID().toString();
	}

	public Note(String name, String noteValue) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.noteValue = noteValue;
		this.lastModified = new Date().toString();
	}

}
