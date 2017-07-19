/**    
 * �ļ�����MemoryPalDBManager.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��17��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�MemoryPalDBManager    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��17�� ����9:42:02    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��17�� ����9:42:02    
 * �޸ı�ע��    
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
     * �����ȡ
     */
    private ConcurrentHashMap<String,byte[]> cache=new  ConcurrentHashMap<String,byte[]>();
   
    /*
     * session id
     * fileid
     */
    private  ConcurrentHashMap<Long,Long> fileIndex=new  ConcurrentHashMap<Long,Long>();
    
    /*
     * ��������
     */
    private  ConcurrentLinkedQueue<PalDBCache> queue=new  ConcurrentLinkedQueue<PalDBCache>();
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private volatile short num=0;//����
    private volatile boolean isRuning=false;//�ж�����
    private final long  timeLen=10*60*1000;//����
    private String dataDir="sessiondata";
    private  int  removeFileNum=0;
    private final int maxClear=1000;//ÿд��1000�Σ�����һ��
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
 * ��ȡ����
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
             return null;//�Ѿ�û��������
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
 * д������
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
        cache.remove(cachedata.key);//ͬ������
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
                        all[i].delete();//ɾ��
                         //ͬʱɾ����ȡ
                         //ͬʱɾ��fileIndex
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
        //ɾ��
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
