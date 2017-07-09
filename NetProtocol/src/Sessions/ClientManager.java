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

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import BufferData.WeakClient;
import DataBus.CacheData;
import DataBus.CacheListener;
import DataBus.CacheTimeListenter;
import NetProtocol.judpClient;

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
    
    private static ReferenceQueue<judpClient> gcQueue=new ReferenceQueue<judpClient>();
    private static ConcurrentHashMap<Long,Session> hashMap=new ConcurrentHashMap<Long,Session>();
    private static CacheData<Long,Session> cache=new CacheData<Long,Session>(20000, 30, false);
    private static  volatile boolean isStart=false;
    private static ConcurrentHashMap<Long,WeakClient<judpClient>> hashMapClient=new ConcurrentHashMap<Long,WeakClient<judpClient>>();
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /*
     * 控制线程名称进行调整
     */
     private static AtomicLong threadName=new AtomicLong(0);
     
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
            CacheTimeListenter<Long,Session> listener=new CacheListener();
            cache.setListenter(listener);
            while(true)
            {
              
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
