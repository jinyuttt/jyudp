/**    
 * �ļ�����Session.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��6��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Sessions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import NetModel.NetDataAddress;
import NetPackaget.ReturnCode;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�Session    
 * ��������    �Ự����
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��6�� ����11:56:25    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��6�� ����11:56:25    
 * �޸ı�ע��    
 * @version     
 *     
 */
public abstract class Session {
   
    protected ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
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
private long seesionid;
/*
 * ��ԴIp
 */
private String srcIP;
/*
* ��Դ�˿�
*/
private int srcPort;

/**
* ����IP
*/
private String localIP;

/*
* ����IP
*/
private int localPort;
/*
* ͨѶЭ������ 0 tcp  1 udp  2 �鲥 3 �㲥
*/
private int  netType;

/*
 * �����ر�
 */
private volatile boolean isClose=false;

/*
 * �߼��ر�
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
 * �����߼��ر�
 */
public void setLogicalClose()
{
    logicalClose=true;
}
/**
 * ��ȡ�Ƿ��߼��ر�
 */
public boolean isLogicalClose()
{
    return logicalClose;
}
public abstract void close();
public abstract void setCall();
public abstract void sendData(String sIP,int sPort,byte[]data);
public  abstract void sendData(String  localIP,int  localPort, String sIP,int sPort,byte[]data);
public abstract void addData(ReturnCode returnCode);
public abstract NetDataAddress read();
public abstract NetDataAddress read(int len);

}