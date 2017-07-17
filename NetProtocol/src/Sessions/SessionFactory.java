/**    
 * �ļ�����SessionFactory.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��8��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Sessions;

import NetPackaget.PackagetRandom;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�SessionFactory    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��8�� ����2:43:35    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��8�� ����2:43:35    
 * �޸ı�ע��    
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
           //���Ͷ�id����Ҫ
           id=PackagetRandom.getSessionID();
           r= ClientManager.addSession(id, session);
       }
       session.setID(id);
        return session;
    }
}
