package tk.ziniulian.util.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import tk.ziniulian.util.Str;
import tk.ziniulian.util.webapp.WebHd;

/**
 * Ajax
 * Created by LZR on 2019/4/15.
 */

public class HttpAjax implements Runnable {
	private WebHd h = null;
	private String url;
	private String method;
	private String content;	// POST的内容
	private boolean busy = false;	// 繁忙

	public HttpAjax (WebHd hd) {
		this.h = hd;
	}

	public void get(String u) {
		if (!this.busy) {
			this.busy = true;
			this.url = u;
			this.method = "GET";
			new Thread(this).start();
		}
	}

	public void post(String u, String c) {
		if (!this.busy) {
			this.busy = true;
			this.url = u;
			this.method = "POST";
			this.content = c;
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		try {
			URL u = new URL(this.url);
			HttpURLConnection c = (HttpURLConnection)u.openConnection();
			c.setRequestMethod(this.method);
			if (this.method.equals("POST")) {	// POST
				c.setDoInput(true);
				c.setUseCaches(false);
				c.setRequestProperty("Accept-Charset", "UTF-8");
				c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				c.connect();
				DataOutputStream o = new DataOutputStream(c.getOutputStream());
				if (this.content.length() > 0) {
					o.write(Str.Dat2Bytes(this.content));
				}
				o.flush();
				o.close();
			} else {	// GET
				c.connect();
			}
			if (c.getResponseCode() == 200) {
				InputStream is = c.getInputStream();
				BufferedReader bf = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String s = "";
				do {	// 读取输入流
					sb.append(s);
					s = bf.readLine();
				} while (s != null);
				is.close();
				bf.close();
				c.disconnect();
				if (this.h != null) {
					this.h.sendMsg("EmUh", "Ajax", 0, 0, sb.toString());
				}
			}
		} catch (Exception e) {
			if (this.h != null) {
				this.h.sendMsg("EmUh", "Err", 0, 0, "网络连接失败！");
			}
		} finally {
			this.busy = false;
		}
	}
}
