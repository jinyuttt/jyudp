/**    
 * �ļ�����MulticastClient.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��5��27��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�JNetSocket    
 * �����ƣ�MulticastClient    
 * ��������    �鲥�ͻ���
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��5��27�� ����1:37:01    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��5��27�� ����1:37:01    
 * �޸ı�ע��    
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
    }//�鲥��ַ  
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
    }//�鲥��ַ  
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
