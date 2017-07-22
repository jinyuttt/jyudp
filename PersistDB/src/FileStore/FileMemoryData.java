/**    
 * �ļ�����FileMemoryData.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��19��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileMemoryData    
 * ��������   ͨ���ļ�����
 * һ�������������ļ�������2����������
 * һ����hashmap,���ڴ����ṩ��ѯ֧��
 * һ�����ڴ����ݿ���У�֧�������޸Ĺ���
 * ������hashmap���Բ�Ҫ�ˣ�ֱ�Ӵ��ڴ���в�ѯ������û��׼ȷ�Ĳ��Ա���
 * �ڴ���ѯ��hashmap�죬���Һܶ�ʱ���ǲ���Ҫ�����ļ���С�ģ�����ͬʱ������
 * ��Ȼ�������Ϊ�Ż���һ����
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��19�� ����8:42:41    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��19�� ����8:42:41    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileMemoryData<K,V> {
    private long fileMax=1*1024*1024*1024;//1G�ֽ�
    private ConcurrentHashMap<K,V> cache=new  ConcurrentHashMap<K,V>();
     public  HashMap<K,FileIndex<K>> findex=new HashMap<K,FileIndex<K>>();
    private volatile long sum=0;//׼���洢��������
    private volatile byte valType=-1;//0��byte[] 1�Ƕ���
    private volatile byte typeLen=-1;
    private volatile boolean isRunning=false;//�ļ��洢
    private volatile boolean isDeleteRunning=false;//ɾ������
    String  dataDir="sessiondata";
    String  dataFile=System.currentTimeMillis()+".DB";
    private final long  timeLen=10*60*1000;//����
    public  volatile boolean isModifyFile=false;//�Ƿ�����ļ���С
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
   
    public void setDir(String dir)
    {
        dataDir=dir;
        FileModifyDBManager.dataDir=dir;
    }
    @SuppressWarnings("unchecked")
   
   /*
    * ��ȡ
    */
    public V get(K key)
    {
      
     V val=cache.get(key);
     if(val==null)
     {
         //������ȡ�ļ�
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
                 //�����л�
                 return  (V) ObjectByte.readStream(data);
             }
         }
     }
    return val;
        
    }
   
    
    /*
     * ���ݼ���
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
     * ɾ�� key
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
     * �������
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
     * �������ݴ洢
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
                //����;����50M
                return ;
            }
        }
        else if(valType==1)
        {
            if(sum<100)
            {
                //100������
                return ;
            }
        }
     
        isRunning=true;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                sum=0;//�������ûأ�
                Thread.currentThread().setName("FileStore");
             //�ж��ļ���С��
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
                //������
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
                      //���л�
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
                         
                         //˵�����޸�
                         indexmem.flage=1;
                         FileModifyDBManager.addDataDeleteIndex(indexmem);
                         //��¼������
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
              //����ڴ�����
              for(int i=0;i<list.size();i++)
              {
                  cache.remove(list.get(i));
              }
               list.clear();
               isRunning=false;
                System.gc();
                //�ж��Ƿ�ɾ����
                File dir=new File(dataDir);
                if(dir.exists())
                {
                    FilenameFilter filter=new DirFilter(".DB");
                    File[] all=  dir.listFiles(filter);
                    if(all.length>10)
                    {
                        //����10���ļ�;10G
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
     * �������
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
     * �����߳�ɾ��
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
     * ɾ����ʱ�ļ�
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
                              all[i].delete();//ɾ��
                             //ͬʱɾ����ȡ
                             //ͬʱɾ��fileIndex
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
            //ɾ��
            if(!list.isEmpty())
            {
                //ɾ���ļ�����
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
