/**    
 * �ļ�����ClientManager.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��8��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ClientManager    
 * ��������    �����Ͷ�
 * �����Ͷ���ʧ�ر�
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��8�� ����1:14:55    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��8�� ����1:14:55    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ClientManager {
    
    /**
     * ��������ռ�����
     */
    private static ReferenceQueue<judpClient> gcQueue=new ReferenceQueue<judpClient>();
   
    /**
     * ����session��Ϣ
     */
    private static ConcurrentHashMap<Long,Session> hashMap=new ConcurrentHashMap<Long,Session>();
   
    /**
     * ���棬��ʱ
     */
    private static CacheData<Long,Session> cache=new CacheData<Long,Session>(5000, 30, false,new CacheListener());
   
    /**
     * �����߳�����
     */
    private static  volatile boolean isStart=false;
    /**
     * �ռ���������
     */
    private static ConcurrentHashMap<Long,WeakClient<judpClient>> hashMapClient=new ConcurrentHashMap<Long,WeakClient<judpClient>>();
  
    /**
     * �̳߳�
     */
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /*
     * �����߳����ƽ��е���
     */
     private static AtomicLong threadName=new AtomicLong(0);
     
     /**
      * ���㻺���session����������
      * ���������
      */
     private static  AtomicLong sessionCacheData=new AtomicLong(0);
     
     /**
      * ����session��Ϣ
      * ����ɾ������
      * sessionid
      */
     private static  ConcurrentLinkedQueue<SessionData> preCache=new ConcurrentLinkedQueue<SessionData>();
     private static  boolean isStartCache=false;
     
     private static  boolean isStartFile=false;
    
     /**
      * �����ļ���
      */
     private static  MemoryManager<String,byte[]> cacheDB=null;
    
   // private static  MemoryPalDBManager palDB=null;
     private static  FileMemoryData<String,byte[]>  fileDB=null;
     /**
      * ����ļ�����
      */
     private static  ConcurrentLinkedQueue<String> removeCache=new ConcurrentLinkedQueue<String>();
     /**
      * M
      */
     private static int maxMemorySize=400;
     private static int varSize=50;
     private static  volatile  int waiData=0;
     /*
      * ��ӳ���
      * ��ȡseesion��������Ϣ
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
           //���������
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
      * ɾ��
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
      * ɾ������
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
              //�����
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
      * �����ڴ����
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
             //�����
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
                     //����50M��һֱ���
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
             System.out.println("sum��"+size);
             waiData=0;
             isStartCache=false;  
             System.gc();
            }
             
         });
     }
    
     /**
      * �����߳�����
      */
     public static String getThreadName()
     {
         return "clientSession_"+threadName.getAndIncrement();
     }
     
     /**
      * session�ж��Լ��ر�ʱʹ��
        
      */
    public static Session getSession(long id)
    {
        cache.remove(id);//session�����ˣ�����Ҳ��û������
      return     hashMap.remove(id);
    }
  
    /*
     * ��ӷ��Ͷ�session
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
   * ��ӷ��Ͷ�
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
  * session��Ӷ�����
  * ��ǰcacheʱ����Ʋ�׼
  * �о��У�ר�����ӽӿ�
  */
 public static void addCache(Session session)
 {
     cache.put(session.getID(), hashMap.get(session.getID()));
 }
  
 /**
  *������
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
        //�ͻ�����ʧ
          if(session!=null&&session.getClientNum()==0&&session.isLogicalClose())
          {
           //�����ʧʱ�Ѿ���ʱ����Ҫ�����ر�;
           session.close();
           hashMap.remove(id);
           Session ctmp= ClientSessionsPools.getSession(session.getLocalBindPort(), session.getSrcIP(), session.getSrcPort());
           if(ctmp.getID()==session.getID())
           {
               //˵�����ڷ��䣬����ɾ����
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
                    //˵��û������
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                       
                    }
                    continue;
                }
                judpClient tmpClient=tmp.get();
                if(tmpClient!=null)
                {
                    tmpClient.close();//���л�����ʧ�����߼��رգ��ɼ������ƹر�
                    long id=tmpClient.getSessionID();
                    Session session=hashMap.get(id);
                    removeSessionByKey(session,id);
                }
                else
                {
                    //ֱ�ӻ�ȡsession;
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
