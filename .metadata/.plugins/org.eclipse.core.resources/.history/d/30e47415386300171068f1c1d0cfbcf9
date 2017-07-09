/**    
 * 文件名：RecviceBuffer.java    
 *    
 * 版本信息：    
 * 日期：2017年6月10日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package RecviceData;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**    
 *     
 * 项目名称：DataStromUtil    
 * 类名称：RecviceBuffer    
 * 类描述：   数据存储 
 * 创建人：jinyu    
 * 创建时间：2017年6月10日 上午11:10:34    
 * 修改人：jinyu    
 * 修改时间：2017年6月10日 上午11:10:34    
 * 修改备注：    
 * @version     
 *     
 */
public class ReceiveBuffer {
    private final AppData[]buffer;

   //读取位置
    private volatile int readPosition=0;
    private long highestReadSequenceNumber=-1;
    //the lowest sequence number stored in this buffer
   // private final long initialSequenceNumber;

    //number of chunks
    private final AtomicInteger numValidChunks=new AtomicInteger(-1);

    //lock and condition for poll() with timeout
    private final Condition notEmpty;
    private final ReentrantLock lock;

    //the size of the buffer
    private final int size;

    private long initialSequenceNumber=0;
    //private long highestReadSequenceNumber=0;
    public ReceiveBuffer(int size, long initialSequenceNumber){
        this.size=size;
        this.buffer=new AppData[size];
        this.initialSequenceNumber=initialSequenceNumber;
        lock=new ReentrantLock(false);
        notEmpty=lock.newCondition();
        highestReadSequenceNumber=-1;
    }
   /**
    * 存数据块
    */
    public boolean offer(AppData data){
        if(numValidChunks.get()==size) {
            return false;//如果获取的大小等于当前大小，则已经满了，不能存储
        }
        lock.lock();
        try{
            long seq=data.getSequenceNumber();//获取包中seq
            if(highestReadSequenceNumber<seq)
            {
                //保存当前来的最大seq
               // highestReadSequenceNumber=seq;
            }
            int insert=(int) (seq% size);//计算存储顺序位置
            buffer[insert]=data;//保存数据
            if(numValidChunks.get()<0)
            {
                numValidChunks.set(0);
            }
            numValidChunks.incrementAndGet();//存储增长
            notEmpty.signal();//唤醒读取
            return true;
        }finally{
            lock.unlock();
        }
    }
    /**
     * 
       
     * poll(超时读取数据块)      
       
     * @param   name    
       
     * @param  @return    设定文件    
     * @Exception 异常对象    
     */
    public AppData poll(int timeout, TimeUnit units)throws InterruptedException{
        lock.lockInterruptibly();//获取锁
        long nanos = units.toNanos(timeout);//转换毫秒

        try {
            for (;;) {
                //循环读取，直到没有数据
                if (numValidChunks.get() != 0) {
                    return poll();
                }
                if (nanos <= 0)
                    return null;//不等待
                try {
                    //没有数据时等待.等待唤醒
                    nanos = notEmpty.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notEmpty.signal(); // 
                    throw ie;
                }

            }
        } finally {
            lock.unlock();
        }
    }
    /**
     * 返回数据块
     */
    public AppData poll(){
        if(numValidChunks.get()==0){
            return null;//没有数据
        }
        AppData r=buffer[readPosition];
        if(r!=null){
            //充值当前值，准备读取下一个
            highestReadSequenceNumber=r.sequenceNumber;//读取的序列
            increment();
        }
        return r;
    }

    /**
     * 当前容积
     */
    public int getSize(){
        return size;
    }

    /*
     * 顺序读取
     */
    void increment(){
        buffer[readPosition]=null;
        readPosition++;
        if(readPosition==size)readPosition=0;
        numValidChunks.decrementAndGet();
    }

    /**
     * 数据是否已经读取完成
     */
    public boolean isEmpty(){
        if(numValidChunks.get()==0&&highestReadSequenceNumber!=-1&&readPosition+1==size)
        {
            return true;
        }
        else
        {
            return false;
        }
        //return numValidChunks.get()==0;
    }
    
    /**
     * 获取该读取seq
     */
   public long waitSequenceNumber()
   {
       //获取下一个seq,可能丢包
       if(readPosition==0)
       {
           //第一个都没有读取到；返回初始化seq
           return initialSequenceNumber;
       }
       else
       {
           return    highestReadSequenceNumber+1;
       }
   }
   public void clear()
   {
       for(int i=0;i<size;i++)
       {
           buffer[i]=null;
       }
   }
}
