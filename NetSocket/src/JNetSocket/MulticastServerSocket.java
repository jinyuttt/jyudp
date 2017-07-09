/**    
 * 文件名：MulticastServerSocket.java    
 *    
 * 版本信息：    
 * 日期：2017年5月27日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
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
 * 项目名称：JNetSocket    
 * 类名称：MulticastServerSocket    
 * 类描述：组播接收端
 * 创建人：jinyu    
 * 创建时间：2017年5月27日 上午1:22:38    
 * 修改人：jinyu    
 * 修改时间：2017年5月27日 上午1:22:38    
 * 修改备注：    
 * @version     
 *     
 */
public class MulticastServerSocket {

    SocketAddress sendAddress=null;
    private boolean isRuning=true;
    MulticastSocket msr = null;//创建组播套接字  
    public boolean InitServer(String addr,int port)
    {
   
      //创建DatagramSocket对象  
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
        }//加入连接  
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
                //从服务器返回给客户端数据  
                String clientAddress = dp.getAddress().getHostName(); //获得客户端的IP地址  
                int clientPort = dp.getPort(); //获得客户端的端口号  
             
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
