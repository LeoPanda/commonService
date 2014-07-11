package jp.leopanda.common.client;

import java.util.EventListener;

import jp.leopanda.common.client.GoogleLoginBar.InfoEvent;

public interface LoginBarListener extends EventListener {
  // ログインされた
  public void onLoggedIn(InfoEvent event);

  // ログオフされた
  public void onLoggedOff(InfoEvent event);
}
