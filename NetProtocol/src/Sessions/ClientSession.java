/**    
 * 文件名：ClientSession.java    
 *    
 * 版本信息：    
 * 日期：2017年7月7日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
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
 * 项目名称：NetProtocol    
 * 类名称：ClientSession    
 * 类描述：  发送端session  
 * 创建人：jinyu    
 * 创建时间：2017年7月7日 上午12:06:47    
 * 修改人：jinyu    
 * 修改时间：2017年7月7日 上午12:06:47    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientSession extends Session {
    ListenerData recData=new ListenerData(this);
    /**
     * 保存数据包
     */
  private  ConcurrentHashMap<Long,byte[]> mapCache=new ConcurrentHashMap<Long,byte[]>();
  
 // private  ConcurrentHashMap<Long,MemoryChunk> mapMemoryCache=new ConcurrentHashMap<Long,MemoryChunk>();
    /**
     * 保存每次的数据量；
     */
  private  ConcurrentHashMap<Long,Integer> mapInitSeq=new ConcurrentHashMap<Long,Integer>();
 
  /**
   * 判断客户端使用情况
   * 被客户端使用一次则增加一次
   * 客户端关闭一次则减少一次
   * 由于判断客户端回收
   */
  private AtomicInteger clientNum=new AtomicInteger(0);
  
  /*
   * 远端IP
   */
  private String srcIP;
  
  /*
   * 远端端口
   */
  private int port;
  public ClientSession()
    {
      
    }
    UDPClient client=new UDPClient();
    private volatile boolean initRec=false;
    
    /*
     * 接收数据
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
                      System.out.println( Thread.currentThread().getName()+"退出");
                      break;
                  }
                  recCall();
                
              }
            }
            
        });
    }
   
    /*
     * 设置使用
     */
    public void setClientIncrement()
    {
        clientNum.incrementAndGet();
    }
    
    /*
     * 设置不使用
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
          //每添加一次数据
          ClientManager.addSessionData(this.getID(),packageid,sendData.length);
          index--;
          if(index>0)
          packageid=PackagetRandom.getInstanceID(this);
          else
              break;
       }
        startThread();//启动监听
        //发送端确认发送acks
        //防止单包数据丢失
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
       //传入ack;
        if(returnCode.isAck)
        {
            //
            if(returnCode.ackPackaget.ackType==1)
            {
                //完成,清除数据
                Integer pSize= mapInitSeq.get(returnCode.ackPackaget.packagetID);
                if(pSize==null)
                {
                    //已经接受到数据
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
                 //已经逻辑关闭，并且没有缓存数据，则真正关闭
                  this.close();
                  ClientManager.getSession(getID());//直接删除
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
                   System.out.println("丢失："+returnCode.ackPackaget.clientID);
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
          //完成
          session.setCall();
          String key=netData.srcIP+netData.srcPort;
          recData.remove(key);
          
      }
        return netData;
    }
    @Override
    public void setCall() {
      //发送端无法
      //接收端组织数据供发送端使用时做重置
        
    }
    @Override
    public void close() {
        if(this.isClose())
        {
            return;//外部在时间或者消失控制时可能是多线程调用
           
        }
        this.setClose();
        //先发送关闭
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
