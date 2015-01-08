package jp.leopanda.common.client;

import jp.leopanda.common.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HostAuthServiceAsync {
  void loginCheck(String requestUri, AsyncCallback<LoginInfo> callback);

  void loginDetails(String token, AsyncCallback<LoginInfo> callback);

}
