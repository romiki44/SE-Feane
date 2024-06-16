package com.example.util;

import java.io.File;

public class AppUtil {
	public static String getUploadedPath(String filename) {
		return new File("src\\main\\resources\\static\\uploads").getAbsolutePath()+"\\"+filename;		
	}
}
