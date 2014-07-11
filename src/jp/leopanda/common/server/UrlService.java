package jp.leopanda.common.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * 外部サーバーへのHTTP接続をサポートする。
 * java.net.HttpUrlConnectionではGAE上でCookieのハンドリングがうまくできなかったため、
 * com.google.appengine.api.urlfetchを使用している。
 * 
 * ex)呼び出し方はDecoratedに
 *  	UrlService urlService = new UrlService();
 * 		Map<Result,String> results = 
 * 						urlService.fetchGet(UrlString,
 * 									addCookie(CookieString,
 * 									addToken(tokenString,
 * 									setHeader(urlServie.ContentType.HTML))));
 * 
 * 戻り値の取り方は
 * String retcode = results.get(urlServie.result.RETCODE);
 * 
 * @author LeoPanda
 *
 */
public class UrlService {
	//条件定数
	public enum Result{RETCODE,BODY,COOKIE};
	public enum ContentType{
		HTML("html/text;charset=UTF8"),
		XML("text/xml;charset=utf-8"),
		JSON("application/json;charset=UTF8"),
		POST_FORM("application/x-www-form-urlencoded;charset=UTF-8"),
		ATOM("application/atom+xml;charset=UTF8;");
		public final String value;
		private ContentType(String value){
			this.value = value;
		}
	};
	//静的定数
//	public 	final String ERROR_STRINGS = "ERROR:";//Error Strings
	private final String HTTPHEAD_GETCOOKIE = "Set-Cookie"; //受信httpヘッダーからcookieを取り出すためのキー
	private final String HTTPHEAD_SETCOOKIE = "Cookie";//送信httpヘッダーへcookieをセットするためのキー
	/**
	 * HTTPヘッダの設定
	 */
	public Map<String,String> setHeader(ContentType contentType){
		Map<String, String> Header = new HashMap<String,String>();
		Header.put("Content-type", contentType.value);
		return Header;
	}
	/**
	 * 認証トークンのセット
	 * @param token
	 * @param header
	 * @return
	 */
	public Map<String,String> addToken(String token,Map<String,String> header){
		header.put("Authorization", "Bearer "+ token);
		return header;
	}
	/**
	 * 送信cookieのセット
	 * @param cookieString
	 * @param header
	 * @return
	 */
	public Map<String,String> addCookie(String cookieString,Map<String,String> header){
		header.put(HTTPHEAD_SETCOOKIE, cookieString);
		return header;
	}
	/**
	 * HTTP Get処理
	 * @param urlStr String 外部ホストのURL
	 * @param Map<String,String> header  HTTPヘッダ
	 * @return Map<Result,String>
	 * 		   key:RETCODE リターンコード。　
	 * 		   key:BODY　ボディ。 リターンコード200以外はエラー前置詞をつけてコードを返す。
	 * 		　　key:COOKIE 外部ホストから返されたcookie
	 */
	public  Map<Result,String> fetchGet(String urlStr,Map<String,String> header){
		URLFetchService ufs = URLFetchServiceFactory.getURLFetchService();
		URL url = getUrl(urlStr);
		FetchOptions fetchOptions = setCommonFetchOptions();	
		HTTPRequest request = 
				setRequestHeader(header, new HTTPRequest(url, HTTPMethod.GET, fetchOptions));
		HTTPResponse response = null;
		try {
			response = ufs.fetch(request);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return getResponseVals(response);
	}
	/**
	 * HTTP PUT処理
	 * エンコード済みの文字列を外部ホストへポストする。
	 * 
	 * @param urlStr　String 外部ホストのURL
	 * @param postByte byte エンコード済みPOSTボディ
	 * @param Map<String,String> header  HTTPヘッダ
	 * @return Map<Result,String>
	 * 		   key:RETCODE リターンコード。　
	 * 		   key:BODY　ボディ。 リターンコード200以外はエラー前置詞をつけてコードを返す。
	 * 		　　key:COOKIE 外部ホストから返されたcookie
	 * @throws IOException
	 */
	public Map<Result, String> fetchPost(String urlStr,byte[] postByte,
													Map<String,String> header) {

		URLFetchService ufs = URLFetchServiceFactory.getURLFetchService();
		URL url = getUrl(urlStr);
		FetchOptions fetchOptions = setCommonFetchOptions();
		HTTPRequest request = 
				setRequestHeader(header, new HTTPRequest(url, HTTPMethod.POST, fetchOptions));
	    request.setPayload(postByte);
		HTTPResponse response = null;
		try {
			response = ufs.fetch(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getResponseVals(response);
	}
	/**
	 * POSTするFormデータを文字列にエンコードする
	 * @param formEntries <フィールド名,値>
	 * @return String post文字列
	 */
	public byte[] encodeFormToString(Map<String,String> formEnties) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> formEntry = formEnties.keySet().iterator();
		boolean isAppend = false; 
		while (formEntry.hasNext()) {
			String fieldName = formEntry.next();
			//複数のフォーム値は"&"でつなぐ
			if(isAppend){
				sb.append("&");
			}else{
				isAppend = true;				
			}
			//フィールド名
			String fieldValue = formEnties.get(fieldName);
			try {
				sb.append(URLEncoder.encode(fieldName,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sb.append("=");
			//フィールド値
			try {
				sb.append(URLEncoder.encode(fieldValue, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		    return sb.toString().getBytes();
	}

	/** HTTP Request ヘッダーのセット
	 * 
	 * @param header
	 * @param request
	 * @return　HTTPRequest
	 */
	private HTTPRequest setRequestHeader(Map<String, String> header,
			HTTPRequest request) {
		Iterator<String> it = header.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			request.addHeader(new HTTPHeader(key, header.get(key)));
		}
		return request;
	}
	/**URL取得
	 * 
	 * @param urlStr
	 * @return
	 */
	private URL getUrl(String urlStr) {
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	/**
	 * Http　Fetchの共通オプションをセットする。
	 * @return FetchOptions
	 */
	private  FetchOptions setCommonFetchOptions() {
		FetchOptions fetchOptions = FetchOptions.Builder.disallowTruncate();
		fetchOptions.setDeadline(10.0);
		fetchOptions.doNotFollowRedirects();
		return fetchOptions;
	}
	/**
	 * HTTP Fetchのレスポンスからコンテキストを取り出す。
	 * @param response HTTPResponse
	 * @return Map<Result,String>
	 * 		   key:RETCODE リターンコード。　
	 * 		   key:BODY　ボディ。 
	 * 		　　key:COOKIE 外部ホストから返されたcookie
	 */
	private  Map<Result,String> getResponseVals(HTTPResponse response) {
		Map<Result,String> returns = new HashMap<Result,String>();
		int retcode = response.getResponseCode();
		returns.put(Result.RETCODE, String.valueOf(retcode));
		try {
			returns.put(Result.BODY,new String(response.getContent(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		returns.put(Result.COOKIE,getCookie(response));
		return returns;
	}
	/**
	 * レスポンスのHTTPヘッダーからcookieを取り出す
	 * ヘッダーにセットされたcookie全体なので、cookie単体のキーと値には分離されていない。
	 * @param response
	 * @return
	 */
	private  String getCookie(HTTPResponse response) {
		String cookie = null;
		for (HTTPHeader header : response.getHeaders() ) {
			  if(header.getName().equals(HTTPHEAD_GETCOOKIE)){
				  cookie = header.getValue();
			  }
			}
		return cookie;
	}
}
	
