/**    
 * 文件名：SessionFactory.java    
 *    
 * 版本信息：    
 * 日期：2017年7月8日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import NetPackaget.PackagetRandom;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：SessionFactory    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月8日 下午2:43:35    
 * 修改人：jinyu    
 * 修改时间：2017年7月8日 下午2:43:35    
 * 修改备注：    
 * @version     
 *     
 */
public class SessionFactory {
    public static ServerSession createObj(String srcIP,int srcPort,String localIP,int localPort)
    {
        ServerSession session=new ServerSession(srcIP,srcPort,localIP,localPort);
        long id=PackagetRandom.getSessionID();
        session.setID(id);
        return session;
    }
    public static ClientSession createObj()
    {
        ClientSession session=new ClientSession();
        long id=PackagetRandom.getSessionID();
        boolean r= ClientManager.addSession(id, session);
       while(!r)
       {
           //发送端id很重要
           id=PackagetRandom.getSessionID();
           r= ClientManager.addSession(id, session);
       }
       session.setID(id);
        return session;
    }
}
