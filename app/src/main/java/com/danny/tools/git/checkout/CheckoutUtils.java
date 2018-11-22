package com.danny.tools.git.checkout;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import java.io.*;
import com.danny.tools.git.repository.*;

public class CheckoutUtils
{
	public static void checkoutBranch(Repository repository, String name) throws GitAPIException {
		Git git = new Git(repository);
		git.checkout().setName(name).call();
	}
	
	public static void checkoutBranch(File directory, String name) throws GitAPIException {
		try {
			checkoutBranch(RepositoryUtils.openRepository(directory), name);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void checkoutBranch(String path, String name) throws GitAPIException {
		try {
			checkoutBranch(RepositoryUtils.openRepository(path), name);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
