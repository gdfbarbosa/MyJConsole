package br.com.movbr.jvm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Main {
	public static void main(String[] args) {
		System.out.println(TimeZone.getDefault());
		System.out.println(converteDataParaString(new Date(), "dd/MM/yyyy HH:mm:ss"));
	}

	public static String converteDataParaString(Date data, String format) {
		if (data == null)
			return null;
		
		DateFormat df = new SimpleDateFormat(format, new Locale( "pt", "BR" ));
		return df.format(data);
	}
}
