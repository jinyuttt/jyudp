/**    
 * �ļ�����TestServer.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Server;

import java.io.IOException;

import EventBus.MessageBus;
import NetProtocol.judpServer;

/**    
 *     
 * ��Ŀ���ƣ�NetTest    
 * �����ƣ�TestServer    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����1:37:23    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����1:37:23    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TestServer {
    public static void main(String[] args) {
    judpServer server=new judpServer();
    server.InitServer("192.168.3.139", 5555);
    ListenerServerData listener=new ListenerServerData();
    try {
        MessageBus.register("5555",listener);
    } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
    try {
        System.in.read();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    }
}
