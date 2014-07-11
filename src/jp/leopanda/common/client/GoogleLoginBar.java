package jp.leopanda.common.client;

import java.util.EventObject;

import jp.leopanda.common.shared.LoginInfo;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.api.gwt.oauth2.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Google　OAuth2 認証のためのパネルを作成するクラス Google OAuth2認証を実装し、googleサービスへ接続するために必要な認証tokenを提供する。
 * 
 * @author LeoPanda
<<<<<<< HEAD
 * <pre>
 *使用するには
 *  1)Google cloud（https://cloud.google.com/console/project）
 *  　にアクセスし、プロジェクトを作成してクライアントIDを取得する。
 *  2)Goole cloud のAPIコンソールへアクセスし、OAuthのリダイレクトURIを登録する。
 *  ローカルでテストを行うためには
 *  http://127.0.0.1:8888/(プロジェクト名)/oauthWindow.html
 *  GAEにデプロイして実行するためには
 *  http://(GAEのURI)/(プロジェクト名)/oauthWindow.html
 *  を登録する。
 * 	3)gwt.xml上に　<inherits name="jp.leopanda.common.Common" />を追加する。
 *  4)cssに .login-xxxを追加する。(このパッケージのwar上にあるサンプルを参照）
 *  ことがそれぞれ必要。
 *  5)web.xmlに以下を記述
 *  <servlet>
 *   <servlet-name>HostAuthServiceImpl</servlet-name>
 *   <servlet-class>jp.leopanda.common.server.HostAuthServiceImpl</servlet-class>
 *  </servlet>
 *  <servlet-mapping>
 *   <servlet-name>HostAuthServiceImpl</servlet-name>
 *   <url-pattern>/(アプリ固有のパス)/HostAuthService</url-pattern>
 *  </servlet-mapping>
 *
 * プロジェクトのEntry pointにあるOnModuleload()メソッドから呼ばれることを想定している。
 * 呼び出し例)　 GoogleLoginBar loginBar = new GoogleLoginBar(clientID,
 * 			   							GoogleLoginBar.addScope(BLOGGER)+
 * 										GoogleLoginBar.addScope(BLOGGERV2));
 * 			　　loginBar.addListerner(new LogInControl());
 * 			   RootPanel.get("loginBarContainer").add(loginBar);
 *
 * 認証tokenの取り出しは、ログイン完了後にこのクラスのgetToken()メソッドでおこなう。
 * 呼び出し元でログイン時/ログオフ時の処理を記述するために、addListener(listener<Class>)メソッドでイベントハンドラを追加できる。
 *</pre>
=======
 * 
 * <pre>{@code
 * 使用するには
 *   1)Google cloud（https://cloud.google.com/console/project）
 *   　にアクセスし、プロジェクトを作成してクライアントIDを取得する。
 *   2)Goole cloud のAPIコンソールへアクセスし、OAuthのリダイレクトURIを登録する。
 *   ローカルでテストを行うためには
 *   http://127.0.0.1:8888/(プロジェクト名)/oauthWindow.html
 *   GAEにデプロイして実行するためには
 *   http://(GAEのURI)/(プロジェクト名)/oauthWindow.html
 *   を登録する。
 *   3)gwt.xml上に　{@code <inherits name="jp.leopanda.common.Common" />}を追加する。
 *   4)cssに .login-xxxを追加する。(このパッケージのwar上にあるサンプルを参照）
 *   ことがそれぞれ必要。
 *   5)web.xmlに以下を記述
 *   <servlet>
 *    <servlet-name>HostAuthServiceImpl</servlet-name>
 *    <servlet-class>jp.leopanda.common.server.HostAuthServiceImpl</servlet-class>
 *   </servlet>
 *   <servlet-mapping>
 *    <servlet-name>HostAuthServiceImpl</servlet-name>
 *    <url-pattern>/(アプリ固有のパス)/HostAuthService</url-pattern>
 *   </servlet-mapping>
 *  プロジェクトのEntry pointにあるOnModuleload()メソッドから呼ばれることを想定している。
 *  呼び出し例)
 *  　 GoogleLoginBar loginBar = new GoogleLoginBar(clientID,
 *                      GoogleLoginBar.addScope(BLOGGER)+
 *                      GoogleLoginBar.addScope(BLOGGERV2));
 *           loginBar.addListerner(new LogInControl());
 *           RootPanel.get("loginBarContainer").add(loginBar);
 *  認証tokenの取り出しは、ログイン完了後にこのクラスのgetToken()メソッドでおこなう。
 *  呼び出し元でログイン時/ログオフ時の処理を記述するために、{@code addListener(listener<Class>) }メソッドでイベントハンドラを追加できる。
 *   }</pre>
