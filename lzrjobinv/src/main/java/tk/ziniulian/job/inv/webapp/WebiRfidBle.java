package tk.ziniulian.job.inv.webapp;

import android.webkit.JavascriptInterface;

import invengo.javaapi.communication.Ble;
import tk.ziniulian.job.inv.rfid.xc2910.RdBle;
import tk.ziniulian.util.EnumMgr;
import tk.ziniulian.util.communication.Blutos.BlutosDev;
import tk.ziniulian.util.webapp.WebHd;

/**
 *  BLE 蓝牙 RFID Web 接口
 * Created by 李泽荣 on 2019/7/11.
 */

public class WebiRfidBle extends WebiRfid {
	private Ble b;

	public WebiRfidBle (WebHd wh, Ble ble) {
		super(wh);
		this.b = ble;

		b.setScanDevEvt(new Ble.OnBleScanDevEvt() {
			@Override
			public void onScanDev(BlutosDev dev) {
				h.sendUrl(EnumMgr.getEmByK("EmUrl", "BleScanDev"), dev.toJson());
			}

			@Override
			public void onScanEnd() {
				h.sendUrl(EnumMgr.getEmByK("EmUrl", "BleScanEnd"));
			}
		});
	}

	@Override
	protected void crtRd() {
		rfd = new RdBle(b);
		rfd.setBank("tid");
		rfd.setHex(true);
	}

/*------------------- 接口 ---------------------*/

	@JavascriptInterface
	public void setDevAdr (String adr) {
		((RdBle)rfd).setDevAdr(adr);
	}

	@JavascriptInterface
	public void scanDevice () {
		b.scanDevice();
	}

}
