/**    
 * 文件名：MulticastClient.java    
 *    
 * 版本信息：    
 * 日期：2017年5月27日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package JNetSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;


/**    
 *     
 * 项目名称：JNetSocket    
 * 类名称：MulticastClient    
 * 类描述：    组播客户端
 * 创建人：jinyu    
 * 创建时间：2017年5月27日 上午1:37:01    
 * 修改人：jinyu    
 * 修改时间：2017年5月27日 上午1:37:01    
 * 修改备注：    
 * @version     
 *     
 */
public class MulticastClient  {
public void sendData(String sIP,int port,byte[]data)
{
    InetAddress group = null;
    try {
        group = InetAddress.getByName(sIP);
    } catch (UnknownHostException e1) {
        e1.printStackTrace();
    }//组播地址  
    MulticastSocket mss = null;  
    try {  
        mss = new MulticastSocket();  
        mss.joinGroup(group);  
        DatagramPacket dp = new DatagramPacket(data, data.length,group,port);  
         mss.send(dp);  
    } catch (Exception e) { 
        System.out.println("host:"+sIP+"port:"+port);
       e.printStackTrace();  
    }finally{  
        try {  
            if(mss!=null){  
                mss.leaveGroup(group);  
                mss.close();  
            }  
        } catch (Exception e2) {   
        }  
    }  

}
public void sendData(SocketAddress local,String sIP,int port,byte[]data)
{
    InetAddress group = null;
    try {
        group = InetAddress.getByName(sIP);
    } catch (UnknownHostException e1) {
        e1.printStackTrace();
    }//组播地址  
    MulticastSocket mss = null;  
    
    try {  
        mss = new MulticastSocket();
        mss.bind(local);
        mss.joinGroup(group);  
        DatagramPacket dp = new DatagramPacket(data, data.length,group,port);  
         mss.send(dp);  
    } catch (Exception e) {  
        e.printStackTrace();  
    }finally{  
        try {  
            if(mss!=null){  
                mss.leaveGroup(group);  
                mss.close();  
            }  
        } catch (Exception e2) {   
        }  
    }  
}

}
