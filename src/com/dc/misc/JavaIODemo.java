package com.dc.misc;

import java.io.FileInputStream;
import java.io.IOException;

public class JavaIODemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("user.dir:" + System.getProperty("user.dir"));
		JavaIODemo obj = new JavaIODemo();

		obj.demoFileInputStream();
		
	}

	void demoFileInputStream() {
		int c;
		FileInputStream f = null;
		try {
			f = new FileInputStream("src/com/dc/misc/ascii.txt");
			while((c = f.read()) != -1) {
				System.out.println(c);
			}
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
