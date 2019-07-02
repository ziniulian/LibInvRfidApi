package com.invengo.lib.util;

import com.invengo.lib.diagnostics.InvengoLog;

/*
 * 1.7.2015081900 : ???
 * 1.8.2015111100 : Diagnostics에 ATLog, Dump 클래스 추가
 * 1.9.2015121600 : HILTI pre release릉 위해서 임시(StringUtil클래스에 String 배열 덤프용 getStringArray메서드 추가)
 * 1.10.2016011900 : ATLog 클래스에 파일 로그 기능 추가
 * 1.10.2016021800 : ATLog 로그파일 최소 4개 남기도록 수정.
 * 1.11.2016022400 : ATLog 로그파일 레벨별 남기도록 수정
 * 1.12.2016082500 : soundpool() 함수 SEGV 11 오류 발생 되어 Mediaplayer 로 변경.
 * 1.13.2016090200 : 1 로그 파일 큐가 비어있을 경우 Flush 처리 추가.
 *                   2 HexUtil - Exception throw 처리 추가. 
 * 1.14.2016092300 : StringUtil에 Null문자 및 Emtpry 문자 검색 기능 추가
 */

public class VersionUtil {
	
	private static final String TAG = VersionUtil.class.getSimpleName();
	
	private static final String VERSION = "1.0";
	
	// get SDK Version
	public static String getVersion() {
		InvengoLog.i(TAG, String.format("INFO. Library Version [atid.system.comm] : %s", VERSION));
		return VERSION;
	}
}
