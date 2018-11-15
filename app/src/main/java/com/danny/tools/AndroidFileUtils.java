package com.danny.tools;
import android.net.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.nio.channels.*;
import android.webkit.*;

public class AndroidFileUtils
{
	public static final String ERROR_NOT_HANDLEABLE = AndroidFileUtils.class.getName() + ".ERROR_NOT_HANDLEABLE";
	private static final String URI_EXTERNAL_STORAGE = "com.android.externalstorage.documents";
	private static final String URI_DOWNLOAD = "com.android.providers.downloads.documents";
	private static final String URI_MEDIA = "com.android.providers.media.documents";
	
	public static String getPathFromUri(Uri uri){
		if (!uri.getScheme().equals("content"))
			return null;
		
		String sRawPath = uri.getLastPathSegment();
		int firstColonIndex = sRawPath.indexOf(':');
		if (firstColonIndex == -1 || firstColonIndex >= sRawPath.length() -1) {
			switch(uri.getAuthority()) {
				case URI_EXTERNAL_STORAGE:
					String type = (String) sRawPath.subSequence(0, firstColonIndex);
					if (type.equals("primary"))
						return Environment.getExternalStorageDirectory().toString();
					else
						return "/storage/" + type;
				case URI_DOWNLOAD:
					return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS;
				case URI_MEDIA:
					return ERROR_NOT_HANDLEABLE;
				default:
					return null;
			}
		}
		String type = (String) sRawPath.subSequence(0, firstColonIndex);
		String hiddenPath = sRawPath.substring(firstColonIndex + 1);
		
		if (uri.getAuthority().equals(URI_EXTERNAL_STORAGE)) {
			if (type.equals("primary"))
				return Environment.getExternalStorageDirectory().toString() + "/" + hiddenPath;
			else
				return "/storage/" + type + "/" + hiddenPath;
		} else if (uri.getAuthority().equals(URI_DOWNLOAD)) {
			return hiddenPath;
		} else if (uri.getAuthority().equals(URI_MEDIA)) {
			return ERROR_NOT_HANDLEABLE;
		}
		else
			return null;
	}
	
	public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
	
	public static String getMimeType(String path) {
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}
	
	public static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
