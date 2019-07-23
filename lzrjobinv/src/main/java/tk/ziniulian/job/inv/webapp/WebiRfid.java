package tk.ziniulian.job.inv.webapp;

import android.webkit.JavascriptInterface;

import tk.ziniulian.job.inv.rfid.EmCb;
import tk.ziniulian.job.inv.rfid.EmPushMod;
import tk.ziniulian.job.inv.rfid.InfTagListener;
import tk.ziniulian.job.inv.rfid.tag.T6C;
import tk.ziniulian.job.inv.rfid.xc2910.Rd;
import tk.ziniulian.util.EnumMgr;
import tk.ziniulian.util.webapp.WebHd;

/**
 * RFID Web 接口
 * Created by 李泽荣 on 2019/7/11.
 */

public class WebiRfid {
	protected WebHd h;
	protected Rd rfd;

	public WebiRfid (WebHd wh) {
		this.h = wh;
	}

	// 提取出不同部分，方便子类修改
	protected void crtRd() {
		rfd = new Rd();
		rfd.setPwd(new byte[] {0x20, 0x26, 0x31, 0x07});
		rfd.setHex(true);
	}

	// 获取读写器
	public Rd getRd () {
		return rfd;
	}

	// 读写器设置
	public void initRd () {
		crtRd();
		rfd.setPm(EmPushMod.Catch);
		rfd.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(T6C bt, InfTagListener itl) {}

			@Override
			public void onWrtTag(T6C bt, InfTagListener itl) {
				h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfWrtOk"));
			}

			@Override
			public void cb(EmCb e, String[] args) {
				// Log.i("--rfd--", e.name());
				switch (e) {
					case Scanning:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfScaning"));
						break;
					case Stopped:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfStoped"));
						break;
					case ErrWrt:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfWrtErr"));
						break;
					case ErrConnect:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfConnectErr"));
						break;
					case Connected:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfConnected"));
						break;
					case DisConnected:
						h.sendUrl(EnumMgr.getEmByK("EmUrl", "RfDisConnected"));
						break;
				}
			}
		});
		rfd.init();
	}

/*------------------- 接口 ---------------------*/

	@JavascriptInterface
	public void open() {
		rfd.open();
	}

	@JavascriptInterface
	public void close() {
		rfd.close();
	}

	@JavascriptInterface
	public boolean isRfidBusy () {
		return rfd.isBusy();
	}

	@JavascriptInterface
	public void rfidScan() {
		rfd.scan();
	}

	@JavascriptInterface
	public void rfidStop() {
		rfd.stop();
	}

	@JavascriptInterface
	public void rfidWrt (String bankNam, String dat, String tid) {
		rfd.wrt(bankNam, dat, tid);
	}

	@JavascriptInterface
	public String rfidCatchScanning() {
		return rfd.catchScanning();
	}

	@JavascriptInterface
	public boolean setBank(String bankNam) {
		return rfd.setBank(bankNam);
	}

}
