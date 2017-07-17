/**    
 * �ļ�����CacheListener.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��8��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package DataBus;



import com.google.common.cache.RemovalNotification;

import Sessions.ClientManager;
import Sessions.ClientSessionsPools;
import Sessions.Session;




/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�CacheListener    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��8�� ����3:16:20    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��8�� ����3:16:20    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class CacheListener extends CacheTimeListenter<Long, Session> {

    @Override
    public void onRemoval(RemovalNotification<Long,  Session> arg) {
        
        Session client= arg.getValue();
        if(client==null)
        {
            //�Ѿ���ʧû�����ã�
            //����Ѿ��رյ��ڣ���ر�
            Session session=ClientManager.getSession(arg.getKey());
            if(session!=null)
            {
                session.setLogicalClose();
                session.close();
                removeSessionByKey(session);
            }
        }
        else
        {
            //û���ⲿ���ã�***����
            client.setLogicalClose();
            if(client.getClientNum()==0)
            {
                //��ʱʱ�Ѿ��رգ��������ر�
                Session session=ClientManager.getSession(arg.getKey());
                if(session!=null)
                {
                   
                    session.close();
                    removeSessionByKey(session);
                }
            }
        }
       
    }
     
    /*
     * 
     */
    private  void removeSessionByKey(Session session)
    {
        if(session!=null&&session.getClientNum()==0&&session.isLogicalClose())
        {
         //�����ʧʱ�Ѿ���ʱ����Ҫ�����ر�;
         session.close();
       
         Session ctmp= ClientSessionsPools.getSession(session.getLocalBindPort(), session.getSrcIP(), session.getSrcPort());
         if(ctmp.getID()==session.getID())
         {
             //˵�����ڷ��䣬����ɾ����
             String key="";
             if(session.getLocalBindPort()==0)
             {
                 key=session.getSrcIP()+session.getSrcPort();
             }
             else
             {
                 key=session.getLocalBindPort()+session.getSrcIP()+session.getSrcPort();
             }
             ClientSessionsPools.removeKey(key);
         }
        }
    }
   

}
