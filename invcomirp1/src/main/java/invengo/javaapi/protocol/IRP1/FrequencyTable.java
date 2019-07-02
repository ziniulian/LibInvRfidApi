package invengo.javaapi.protocol.IRP1;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FrequencyTable {

	String[] list;

	public String[] getList() {
		return list;
	}

	double f1;

	public double getF1() {
		return f1;
	}

	double dec;

	public double getDec() {
		return dec;
	}

	public enum Name {
		CN_840_845, CN_920_925, US_902_928, EU_865_868,NCC_922_927
	}

	public FrequencyTable(Name ftName) {
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.applyPattern("0.000");
		switch (ftName) {
		case CN_840_845:
			f1 = 840.625;
			dec = 0.25;
			list = new String[16];
			for (int i = 0; i < 16; i++) {
				list[i] = String.valueOf(f1 + i * dec);
			}
			break;
		case CN_920_925:
			f1 = 920.625;
			dec = 0.25;
			list = new String[16];
			for (int i = 0; i < 16; i++) {
				list[i] = String.valueOf(f1 + i * dec);
			}
			break;
		case US_902_928:
			f1 = 902.750;
			dec = 0.5;
			list = new String[50];
			for (int i = 0; i < 50; i++) {
				list[i] = df.format(f1 + i * dec);
			}
			break;
		case EU_865_868:
			f1 = 865.7;
            dec = 0.6;
            list = new String[4];
            for (int i = 0; i < 4; i++) {
            	list[i] = df.format(f1 + i * dec);
			}
			break;
		case NCC_922_927:
			 f1 = 922.25;
             dec = 0.5;
             list = new String[12];
             for (int i = 0; i < 12; i++)
             {
                 list[i] = df.format(f1 + i * dec);
             }
             break;
		}
	}
}
