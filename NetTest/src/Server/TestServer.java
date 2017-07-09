/**    
 * 文件名：TestServer.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Server;

import java.io.IOException;

import EventBus.MessageBus;
import NetProtocol.judpServer;

/**    
 *     
 * 项目名称：NetTest    
 * 类名称：TestServer    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 上午1:37:23    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 上午1:37:23    
 * 修改备注：    
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
