/**    
 * 文件名：FileMemoryData.java    
 *    
 * 版本信息：    
 * 日期：2017年7月19日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileStore;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Tools.DirFilter;
import Tools.ObjectByte;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：FileMemoryData    
 * 类描述：   通过文件保存
 * 一旦设置了整理文件，则有2份索引数据
 * 一份是hashmap,在内存中提供查询支持
 * 一份是内存数据库表中，支持最新修改工作
 * 理论上hashmap可以不要了，直接从内存表中查询，但是没有准确的测试表明
 * 内存表查询有hashmap快，而且很多时候是不需要整理文件大小的，所以同时保留了
 * 当然这可以作为优化的一部分
 * 创建人：jinyu    
 * 创建时间：2017年7月19日 下午8:42:41    
 * 修改人：jinyu    
 * 修改时间：2017年7月19日 下午8:42:41    
 * 修改备注：    
 * @version     
 *     
 */
public class FileMemoryData<K,V> {
    private long fileMax=1*1024*1024*1024;//1G字节
    private ConcurrentHashMap<K,V> cache=new  ConcurrentHashMap<K,V>();
     public  HashMap<K,FileIndex<K>> findex=new HashMap<K,FileIndex<K>>();
    private volatile long sum=0;//准备存储的数据量
    private volatile byte valType=-1;//0是byte[] 1是对象
    private volatile byte typeLen=-1;
    private volatile boolean isRunning=false;//文件存储
    private volatile boolean isDeleteRunning=false;//删除启动
    String  dataDir="sessiondata";
    String  dataFile=System.currentTimeMillis()+".DB";
    private final long  timeLen=10*60*1000;//毫秒
    public  volatile boolean isModifyFile=false;//是否更新文件大小
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
   
    public void setDir(String dir)
    {
        dataDir=dir;
        FileModifyDBManager.dataDir=dir;
    }
    @SuppressWarnings("unchecked")
   
   /*
    * 获取
    */
    public V get(K key)
    {
      
     V val=cache.get(key);
     if(val==null)
     {
         //继续读取文件
         FileIndex<K> f= findex.get(key);
         FileRead  reader=new FileRead();
         reader.path=f.fileid;
         byte[]data= reader.read(f.position, f.len);
         if(data==null)
         {
             return null;
         }
         else
         {
             if(val instanceof byte[])
             {
                 return (V) data;
             }
             else
             {
                 //反序列化
                 return  (V) ObjectByte.readStream(data);
             }
         }
     }
    return val;
        
    }
   
    
    /*
     * 数据加入
     */
    public void put(K key,V val)
    {
        
        cache.put(key, val);
        if(valType==-1)
        {
            if(val instanceof byte[])
            {
                valType=0;
            }
            else
                {check(val);}
            if(valType==-1)
            {
                valType=1;
            }
        }
        if(valType==0)
        {
          byte[] data=(byte[]) val ;
            sum+=data.length;
        }
        else if(valType==1)
        {
            sum+=1;
        }
        else if(valType==2)
        {
            sum+=typeLen;
        }
        startThreadStore();
    }
    
    /*
     * 删除 key
     */
    public void delete(K key)
    {
        cache.remove(key);
        FileIndex<K> f= findex.get(key);
        if(f!=null)
        {
            if(isModifyFile)
            {
                DataDeleteIndex indexmem=new DataDeleteIndex();
                indexmem.fileid=f.fileid;
                indexmem.flage=2;
                indexmem.len=f.len;
                indexmem.position=f.position;
                FileModifyDBManager.addDataDeleteIndex(indexmem);
            }
        }
    }
   
    /*
     * 清除所有
     */
    public void clear()
    {
        cache.clear();
        findex.clear();
        File dir=new File(dataDir);
        dir.delete();
        dir.mkdir();
    }
    
