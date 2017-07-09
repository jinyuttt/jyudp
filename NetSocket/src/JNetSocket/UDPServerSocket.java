package JNetSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import EventBus.MessageBus;
import NetModel.DataModel;


public class UDPServerSocket  {
    private DatagramSocket socket;
    SocketAddress sendAddress=null;
    private boolean isRuning=true;
    /*
     * ��ʼ������
     */
    public boolean InitServer(String host,int port)
    {
      //����DatagramSocket����  
        if(host==null||host.isEmpty()||host.equals("host"))
        {
            try {
                socket = new DatagramSocket(port);
            } catch (SocketException e) {
               
                e.printStackTrace();
                return false;
            }  
        }
        else
        {
            InetAddress addr = null;
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            }
            try {
                socket = new DatagramSocket(port,addr);
            } catch (SocketException e) {
                e.printStackTrace();
                return false;
            }  
        }
        try {
            socket.setReceiveBufferSize(128);
            socket.setSendBufferSize(128);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread recData=new Thread(new Runnable(){
            @Override
            public void run() {
                byte[] buf = new byte[65535];  //����byte����  
                String curIP="";
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //����DatagramPacket����  
                while(isRuning)
                {
                try {
                    socket.receive(packet);
                    if(curIP.isEmpty())
                    {
                        socket.getLocalAddress().getHostAddress();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } 
                //�ӷ��������ظ��ͻ�������  
                    String clientAddress = packet.getAddress().getHostAddress(); //��ÿͻ��˵�IP��ַ  
                    int clientPort = packet.getPort(); //��ÿͻ��˵Ķ˿ں�
                    int len=packet.getLength();
                    byte[]data=new byte[len];
                    System.arraycopy(buf, 0, data, 0, len);
                    DataModel  dataModel=new DataModel();
                    dataModel.data=data;
                    dataModel.netType=1;
                    dataModel.srcIP=clientAddress;
                    dataModel.srcPort=clientPort;
                    dataModel.localPort=port;
                    dataModel.localIP=curIP;
                    try
                    {
                    MessageBus.post("udp", dataModel);
                    }
                    catch(Exception ex)
                    {
                        
                    }
                
            }
                socket.close();//����ѭ����ر�
            }
            
        });
        recData.setDaemon(true);
        recData.setName("UDPServer");
        recData.start();
        return true;
     
    }
   
    public void sendData(String host,int port,byte[]data)
    {
        if(socket!=null)
        {
        InetAddress address = null;
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
               
                e.printStackTrace();
                return;
            }
            DatagramPacket dataGramPacket = new DatagramPacket(data, data.length, address, port);
            try {
                socket.send(dataGramPacket);
            } catch (IOException e) {
               
                e.printStackTrace();
            }
        }
    }
  
   public void sendData(SocketAddress addr,byte[]data)
 {
   if(socket!=null)
   {
     //�������ͷ������ݱ���Ϣ  
     DatagramPacket dataGramPacket = new DatagramPacket(data, data.length,addr);  
     try {
         socket.send(dataGramPacket);
     } catch (IOException e) {
         e.printStackTrace();
     }  
   }
 }
 
   public void close()
    {
        isRuning=false;
        socket.close();
    }

}
