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

import java.lang.ref.WeakReference;

import com.google.common.cache.RemovalNotification;

import NetProtocol.judpClient;
import Sessions.ClientManager;
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
public class CacheListener extends CacheTimeListenter<Long,WeakReference<judpClient>> {

    @Override
    public void onRemoval(RemovalNotification<Long, WeakReference<judpClient>> arg) {
        
        WeakReference<judpClient> client= arg.getValue();
        if(client==null||client.get()==null)
        {
            //已经消失没有引用；
            //如果已经关闭到期；则关闭
            Session session=ClientManager.getSession(arg.getKey());
            if(session!=null)
            {
                session.close();
            }
        }
        else
        {
            judpClient jclient=client.get();
            jclient.setOutTime();
            if(jclient.isClose())
            {
                //超时时已经关闭，则真正关闭
                Session session=ClientManager.getSession(arg.getKey());
                if(session!=null)
                {
                    session.close();
                }
            }
        }
       
    }

   

}
