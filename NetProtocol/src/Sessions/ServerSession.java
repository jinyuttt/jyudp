/**    
 * �ļ�����ServerSession.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��7��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ServerSession    
 * ��������    session ��������
 * ��������˽������ݼ��ͻ��˽��շ������ݵ���֯
 * ������Ҫ����֯����ʹ��
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��7�� ����12:07:22    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��7�� ����12:07:22    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ServerSession extends Session {
    private ReceiveBuffer buffer;
    private ReceiveBuffer preBuf;
    private Lock lock = new ReentrantLock();// ������
    private AppData currentChunk;//����ȡ������;
    private int readNull;//��ȡnull
    private volatile boolean isCall=false;//���ض˲�����Ҫ��ȡ
    private  ArrayList<byte[]> list=new  ArrayList<byte[]>();//��ȡ����
    private  TransferQueue<NetDataAddress>  queue=new LinkedTransferQueue<NetDataAddress>();
    private int readState=0;//��ȡ״̬
    private long readLen=0;//��ȡ����
    private long readOffset=0;//�ⲿ��ȡ����
    private long lastInitSeq=-1;
    private long readInitSeq=0;//��ȡ��initseq
    private volatile boolean clientClose=false;
    private int readbufIndex=0;
    private final int bufsize=128;
    private ReceiveBuffer[] buf=new ReceiveBuffer[bufsize];
    private AtomicLong  bufNum=new AtomicLong(0);
    private volatile boolean isStartData=false;
    //private ConcurrentHashMap<Long,ReceiveBuffer> mapBuf=new ConcurrentHashMap<Long,ReceiveBuffer>();
    public ServerSession(String srcIP, int srcPort, String localIP, int localPort) {
       super(srcIP,srcPort,localIP,localPort);
      
       }
    
    /*
     * ��������������֯
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
                Thread.currentThread().setName(ServerManager.getThreadName());
                if(bufNum.get()==0)
                {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                      
                        e.printStackTrace();
                    }
                }
               while(true) {
                checkRecvice();
                if(bufNum.get()==0)
                {
                    isStartData=false;
                    System.gc();
                    System.out.println( Thread.currentThread().getName()+"�˳�");
                    //���ݽ�����ɣ�
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
     * ���÷���
     * ֻ���ڷ��ͷ���������ʹ��
     */
    public void setCall()
    {
        isCall=true;
    }
    /*
     * ��ȡ��������
     */
  private void checkRecvice()
    {
      
        byte[] all= readAll();//������ȡ
        NetDataAddress data=new NetDataAddress();
        data.srcIP=this.getSrcIP();
        data.srcPort=this.getSrcPort();
        data.localIP=this.getLocalIP();
        data.localPort=this.getLocalPort();
        data.netData=all;
        readState++;
        MessageBus.post(String.valueOf(data.localPort), data);
        if(queue.getWaitingConsumerCount()==0)
        {
            queue.clear();
        }
        queue.tryTransfer(data);
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
        //���
        if(lastInitSeq!=readInitSeq)
        {
            UDPClient client=new UDPClient();
            try
            {
          
           AckPackaget ack=new AckPackaget();
           ack.packagetID=readInitSeq;
           ack.sessionid=this.getID();
           ack.ackType=1;
           ack.sessionid=this.getID();
            byte[]senddata= CreateNetPackaget.createAckPackaget(ack);
            client.sendData(this.getSrcIP(), this.getSrcPort(), senddata);
            lastInitSeq=readInitSeq;//����ֻ����һ��
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

        buf[this.readbufIndex]=null;
        if(buffer!=null)
        buffer.clear();
        if(bufNum.get()<=0&&clientClose)
        {
            bufNum.set(0);
            //�������ȫ����
            this.setClose();
            cachedThreadPool.shutdown();
            String key=this.getSrcIP()+this.getSrcPort();
            ServerManager.addSessionMap(key);
            ServerManager.addSession(key, this.getID());
          
        }
    }
  private  void intBuffer(int num,long initSeq)
  {
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
              bufNum.getAndIncrement();
          }
          startThread();
      
     
  }
  /*
   * ����
   */
  private boolean  setData(AppData data)
  {
      return buf[(int) (data.getInitSequenceNumber()%bufsize)].offer(data);
  }
  
    @Override
    public void sendData(String sIP, int sPort, byte[] data) {
        
    }

    @Override
    public void sendData(String localIP, int localPort, String sIP, int sPort, byte[] data) {
        
    }

    @Override
    public void addData(ReturnCode returnCode) {
        intBuffer(returnCode.packagetNum,returnCode.InitSeq);
        if(this.isClose())
        {
            return;//�Ѿ�����ʹ�õ�session
        }
        if(returnCode.isAck)
        {
            //
            if(returnCode.ackPackaget.ackType==3)
            {
                this.clientClose=true;
            }
            
        }
        AppData buf=new AppData(returnCode.packagetNum,returnCode.InitSeq,returnCode.PackagetID, returnCode.data);
        setData(buf);
    }
    
    /**
     * ��ȡ��������
     */
    private byte[] readAll()
    {
        list.clear();
        int len=0;
        readLen=0;
        buffer=buf[readbufIndex%bufsize];
        int waitIndex=0;
        while(buffer==null)
        {
        if(buffer==null)
        {
            if(bufNum.get()==0)
            {
                //�Ѿ���ȡ����;
                return null;
            }
            else
            {
                try {
                    TimeUnit.SECONDS.sleep(1);
                     waitIndex++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(waitIndex%2==0)
                {
                    readbufIndex++;
                }
            }
        }
        }
        
        //׼����ȡ����
        bufNum.decrementAndGet();
        while(true)
        {
            if(isCall)
            {
                //���ͷ��Ѿ����ٽ���
                list.clear();
                buffer.clear();
                return null;
            }
           updateCurrentChunk(false);//ȡһ������
            while(currentChunk!=null){
                    //��ȡ�����ݾ�һֱ��ȡ��
                    byte[]data=currentChunk.data;
                    list.add(data);
                    len+=data.length;
                    readLen+=data.length;
                    readInitSeq=currentChunk.getInitSequenceNumber();
                    currentChunk=null;
                    readNull=0;//���ã�����ͨѶ
                    //
                    if(buffer.isEmpty())
                    {
                        break;
                    }
                   
                    updateCurrentChunk(true);
                }
                if(readNull>10)
                {
                    //��Ϊ�ط�ʧ�ܣ��޷��ٻ�ȡ���ݣ��ȴ�10�������ط���
                    //���������
                    this.setClose();
                    list.clear();
                    buffer.clear();
                    return null;
                }
               if(buffer.isEmpty())
               {
                   //��ȡ���
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
            
        }
             
    }
    /*
     * ��ȡ�������ݿ�
     */
    private void updateCurrentChunk(boolean block)
    {
        if(currentChunk!=null)return;
        while(true){
            try{
                if(block){
                     currentChunk=buffer.poll(1, TimeUnit.MILLISECONDS);//����ȡ��
                    while (currentChunk==null){
                        //ѭ��ֱ��ȡ��
                        currentChunk=buffer.poll(1000, TimeUnit.MILLISECONDS);
                        if(currentChunk==null)
                        {
                           
                            //���ܶ�����
                            readNull++;
                            UDPClient client=new UDPClient();
                            AckPackaget ack=new AckPackaget();
                            ack.packagetID=buffer.waitSequenceNumber();
                            ack.sessionid=this.getID();
                            ack.ackType=2;
                            byte[]data= CreateNetPackaget.createAckPackaget(ack);
                            client.sendData(this.getSrcIP(), this.getSrcPort(), data);
                            if(readNull>10)
                            {
                                //û������ʱ��ȴ�1�룬10��10��
                                break;
                            }
                        }
                    }
                }
                else currentChunk=buffer.poll(100, TimeUnit.MILLISECONDS);
               //����ȡ��ȡ��ִ��
            }catch(InterruptedException ie){
                IOException ex=new IOException();
                ex.initCause(ie);
            }
            return;//����ѭ��
        }
    }

    @Override
    public NetDataAddress read(int len) {
        //����������ȡ������Ҫɾ�����ݣ�����������ĵط��������ӿ�
        NetDataAddress data=new NetDataAddress();
        data.srcIP=this.getSrcIP();
        data.srcPort=this.getSrcPort();
        data.localIP=this.getLocalIP();
        data.localPort=this.getLocalPort();
        if(readOffset<readLen)
        {
            //�����ݿ��Զ�ȡ��
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
                //�㹻����
              
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
                    //ʣ��
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
                    //ʣ��
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
            //û������
            if(readState>0)
            {
                //�Ѿ����
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

}