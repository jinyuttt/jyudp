/**    
 * �ļ�����judp.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��11��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetProtocol;

import NetModel.NetDataAddress;
import Sessions.ClientSession;
import Sessions.SessionFactory;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�judp    
 * ��������   udp�ͻ���
 * ����װ���� 
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��11�� ����7:12:40    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��11�� ����7:12:40    
 * �޸ı�ע��    
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
     * ��������
     * 
     */
    public void sendData(String sIP,int sPort,byte[]data)
    {
        clientSession.sendData(sIP, sPort, data);
    }
    /*
     * ��������
     * �󶨱��ص�ַ
     */
    public void sendData(String  localIP,int  localPort, String sIP,int sPort,byte[]data)
    {
       
        clientSession.sendData(localIP, localPort, sIP, sPort, data);
    }
    
 
    /**
     * �߼��Ϲر�
     */
    public void close()
    {
        clientSession.setLogicalClose();
        isColse=true;
    }
    /**
     * �����߼��ر�
     */
    public boolean isClose()
    {
        clientSession.isLogicalClose();
        return isColse;
    }
    
    /*
     * ��������
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
     * ��������
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