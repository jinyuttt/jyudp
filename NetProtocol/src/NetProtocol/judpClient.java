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
import Sessions.ClientSession;
import Sessions.SessionFactory;

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
        clientSession=SessionFactory.createObj();
    }
    private  boolean isColse=false;
    private  boolean managerOutTime=false;
    private  ClientSession  clientSession=null;
    public boolean isOutTime()
    {
        return managerOutTime;
    }
    public void setOutTime()
    {
        managerOutTime=true;
    }
 
    public long getSessionID()
    {
      return  clientSession.getID();
    }
    /**
     * 发送数据
     * 
     */
    public void sendData(String sIP,int sPort,byte[]data)
    {
        clientSession.sendData(sIP, sPort, data);
    }
    /*
     * 发送数据
     * 绑定本地地址
     */
    public void sendData(String  localIP,int  localPort, String sIP,int sPort,byte[]data)
    {
       
        clientSession.sendData(localIP, localPort, sIP, sPort, data);
    }
    
 
    /**
     * 逻辑上关闭
     */
    public void close()
    {
        clientSession.setLogicalClose();
        isColse=true;
    }
    /**
     * 返回逻辑关闭
     */
    public boolean isClose()
    {
        clientSession.isLogicalClose();
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
