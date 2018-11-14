package com.danny.tools.git.commit;
import org.eclipse.jgit.api.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;

public class AddUtils
{
	public static void addAllToStage(String path) throws GitAPIException {
		try {
			addAllToStage(RepositoryUtils.openRepository(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addAllToStage(Repository repository) throws GitAPIException {
		Git git = new Git(repository);
		git.add().addFilepattern(".").call();
	}
}
