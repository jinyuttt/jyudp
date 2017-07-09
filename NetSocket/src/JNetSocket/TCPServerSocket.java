/**    
 * �ļ�����TCPSocket.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��5��27��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package JNetSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**    
 *     
 * ��Ŀ���ƣ�JNetSocket    
 * �����ƣ�TCPSocket    
 * �������� TCP�����
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��5��27�� ����12:09:42    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��5��27�� ����12:09:42    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TCPServerSocket {
    private IRecData recInstance=null;
    SocketAddress sendAddress=null;
    private boolean isRuning=true;
    ServerSocket  socket=null;
 
    public boolean InitServer(IRecData rec,String host,int port)
    {
        recInstance=rec;
      //����DatagramSocket����  
        if(host==null||host.isEmpty())
        {
                try {
                    socket = new ServerSocket(port);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
                    socket = new ServerSocket(port,0,addr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
           
        }
        try {
            socket.setReceiveBufferSize(128);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread recData=new Thread(new Runnable(){
            @Override
            public void run() {
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool(); 
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);  
                AtomicInteger count=new AtomicInteger(0);
                byte[] buf = new byte[1024];  //����byte����  
              
                while(isRuning)
                {
                 Socket client = null;
                try {
                    client = socket.accept();
                } catch (IOException e) {
                  
                    e.printStackTrace();
                }
           
                //�ӷ��������ظ��ͻ�������  
                String clientAddress = client.getInetAddress().getHostName(); //��ÿͻ��˵�IP��ַ  
                int clientPort = client.getPort(); //��ÿͻ��˵Ķ˿ں�  
                int r = 0;
                try {
                    r = client.getInputStream().read(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[]data=new byte[r];
                System.arraycopy(buf, 0, data, 0, r);
                Socket cur=client;
                if(recInstance!=null)
                {
                    if(count.get()<100)//�����ǰ����ִ�еķ���С��100��
                    {
                     cachedThreadPool.execute(new Runnable(){
                        @Override
                        public void run() {
                          
                            count.incrementAndGet();
                            //TCPClient client=new TCPClient();
                       
                            recInstance.setServer(cur);
                            recInstance.recviceData(clientAddress+":"+clientPort, data);
                            count.decrementAndGet();
                        }
                
                    });
                    }
                    else
                    {
                        //����ȴ�����
                        fixedThreadPool.execute(new Runnable(){
                        @Override
                        public void run() {
                            recInstance.setServer(cur);
                            recInstance.recviceData(clientAddress+":"+clientPort, data);
                        }
                
                    });
                    }
                }
            }
               
            }
            
        });
        recData.setDaemon(true);
        recData.setName("UDPServer");
        recData.start();
        return false;
     
    }
    public void close()
    {
        isRuning=false;
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }
  
}
