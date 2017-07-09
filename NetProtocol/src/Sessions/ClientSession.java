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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import BufferData.AckPackaget;
import BufferData.MemoryChunk;
import BufferData.MemoryPool;
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
//  private  ConcurrentHashMap<Long,byte[]> mapCache=new ConcurrentHashMap<Long,byte[]>();
  
  private  ConcurrentHashMap<Long,MemoryChunk> mapMemoryCache=new ConcurrentHashMap<Long,MemoryChunk>();
    /**
     * 保存每次的数据量；
     */
  private  ConcurrentHashMap<Long,Integer> mapInitSeq=new ConcurrentHashMap<Long,Integer>();
 
  /**
   * 判断客户端使用情况
   * 被客户端使用一次则增加一次
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
          byte[]sendData=CreateNetPackaget.createNetPackaget(id,this.getID(), initseq,packageid , size, subPackaget.removeFirst());
          client.sendData(sIP, sPort, sendData);
          //mapCache.put(packageid, sendData);
          MemoryChunk chunk= MemoryPool.getBlock(sendData.length);
          chunk.setData(this.getID(),packageid,sendData);
          mapMemoryCache.put(packageid, chunk);
          index--;
       }
        startThread();//启动监听
        //
        ArrayList<Long> removeID= MemoryPool.getPackaget(this.getID());
        if(removeID!=null)
        {
            for(int i=0;i<removeID.size();i++)
            {
                mapMemoryCache.remove(removeID.get(i));
            }
        }
        System.gc();
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
                  mapMemoryCache.remove(returnCode.ackPackaget.packagetID+i);
                  //mapCache.remove(returnCode.ackPackaget.packagetID+i);
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
                MemoryChunk chunk=  mapMemoryCache.get(returnCode.ackPackaget.packagetID);
                if(chunk!=null)
                {
                    this.sendData(returnCode.ackPackaget.clientID,this.srcIP, this.port, chunk.getData());
                }
               // byte[] data=mapCache.get(returnCode.ackPackaget.packagetID);
                //this.sendData(returnCode.ackPackaget.clientID,this.srcIP, this.port, data);
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
 

}
