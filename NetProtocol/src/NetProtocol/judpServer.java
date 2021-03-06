/**    
 * 文件名：judpServer.java    
 *    
 * 版本信息：    
 * 日期：2017年6月11日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetProtocol;

import java.util.concurrent.LinkedTransferQueue;

import java.util.concurrent.TransferQueue;

import EventBus.MessageBus;
import JNetSocket.UDPServerSocket;
import Sessions.ServerManager;
import Sessions.Session;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：judpServer    
 * 类描述：udp服务端接收
 * 创建人：jinyu    
 * 创建时间：2017年6月11日 下午7:23:58    
 * 修改人：jinyu    
 * 修改时间：2017年6月11日 下午7:23:58    
 * 修改备注：    
 * @version     
 *     
 */
public class judpServer {
    UDPServerSocket  server=null;
    ListenerData listener=null;
    int port=0;
    //judpSocket socket=null;
    TransferQueue<judpSocket> queue=new LinkedTransferQueue<judpSocket>();
    public void addData(Session session)
    {
        judpSocket socket=new judpSocket(); 
        socket.setSession(session);
        try {
            queue.put(socket);
        } catch (InterruptedException e) {
          
            e.printStackTrace();
        }
    }
    /**
     * 初始化监听
     */
    public boolean InitServer(String host,int port)
    {
        if(server==null)
        {
            this.port=port;
            server=new UDPServerSocket();
            boolean r= server.InitServer(host, port);
            if(r)
            {
                //初始化成功
                listener=new ListenerData();
                //所有使用封装都发送到该实例
                MessageBus.register("udp", listener);
                ServerManager.addSocket(port, this);
            }
        }
        return true;
    }
    
    /**
     * 关闭
     */
    public void close()
    {
        ServerManager.remove(port);
        if(server!=null)
        {
            server.close();
        }
    }

    public judpSocket accept()
    {
        try {
            return queue.take();
        } catch (InterruptedException e) {
          
           return null;
        }
        
    }
}
