/**    
 * �ļ�����MulticastServerSocket.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��5��27��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package JNetSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import EventBus.MessageBus;
import NetModel.DataModel;


/**    
 *     
 * ��Ŀ���ƣ�JNetSocket    
 * �����ƣ�MulticastServerSocket    
 * ���������鲥���ն�
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��5��27�� ����1:22:38    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��5��27�� ����1:22:38    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class MulticastServerSocket {

    SocketAddress sendAddress=null;
    private boolean isRuning=true;
    MulticastSocket msr = null;//�����鲥�׽���  
    public boolean InitServer(String addr,int port)
    {
   
      //����DatagramSocket����  
        InetAddress maddr = null;
        try {
            maddr = InetAddress.getByName(addr);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        try {
            msr = new MulticastSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        } 
        try {
            msr.setReceiveBufferSize(128);
            msr.setReuseAddress(true);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        try {
            msr.joinGroup(maddr);
        } catch (IOException e1) {
            e1.printStackTrace();
        }//��������  
        byte[] buffer = new byte[8192];  
        Thread recData=new Thread(new Runnable(){
            @Override
            public void run() {
               while(isRuning)
               {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);  
                String curIP=addr;
                try {
                    msr.receive(dp);
                   
                } catch (IOException e) {
                    e.printStackTrace();
                }  
                //�ӷ��������ظ��ͻ�������  
                String clientAddress = dp.getAddress().getHostName(); //��ÿͻ��˵�IP��ַ  
                int clientPort = dp.getPort(); //��ÿͻ��˵Ķ˿ں�  
             
                    byte[]data=dp.getData();
                    DataModel  dataModel=new DataModel();
                    dataModel.data=data;
                    dataModel.netType=2;
                    dataModel.srcIP=clientAddress;
                    dataModel.srcPort=clientPort;
                    dataModel.localPort=port;
                    dataModel.localIP=curIP;
                    MessageBus.post("udpMultcast", dataModel);
                
               }
               msr.close();
            }
        });
        recData.setDaemon(true);
        recData.setName("UDPMultcastServer");
        recData.start();
        return false;
     
    }
    public void close()
    {
        isRuning=false;
    }

}
