package jp.leopanda.common.server;

/**
 * urlserviceの戻り値保持クラス
 * 
 * @author LeoPanda
 *
 */
public class Results {
  private String body = "";
  private int retCode = 0;
  private String cookie = "";

  public String getBody() {
    return body;
  }

  public int getRetCode() {
    return retCode;
  }

  public String getCookie() {
    return cookie;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void setRetCode(int retCode) {
    this.retCode = retCode;
  }

  public void setCookie(String cookie) {
    this.cookie = cookie;
  }

}
