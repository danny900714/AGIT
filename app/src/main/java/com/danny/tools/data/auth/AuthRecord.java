package com.danny.tools.data.auth;

public class AuthRecord
{
	long id;
	String name;
	String remoteName;
	String userName;
	String password;
	boolean isIgnored;
	
	public AuthRecord() {
		
	}

	public AuthRecord(String name, String remoteName, String userName, String password, boolean isIgnored)
	{
		this.name = name;
		this.remoteName = remoteName;
		this.userName = userName;
		this.password = password;
		this.isIgnored = isIgnored;
	}
	
	AuthRecord(long id, String name, String remoteName, String userName, String password, boolean isIgnored) {
		this.id = id;
		this.name = name;
		this.remoteName = remoteName;
		this.userName = userName;
		this.password = password;
		this.isIgnored = isIgnored;
	}

	public void setIsIgnored(boolean isIgnored)
	{
		this.isIgnored = isIgnored;
	}

	public boolean isIgnored()
	{
		return isIgnored;
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

	public void setRemoteName(String remoteName)
	{
		this.remoteName = remoteName;
	}

	public String getRemoteName()
	{
		return remoteName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return password;
	}
}
