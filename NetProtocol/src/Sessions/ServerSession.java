/**    
 * 文件名：ServerSession.java    
 *    
 * 版本信息：    
 * 日期：2017年7月7日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import BufferData.AckPackaget;
import BufferData.AppData;
import BufferData.ReceiveBuffer;
import EventBus.MessageBus;
import JNetSocket.UDPClient;
import NetModel.NetDataAddress;
import NetPackaget.CreateNetPackaget;
import NetPackaget.ReturnCode;
import NetProtocol.judpServer;


/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：ServerSession    
 * 类描述：    session 接收数据
 * 包括服务端接收数据及客户端接收返回数据的组织
 * 该类主要是组织数据使用
 * 创建人：jinyu    
 * 创建时间：2017年7月7日 上午12:07:22    
 * 修改人：jinyu    
 * 修改时间：2017年7月7日 上午12:07:22    
 * 修改备注：    
 * @version     
 *     
 */
public class ServerSession extends Session {
    private ReceiveBuffer buffer;
    private ReceiveBuffer preBuf;
    private Lock lock = new ReentrantLock();// 锁对象
    private AppData currentChunk;//最后读取的数据;
    private int readNull;//读取null
    private volatile boolean isCall=false;//返回端不再需要读取
    private  ArrayList<byte[]> list=new  ArrayList<byte[]>();//读取数据
    private  TransferQueue<NetDataAddress>  queue=new LinkedTransferQueue<NetDataAddress>();
    private int readState=0;//读取状态
    private long readLen=0;//读取长度
    private long readOffset=0;//外部读取长度
    private long lastInitSeq=-1;//最新一次初始序列
    private long readInitSeq=0;//读取的initseq
    private volatile boolean clientClose=false;//session关闭
    private int readbufIndex=0;
    private final int bufsize=2000;
    private ReceiveBuffer[] buf=new ReceiveBuffer[bufsize];
    private AtomicLong  bufNum=new AtomicLong(0);
    private volatile boolean isStartData=false;//读取线程触发
    private volatile boolean zeroNum=true;//当前还是一包都没有读取;
    private volatile int zeroRead=0;//当前首包读取次数
    
    //private ConcurrentHashMap<Long,ReceiveBuffer> mapBuf=new ConcurrentHashMap<Long,ReceiveBuffer>();
    public ServerSession(String srcIP, int srcPort, String localIP, int localPort) {
       super(srcIP,srcPort,localIP,localPort);
      
       }
    
