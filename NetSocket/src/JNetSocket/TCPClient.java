/**    
 * �ļ�����TCPClient.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��5��27��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package JNetSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**    
 *     
 * ��Ŀ���ƣ�JNetSocket    
 * �����ƣ�TCPClient    
 * ��������    TCP�ͻ���
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��5��27�� ����1:01:49    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��5��27�� ����1:01:49    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TCPClient {
    Socket clientSocket=null;
public void sendData(String sIP,int sPort,byte[]data)
{
    try {
        clientSocket = new Socket(sIP,sPort);
        clientSocket.setSendBufferSize(128);
    } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }  
    try {
        clientSocket.getOutputStream().write(data);
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
public void sendData(SocketAddress local,String sIP,int sPort,byte[]data)
{
    try {
        if(local==null)
        {
            clientSocket = new Socket(sIP,sPort);
            clientSocket.setSendBufferSize(128);
        }
        else
        {
            
            clientSocket=new Socket();
            clientSocket.bind(local);
            SocketAddress endpoint = new InetSocketAddress(sIP, sPort);
            clientSocket.connect(endpoint);
            
        }
    } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }  
    try {
        clientSocket.getOutputStream().write(data);
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
public void close()
{
    try {
        clientSocket.close();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
public byte[]  getCallBackData()
{
    if(clientSocket!=null&&clientSocket.isConnected())
    {
        byte[]buf=new byte[1024];
        try {
            int r= clientSocket.getInputStream().read(buf);
            if(r==0||r<0)
            {
                return null;
            }
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return null;
}


}
