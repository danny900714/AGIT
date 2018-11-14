package com.danny.tools.git.commit;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import java.util.*;

public class CommitUtils
{
	public static void commit(Repository repository, PersonIdent author, PersonIdent committer, String message) throws GitAPIException {
		Git git = new Git(repository);
		git.commit().setAll(true).setAuthor(author).setCommitter(committer).setMessage(message).call();
	}
	
	public static void commit(File directory, PersonIdent author, PersonIdent committer, String message) throws GitAPIException {
		try {
			commit(RepositoryUtils.openRepository(directory), author, committer, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void commit(String path, PersonIdent author, PersonIdent committer, String message) throws GitAPIException {
		try {
			commit(RepositoryUtils.openRepository(path), author, committer, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void commitAll(Repository repository, PersonIdent author, PersonIdent committer, String message) throws GitAPIException {
		AddUtils.addAllToStage(repository);
		commit(repository, author, committer, message);
	}
	
	public static void commitAll(File directory, PersonIdent author, PersonIdent committer, String message) throws GitAPIException {
		try {
			commitAll(RepositoryUtils.openRepository(directory), author, committer, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void commitAll(String path, PersonIdent author, PersonIdent committer, String message) throws GitAPIException {
		try {
			commitAll(RepositoryUtils.openRepository(path), author, committer, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
