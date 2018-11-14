package com.danny.tools.git.commit;

import com.danny.tools.git.repository.*;
import java.io.*;
import java.util.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.revwalk.*;

public class LogUtils
{
	@Deprecated
	public static List<String> getCommitMessageInverse(String path) throws GitAPIException {
		ArrayList<String> logList = new ArrayList<>();

		try {
			Git git = new Git(RepositoryUtils.openRepository(path));
			Iterable<RevCommit> logs = git.log().call();
			for (RevCommit log: logs) {
				logList.add(log.getFullMessage());
			}

			return logList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	public static List<String> getCommitMessage(String path) throws GitAPIException {
		List<String> inverseCommit = getCommitMessageInverse(path);
		Collections.reverse(inverseCommit);
		return inverseCommit;
	}

	public static List<RevCommit> getAllLogsInverse(String path) throws GitAPIException {
		ArrayList<RevCommit> logList = new ArrayList<>();

		try {
			Git git = new Git(RepositoryUtils.openRepository(path));
			Iterable<RevCommit> logs = git.log().call();
			for (RevCommit log: logs) {
				logList.add(log);
			}

			return logList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<RevCommit> getAllLogs(String path) throws GitAPIException {
		List<RevCommit> inversedList = getAllLogsInverse(path);
		Collections.reverse(inversedList);
		return inversedList;
	}
}
