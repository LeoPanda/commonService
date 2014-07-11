package jp.leopanda.common.server;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import jp.leopanda.common.client.HostAuthService;
import jp.leopanda.common.server.UrlService;
import jp.leopanda.common.server.UrlService.ContentType;
import jp.leopanda.common.shared.LoginInfo;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class HostAuthServiceImpl extends RemoteServiceServlet implements HostAuthService {
  private static final long serialVersionUID = 1L;

  /**
   * Google OAuth Loginチェック UserService.Userから取得出来る情報をLoginInfoに返す
   *
   * @param requestUri : OAuth2 request Uri
   * @return LoginInfo ログイン情報
   */
  public LoginInfo loginCheck(final String requestUri) {
    final UserService userService = UserServiceFactory.getUserService();
    final User user = userService.getCurrentUser();
    final LoginInfo loginInfo = new LoginInfo();
    if (user != null) {
      loginInfo.setLoggedIn(true);
      loginInfo.setName(user.getNickname());
      loginInfo.setEmailAddress(user.getEmail());
      loginInfo.setUserID(user.getUserId());
      loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
    } else {
      loginInfo.setLoggedIn(false);
      loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
    }
    return loginInfo;
  }

  /**
   * Google User情報の取得
   *
   * @param token : OAhth2 認証token
   * @return LoginInfo ログイン情報
   */
  public LoginInfo loginDetails(final String token) {
    String urlStr = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + token;
    final LoginInfo loginInfo = new LoginInfo();
    try {
      final JsonFactory f = new JsonFactory();
      JsonParser jp;
      UrlService urlService = new UrlService();
      String jsonSource = urlService.fetchGet(urlStr,
          urlService.addToken(token, urlService.setHeader(ContentType.XML))).getBody();
      jp = f.createJsonParser(jsonSource);
      jp.nextToken();
      while (jp.nextToken() != JsonToken.END_OBJECT) {
        final String fieldname = jp.getCurrentName();
        jp.nextToken();
        if ("picture".equals(fieldname)) {
          loginInfo.setPictureUrl(jp.getText());
        } else if ("name".equals(fieldname)) {
          loginInfo.setName(jp.getText());
        } else if ("email".equals(fieldname)) {
          loginInfo.setEmailAddress(jp.getText());
        }
      }
    } catch (final JsonParseException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return loginInfo;
  }

}
