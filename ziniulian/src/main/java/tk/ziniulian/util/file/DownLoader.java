package tk.ziniulian.util.file;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import tk.ziniulian.util.webapp.WebHd;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * 安卓下载器
 * Created by LZR on 2019/4/17.
 */

public class DownLoader {
	private WebHd h = null;
	private Context c;
	private DownLoaderDb db = null;
	private DownloadCompleteReceiver receiver = null;

	public DownLoader (Context c) {
		this.c = c;
	}

	public void setDb(DownLoaderDb db) {
		this.db = db;
	}

	public DownLoader setH(WebHd h) {
		this.h = h;
		return this;
	}

	// 获取文件类型
	public String getMIMEType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//Log.i("-----extension:{}", extension + "");
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
//Log.i("-----type:{}", type + "");
		if (TextUtils.isEmpty(type)) {
			type = "*/*";
		}
		return type;
	}

	// 通过系统下载器下载文件
	public void downloadBySystem (String url, String fileName, String fid) {
		// 指定下载地址
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		// 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
		request.allowScanningByMediaScanner();

		/*
		 * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
		 *    VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
		 *    VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
		 *    VISIBILITY_HIDDEN:                    始终不显示通知
		 */
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		// 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
		// 设置通知栏的描述
//        request.setDescription("This is description");
		// 允许在计费流量下下载
		request.setAllowedOverMetered(true);
		// 允许该记录在下载管理界面可见
		request.setVisibleInDownloadsUi(true);
		// 允许漫游时下载
		request.setAllowedOverRoaming(true);

		/*
		 * 设置允许使用的网络类型, 可选值:
		 *     NETWORK_MOBILE:      移动网络
		 *     NETWORK_WIFI:        WIFI网络
		 *     NETWORK_BLUETOOTH:   蓝牙网络
		 * 默认为所有网络都允许
		 */
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

		// 设置下载文件保存的路径和文件名
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
		// 设置标题
		request.setTitle(fileName);
		final DownloadManager downloadManager = (DownloadManager) this.c.getSystemService(DOWNLOAD_SERVICE);
		// 添加一个下载任务
		long downloadId = downloadManager.enqueue(request);
//Log.i("-----downloadId:{}", downloadId + "");

		if (this.db != null) {
			this.db.addFl(fid, downloadId, getMIMEType(fileName));
		}
	}

	// 获取下载监听
	public void setRcBc () {
		if (this.receiver == null) {
			this.receiver = new DownloadCompleteReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			this.c.registerReceiver(this.receiver, intentFilter);
		}
	}

	// 取消下载监听
	public void dropRcBc () {
		if (this.receiver != null) {
			this.c.unregisterReceiver(this.receiver);
			this.receiver = null;
		}
	}

	public void openFilByDid (Context context, Long downloadId) {
		DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
		String type = null;
		if (this.db != null) {
			type = this.db.getTyp(downloadId);
		} else {
			type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
		}

//Log.i("-----typ-----", type + "");
		Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
//Log.i("-----Uri:{}", uri.toString() + "," + uri.getPath());
		if (uri != null) {
			Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
			if (Build.VERSION.SDK_INT >= 24) {	//判读版本是否在7.0以上
				handlerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);	//添加这一句表示对目标应用临时授权该Uri所代表的文件
			}
			handlerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			handlerIntent.setDataAndType(uri, type);
			try {
				context.startActivity(handlerIntent);
			} catch (Exception e) {
				if (this.h != null) {
					this.h.sendMsg("EmUh", "Err", 0, 0, "无法打开文件！");
				}
			}
		}
	}

	// 下载管理器回调
	public class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
//Log.i("-----onReceive:{}", (intent != null ? intent.toUri(0) : null) + "");
			if (intent != null) {
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
					long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//Log.i("-----downloadId:{}", downloadId + "");
					openFilByDid(context, downloadId);
				}
			}
		}
	}
}
