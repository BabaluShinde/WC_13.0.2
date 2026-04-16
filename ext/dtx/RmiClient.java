package ext.dtx;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import wt.method.RemoteMethodServer;

public class RmiClient {
   public static void main(String[] args) throws RemoteException, InvocationTargetException {
      RemoteMethodServer rms = RemoteMethodServer.getDefault();
      rms.setUserName("wcadmin");
      rms.setPassword("wcadmin");
      Object result = rms.invoke("main", "ext.dtx.ECNSummaryEmail", (Object)null, (Class[])null, (Object[])null);
   }
}
