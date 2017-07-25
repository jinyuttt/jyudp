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
import java.util.concurrent.atomic.AtomicInteger;

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
  
 // private  ConcurrentHashMap<Long,MemoryChunk> mapMemoryCache=new ConcurrentHashMap<Long,MemoryChunk>();
    /**
     * ����ÿ�ε���������
     */
  private  ConcurrentHashMap<Long,Integer> mapInitSeq=new ConcurrentHashMap<Long,Integer>();
 
  /**
   * �жϿͻ���ʹ�����
   * ���ͻ���ʹ��һ��������һ��
   * �ͻ��˹ر�һ�������һ��
   * �����жϿͻ��˻���
   */
  private AtomicInteger clientNum=new AtomicInteger(0);
  
  /*
   * Զ��IP
   */
  private String srcIP;
  
  /*
   * Զ�˶˿�
   */
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
       if(data!=null)
       {
        model.srcIP=data.srcIP;
        model.srcPort=data.srcPort;
        model.data=data.netData;
        this.setLocalIP(client.getLocalHost());
        this.setLocalPort(client.getLocalPort());
        recData.monitorServer(model);
       }
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
            Thread.currentThread().setName("clientSession_"+getID());
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
   
    /*
     * ����ʹ��
     */
    public void setClientIncrement()
    {
        clientNum.incrementAndGet();
    }
    
    /*
     * ���ò�ʹ��
     */
    public void setClientDecrement()
    {
        clientNum.decrementAndGet();
    }
    
    @Override
    public void sendData(long id,String sIP, int sPort, byte[] data) {
        //
        if(data==null)
        {
            return;
        }
        LinkedList<byte[]> subPackaget=SubPackaget.subData(data);
        long initseq=PackagetRandom.getInstanceID(this);
        int size=subPackaget.size();
        int index=size;
        mapInitSeq.put(initseq, size);
        this.srcIP=sIP;
        this.port=sPort;
        long packageid=initseq;
      while(true)
       {
          byte[]sendData=CreateNetPackaget.createNetPackaget(id,this.getID(), initseq,packageid , size, subPackaget.removeFirst());
          client.sendData(sIP, sPort, sendData);
          mapCache.put(packageid, sendData);
          //ÿ���һ������
          ClientManager.addSessionData(this.getID(),packageid,sendData.length);
          index--;
          if(index>0)
          packageid=PackagetRandom.getInstanceID(this);
          else
              break;
       }
        startThread();//��������
        //���Ͷ�ȷ�Ϸ���acks
        //��ֹ�������ݶ�ʧ
        AckPackaget ack=new AckPackaget();
        ack.ackType=0;
        ack.clientID=id;
        ack.packagetNum=size;
        ack.sessionid=getID();
        ack.packagetID=initseq;
        byte[] ackbytes= CreateNetPackaget.createAckPackaget(ack);
        client.sendData(sIP, sPort, ackbytes);
        //
      
    }

    @Override
    public void sendData(long id,String localIP, int localPort, String sIP, int sPort, byte[] data) {
        client.bindLocal(localIP, localPort);
        this.sendData(id,sIP, sPort, data);
        
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
                Integer pSize= mapInitSeq.get(returnCode.ackPackaget.packagetID);
                if(pSize==null)
                {
                    //�Ѿ����ܵ�����
                    return;
                }
              int size= pSize ;
              for(int i=0;i<=size;i++)
              {
                byte[] cachedata= mapCache.remove(returnCode.ackPackaget.packagetID+i);
                if(cachedata==null)
                {
                    ClientManager.addRemoveFile(getID(), returnCode.ackPackaget.packagetID+i);;
                }
              }
              mapInitSeq.remove(returnCode.ackPackaget.packagetID);
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
               if(data==null)
               {
                  data=ClientManager.getDBData(getID(), returnCode.ackPackaget.packagetID);
               }
               if(data==null)
               {
                   System.out.println("��ʧ��"+returnCode.ackPackaget.clientID);
               }
               this.sendData(returnCode.ackPackaget.clientID,this.srcIP, this.port, data);
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
      //���Ͷ��޷�
      //���ն���֯���ݹ����Ͷ�ʹ��ʱ������
        
    }
    @Override
    public void close() {
        if(this.isClose())
        {
            return;//�ⲿ��ʱ�������ʧ����ʱ�����Ƕ��̵߳���
           
        }
        this.setClose();
        //�ȷ��͹ر�
        AckPackaget ack=new AckPackaget();
        ack.ackType=3;
        ack.sessionid=this.getID();
        byte[]data= CreateNetPackaget.createAckPackaget(ack);
        client.sendData(srcIP, port, data);
        client.close();
        
    }
    @Override
    public int getClientNum() {
       return clientNum.get();
    }
    @Override
    public byte[] removeData(long packagetid) {
      return  mapCache.remove(packagetid);
    }
 

}
