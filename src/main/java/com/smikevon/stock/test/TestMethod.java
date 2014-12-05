package com.smikevon.stock.test;

import java.util.Calendar;
import java.util.Locale;

public class TestMethod {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK)-1);
		
		System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
		System.out.println(calendar.get(Calendar.MINUTE));
		
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		
		System.out.println(""+week+hour+minute);
		
		
	}

}
