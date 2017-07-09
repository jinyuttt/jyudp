/**    
 * 文件名：judpSocket.java    
 *    
 * 版本信息：    
 * 日期：2017年7月8日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetProtocol;

import NetModel.NetDataAddress;
import Sessions.Session;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：judpSocket    
 * 类描述：    网络通信
 * 创建人：jinyu    
 * 创建时间：2017年7月8日 下午5:06:11    
 * 修改人：jinyu    
 * 修改时间：2017年7月8日 下午5:06:11    
 * 修改备注：    
 * @version     
 *     
 */
public class judpSocket {
    Session session=null;
    public void setSession(Session session)
    {
       this.session=session;
    }
public NetDataAddress  read(int len)
{
    NetDataAddress addrdata=  session.read();
    return addrdata;
    
}
public NetDataAddress readAll()
{
   return session.read();
    
}
}
