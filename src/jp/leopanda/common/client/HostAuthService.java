package jp.leopanda.common.client;

import jp.leopanda.common.shared.LoginInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("HostAuthService")
public interface HostAuthService extends RemoteService{
	LoginInfo loginCheck(String requestUri);	
	LoginInfo loginDetails(String token);	
}
