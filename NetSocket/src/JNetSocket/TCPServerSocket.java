/**    
 * 文件名：TCPSocket.java    
 *    
 * 版本信息：    
 * 日期：2017年5月27日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
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
 * 项目名称：JNetSocket    
 * 类名称：TCPSocket    
 * 类描述： TCP服务端
 * 创建人：jinyu    
 * 创建时间：2017年5月27日 上午12:09:42    
 * 修改人：jinyu    
 * 修改时间：2017年5月27日 上午12:09:42    
 * 修改备注：    
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
      //创建DatagramSocket对象  
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
                byte[] buf = new byte[1024];  //定义byte数组  
              
                while(isRuning)
                {
                 Socket client = null;
                try {
                    client = socket.accept();
                } catch (IOException e) {
                  
                    e.printStackTrace();
                }
           
                //从服务器返回给客户端数据  
                String clientAddress = client.getInetAddress().getHostName(); //获得客户端的IP地址  
                int clientPort = client.getPort(); //获得客户端的端口号  
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
                    if(count.get()<100)//如果当前正在执行的方法小于100个
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
                        //放入等待队列
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
