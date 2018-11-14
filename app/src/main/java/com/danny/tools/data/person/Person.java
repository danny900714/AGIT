package com.danny.tools.data.person;

public class Person
{
	long id;
	String name;
	String email;
	String profilePath;
	
	public Person() {
		
	}

	public Person(String name, String email)
	{
		this.name = name;
		this.email = email;
	}

	public Person(String name, String email, String profilePath)
	{
		this.name = name;
		this.email = email;
		this.profilePath = profilePath;
	}
	
	public long getId() {
		return id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return email;
	}

	public void setProfilePath(String profilePath)
	{
		this.profilePath = profilePath;
	}

	public String getProfilePath()
	{
		return profilePath;
	}
}
