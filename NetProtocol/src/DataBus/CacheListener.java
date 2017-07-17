/**    
 * 文件名：CacheListener.java    
 *    
 * 版本信息：    
 * 日期：2017年7月8日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package DataBus;



import com.google.common.cache.RemovalNotification;

import Sessions.ClientManager;
import Sessions.ClientSessionsPools;
import Sessions.Session;




/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：CacheListener    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月8日 下午3:16:20    
 * 修改人：jinyu    
 * 修改时间：2017年7月8日 下午3:16:20    
 * 修改备注：    
 * @version     
 *     
 */
public class CacheListener extends CacheTimeListenter<Long, Session> {

    @Override
    public void onRemoval(RemovalNotification<Long,  Session> arg) {
        
        Session client= arg.getValue();
        if(client==null)
        {
            //已经消失没有引用；
            //如果已经关闭到期；则关闭
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
            //没有外部引用；***待定
            client.setLogicalClose();
            if(client.getClientNum()==0)
            {
                //超时时已经关闭，则真正关闭
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
         //如果消失时已经超时，则要真正关闭;
         session.close();
       
         Session ctmp= ClientSessionsPools.getSession(session.getLocalBindPort(), session.getSrcIP(), session.getSrcPort());
         if(ctmp.getID()==session.getID())
         {
             //说明正在分配，必须删除；
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
