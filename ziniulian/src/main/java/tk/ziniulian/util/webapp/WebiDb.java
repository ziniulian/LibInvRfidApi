package tk.ziniulian.util.webapp;

import android.content.Context;
import android.webkit.JavascriptInterface;

import tk.ziniulian.util.dao.DbLocal;

/**
 * Lzr Web App 数据库基本接口
 * Created by 李泽荣 on 2019/7/11.
 */

public class WebiDb {
	private DbLocal ldao;

	public WebiDb (Context c, int version) {
		ldao = new DbLocal(c, version);
	}

	public void close () {
		ldao.close();
	}

/*------------------- 接口 ---------------------*/

	@JavascriptInterface
	public String kvGet(String k) {
		return ldao.kvGet(k);
	}

	@JavascriptInterface
	public void kvSet(String k, String v) {
		ldao.kvSet(k, v);
	}

	@JavascriptInterface
	public void kvDel(String k) {
		ldao.kvDel(k);
	}
}
