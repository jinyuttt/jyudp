/**    
 * �ļ�����ClientSession.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��7��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Sessions;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import BufferData.AckPackaget;
import JNetSocket.UDPClient;
import NetModel.DataModel;
import NetModel.NetDataAddress;
import NetPackaget.CreateNetPackaget;
import NetPackaget.PackagetRandom;
import NetPackaget.ReturnCode;

import NetPackaget.SubPackaget;
import NetProtocol.ListenerData;


/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ClientSession    
 * ��������  ���Ͷ�session  
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��7�� ����12:06:47    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��7�� ����12:06:47    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ClientSession extends Session {
    ListenerData recData=new ListenerData(this);
    /**
     * �������ݰ�
     */
  private  ConcurrentHashMap<Long,byte[]> mapCache=new ConcurrentHashMap<Long,byte[]>();
    /**
     * ����ÿ�ε���������
     */
  private  ConcurrentHashMap<Long,Integer> mapInitSeq=new ConcurrentHashMap<Long,Integer>();
   
  private String srcIP;
  private int port;
  public ClientSession()
    {
      
    }
    UDPClient client=new UDPClient();
    private volatile boolean initRec=false;
    
    /*
     * ��������
     */
    private void recCall()
    {
        NetDataAddress data= client.getCallData();
        DataModel model=new DataModel();
        model.srcIP=data.srcIP;
        model.srcPort=data.srcPort;
        model.data=data.netData;
        this.setLocalIP(client.getLocalHost());
        this.setLocalPort(client.getLocalPort());
        recData.monitorServer(model);
    }
    private void startThread()
    {
        if(initRec)
        {
            return;
        }
        initRec=true;
        cachedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
            Thread.currentThread().setName(ClientManager.getThreadName());
              while(true)
              {
                  if(isClose())
                  {
                      System.out.println( Thread.currentThread().getName()+"�˳�");
                      break;
                  }
                  recCall();
                
              }
            }
            
        });
    }
    @Override
    public void sendData(String sIP, int sPort, byte[] data) {
        //
        LinkedList<byte[]> subPackaget=SubPackaget.subData(data);
        long initseq=PackagetRandom.getInstanceID(this);
        int size=subPackaget.size();
        int index=size;
        mapInitSeq.put(initseq, size);
        this.srcIP=sIP;
        this.port=sPort;
        while(index>0)
       {
          long packageid=PackagetRandom.getInstanceID(this);
         
          byte[]sendData=CreateNetPackaget.createNetPackaget(this.getID(), initseq,packageid , size, subPackaget.removeFirst());
          client.sendData(sIP, sPort, sendData);
          mapCache.put(packageid, sendData);
          index--;
       }
        startThread();//��������
    }

    @Override
    public void sendData(String localIP, int localPort, String sIP, int sPort, byte[] data) {
        client.bindLocal(localIP, localPort);
        this.sendData(sIP, sPort, data);
        
    }

    @Override
    public void addData(ReturnCode returnCode) {
       //����ack;
        if(returnCode.isAck)
        {
            //
            if(returnCode.ackPackaget.ackType==1)
            {
                //���,�������
                Integer pSize= mapInitSeq.get(returnCode.InitSeq);
                if(pSize==null)
                {
                    //�Ѿ����ܵ�����
                    return;
                }
              int size= pSize ;
              for(int i=0;i<=size;i++)
              {
                  mapCache.remove(returnCode.InitSeq+i);
              }
              mapInitSeq.remove(returnCode.InitSeq);
              if(this.isLogicalClose()&&mapInitSeq.isEmpty())
              {
                 //�Ѿ��߼��رգ�����û�л������ݣ��������ر�
                  this.close();
                  ClientManager.getSession(getID());//ֱ��ɾ��
                  recData=null;
                  cachedThreadPool.shutdown();
                  
              }
            }
            else if(returnCode.ackPackaget.ackType==2)
            {
                byte[] data=mapCache.get(returnCode.ackPackaget.packagetID);
                this.sendData(this.srcIP, this.port, data);
            }
        }
        
    }

    @Override
    public NetDataAddress read() {
     
      return  recData.getData(this.getSrcIP(), this.getSrcPort(), this.getLocalIP(), this.getLocalPort(), this.getID(), this.getNetType());
    }

    @Override
    public NetDataAddress read(int len) {
      Session session=  recData.getDataLen(this.getSrcIP(), this.getSrcPort(), this.getLocalIP(), this.getLocalPort(), this.getID(), this.getNetType());
      NetDataAddress netData= session.read(len);
      if(netData.len==-1)
      {
          //���
          session.setCall();
          String key=netData.srcIP+netData.srcPort;
          recData.remove(key);
          
      }
        return netData;
    }
    @Override
    public void setCall() {
      
        
    }
    @Override
    public void close() {
        AckPackaget ack=new AckPackaget();
        ack.ackType=3;
        ack.sessionid=this.getID();
        byte[]data= CreateNetPackaget.createAckPackaget(ack);
        client.sendData(srcIP, port, data);
        client.close();
        
    }
 

}