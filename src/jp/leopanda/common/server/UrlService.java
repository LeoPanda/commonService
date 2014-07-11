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
 * 外部サーバーへのHTTP接続をサポートする。 java.net.HttpUrlConnectionではGAE上でCookieのハンドリングがうまくできなかったため、
 * com.google.appengine.api.urlfetchを使用している。
 * ex)呼び出し方はDecoratedに UrlService urlService = new UrlService(); Results results =
 * urlService.fetchGet(UrlString, addCookie(CookieString, addToken(tokenString,
 * setHeader(urlServie.ContentType.HTML))));
 * 
 * 
 * @author LeoPanda
 *
 */
public class UrlService {
  // 条件定数
  public enum ContentType {
    HTML("html/text;charset=UTF8"), XML("text/xml;charset=utf-8"), JSON(
        "application/json;charset=UTF8"), POST_FORM(
        "application/x-www-form-urlencoded;charset=UTF-8"), ATOM(
        "application/atom+xml;charset=UTF8;");
    public final String value;
    private ContentType(String value) {
      this.value = value;
    }
  }

  // 静的定数
  // public final String ERROR_STRINGS = "ERROR:";//Error Strings
  private final String HTTPHEAD_GETCOOKIE = "Set-Cookie"; // 受信httpヘッダーからcookieを取り出すためのキー
  private final String HTTPHEAD_SETCOOKIE = "Cookie";// 送信httpヘッダーへcookieをセットするためのキー

  /**
   * HTTPヘッダの設定
   */
  public Map<String, String> setHeader(ContentType contentType) {
    Map<String, String> header = new HashMap<String, String>();
    header.put("Content-type", contentType.value);
    return header;
  }

  /**
   * 認証トークンのセット
   */
  public Map<String, String> addToken(String token, Map<String, String> header) {
    header.put("Authorization", "Bearer " + token);
    return header;
  }

  /**
   * 送信cookieのセット
   */
  public Map<String, String> addCookie(String cookieString, Map<String, String> header) {
    header.put(HTTPHEAD_SETCOOKIE, cookieString);
    return header;
  }

  /**
   * HTTP Get処理
   */
  public Results fetchGet(String urlStr, Map<String, String> header) {
    URLFetchService ufs = URLFetchServiceFactory.getURLFetchService();
    URL url = getUrl(urlStr);
    FetchOptions fetchOptions = setCommonFetchOptions();
    HTTPRequest request = setRequestHeader(header, new HTTPRequest(url, HTTPMethod.GET,
        fetchOptions));
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
   * HTTP PUT処理 エンコード済みの文字列を外部ホストへポストする。
   */
  public Results fetchPost(String urlStr, byte[] postByte, Map<String, String> header) {

    URLFetchService ufs = URLFetchServiceFactory.getURLFetchService();
    URL url = getUrl(urlStr);
    FetchOptions fetchOptions = setCommonFetchOptions();
    HTTPRequest request = setRequestHeader(header, new HTTPRequest(url, HTTPMethod.POST,
        fetchOptions));
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
   */
  public byte[] encodeFormToString(Map<String, String> formEnties) {
    StringBuffer sb = new StringBuffer();
    Iterator<String> formEntry = formEnties.keySet().iterator();
    boolean isAppend = false;
    while (formEntry.hasNext()) {
      String fieldName = formEntry.next();
      // 複数のフォーム値は"&"でつなぐ
      if (isAppend) {
        sb.append("&");
      } else {
        isAppend = true;
      }
      // フィールド名
      String fieldValue = formEnties.get(fieldName);
      try {
        sb.append(URLEncoder.encode(fieldName, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      sb.append("=");
      // フィールド値
      try {
        sb.append(URLEncoder.encode(fieldValue, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    return sb.toString().getBytes();
  }

  /**
   * HTTP Request ヘッダーのセット
   */
  private HTTPRequest setRequestHeader(Map<String, String> header, HTTPRequest request) {
    Iterator<String> it = header.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      request.addHeader(new HTTPHeader(key, header.get(key)));
    }
    return request;
  }

  /**
   * URL取得
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
   */
  private FetchOptions setCommonFetchOptions() {
    FetchOptions fetchOptions = FetchOptions.Builder.disallowTruncate();
    fetchOptions.setDeadline(10.0);
    fetchOptions.doNotFollowRedirects();
    return fetchOptions;
  }

  /**
   * HTTP Fetchのレスポンスからコンテキストを取り出す。
   */
  private Results getResponseVals(HTTPResponse response) {
    if (response == null) {
      return null;
    }
    Results result = new Results();
    result.setRetCode(response.getResponseCode());
    try {
      result.setBody(new String(response.getContent(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    result.setCookie(getCookie(response));
    return result;
  }

  /**
   * レスポンスのHTTPヘッダーからcookieを取り出す ヘッダーにセットされたcookie全体なので、cookie単体のキーと値には分離されていない。
   */
  private String getCookie(HTTPResponse response) {
    String cookie = null;
    for (HTTPHeader header : response.getHeaders()) {
      if (header.getName().equals(HTTPHEAD_GETCOOKIE)) {
        cookie = header.getValue();
      }
    }
    return cookie;
  }
}
