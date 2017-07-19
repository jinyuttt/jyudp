/**    
 * 文件名：MemoryPalDBManager.java    
 *    
 * 版本信息：    
 * 日期：2017年7月17日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package StoreDisk;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.linkedin.paldb.api.PalDB;
import com.linkedin.paldb.api.StoreReader;
import com.linkedin.paldb.api.StoreWriter;

import Tools.DirFilter;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：MemoryPalDBManager    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月17日 下午9:42:02    
 * 修改人：jinyu    
 * 修改时间：2017年7月17日 下午9:42:02    
 * 修改备注：    
 * @version     
 *     
 */
public class MemoryPalDBManager {
 
    /*
     * session id
     * 
     */
    private HashMap<Long,StoreReader> reader=new  HashMap<Long,StoreReader>();
    /**
     * sessionid
     */
    private  HashMap<Long,StoreWriter> writerMap=new  HashMap<Long,StoreWriter>();
    
    /*
     * 缓存读取
     */
    private ConcurrentHashMap<String,byte[]> cache=new  ConcurrentHashMap<String,byte[]>();
   
    /*
     * session id
     * fileid
     */
    private  ConcurrentHashMap<Long,Long> fileIndex=new  ConcurrentHashMap<Long,Long>();
    
    /*
     * 缓存数据
     */
    private  ConcurrentLinkedQueue<PalDBCache> queue=new  ConcurrentLinkedQueue<PalDBCache>();
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private volatile short num=0;//计数
    private volatile boolean isRuning=false;//判断运行
    private final long  timeLen=10*60*1000;//毫秒
    private String dataDir="sessiondata";
    private  int  removeFileNum=0;
    private final int maxClear=1000;//每写入1000次，清理一次
public MemoryPalDBManager()
{
    
}
public void setDir(String dir)
{
    
}
public void put(long sessionid,String key, byte[]data)
{
    PalDBCache cachedata=new PalDBCache();
    cachedata.data=data;
    cachedata.sessionid=sessionid;
    cachedata.key=key;
    queue.add(cachedata);
    cache.put(key, data);
    num++;
    if(num>100)
    {
   
        num=0;
      
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                writeData();
            }
            
        });
        if(removeFileNum>maxClear)
        {
            removeFileNum=0;
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    clearFiles();
                }
                
            });
        }
    }
        
}

/*
 * 获取数据
 */
public byte[] get(long sessionid,String key)
{
  byte[]data=  cache.get(key);
  if(data!=null)
  {
      return data;
  }
  else
  {
      StoreReader rd= reader.get(sessionid);
      if(rd!=null)
      {
        return  rd.get(key);
      }
      else
      {
         Long id= fileIndex.get(sessionid);
         if(id==null)
         {
             return null;//已经没有数据了
         }
         else
         {
             StoreReader srd=PalDB.createReader(new File(dataDir+"/"+id+".DB"));
             reader.put(sessionid, srd);
           return  srd.get(key);
         }
      }
          
  }
}


/*
 * 写入数据
 */
private void writeData()
{ 
    if(isRuning)
    {
        return;
    }
    removeFileNum++;
    isRuning=true;
    StoreWriter writer=null;
    while(!queue.isEmpty())
    {
        PalDBCache cachedata= queue.poll();
        if(cachedata!=null)
        {
           Long id= fileIndex.get(cachedata.sessionid);
           if(id==null)
           {
               long fileid=System.currentTimeMillis();
                writer = PalDB.createWriter(new File(dataDir+"/"+fileid+".DB"));
                fileIndex.put(cachedata.sessionid, fileid);
           }
           else
           {
               writer=writerMap.get(cachedata.sessionid);
               if(writer==null)
               {
                   writer = PalDB.createWriter(new File(dataDir+"/"+id+".DB"));
                   writerMap.put(cachedata.sessionid, writer);
               }
           }
        }
        writer.put(cachedata.key, cachedata.data);
        cache.remove(cachedata.key);//同步控制
    }
    isRuning=false;
    for (Entry<Long, StoreWriter> entry : writerMap.entrySet()) {
        try
        {
        entry.getValue().close();
        }
        finally
        {
            
        }
       }  
    writerMap.clear();
}

private void clearFiles()
{
    File dir=new File(dataDir);
    if(dir.exists())
    {
        FilenameFilter filter=new DirFilter(".DB");
        File[] all=  dir.listFiles(filter);
        ArrayList<Long> list=new ArrayList<Long>();
        for(int i=0;i<all.length;i++)
        {
           String name= all[i].getName();
           String time=name.substring(0, name.length()-3);
           if(time!=null)
           {
               try
               {
                 long id=Long.valueOf(time);
                 if(id!=0)
                 {
                    if(System.currentTimeMillis()-id>timeLen)
                    {
                        all[i].delete();//删除
                         //同时删除读取
                         //同时删除fileIndex
                        for(Entry<Long, Long> entry: fileIndex.entrySet()) {  
                            if(entry.getValue()==id)
                            {
                                list.add(entry.getKey());
                            }
                       }  
                    }
                 }
               
               }
               finally
               {
                   
               }
               
           }
        }
        //删除
        if(!list.isEmpty())
        {
            for(int i=0;i<list.size();i++)
            {
                fileIndex.remove(list.get(i));
            }
        }
    }
}

}
