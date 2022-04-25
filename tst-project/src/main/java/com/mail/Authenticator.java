package com.mail;

import com.util.PublicUtil;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import java.util.Properties;


public class Authenticator {
  private Store store;
  
  public Authenticator()
  {
  }

  private boolean connect(String user, String password) {
      if(user != null && !user.equals("") && password != null && !password.equals("")) {
          Properties props = new PublicUtil().loadProperties("mail.properties");
          Session mailSession = Session.getInstance(props, null);
          Transport transport = null;
          try {
            transport = mailSession.getTransport("smtp");
          } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
            return false;
          }
          try {
            transport.connect((String) props.get("mail.smtp.host"), user, password);
          } catch (MessagingException e) {
            e.printStackTrace();
            return false;
          } finally {
            if (transport!=null) {
              try {
                transport.close();
              } catch (MessagingException e) {
                e.printStackTrace();
              }
            }
          }
          return true;
      } else {
          return false;
      }
  }

  private void close()
  {
      try
      {
          if(store != null && store.isConnected())
              store.close();
      }
      catch(MessagingException e) { }
  }

  public boolean authenticate(String user, String password)
  {
      if(connect(user, password))
      {
          close();
          return true;
      } else
      {
          return false;
      }
  }

}
