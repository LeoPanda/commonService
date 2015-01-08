# GWT(Google Web Toolkit)用　共通機能パッケージ
##主な機能

*Google OAuth2 ログインバーパネル

	jp.leopanda.common.client.GoogleLoginBar.java

*サーバー用HTML fetch

	jp.leopanda.common.server.UrlService.java
	
##セットアップ方法
１）利用する側のプロジェクトのビルドパスで、本パッケージのsrcフォルダへのソースリンクを作成するか、
　　jarファイルへエクスポートしてビルドバスに加える。

２）このパッケージのwar/WEB-INF/libにある

gwt-oauth2-0.2-alpha.jar
jakson-core-asi-1.7.0.jar

２つのパッケージを、利用する側のパッケージのlibへコピーする。

3)上記２つのライブラリをビルドパスへ追加する。

４）利用する側のweb.xmlに以下を追加する
```xml
	<servlet>
    	<servlet-name>HostAuthServiceImpl</servlet-name>
    	<servlet-class>jp.leopanda.common.server.HostAuthServiceImpl</servlet-class>
  	</servlet>
   
  	<servlet-mapping>
    	<servlet-name>HostAuthServiceImpl</servlet-name>
    	<url-pattern>/(プロジェクトのアプリケーションパス)/HostAuthService</url-pattern>
  	</servlet-mapping>
```
 
 5)gwt.xmlへ以下を追加する
```xml
 	<inherits name="jp.leopanda.common.CommonService" />
```
##利用方法
###Google OAuth2 ログインバー パネル
 1)Google cloud（https://cloud.google.com/console/project）
 	にアクセスし、プロジェクトを作成してクライアントIDを取得する。
 
 2)Goole cloud のAPIコンソールへアクセスし、OAuthのリダイレクトURIを登録する。
 
   ローカルでテストを行うためには
   http://127.0.0.1:8888/(プロジェクト名)/oauthWindow.html
   GAEにデプロイして実行するためには
   http://(GAEのURI)/(プロジェクト名)/oauthWindow.html
   を登録する。
 
 3)gwt.xml上に　<inherits name="jp.leopanda.common.Common" />を追加する。
 
 4)cssに .login-xxxを追加する。(このパッケージのwar上にあるサンプルを参照）
 
 5)web.xmlに以下を記述

```xml
   <servlet>
    <servlet-name>HostAuthServiceImpl</servlet-name>
    <servlet-class>jp.leopanda.common.server.HostAuthServiceImpl</servlet-class>
   </servlet>
   <servlet-mapping>
    <servlet-name>HostAuthServiceImpl</servlet-name>
    <url-pattern>/(アプリ固有のパス)/HostAuthService</url-pattern>
   </servlet-mapping>
```

*呼び出し例)
  プロジェクトのEntry pointにあるOnModuleload()メソッドから呼ばれることを想定している。

```java
 　 GoogleLoginBar loginBar = new GoogleLoginBar(clientID,
  			   							GoogleLoginBar.addScope(BLOGGER)+
  										GoogleLoginBar.addScope(BLOGGERV2));
  			　　loginBar.addListerner(new LogInControl());
  			   RootPanel.get("loginBarContainer").add(loginBar);
  ```
 
  認証tokenの取り出しは、ログイン完了後にこのクラスのgetToken()メソッドでおこなう。
  呼び出し元でログイン時/ログオフ時の処理を記述するために、addListener(listener<Class>)メソッドでイベントハンドラを追加できる。
  
###サーバーサイド　UrlService
呼び出し方はDecoratedに

```java
   	UrlService urlService = new UrlService();
  		Results results = 
  						urlService.fetchGet(UrlString,
  									addCookie(CookieString,
  									addToken(tokenString,
  									setHeader(urlServie.ContentType.HTML))));
 ```
  

  