package com.danny.tools.git.branch;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import java.io.*;
import com.danny.tools.git.repository.*;
import org.eclipse.jgit.api.errors.*;
import java.util.*;

public class BranchUtils
{
	public static final int TYPE_LOCAL = 1;
	public static final int TYPE_REMOTE = 2;
	
	public static String getCurrentBranch(Repository repository) throws IOException {
		return repository.getBranch();
	}
	
	public static String getCurrentBranch(File directory) throws IOException {
		return getCurrentBranch(RepositoryUtils.openRepository(directory));
	}
	
	public static String getCurrentBranch(String path) throws IOException {
		return getCurrentBranch(RepositoryUtils.openRepository(path));
	}
	
	public static List<Ref> getAllBranches(Repository repository) throws GitAPIException {
		Git git = new Git(repository);
		return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
	}
	
	public static List<Ref> getAllBranches(File directory) throws GitAPIException {
		try {
			return getAllBranches(RepositoryUtils.openRepository(directory));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Ref> getAllBranches(String path) throws GitAPIException {
		try {
			return getAllBranches(RepositoryUtils.openRepository(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Ref> getLocalBranches(Repository repository) throws GitAPIException {
		Git git = new Git(repository);
		return git.branchList().call();
	}
	
	public static List<Ref> getLocalBranches(File directory) throws GitAPIException {
		try {
			return getLocalBranches(RepositoryUtils.openRepository(directory));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Ref> getLocalBranches(String path) throws GitAPIException {
		try {
			return getLocalBranches(RepositoryUtils.openRepository(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Ref> getRemoteBranches(Repository repository) throws GitAPIException {
		Git git = new Git(repository);
		return git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
	}
	
	public static List<Ref> getRemoteBranches(File directory) throws GitAPIException {
		try {
			return getRemoteBranches(RepositoryUtils.openRepository(directory));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Ref> getRemoteBranches(String path) throws GitAPIException {
		try {
			return getRemoteBranches(RepositoryUtils.openRepository(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> getPureBranchList(List<Ref> branchList) {
		List<String> result = new ArrayList<>();
		for (Ref branch: branchList) {
			result.add(getPureBranchName(branch));
		}
		
		return result;
	}
	
	public static String getPureBranchName(Ref branch) {
		String sRawName = branch.getName();
		return getPureBranchName(sRawName);
	}
	
	public static String getPureBranchName(String rawName) {
		if (rawName.indexOf("refs/heads/") == 0)
			return rawName.substring(11);
		else if (rawName.indexOf("refs/") == 0)
			return rawName.substring(5);
		else
			return rawName;
	}
	
	public static int getBranchType(String branchName) {
		if (branchName.indexOf("refs/heads/") == 0)
			return TYPE_LOCAL;
		else if (branchName.indexOf("refs/remotes/") == 0)
			return TYPE_REMOTE;
		else
			return -1;
	}
}
