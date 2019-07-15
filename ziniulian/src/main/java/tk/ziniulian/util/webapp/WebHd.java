package tk.ziniulian.util.webapp;

import android.os.Handler;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import tk.ziniulian.util.EnumMgr;
import tk.ziniulian.util.Str;

/**
 * 与 WebView 关联的主线程通信的通用的消息机制
 * Created by 李泽荣 on 2019/7/5.
 */

public class WebHd extends Handler {
	private WebView wv;

	// 浏览器初始化
	public WebView initWv (WebView v) {
		this.wv = v;
		wv.setWebChromeClient(new WebChromeClient());
		WebSettings ws = wv.getSettings();
		ws.setDefaultTextEncodingName("UTF-8");
		// wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);	// 滚动条样式设置
		ws.setJavaScriptEnabled(true);
		return wv;
	}

	// 获取当前页面对应的枚举
	public Enum getCurUi () {
		if (this.wv == null) {
			return null;
		} else {
			return EnumMgr.getEmByK("EmUrl", wv.getTitle());
		}
	}

	// 基本的信息发送
	public boolean sendMsg (int what, int arg1, int arg2, Object o) {
		return sendMessage(obtainMessage(what, arg1, arg2, o));
	}

	// 基本的信息发送
	public boolean sendMsg (int what) {
		return sendMsg(what, 0, 0, null);
	}

	// 基本的信息发送（枚举反射）
	public boolean sendMsg (String cnam, String enam, int arg1, int arg2, Object o) {
		Integer ei = EnumMgr.getIdByK(cnam, enam);
		if (ei != null) {
			return sendMsg(ei, arg1, arg2, o);
		}
		return false;
	}

	// 页面跳转
	public void sendUrl (String url) {
		Integer w = EnumMgr.getIdByK("EmUh", "Url");
		if (w != null) {
			sendMsg(w, 0, 0, url);
		}
	}

	// 页面跳转
	public void sendUrl (Enum eurl) {
		sendUrl(eurl.toString());
	}

	// 页面跳转
	public void sendUrl (Enum eurl, String... args) {
		sendUrl(Str.meg(eurl.toString(), args));
	}

	// 发送页面处理消息
	public void sendUh (Enum euh) {
		sendMsg(euh.ordinal());
	}

	// 页面处理器 （针对实际的项目，需要覆盖此方法）
	@Override
	public void handleMessage(Message msg) {
		/*
		// 项目中的使用方式 ：
		EmUh e = EmUh.values()[msg.what];
		switch (e) {
			case Url:
				wv.loadUrl((String)msg.obj);
				break;
		}
		*/
		if (msg.what == EnumMgr.getIdByK("EmUh", "Url")) {
			wv.loadUrl((String)msg.obj);
		}
	}
}
