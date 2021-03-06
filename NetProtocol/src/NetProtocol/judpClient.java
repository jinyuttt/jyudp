/**    
 * 文件名：judp.java    
 *    
 * 版本信息：    
 * 日期：2017年6月11日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetProtocol;

import NetModel.NetDataAddress;
import NetPackaget.PackagetRandom;
import Sessions.ClientManager;
import Sessions.ClientSession;
import Sessions.ClientSessionsPools;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：judp    
 * 类描述：   udp客户端
 * 被封装发送 
 * 创建人：jinyu    
 * 创建时间：2017年6月11日 下午7:12:40    
 * 修改人：jinyu    
 * 修改时间：2017年6月11日 下午7:12:40    
 * 修改备注：    
 * @version     
 *     
 */
public class judpClient {
    public judpClient()
    {
        id=PackagetRandom.getSequeueID();
    }
    private  boolean isColse=false;
    private  ClientSession  clientSession=null;
    private long id=-1;
    public  long  getID()
    {
        return id;
    }
    public long getSessionID()
    {
        if(clientSession==null)
        {
            return -1;
        }
      return  clientSession.getID();
    }
    /**
     * 发送数据
     * 
     */
    public void sendData(String sIP,int sPort,byte[]data)
    {
       if(clientSession==null)
       {
           clientSession=ClientSessionsPools.getSession(0, sIP, sPort);
       }
       ClientManager.addClient(this);
       clientSession.sendData(id,sIP, sPort, data);
    }
    /*
     * 发送数据
     * 绑定本地地址
     */
    public void sendData(String  localIP,int  localPort, String sIP,int sPort,byte[]data)
    {
        if(clientSession==null)
        {
            clientSession=ClientSessionsPools.getSession(localPort, sIP, sPort);
            clientSession.setLocalBindPort(localPort);
        }
       clientSession.sendData(id,localIP, localPort, sIP, sPort, data);
    }
    
 
    /**
     * 逻辑上关闭
     */
    public void close()
    {
        clientSession.setClientDecrement();
        isColse=true;
    }
    /**
     * 返回逻辑关闭
     */
    public boolean isClose()
    {
      
        return isColse;
    }
    
    /*
     * 接收数据
     */
    public byte[]  getCallBackData()
    {
          NetDataAddress   data=  clientSession.read();
           if(data!=null)
           {
               return data.netData;
           }
           else
           {
               return null;
           }
    }
    /*
     * 接收数据
     */
    public int  getCallBackData(byte[] data)
    {
        if(data==null||data.length==0)
        {
            return 0;
        }
        NetDataAddress   calldata=  clientSession.read(data.length);
           if(calldata!=null)
           {
               System.arraycopy(calldata.netData, 0, data, 0, calldata.netData.length);
               
           }
           return calldata.netData.length;
    }
}