>>>>>>> refs/heads/master
 */
@SuppressWarnings("deprecation")
public class GoogleLoginBar extends HorizontalPanel {
  // 画面構成要素
  private Anchor signInLink = new Anchor("");
  private Image loginImage = new Image();
  private TextBox nameField = new TextBox();
  // 静的メンバ変数
  private String GOOGLE_CLIENT_ID;// GoogleクライアントID
  private HostAuthServiceAsync hac_ = GWT.create(HostAuthService.class); // RPCハンドル
  // メンバ変数
  private String loginToken_; // Google認証トークン
  private String gmailAdress_; // Google mailアドレス
  private String googleInnerId_; // GoogleユーザのインナーID
  private String googleUserName_; // Googleユーザ名
  private String googleIconUrl_; // GoogleユーザアイコンのURL

  // メンバ変数のGetter
  public String getToken() {
    return loginToken_;
  }

  public String getGmailAdress() {
    return gmailAdress_;
  }

  public String getGoogleInnerId() {
    return googleInnerId_;
  }

  public String getGoogleUserName() {
    return googleUserName_;
  }

  public String getGoogleUserIconUrl() {
    return googleIconUrl_;
  }

  // Googleサービスのスコープ
  public enum ScopeName {
    PICASA("https://picasaweb.google.com/data/"),
    BLOGGER("https://www.googleapis.com/auth/blogger"), 
    BLOGGERV2("http://www.blogger.com/feeds/");
    public final String scopeUrl;

    private ScopeName(String scopeUrl) {
      this.scopeUrl = scopeUrl;
    }
  }

  // googleサービスのスコープ作成補助
  public static String addScope(ScopeName scopeName) {
    return " " + scopeName.scopeUrl + " ";
  }

  // 未登録のスコープ作成補助
  public static String addCustomScope(String customScopeUrl) {
    return " " + customScopeUrl + " ";
  }

  // 認証結果通知用リスナー
  private LoginBarListener listener_;

  public void addListerner(LoginBarListener listener) {
    listener_ = listener;
  } // リスナーのセッター

