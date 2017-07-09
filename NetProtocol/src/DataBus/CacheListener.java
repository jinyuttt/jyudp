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

import java.lang.ref.WeakReference;

import com.google.common.cache.RemovalNotification;

import NetProtocol.judpClient;
import Sessions.ClientManager;
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
public class CacheListener extends CacheTimeListenter<Long,WeakReference<judpClient>> {

    @Override
    public void onRemoval(RemovalNotification<Long, WeakReference<judpClient>> arg) {
        
        WeakReference<judpClient> client= arg.getValue();
        if(client==null||client.get()==null)
        {
            //�Ѿ���ʧû�����ã�
            //����Ѿ��رյ��ڣ���ر�
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
                //��ʱʱ�Ѿ��رգ��������ر�
                Session session=ClientManager.getSession(arg.getKey());
                if(session!=null)
                {
                    session.close();
                }
            }
        }
       
    }

   

}