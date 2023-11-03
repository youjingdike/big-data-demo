package com.mail;


/**
 * LdapUtil:
 * @version 1.0 2012-5-14
 */
public class LdapUtil {
	/*private static JcdpLogger dmslog = (JcdpLogger) LogFactory.getLogger(LdapUtil.class);  init log handle 
	private static String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
	public static boolean oldauthenticate(String userName,String passwd) 
	{
		boolean result = false;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
		env.put(Context.PROVIDER_URL, AppConfig.getParam("LDAP_HOST1"));
		//env.put(Context.PROVIDER_URL, AppConfig.getParam("LDAP_HOST2"));
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put(Context.SECURITY_PRINCIPAL, userName +"@"+AppConfig.getParam("LDAP_DOMAIN_NAME"));

		if ("".equals(passwd) || passwd == null)
			return false;
		
		env.put(Context.SECURITY_CREDENTIALS, passwd);
		
		DirContext ctx = null;
		
		try {
			ctx = new InitialDirContext(env);
			dmslog.info(userName +" is authenticated！");
			result = true;
		} catch (Exception e) {
			dmslog.error(userName+" is not authenticated！", e.getCause());
			result = false;
		}finally{
			if (ctx != null){
				try {
					ctx.close(); 
				} catch (NamingException e) { 
//					System.out.println("NamingException in close():" + e); 
				}
			}
		}

		return result;
	}*/
	
	public static boolean authenticate(String userName,String password){
		Authenticator util = new Authenticator();
		return util.authenticate(userName, password);
	}
	public static void main(String [] args){
		boolean isAuth=authenticate("xingqian3@QQ.com.cn", "1111");	
		System.out.println(isAuth);
	}
}