    /*
     * 启动接收数据组织
     */
    private void startThread()
    {
        if(isStartData)
        {
            return ;
        }
        isStartData=true;
        cachedThreadPool.execute(new Runnable(){
            @Override
            public void run() {
                Thread.currentThread().setName(ServerManager.getThreadName()+"_"+getID());
                if(bufNum.get()==0)
                {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                      
                        e.printStackTrace();
                    }
                }
               while(true) {
                checkRecvice();
                if(bufNum.get()==0)
                {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                   if(bufNum.get()>0)
                   {
                       continue;
                   }
                    isStartData=false;
                 //   ReceiveBuffer[] buftmp=buf;
                 //   int ss= readbufIndex;
                    System.gc();
                    System.out.println( Thread.currentThread().getName()+"退出");
                    //数据接收完成；
                    break;
                }
                if(isClose())
                {
                    isStartData=false;
                    System.gc();
                    break;
                }
               }
            }
           });
    }
    /*
     * 设置返回
     * 只是在发送方接收数据使用
     */
    @Override
    public void setCall()
    {
        isCall=true;
    }
    /*
     * 收取所有数据
     */
  private void checkRecvice()
    {
      
        byte[] all= readAll();//阻塞读取
        NetDataAddress data=new NetDataAddress();
        data.srcIP=this.getSrcIP();
        data.srcPort=this.getSrcPort();
        data.localIP=this.getLocalIP();
        data.localPort=this.getLocalPort();
        data.srcid=this.getClientID();
        data.netData=all;
        readState++;
        MessageBus.post(String.valueOf(data.localPort), data);
        if(queue.getWaitingConsumerCount()==0)
        {
            queue.clear();
        }
        queue.tryTransfer(data);
        System.out.println("完成sessionid:"+this.getID());
        judpServer tmp=  ServerManager.getSocket(data.localPort);
        if(tmp!=null)
        {
            tmp.addData(this);
        }
        if(all==null)
        {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
             
                e.printStackTrace();
            }
        }
        //完成
        if(lastInitSeq!=readInitSeq)
        {
            UDPClient client=new UDPClient();
            try
            {
          
           AckPackaget ack=new AckPackaget();
           ack.packagetID=readInitSeq;
           ack.sessionid=this.getID();
           ack.ackType=1;
           ack.clientID=this.getClientID();
         
            byte[]senddata= CreateNetPackaget.createAckPackaget(ack);
            client.sendData(this.getSrcIP(), this.getSrcPort(), senddata);
            lastInitSeq=readInitSeq;//控制只发送一次
            client.close();
            }
            catch(Exception ex)
            {
                client.close();
            }
            finally
            {
                client.close();
            }
        }
        //

        buf[readbufIndex%bufsize]=null;
        if(buffer!=null)
        buffer.clear();
        readbufIndex++;
        if(bufNum.get()<=0&&clientClose)
        {
            bufNum.set(0);
            //可以清除全部；
            this.setClose();
            cachedThreadPool.shutdown();
            String key=this.getSrcIP()+this.getSrcPort();
            ServerManager.addSessionMap(key);
            ServerManager.addSession(key, this.getID());
          
        }
    }
  private  void intBuffer(int num,long initSeq)
  {
      if(num==-1||initSeq==-1)
      {
          return;
      }
          preBuf=buf[(int) (initSeq%bufsize)];
          if(preBuf==null)
          {
              try {
                  lock.lockInterruptibly();
                  if(preBuf==null)
                  {
                      preBuf=new ReceiveBuffer(num,initSeq);
                  }
                  lock.unlock();
              } catch (InterruptedException e) {
                  lock.unlock();
              }
              buf[(int) (initSeq%bufsize)]=preBuf;
              bufNum.incrementAndGet();
          }
          startThread();
  }
  /*
   * 保存
   */
  private boolean  setData(AppData data)
  {
      return buf[(int) (data.getInitSequenceNumber()%bufsize)].offer(data);
  }
  
    @Override
    public void sendData(long id,String sIP, int sPort, byte[] data) {
        
    }

    @Override
    public void sendData(long id,String localIP, int localPort, String sIP, int sPort, byte[] data) {
        
    }

    @Override
    public void addData(ReturnCode returnCode) {
        intBuffer(returnCode.packagetNum,returnCode.InitSeq);
        if(this.isClose())
        {
            return;//已经不适使用的session
        }
        if(returnCode.isAck)
        {
            //
            if(returnCode.ackPackaget.ackType==3)
            {
                this.clientClose=true;
            }
            return;
        }
        AppData buf=new AppData(returnCode.packagetNum,returnCode.InitSeq,returnCode.PackagetID, returnCode.data);
        setData(buf);
    }
    
    /**
     * 读取所有数据
     */
    private byte[] readAll()
    {
        list.clear();
        int len=0;
        readLen=0;
        buffer=buf[readbufIndex%bufsize];
        boolean isWait=false;
        while(buffer==null)
        {
        if(buffer==null)
        {
            if(bufNum.get()==0)
            {
                //已经读取完了;
                return null;
            }
            else
            {
                if(isWait)
                {
                  readbufIndex++;
                  isWait=false;//置回下次等待
                }
                else
                {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    isWait=true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            }
        }
          buffer=buf[readbufIndex%bufsize];
        }
        
        //准备读取数据
        bufNum.decrementAndGet();
        while(true)
        {
            if(isCall)
            {
                //发送方已经不再接收
                list.clear();
                buffer.clear();
                return null;
            }
           updateCurrentChunk(false);//取一次数据
            while(currentChunk!=null){
                    //读取到数据就一直读取；
                    byte[]data=currentChunk.data;
                    list.add(data);
                    len+=data.length;
                    readLen+=data.length;
                    readInitSeq=currentChunk.getInitSequenceNumber();
                    currentChunk=null;
                    readNull=0;//重置，可以通讯
                    zeroNum=false;
                    //
                    if(buffer.isEmpty())
                    {
                        break;
                    }
                   
                    updateCurrentChunk(true);
                }
                if(readNull>10)
                {
                    //视为重发失败，无法再获取数据，等待10秒数据重发；
                    //数据清除；
                    this.setClose();
                    list.clear();
                    buffer.clear();
                    return null;
                }
               if(buffer.isEmpty())
               {
                   //读取完成
                   byte[] all=new byte[len];
                   int offset=0;
                   for(int i=0;i<list.size();i++)
                   {
                       byte[] tmp=list.get(i);
                       System.arraycopy(tmp, 0, all, offset, tmp.length);
                       offset+=tmp.length;
                   }
                   list.clear();
                  
                   return all;
               }
               if(zeroRead>0&&zeroNum)
               {
                   try {
                    TimeUnit.SECONDS.sleep(5);
                  
                } catch (InterruptedException e) {
                  
                    e.printStackTrace();
                }
               }
            
        }
             
    }
    /*
     * 获取单个数据块
     */
    private void updateCurrentChunk(boolean block)
    {
        if(currentChunk!=null)return;
        while(true){
            try{
                if(block){
                     currentChunk=buffer.poll(100, TimeUnit.MILLISECONDS);//整个取出
                    while (currentChunk==null){
                        //循环直到取出
                        currentChunk=buffer.poll(1000, TimeUnit.MILLISECONDS);
                        if(currentChunk==null)
                        {
                            //可能丢包了
                            readNull++;
                            UDPClient client=new UDPClient();
                            AckPackaget ack=new AckPackaget();
                            ack.packagetID=buffer.waitSequenceNumber();
                            ack.sessionid=this.getID();
                            ack.ackType=2;
                            ack.clientID=this.getClientID();
                            byte[]data= CreateNetPackaget.createAckPackaget(ack);
                            client.sendData(this.getSrcIP(), this.getSrcPort(), data);
                            if(readNull>10)
                            {
                                //没有数据时会等待1秒，10次10秒
                                break;
                            }
                        }
                    }
                }
                else {
                        currentChunk=buffer.poll(100, TimeUnit.MILLISECONDS);
                        zeroRead++;
                    }
               //不管取不取都执行
            }catch(InterruptedException ie){
                IOException ex=new IOException();
                ex.initCause(ie);
            }
            return;//跳出循环
        }
    }

    @Override
    public NetDataAddress read(int len) {
        //如果在这里读取，则需要删除数据，不能有另外的地方调其它接口
        NetDataAddress data=new NetDataAddress();
        data.srcIP=this.getSrcIP();
        data.srcPort=this.getSrcPort();
        data.localIP=this.getLocalIP();
        data.localPort=this.getLocalPort();
        if(readOffset<readLen)
        {
            //有数据可以读取；
            long dataLen=readLen-readOffset;
            int index=0;
            if(dataLen<len)
            {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    
                } catch (InterruptedException e) {
                  
                    e.printStackTrace();
                }
            }
            if(dataLen>=len)
            {
                //足够长；
              
                byte[] bytedata=new byte[len];
                while(index<len)
                {
                byte[] rData=list.remove(0);
                //
                int curLen=len-index;
                if(curLen>=rData.length)
                {
                    System.arraycopy(rData, 0, bytedata, index, rData.length);
                    index+=rData.length;
                }
                else
                {
                    System.arraycopy(rData, 0, bytedata, index, curLen);
                    //剩余
                    byte[] bytetmp=new byte[rData.length-curLen];
                    System.arraycopy(rData, curLen, bytetmp, 0, bytetmp.length);
                    list.set(0, bytetmp);
                }
                
                }
                data.netData=bytedata;
                readOffset+=len;
            }
            else
            {
                byte[] bytedata=new byte[(int) dataLen];
                while(index<dataLen)
                {
                    if(list.isEmpty())
                    {
                        break;
                    }
                 byte[] rData=list.remove(0);
                //
                int curLen=len-index;
                if(curLen>=rData.length)
                {
                    System.arraycopy(rData, 0, bytedata, index, rData.length);
                    index+=rData.length;
                }
                else
                {
                    System.arraycopy(rData, 0, bytedata, index, curLen);
                    //剩余
                    byte[] bytetmp=new byte[rData.length-curLen];
                    System.arraycopy(rData, curLen, bytetmp, 0, bytetmp.length);
                    list.set(0, bytetmp);
                }
                
                }
                data.netData=bytedata;
                readOffset+=dataLen;
            }
            //
            data.len=data.netData.length;
        }
        else
        {
            //没有数据
            if(readState>0)
            {
                //已经完成
                data.netData=null;
                data.len=-1;
                readOffset=0;
            }
            else
            {
                data.netData=new byte[0];
                data.len=0;
            }
        }
       return data;
    }

    @Override
    public NetDataAddress read() {
        try {
          return queue.poll(1,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
         
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void close() {
        this.setClose();
    }

    @Override
    public int getClientNum() {
     //接收端无用
        return 0;
    }

    @Override
    public byte[] removeData(long packagetid) {
        // TODO Auto-generated method stub
        return null;
    }

}
