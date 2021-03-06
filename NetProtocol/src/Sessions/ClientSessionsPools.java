/**    
 * 文件名：ClientPools.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import java.util.concurrent.ConcurrentHashMap;



/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：ClientSessionsPools    
 * 类描述：   管理用于数据发送的seesion
 * 1.一般session(host+srp+srport)
 * 2.绑定本地（localIP+port+srp+srport)
 * 3.本地本地端口(port+srp+srport)
 * 4.当前2.3公用
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 下午5:44:18    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 下午5:44:18    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientSessionsPools {
 private static int maxClientNum=1000;

 public static ConcurrentHashMap<String,ControlSession> map=new ConcurrentHashMap<String,ControlSession>();

 /**
 * 获取session
 * 
 */
public static synchronized ClientSession getSession(int port,String srcIP,int srcPort)
{
    String key="";
    if(port==0)
    {
        //说明是一般session;
         key=srcIP+srcPort;
    }
    else
    {
         key=port+srcIP+srcPort;
    }
    ControlSession ctrsession=map.get(key);
    if(ctrsession==null)
    {
        ctrsession=new ControlSession();
        ClientSession session= SessionFactory.createObj();
        ctrsession.setSession(session);
        ctrsession.setInitMax(maxClientNum);
        map.put(key, ctrsession);
    }
    if(ctrsession.getNum()==0)
    {
        //分配完重新分配
        //更新；
        map.remove(key);
    }
    ClientSession client=ctrsession.getSession();
    if(client.isClose())
    {
        //更新；
        map.remove(key);
        ctrsession=new ControlSession();
        ClientSession session= SessionFactory.createObj();
        ctrsession.setSession(session);
        ctrsession.setInitMax(maxClientNum);
        map.put(key, ctrsession);
    }
    client.setClientIncrement();//被客户端使用
    return client;
}

/**
 * 设置最大共享
 */
public static void setSessionMax(int num)
{
    maxClientNum=num;
}

/*
 * 删除正在分配session
 */
public static void removeKey(String key)
{
    map.remove(key);
}
}
