/**    
 * �ļ�����ListenerServerData.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Server;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import NetModel.NetDataAddress;

/**    
 *     
 * ��Ŀ���ƣ�NetTest    
 * �����ƣ�ListenerServerData    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����1:39:51    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����1:39:51    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ListenerServerData {
    @Subscribe
    @AllowConcurrentEvents
public void recData(NetDataAddress data)
{
        if(data.netData==null)
        {
           System.out.println(data.srcIP+","+data.srcPort);
        }
        else
        {
            System.out.println(data.srcIP+","+data.srcPort+","+new String(data.netData));
        }
}
}