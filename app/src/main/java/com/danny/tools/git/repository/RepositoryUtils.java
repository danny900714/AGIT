package com.danny.tools.git.repository;
import java.io.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.storage.file.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.revwalk.filter.*;

public class RepositoryUtils
{
	public static Repository openRepository(String path) throws IOException {
		return openRepository(new File(path));
	}
	
	public static Repository openRepository(File directory) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		return builder
				.setMustExist(true)
				.findGitDir(directory)
				.readEnvironment()
				.findGitDir()
				.build();
	}
	
	public static Git createNewRepository(String path) throws GitAPIException, IllegalStateException {
		return createNewRepository(new File(path));
	}
	
	public static Git createNewRepository(File directory) throws GitAPIException, IllegalStateException {
		return Git.init().setDirectory(directory).call();
	}
	
	public static void createNewRepositoryWithCommit(File directory) throws IllegalStateException, GitAPIException {
		Git git = Git.init().setDirectory(directory).call();
		git.commit().setMessage("Initial Commit").call();
	}
	
	public static void createNewRepositoryWithCommit(String path) throws IllegalStateException, GitAPIException {
		createNewRepositoryWithCommit(new File(path));
	}
}
