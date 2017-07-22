/**    
 * 文件名：ClientManager.java    
 *    
 * 版本信息：    
 * 日期：2017年7月8日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import BufferData.SessionData;
import BufferData.WeakClient;
import DataBus.CacheData;
import DataBus.CacheListener;
import FileStore.FileMemoryData;
import FileStore.FileModifyDBManager;
import NetProtocol.judpClient;
import NetProtocol.judpSocket;
import StoreDisk.MemoryManager;
import Tools.PathTool;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：ClientManager    
 * 类描述：    管理发送端
 * 管理发送端消失关闭
 * 创建人：jinyu    
 * 创建时间：2017年7月8日 下午1:14:55    
 * 修改人：jinyu    
 * 修改时间：2017年7月8日 下午1:14:55    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientManager {
    
    /**
     * 保存对象，收集回收
     */
    private static ReferenceQueue<judpClient> gcQueue=new ReferenceQueue<judpClient>();
   
    /**
     * 保存session信息
     */
    private static ConcurrentHashMap<Long,Session> hashMap=new ConcurrentHashMap<Long,Session>();
   
    /**
     * 缓存，计时
     */
    private static CacheData<Long,Session> cache=new CacheData<Long,Session>(5000, 30, false,new CacheListener());
   
    /**
     * 控制线程启动
     */
    private static  volatile boolean isStart=false;
    /**
     * 收集回收数据
     */
    private static ConcurrentHashMap<Long,WeakClient<judpClient>> hashMapClient=new ConcurrentHashMap<Long,WeakClient<judpClient>>();
  
    /**
     * 线程池
     */
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /*
     * 控制线程名称进行调整
     */
     private static AtomicLong threadName=new AtomicLong(0);
     
     /**
      * 计算缓存的session发送数据量
      * 超量就清除
      */
     private static  AtomicLong sessionCacheData=new AtomicLong(0);
     
     /**
      * 保存session信息
      * 用于删除数据
      * sessionid
      */
     private static  ConcurrentLinkedQueue<SessionData> preCache=new ConcurrentLinkedQueue<SessionData>();
     private static  boolean isStartCache=false;
     
     private static  boolean isStartFile=false;
    
     /**
      * 缓存文件中
      */
     private static  MemoryManager<String,byte[]> cacheDB=null;
    
   // private static  MemoryPalDBManager palDB=null;
     private static  FileMemoryData<String,byte[]>  fileDB=null;
     /**
      * 清除文件数据
      */
     private static  ConcurrentLinkedQueue<String> removeCache=new ConcurrentLinkedQueue<String>();
     /**
      * M
      */
     private static int maxMemorySize=400;
     private static int varSize=50;
     private static  volatile  int waiData=0;
     /*
      * 添加长度
      * 获取seesion的数据信息
      */
     public static void addSessionData(long sessionid,long packagetid,long len)
     {
        long num=  sessionCacheData.addAndGet(len+16);
        SessionData data=new SessionData();
        data.len=(int) len;
        data.sessionid=sessionid;
        data.packagetid=packagetid;
        preCache.add(data);
       if(num>maxMemorySize*1024*1024)
       {
           //启动清除；
           startThread();
           stratFileRemove();
          while(waiData>0)
          {
             try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
          }
       }
     }
     
     /*
      * 删除
      */
     public static void addRemoveFile(long sessionid,long packagetid)
     {
         
         removeCache.offer(sessionid+":"+packagetid);
     }
    
     public static byte[] getDBData(long sessionid,long packagetid)
     {
       // return  cacheDB.get(sessionid+":"+packagetid);
        // return palDB.get(sessionid, sessionid+":"+packagetid);
         return fileDB.get(sessionid+":"+packagetid);
     }
     
     /*
      * 删除数据
      */
     private static  void stratFileRemove()
     {
         if(isStartFile)
         {
             return;
         }
         isStartFile=true;
         cachedThreadPool.execute(new Runnable() {

             @Override
             public void run() {
                 Thread.currentThread().setName("fileRemove");
              //先清除
                 long sum=0;
                 try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
              while(sum>varSize)
              {
               String key=  removeCache.poll();
               if(key==null)
               {
                   try {
                       TimeUnit.SECONDS.sleep(3);
                   } catch (InterruptedException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                   break;
               }
               if(cacheDB!=null)
               cacheDB.deleteByKey(key);
              }
              isStartFile=false;  
             }
              
          });
     }
    
     
     /*
      * 启动内存清除
      */
     private static void startThread()
     {
         //
         if(isStartCache)
         {
             return;
         }
         isStartCache=true;
//         if(cacheDB==null)
//         {
//             MemoryManager.fileDB=PathTool.getDirPath(new judpSocket())+"/clientDB.db";
//             cacheDB=new  MemoryManager<String,byte[]>();
//         }
//         if(palDB==null)
//         {
//             palDB=new MemoryPalDBManager();
//             String dir=PathTool.getDirPath(new judpSocket())+"/sessiondata";
//             File file=new File(dir);
//             if(file.exists())
//             {
//                 file.delete();
//             }
//             file.mkdir();
//             palDB.setDir(dir);
//         }
         if(fileDB==null)
         {
             fileDB=new FileMemoryData<String, byte[]>();
            // if(fileDB.isModifyFile)
            // {
                 FileModifyDBManager.hashindex=fileDB.findex;
            // }
             String dir=PathTool.getDirPath(new judpSocket())+"/sessiondata";
             File file=new File(dir);
              if(file.exists())
                 {
                     file.delete();
                }
                 file.mkdir();
                 fileDB.setDir(dir);
         }
         cachedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("memoryRemove");
             //先清除
               long sum=0;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //
                boolean isDBAll=false;
                if(sessionCacheData.get()>(maxMemorySize+50)*1024*1024)
                {
                    waiData++;
                    isDBAll=true;
                }
                
             while(sum<varSize*1024*1024||isDBAll)
             {
                 if(sessionCacheData.get()<(maxMemorySize+50)*1000)
                 {
                     //大于50M就一直清除
                     isDBAll=false;
                 }
               SessionData tmp=  preCache.poll();
               if(tmp==null)
               {
                   try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                   break;
               }
               
               Session s= hashMap.get(tmp.sessionid);
               if(s!=null)
               {
                    byte[] data=s.removeData(tmp.packagetid);
                    sessionCacheData.getAndAdd(-tmp.len-16);
                    if(data!=null)
                    {
                        sum+=tmp.len;
                       // cacheDB.put(tmp.sessionid+":"+tmp.packagetid, data);
                       // palDB.put(tmp.sessionid, tmp.sessionid+":"+tmp.packagetid, data);
                        
                        fileDB.put(tmp.sessionid+":"+tmp.packagetid, data);
                    }
               }
             }
             long size=sessionCacheData.get();
             System.out.println("sum："+size);
             waiData=0;
             isStartCache=false;  
             System.gc();
            }
             
         });
     }
    
     /**
      * 控制线程名称
      */
     public static String getThreadName()
     {
         return "clientSession_"+threadName.getAndIncrement();
     }
     
     /**
      * session判断自己关闭时使用
        
      */
    public static Session getSession(long id)
    {
        cache.remove(id);//session不用了，缓存也就没有用了
      return     hashMap.remove(id);
    }
  
    /*
     * 添加发送端session
     */
   public static boolean addSession(long id,Session session)
   {
       
       if(hashMap.containsKey(id))
       {
           return false;
       }
       else
       {
           hashMap.put(id, session);
           return true;
       }
   }

  /**
   * 添加发送端
   */
 public  static  void addClient(judpClient client)
{
     WeakClient<judpClient> tmp=new WeakClient<judpClient>(client.getSessionID(),client,gcQueue);
     tmp.okey=client.getID();
     cache.put(client.getSessionID(), hashMap.get(client.getSessionID()));
     hashMapClient.put(client.getID(), tmp);
    //
    check();
    
}
 
 /*
  * session添加都缓存
  * 当前cache时间控制不准
  * 研究中，专门增加接口
  */
 public static void addCache(Session session)
 {
     cache.put(session.getID(), hashMap.get(session.getID()));
 }
  
 /**
  *检查回收
  */
  private static void check()
{
    if(isStart)
    {
        return;
    }
    isStart=true;
    cachedThreadPool.execute(new Runnable() {
 private  void removeSessionByKey(Session session,long id)
      {
        //客户端消失
          if(session!=null&&session.getClientNum()==0&&session.isLogicalClose())
          {
           //如果消失时已经超时，则要真正关闭;
           session.close();
           hashMap.remove(id);
           Session ctmp= ClientSessionsPools.getSession(session.getLocalBindPort(), session.getSrcIP(), session.getSrcPort());
           if(ctmp.getID()==session.getID())
           {
               //说明正在分配，必须删除；
               String key="";
               if(session.getLocalBindPort()==0)
               {
                   key=session.getSrcIP()+session.getSrcPort();
               }
               else
               {
                   key=session.getLocalBindPort()+session.getSrcIP()+session.getSrcPort();
               }
               ClientSessionsPools.removeKey(key);
           }
          }
      }
        @Override
        public void run() {
           // CacheTimeListenter<Long,Session> listener=new CacheListener();
          //  cache.setListenter(listener);
            while(true)
            {
              
                @SuppressWarnings("unchecked")
                WeakClient<judpClient> tmp= (WeakClient<judpClient>) gcQueue.poll();
                if(tmp==null)
                {
                    //说明没有数据
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                       
                    }
                    continue;
                }
                judpClient tmpClient=tmp.get();
                if(tmpClient!=null)
                {
                    tmpClient.close();//先有回收消失，则逻辑关闭，由监听控制关闭
                    long id=tmpClient.getSessionID();
                    Session session=hashMap.get(id);
                    removeSessionByKey(session,id);
                }
                else
                {
                    //直接获取session;
                    long id=tmp.key;
                    Session session=hashMap.get(id);
                    removeSessionByKey(session,id);
                    
                }
                hashMapClient.remove(tmp.okey);
              
            }
            
        }
        
    });
   
}
}
