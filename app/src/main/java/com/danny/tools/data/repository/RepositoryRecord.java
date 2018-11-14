package com.danny.tools.data.repository;
import java.util.*;
import android.util.*;

public class RepositoryRecord
{
	public static final int ID_UNDEFINED = 0;
	
	protected long id;
	protected String name;
	protected String path;
	
	public RepositoryRecord() {
	}

	public RepositoryRecord(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
