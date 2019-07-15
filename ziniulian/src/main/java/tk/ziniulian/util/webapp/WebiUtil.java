package tk.ziniulian.util.webapp;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Lzr Web App 常用 Web 接口
 * Created by 李泽荣 on 2019/7/11.
 */

public class WebiUtil {
	// 测试输出
	@JavascriptInterface
	public void log(String msg) {
		Log.i("---- Web ----", msg);
	}
}
