/**    
 * 文件名：PackagetRandom.java    
 *    
 * 版本信息：    
 * 日期：2017年6月11日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetPackaget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**    
 *     
 * 项目名称：Common    
 * 类名称：PackagetRandom    
 * 类描述：   输出一个随机的唯一ID 
 * 创建人：jinyu    
 * 创建时间：2017年6月11日 下午5:01:10    
 * 修改人：jinyu    
 * 修改时间：2017年6月11日 下午5:01:10    
 * 修改备注：    
 * @version     
 *     
 */
public class PackagetRandom {
    private static String jvmPath="";
    private static Lock lock = new ReentrantLock();
    private static Lock jvmLock = new ReentrantLock();
    private   static AtomicLong aid=new AtomicLong(0);
    private   static WeakHashMap<Object,AtomicLong> atomicMap=new WeakHashMap<Object,AtomicLong>();
    /**
     * 输出随机数
     */
public static long getSessionID()
{
    Random r = new Random(UUID.randomUUID().hashCode());
    return  r.nextInt()*100+Calendar.getInstance().MINUTE;
}

/*
 * 输出递增值
 */
public static long getSequeueID()
{
    return aid.getAndIncrement();
}

/*
 * 给不同实例输出ID
 */
public static long getInstanceID(Object self)
{
    AtomicLong tmp=atomicMap.getOrDefault(self, null);
    if(tmp==null)
    {
        try
        {
        lock.lock();
        if(tmp==null)
        {
            tmp=new   AtomicLong(0);
            atomicMap.put(self, tmp);
        }
      
        }
        finally
        {
            lock.unlock();
        }
    }
    return tmp.getAndIncrement();
}

/**
 * 获取某实例对象的AtomicLong
 */
public static AtomicLong getInstanceLong(Object self)
{
    AtomicLong tmp=atomicMap.getOrDefault(self, null);
    if(tmp==null)
    {
        try
        {
        lock.lock();
        if(tmp==null)
        {
            tmp=new   AtomicLong(0);
            atomicMap.put(self, tmp);
        }
        }
        finally
        {
            lock.unlock();
        }
    }
    return tmp;
}

/*
 * 重置某对象的id值
 */
public static void  resetInstanceLong(Object self,long id)
{
    AtomicLong tmp=atomicMap.getOrDefault(self, null);
    if(tmp==null)
    {
        try
        {
        lock.lock();
        if(tmp==null)
        {
            tmp=new   AtomicLong(0);
            atomicMap.put(self, tmp);
        }
        }
        finally
        {
            lock.unlock();
        }
     
    }
   tmp.set(id);
}
/**
 * 返回JVM共享ID(还在研究，已有方案都不行）
 */

public static long getJVMID()
{
    long longid=0;
    if(jvmPath.isEmpty())
    {
        try
        {
            jvmLock.lock();
           Properties props=System.getProperties();//获取当前的系统属性
           String path=props.getProperty("java.home");
           if(path!=null&&!path.isEmpty())
           {
               jvmPath=path;
           }
           else
           {
               path=props.getProperty("java.library.path");
               if(path!=null&&!path.isEmpty())
               {
                   jvmPath=path;
               }
               else
               {
                   path=props.getProperty("ava.io.tmpdir");
                   if(path!=null&&!path.isEmpty())
                   {
                       jvmPath=path;
                   }
               }
           }
        }
        finally
        {
            jvmLock.unlock();
        }
    }
    //文件
    //文件锁所在文件
    boolean isCreate=false;
    File lockFile = new File(jvmPath,"jvmlock.dat");
    if(!lockFile.exists())
    {
        try {
            lockFile.createNewFile();
            isCreate=true;
        } catch (IOException e) {
          
            e.printStackTrace();
        }
        
    }
    FileOutputStream outStream = null;
    FileInputStream  intputStream= null;
    FileLock lock = null;

    try {
        try {
            outStream = new FileOutputStream(lockFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            intputStream=new FileInputStream(lockFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileChannel channel = outStream.getChannel();
        ByteBuffer buf=ByteBuffer.allocate(8);
        try {
            //方法一
            try {
                lock = channel.lock();
            } catch (IOException e) {
                
                e.printStackTrace();
            }
            if(isCreate)
            {
                buf.putLong(0);
                try {
                    channel.write(buf);
                } catch (IOException e) {
                
                    e.printStackTrace();
                }
            }
            //
            buf.clear();
            try {
                intputStream.getChannel().read(buf);
            } catch (IOException e) {
         
                e.printStackTrace();
            }
            longid=buf.getLong();
            //
            buf.clear();
            buf.putLong(longid+1);
            try {
                channel.write(buf);
            } catch (IOException e) {
         
                e.printStackTrace();
            }
        } 
        finally
        {
            try {
                intputStream.close();
          
                outStream.close();
            } catch (IOException e) {
          
                e.printStackTrace();
            }
        }
    }

    finally{
        if(null != lock){
            try {
                lock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(outStream != null){
            try {
                outStream.close();
            } catch (IOException e) {
    
                e.printStackTrace();
            }
        }
        if(intputStream != null){
            try {
                intputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return longid;
}
}
