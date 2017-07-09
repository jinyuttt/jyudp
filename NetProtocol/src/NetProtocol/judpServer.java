/**    
 * �ļ�����judpServer.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��11��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�judpServer    
 * ��������udp����˽���
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��11�� ����7:23:58    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��11�� ����7:23:58    
 * �޸ı�ע��    
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
     * ��ʼ������
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
                //��ʼ���ɹ�
                listener=new ListenerData();
                //����ʹ�÷�װ�����͵���ʵ��
                MessageBus.register("udp", listener);
              
                ServerManager.addSocket(port, this);
            }
        }
        return true;
    }
    
    /**
     * �ر�
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