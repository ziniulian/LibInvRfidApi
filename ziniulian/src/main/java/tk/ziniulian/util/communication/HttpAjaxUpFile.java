package tk.ziniulian.util.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import tk.ziniulian.util.Str;
import tk.ziniulian.util.webapp.WebHd;

/**
 * Ajax 上传文件
 * Created by LZR on 2019/4/15.
 */

public class HttpAjaxUpFile implements Runnable {
	private WebHd h = null;
	private String url;
	private String filNam;	// POST的内容
	private boolean busy = false;	// 繁忙

	private String pid;
	private String memo;
	private String uid;

	public HttpAjaxUpFile(WebHd hd) {
		this.h = hd;
	}

	public boolean post(String u, String fil, String id, String m, String uid) {
		if (!this.busy) {
			this.busy = true;
			this.url = u;
			this.filNam = fil;
			this.pid = id;
			this.memo = m;
			this.uid = uid;
			new Thread(this).start();
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		try {
			URL u = new URL(this.url);
			HttpURLConnection c = (HttpURLConnection)u.openConnection();
			c.setRequestMethod("POST");

			c.setDoInput(true);
			c.setUseCaches(false);
			c.setRequestProperty("Accept-Charset", "UTF-8");
			c.setRequestProperty("Content-Type", "multipart/form-data;boundary=ZnGpDtePMx0KLzr_G0X99Yef9rZiniulian");
			c.connect();
			DataOutputStream o = new DataOutputStream(c.getOutputStream());

			// 参数 pid
			o.writeBytes("--ZnGpDtePMx0KLzr_G0X99Yef9rZiniulian\r\n" +
					"Content-Disposition: form-data; name=\"pid\"\r\n" +
					"Content-Type: text/plain; charset=UTF-8\r\n" +
					"Content-Transfer-Encoding: 8bit\r\n\r\n");
			o.write(Str.Dat2Bytes(this.pid));

			// 参数 uid
			o.writeBytes("\r\n--ZnGpDtePMx0KLzr_G0X99Yef9rZiniulian\r\n" +
					"Content-Disposition: form-data; name=\"uid\"\r\n" +
					"Content-Type: text/plain; charset=UTF-8\r\n" +
					"Content-Transfer-Encoding: 8bit\r\n\r\n");
			o.write(Str.Dat2Bytes(this.uid));

			// 参数 memo
			o.writeBytes("\r\n--ZnGpDtePMx0KLzr_G0X99Yef9rZiniulian\r\n" +
					"Content-Disposition: form-data; name=\"memo\"\r\n" +
					"Content-Type: text/plain; charset=UTF-8\r\n" +
					"Content-Transfer-Encoding: 8bit\r\n\r\n");
			o.write(Str.Dat2Bytes(this.memo));

			//  文件部分
			File f = new File (this.filNam);
			o.writeBytes("\r\n--ZnGpDtePMx0KLzr_G0X99Yef9rZiniulian\r\n" +
					"Content-Disposition: form-data; name=\"myfile\"; filename=\"" + f.getName() + "\"\r\n" +
					"Content-Type: application/octet-stream\r\n" +
					"Content-Transfer-Encoding: binary\r\n\r\n");
			InputStream in = new FileInputStream(f);
			byte[] buf = new byte[1024];
			int size = 0;
			while ((size = in.read(buf)) != -1) {
				o.write(buf, 0, size);
			}

			// 结尾
			o.writeBytes("\r\n--ZnGpDtePMx0KLzr_G0X99Yef9rZiniulian--\r\n\r\n");
			o.flush();
			o.close();

			if (c.getResponseCode() == 200) {
				InputStream is = c.getInputStream();
				BufferedReader bf = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String s = "";
				do {
					sb.append(s);
					s = bf.readLine();
				} while (s != null);
				is.close();
				bf.close();
				c.disconnect();
				if (this.h != null) {
					this.h.sendMsg("EmUh", "Ajax", 1, 0, sb.toString());
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
