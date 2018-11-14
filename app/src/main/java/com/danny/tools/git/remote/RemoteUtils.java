package com.danny.tools.git.remote;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import java.io.*;
import com.danny.tools.git.repository.*;
import java.util.*;

public class RemoteUtils
{
	public static void remoteAdd(Repository repository, String name, String url) throws IOException {
		StoredConfig config = repository.getConfig();
		config.setString("remote", name, "url", url);
		config.save();
	}
	
	public static void remoteAdd(File directory, String name, String url) throws IOException {
		remoteAdd(RepositoryUtils.openRepository(directory), name, url);
	}
	
	public static void remoteAdd(String path, String name, String url) throws IOException {
		remoteAdd(RepositoryUtils.openRepository(path), name, url);
	}
	
	public static String getRemoteUrl(Repository repository, String name) {
		StoredConfig config = repository.getConfig();
		return config.getString("remote", name, "url");
	}
	
	public static String getRemoteUrl(File directory, String name) {
		try {
			return getRemoteUrl(RepositoryUtils.openRepository(directory), name);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getRemoteUrl(String path, String name) {
		try {
			return getRemoteUrl(RepositoryUtils.openRepository(path), name);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> getAllRemoteUrl(Repository repository) {
		StoredConfig config = repository.getConfig();
		Set<String> remoteSet = config.getSubsections(ConfigConstants.CONFIG_REMOTE_SECTION);
		List<String> remoteList = new ArrayList<String>(remoteSet);
		return remoteList;
	}
	
	public static List<String> getAllRemoteUrl(File directory) {
		try {
			return getAllRemoteUrl(RepositoryUtils.openRepository(directory));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> getAllRemoteUrl(String path) {
		try {
			return getAllRemoteUrl(RepositoryUtils.openRepository(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
