package tk.ziniulian.job.inv.webapp;

import android.webkit.JavascriptInterface;

import tk.ziniulian.job.inv.qr.EmQrCb;
import tk.ziniulian.job.inv.qr.InfQrListener;
import tk.ziniulian.job.inv.qr.xc2910.Qrd;
import tk.ziniulian.util.EnumMgr;
import tk.ziniulian.util.webapp.WebHd;

/**
 * 二维码 Web 接口
 * Created by 李泽荣 on 2019/7/15.
 */

public class WebQr {
	private WebHd h;
	private Qrd qr = new Qrd();

	public WebQr (WebHd wh) {
		this.h = wh;
	}

	// 二维码设置
	public void initQr() {
		qr.setQrListenter(new InfQrListener() {
			@Override
			public void onRead(String content) {
				h.sendUrl(EnumMgr.getEmByK("EmUrl", "QrOnRead"), content);
			}

			@Override
			public void cb(EmQrCb e, String[] args) {
				// Log.i("--qr--", e.name());
				switch (e) {
					case ErrConnect:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "QrConnectErr"));
						break;
					case Connected:
						h.sendUh(EnumMgr.getEmByK("EmUrl", "QrConnected"));
						break;
				}
			}
		});
		qr.init();
	}

	public void open() {
		qr.open();
	}

	public void close() {
		qr.close();
	}

	public void qrDestroy() {
		qr.destroy();
	}

/*------------------- 接口 ---------------------*/

	@JavascriptInterface
	public boolean isQrBusy() {
		return qr.isBusy();
	}

	@JavascriptInterface
	public void qrScan() {
		qr.scan();
	}

	@JavascriptInterface
	public void qrStop() {
		qr.stop();
	}

}
