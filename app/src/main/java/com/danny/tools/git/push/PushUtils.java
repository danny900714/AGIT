package com.danny.tools.git.push;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.transport.*;
import com.danny.tools.git.remote.*;
import org.eclipse.jgit.api.errors.*;
import java.io.*;
import com.danny.tools.git.repository.*;

public class PushUtils
{
	public static void push(Repository repository, String name, String username, String password) throws GitAPIException {
		Git git = new Git(repository);
		String url = RemoteUtils.getRemoteUrl(repository, name);
		git.push().setRemote(url)
			.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
	}
	
	public static void push(File directory, String name, String username, String password) throws GitAPIException {
		try {
			push(RepositoryUtils.openRepository(directory), name, username, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void push(String path, String name, String username, String password) throws GitAPIException {
		try {
			push(RepositoryUtils.openRepository(path), name, username, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
