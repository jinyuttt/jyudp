/**    
 * �ļ�����judpSocket.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��8��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetProtocol;

import NetModel.NetDataAddress;
import Sessions.Session;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�judpSocket    
 * ��������    ����ͨ��
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��8�� ����5:06:11    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��8�� ����5:06:11    
 * �޸ı�ע��    
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