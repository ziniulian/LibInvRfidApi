package invengo.javaapi.protocol.IRP1;

public class PowerTable {
	double[] list;

	public double[] getList() {
		return list;
	}

	double p1;

	public double getP1() {
		return p1;
	}

	double dec;

	public double getDec() {
		return dec;
	}

	public PowerTable() {
		p1 = 20;
		dec = 0.5;
		list = new double[21];
		for (int i = 0; i < list.length; i++) {
			list[i] = p1 + i * dec;
		}
	}
}
