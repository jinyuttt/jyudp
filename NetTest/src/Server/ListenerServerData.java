/**    
 * 文件名：ListenerServerData.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Server;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import NetModel.NetDataAddress;

/**    
 *     
 * 项目名称：NetTest    
 * 类名称：ListenerServerData    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 上午1:39:51    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 上午1:39:51    
 * 修改备注：    
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
