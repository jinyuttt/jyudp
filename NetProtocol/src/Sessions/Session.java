/**    
 * 文件名：Session.java    
 *    
 * 版本信息：    
 * 日期：2017年7月6日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import NetModel.NetDataAddress;
import NetPackaget.ReturnCode;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：Session    
 * 类描述：    会话基类
 * 创建人：jinyu    
 * 创建时间：2017年7月6日 下午11:56:25    
 * 修改人：jinyu    
 * 修改时间：2017年7月6日 下午11:56:25    
 * 修改备注：    
 * @version     
 *     
 */
public abstract class Session {
   
    protected ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public long createTime=System.currentTimeMillis();
    public Session(String srcIP,int srcPort,String localIP,int localPort)
    {
        this.srcIP=srcIP;
        this.srcPort=srcPort;
        this.localIP=localIP;
        this.localPort=localPort;
    }
    public Session()
    {
        
    }
    
private long clientid;
private long seesionid;
/*
 * 来源Ip
 */
private String srcIP;
/*
* 来源端口
*/
private int srcPort;

/**
* 本地IP
*/
private String localIP;

/*
* 本地IP
*/
private int localPort;

/*
 * 本地绑定端口
 */
private int localBindPort=0;
/*
* 通讯协议类型 0 tcp  1 udp  2 组播 3 广播
*/
private int  netType;

/*
 * 真正关闭
 */
private volatile boolean isClose=false;

/*
 * 逻辑关闭
 */
private volatile boolean logicalClose=false;


public void setClose()
{
    isClose=true;
}
public boolean isClose()
{
    return isClose;
}
public long getID()
{
    return seesionid;
}
public void setID(long id)
{
    seesionid=id;
}
public String getSrcIP()
{
    return srcIP;
}
public void setSrcIP(String addr)
{
    srcIP=addr;
}
public int getSrcPort()
{
    return srcPort;
}
public void setSrcPort(int port)
{
    srcPort=port;
}
public String getLocalIP()
{
    return localIP;
}
public void setLocalIP(String addr)
{
    localIP=addr;
}
public int getLocalPort()
{
    return localPort;
}
public void setLocalPort(int port)
{
    localPort=port;
}
public int getNetType()
{
    return netType;
}
public void setNetType(int type)
{
    netType=type;
}

/**
 * 设置逻辑关闭
 * 时间到期
 */
public void setLogicalClose()
{
    logicalClose=true;
}
/**
 * 获取是否逻辑关闭
 */
public boolean isLogicalClose()
{
    return logicalClose;
}

/*
 * 设置绑定端口
 */
public void setLocalBindPort(int port)
{
    localBindPort=port;
}
/*
 * 获取绑定端口
 */
public int getLocalBindPort()
{
    return localBindPort;
}
//serversession准备
public void setClientID(long id)
{
    clientid=id;
}
public long getClientID()
{
    return clientid;
}
/**
 * 关闭
 */
public abstract void close();

/**
 * 
 * 设置返回
 */
public abstract void setCall();

/*
 * 发送数据
 */
public abstract void sendData(long id,String sIP,int sPort,byte[]data);

/*
 * 发送数据
 */
public  abstract void sendData(long id,String  localIP,int  localPort, String sIP,int sPort,byte[]data);

/*
 * 加入解析数据
 */
public abstract void addData(ReturnCode returnCode);

/*
 * 读取数据
 */
public abstract NetDataAddress read();

/*
 * 读取定长度的数据
 */
public abstract NetDataAddress read(int len);

/*
 * 获取占用的客户端数据
 * 主要是发送端
 */
public abstract int getClientNum();

/*
 * 清除数据
 */
public abstract byte[] removeData(long packagetid);
}