  // 認証結果通知イベントオブジェクト
  public class InfoEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public InfoEvent(Object arg) {
      super(arg);
    }
  }

  /**
   * Googleログインバー　コンストラクタ
   * 
   * @param clientId
   *          String googleクライアントID
   * @param apiScope
   *          String APIスコープ
   */
  public GoogleLoginBar(String clientId, final String apiScope) {
    GOOGLE_CLIENT_ID = clientId;
    // ログインバーの表示構成
    signInLink.getElement().setClassName("login-area");
    signInLink.setTitle("sign out");
    loginImage.getElement().setClassName("login-area");
    this.add(signInLink);
    // google ログイン状態のチェック
    hac_.loginCheck(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
      @Override
      // RPC接続失敗
      public void onFailure(final Throwable caught) {
        GWT.log("google login -> Failure");
        GWT.log(GWT.getModuleBaseURL() + "HostAuthService");
      }

      @Override
      public void onSuccess(final LoginInfo result) {
        // ログイン判定
        if (result.isLoggedIn()) {
          // ログイン済み
          gmailAdress_ = result.getEmailAddress();
          googleInnerId_ = result.getUserID();
          getGoogleToken(apiScope); // 認証トークンの取得
          dspLogout(result); // ログイン情報の表示
        } else {
          // ログインしていない
          dspLogin(result); // ログインリンクの表示
          listener_.onLoggedOff(new InfoEvent(this)); // ログオフの通知イベントを発生させる。
        }
      }
    });
  }

  /**
   * ログインバー　ログイン時表示
   * 
   * @param loginInfo Googleログイン情報保持クラス
   */
  private void dspLogout(final LoginInfo loginInfo) {
    signInLink.setHref(loginInfo.getLogoutUrl());
    signInLink.setText(loginInfo.getName());
    signInLink.setTitle("サインアウト");
    nameField.setEnabled(true);
  }

  /**
   * ログインバー　未ログイン時表示
   * 
   * @param loginInfo: Googleログイン情報保持クラス
   */
  private void dspLogin(final LoginInfo loginInfo) {
    signInLink.setHref(loginInfo.getLoginUrl());
    signInLink.setText("ここをクリックしてGoogleにサインインしてください。");
    signInLink.setTitle("サインイン");
  }

  /**
   * Google認証tokenの取得を行う。 認証取得手続きは非同期のため、取得後の処理はイベントハンドラの応答を待ってから行われる。
   * token本体はクラス変数loginToken_にストアされ、getToken()メソッドで外部のクラスから取り出すことができる。
   */
  private void getGoogleToken(String moreScope) {
    // 認証リクエスト
    String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth";
    // PluseMEスコープはユーザ情報取得に必要なのでデフォルトでセット。
    String apiScope = "https://www.googleapis.com/auth/userinfo.profile " + moreScope;
    final AuthRequest req = new AuthRequest(googleAuthUrl, GOOGLE_CLIENT_ID).withScopes(apiScope);
    // 認証トークンの取得
    Auth.get().login(req, new Callback<String, Throwable>() {
      // 取得成功
      @Override
      public void onSuccess(final String token) {
        loginToken_ = token;
        if (!token.isEmpty()) {
          getLoginDetails(token);// ユーザー情報の取得
        }
      }

      // 取得失敗
      @Override
      public void onFailure(final Throwable caught) {
        GWT.log("Google Login Error -> loginDetails\n" + caught.getMessage());
        GWT.log(GWT.getModuleBaseURL() + "HostGateService");
      }
    });
  }

  /**
   * google パーソナル情報の取得 getGoogleToken()から呼び出される。
   * 
   * @param token
   *          Google認証token
   */
  private void getLoginDetails(String token) {
    hac_.loginDetails(token, new AsyncCallback<LoginInfo>() {
      @Override
      // RPC接続失敗
      public void onFailure(final Throwable caught) {
        GWT.log("loginDetails -> onFailure");
        GWT.log(GWT.getModuleBaseURL() + "HostGateService");
      }

      @Override
      // RPC接続成功
      public void onSuccess(final LoginInfo loginInfo) {
        GWT.log("loginDetails get.");
        if (loginInfo.getName() == null) {
          Window.alert("Googleユーザー名がnullです。認証エラーの可能性があります。");
        } else {
          googleUserName_ = loginInfo.getName();
          googleIconUrl_ = loginInfo.getPictureUrl();
          dspLoginInfo(loginInfo); // ユーザー情報をログインバーへ表示する
          loginImage.addLoadHandler(new ImgLoadHandler()); // ユーザーアイコンのサイズ調整
        }
        listener_.onLoggedIn(new InfoEvent(this));// ログインの通知イベントを発生させる
      }
    });
  }

  /**
   * ユーザー情報をログインバーへ表示する
   * 
   * @param LoginInfo
   *          : Googleログイン情報保持クラス
   */
  private void dspLoginInfo(LoginInfo loginInfo) {
    signInLink.setText(loginInfo.getName());
    nameField.setText(loginInfo.getName());
    signInLink.setStyleName("login-area");
    loginImage.setUrl(loginInfo.getPictureUrl());
    loginImage.setVisible(false);
    this.add(loginImage);
  }

  /**
   * ユーザーアイコンイメージ ロード時ハンドラ ログインバーに合わせてサイズを調整する。
   * 
   * @author LeoPanda
   *
   */
  private class ImgLoadHandler implements LoadHandler {
    public void onLoad(final LoadEvent event) {
      // ユーザーアイコンの画像表示
      final int newWidth = 24;
      final com.google.gwt.dom.client.Element element = event.getRelativeElement();
      if (element.equals(loginImage.getElement())) {
        final int originalHeight = loginImage.getOffsetHeight();
        final int originalWidth = loginImage.getOffsetWidth();
        if (originalHeight > originalWidth) {
          loginImage.setHeight(newWidth + "px");
        } else {
          loginImage.setWidth(newWidth + "px");
        }
        loginImage.setVisible(true);
      }
    }
  }
}
