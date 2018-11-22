package com.danny.tools.git.tag;
import java.util.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import java.io.*;
import com.danny.tools.git.repository.*;

public class TagUtils
{
	public static List<Ref> getAllTags(Repository repository) throws GitAPIException {
		Git git = new Git(repository);
		return git.tagList().call();
	}
	
	public static List<Ref> getAllTags(File directory) throws GitAPIException {
		try {
			return getAllTags(RepositoryUtils.openRepository(directory));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Ref> getAllTags(String path) throws GitAPIException {
		try {
			return getAllTags(RepositoryUtils.openRepository(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> getPureTagList(List<Ref> tagList) {
		List<String> result = new ArrayList<>();
		for (Ref tag: tagList) {
			String sRawName = tag.getName();
			if (sRawName.indexOf("refs/tags/") == 0) {
				String sName = sRawName.substring(10);
				result.add(sName);
			}
		}

		return result;
	}
}