    /*
     * 启动数据存储
     */
    private void startThreadStore()
    {
        if(isRunning)
        {
            return;
        }
        //
        if(valType==0||valType==2)
        {
            if(sum<10*1024*1024)
            {
                //启动;大于50M
                return ;
            }
        }
        else if(valType==1)
        {
            if(sum<100)
            {
                //100个对象
                return ;
            }
        }
     
        isRunning=true;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                sum=0;//启动后置回；
                Thread.currentThread().setName("FileStore");
             //判断文件大小；
                File f=new File(dataDir+"/"+dataFile);
                if(f.length()>=fileMax)
                {
                    dataFile=System.currentTimeMillis()+".DB";
                }
                //
                if(FileModifyDBManager.fileName.equalsIgnoreCase(dataFile))
                {
                   try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                 
                    e.printStackTrace();
                }
                    isRunning=false;
                    return;
                }
                FileWrite  fw=new FileWrite();
                fw.path=dataDir+"/"+dataFile;
                long position=fw.getFile();
                //遍历：
                 ArrayList<K>  list=new ArrayList<K>();
                 for (Entry<K, V> entry: cache.entrySet()) {
                     //
                     if(FileModifyDBManager.fileName.equalsIgnoreCase(dataFile))
                     {
                        try {
                         TimeUnit.SECONDS.sleep(1);
                     } catch (InterruptedException e) {
                      
                         e.printStackTrace();
                     }
                        break;
                     }
                     
                  K key = entry.getKey();
                  V value = entry.getValue();
                  byte[] data=null;
                  if(valType==0)
                   {
                      data=(byte[]) value;
                   }
                  else if(valType==1)
                  {
                      //序列化
                     data=ObjectByte.writeObject(value);
                     
                  }
                  else if(valType==2)
                  {
                      //
                      data=new byte[typeLen];
                      ByteBuffer buf=ByteBuffer.wrap(data);
                      switch(typeLen)
                      {
                      case 1:
                          buf.put((byte) value);
                          break;
                      case 2:
                          buf.putShort((short) value);
                          break;
                      case 4:
                          buf.putInt((int) value);
                          break;
                      case 8:
                          buf.putLong((long) value);
                          break;
                          default:
                              buf.putLong((long) value);
                              break;   
                      }
                      buf.flip();
                  }
                  //
                  fw.writeFile(data);
                  //
                  FileIndex<K> index=new FileIndex<K>();
                  index.fileid=dataFile;
                 
                  index.key=key;
                  index.len=data.length;
                  index.position=position;
                  position+=index.len;
                  if(isModifyFile)
                  {
                      //FileModifyManager.hashindex= (HashMap<String, FileIndex<String>>)findex;
                      DataDeleteIndex indexmem=new DataDeleteIndex();
                      indexmem.fileid=dataFile;
                      indexmem.flage=0;
                      indexmem.len=index.len;
                      indexmem.position=position;
                     if(findex.containsKey(key))
                     {
                         
                         //说明是修改
                         indexmem.flage=1;
                         FileModifyDBManager.addDataDeleteIndex(indexmem);
                         //记录旧数据
                         //
                         FileIndex<K> oldtmp=   findex.get(key);
                          indexmem=new DataDeleteIndex();
                         indexmem.fileid=oldtmp.fileid;
                         indexmem.flage=2;
                         indexmem.len=oldtmp.len;
                         indexmem.position=oldtmp.position;
                         FileModifyDBManager.addFile(indexmem);
                     }
                     else
                     {
                        
                         FileModifyDBManager.addDataDeleteIndex(indexmem);
                     }
                  }
                  findex.put(key, index);
                  list.add(key);
                }
              //清除内存数据
              for(int i=0;i<list.size();i++)
              {
                  cache.remove(list.get(i));
              }
               list.clear();
               isRunning=false;
                System.gc();
                //判断是否删除；
                File dir=new File(dataDir);
                if(dir.exists())
                {
                    FilenameFilter filter=new DirFilter(".DB");
                    File[] all=  dir.listFiles(filter);
                    if(all.length>10)
                    {
                        //超过10个文件;10G
                         startThreadDelete();
                    }
                }
                //
                FileObject objsw=new FileObject();
                objsw.path=dataDir+"/"+"data.index";
                objsw.writeObject(findex);
            }
            
        });
    }
    
    /*
     * 检查类型
     */
    private void check(V val)
    {
        if(val instanceof Long)
        {
            valType=2;
            typeLen=8;
        }
        else if(val instanceof Integer)
        {
            valType=2;
            typeLen=4;
        }
        else   if(val instanceof Short)
        {
            valType=2;
            typeLen=2;
        }
        else   if(val instanceof Byte)
        {
            valType=2;
            typeLen=1;
        }
    }
    
    /*
     * 开启线程删除
     */
    private void startThreadDelete()
    {
        if(isDeleteRunning)
        {
            return;
        }
        isDeleteRunning=true;
        cachedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
              Thread.currentThread().setName("fileDelte");
              fileDelete();
              isDeleteRunning=false;
            }
        
        });
    }
    /*
     * 删除过时文件
     */
    private void  fileDelete()
    {
        File dir=new File(dataDir);
        if(dir.exists())
        {
            FilenameFilter filter=new DirFilter(".DB");
            File[] all=  dir.listFiles(filter);
            ArrayList<String> list=new ArrayList<String>();
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
                            if(System.currentTimeMillis()-all[i].lastModified()>timeLen)
                            {
                              all[i].delete();//删除
                             //同时删除读取
                             //同时删除fileIndex
                              list.add(name);
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
                //删除文件索引
                Iterator<Entry<K, FileIndex<K>>> entries = findex.entrySet().iterator();  
                while (entries.hasNext()) {  
                    Entry<K, FileIndex<K>> entry = entries.next();  
                    if(entry.getValue().fileid!=null)
                    {
                        if(list.contains(entry.getValue().fileid))
                          {
                              entries.remove();
                              if(isModifyFile)
                              {
                                  FileIndex<K> tmp=entry.getValue();
                              
                                  DataDeleteIndex indexmem=new DataDeleteIndex();
                                  indexmem.fileid=tmp.fileid;
                                  indexmem.flage=2;
                                  indexmem.len=tmp.len;
                                  indexmem.position=tmp.position;
                                  FileModifyDBManager.addDataDeleteIndex(indexmem);
                              }
                          }
                    }
                }  
                FileObject objsw=new FileObject();
                objsw.path=dataDir+"/"+"data.index";
                objsw.writeObject(findex);
            
        }
    }
    }
}
