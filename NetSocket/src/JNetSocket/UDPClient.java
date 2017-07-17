/**    
 * �ļ�����UDPClient.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��5��26��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package JNetSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import NetModel.NetDataAddress;


/**    
 *     
 * ��Ŀ���ƣ�JNetSocket    
 * �����ƣ�UDPClient    
 * ��������    udp�ͻ���
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��5��26�� ����11:49:16    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��5��26�� ����11:49:16    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class UDPClient  {
    private  DatagramSocket socket =null;  //�����׽���
    private boolean isColse=false;
    private String localAddr;
    private int localPort;
    private String remoteAddr;
    private int remotePort;
    private boolean isBind=false;
    /**
     * Ϊ����װʹ�õ�
     * ��ʱ�������ر�
     */
    private boolean isProtocolClose=false;
    
public void bindLocal(String host,int port)
    {
        isBind=true;
        localAddr=host;
        localPort=port;
    }
    
public void sendData(String host,int port,byte[]data)
{
        try {
            if(socket==null)
            {
                socket =new DatagramSocket();
                socket.setSendBufferSize(128);
           if(isBind)
            {
                  SocketAddress local=new InetSocketAddress(localAddr,localPort);
                   socket.bind(local);
            }
            }
        } catch (SocketException e2) {
            e2.printStackTrace();
        } 
       DatagramPacket dataGramPacket = null;
    try {
        dataGramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(host) , port);
    } catch (UnknownHostException e1) {
        e1.printStackTrace();
    }
        try {
            socket.send(dataGramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }  //ͨ���׽��ַ�������  
        remoteAddr=host;
        remotePort=port;
        localAddr=socket.getLocalAddress().getHostAddress();
        localPort=socket.getLocalPort();
    }
     public void sendData(SocketAddress addr,byte[]data)
    {
        try {
            if(socket==null)
            {
            socket =new DatagramSocket();
            if(isBind)
            {
                  SocketAddress local=new InetSocketAddress(localAddr,localPort);
                   socket.bind(local);
            }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }  //�����׽���
        //�������ͷ������ݱ���Ϣ  
        DatagramPacket dataGramPacket = new DatagramPacket(data, data.length,addr);  
        try {
            socket.send(dataGramPacket);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  //ͨ���׽��ַ������� 
        remoteAddr=dataGramPacket.getAddress().getHostAddress();
        remotePort=dataGramPacket.getPort();
        localAddr=socket.getLocalAddress().getHostAddress();
        localPort=socket.getLocalPort();
    }
     /**
      * ���շ�������
      */
    public byte[]  getCallBackData()
      {
         if(isColse)
         {
             return null;
         }
         //���շ�������  
         byte[] backbuf = new byte[65535];  
         DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);  
         try {
            socket.receive(backPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } 
         //
         int len=backPacket.getLength();
         byte[] data=new byte[len];
         System.arraycopy(backbuf, 0, data, 0, len);
        return  data;
   }
    /**
     * ���շ�������
     */
   public NetDataAddress  getCallData()
     {
       NetDataAddress netdata=new NetDataAddress();
        if(isColse)
        {
            return null;
        }
        //���շ�������  
        byte[] backbuf = new byte[65535];  
        DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);  
        try {
           socket.receive(backPacket);
       } catch (IOException e) {
           return null;
       } 
        //
        int len=backPacket.getLength();
        byte[] data=new byte[len];
        System.arraycopy(backbuf, 0, data, 0, len);
        netdata.netData=data;
        int port=backPacket.getPort();
        String addr=backPacket.getAddress().getHostAddress();
        netdata.srcIP=addr;
        netdata.srcPort=port;
       return  netdata;
  }
    /*
     * �ر�socket
     */
    public void close()
   {
        try
        {
            isColse=true;
           socket.close();
        }
        catch(Exception ex)
        {
            
        }
        finally
        {
            
        }
   }
    /*
     * �Ƿ�ر�socket;
     */
    public boolean isClose()
    {
        return isColse;
    }
  
public String getLocalHost()
  {
      return this.localAddr;
  }
  
public int getLocalPort()
  {
      return this.localPort;
  }
 
public String getRemoteHost()
 {
     return this.remoteAddr;
 }
  
public int getRemotePort()
  {
      return this.remotePort;
  }

  /**
   * ����װ�رգ��߼��ϵģ�
   */
 public  void protocolClose()
{
      isProtocolClose=true;
}
 /*
  * ��������Ƿ�ر�
  */
public  boolean isProtocolClose()
{
    return isProtocolClose;
}
}
