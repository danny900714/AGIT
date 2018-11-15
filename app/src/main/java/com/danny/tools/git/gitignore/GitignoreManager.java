package com.danny.tools.git.gitignore;
import android.content.*;
import java.util.*;
import java.io.*;
import android.widget.*;
import com.danny.tools.*;

public class GitignoreManager
{
	public static String PATH = "gitignore";
	public static String EXTENTION = ".gitignore";
	
	private Context context;
	private List<String> languageList;
	
	public GitignoreManager(Context context) {
		this.context = context;
	}
	
	public List<String> getLanguageList() {
		if (languageList != null)
			return languageList;
		
		List<String> result = null;
		
		try {
			String[] childrenArray = context.getAssets().list(PATH);
			List<String> fileList = Arrays.asList(childrenArray);
			result = fileToLanguage(fileList);
		} catch (IOException e){
			e.printStackTrace();
		}
		
		languageList = result;
		
		return result;
	}
	
	public void copyGitignoreTo(String language, String path) {
		String realFileName = language + EXTENTION;
		
		InputStream in = null;
		OutputStream out = null;
		
		try {
			in = context.getAssets().open(PATH + File.separator + realFileName);
			File outFile = new File(path);
			out = new FileOutputStream(outFile);
			AndroidFileUtils.copyFile(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<String> fileToLanguage(List<String> fileList) {
		List<String> result = new ArrayList<>();
		
		for (String name: fileList) {
			int index = name.lastIndexOf(EXTENTION);
			if (index < 0)
				continue;
			
			String language = name.substring(0, index);
			result.add(language);
		}
		
		return result;
	}
}
