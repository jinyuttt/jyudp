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
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
    
    //private static WeakHashMap<judpClient,String> map=new WeakHashMap<judpClient,String>();
    //private static HashMap<WeakReference<judpClient>,String> objmap=new HashMap<WeakReference<judpClient>,String>();
    private static ReferenceQueue<judpClient> gcQueue=new ReferenceQueue<judpClient>();
    private static ConcurrentHashMap<Long,Session> hashMap=new ConcurrentHashMap<Long,Session>();
    private static CacheData<Long,WeakReference<judpClient>> cache=new CacheData<Long,WeakReference<judpClient>>(20000, 6000, false);
    private static  volatile boolean isStart=false;
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
    WeakReference<judpClient> tmp=new  WeakReference<judpClient>(client,gcQueue);
    //map.put(client, "");
   // objmap.put(tmp, "");
    cache.put(client.getSessionID(), tmp);
    //
    check();
    
    
}
private static void check()
{
    if(isStart)
    {
        return;
    }
    isStart=true;
    cachedThreadPool.execute(new Runnable() {

        @Override
        public void run() {
            CacheTimeListenter<Long, WeakReference<judpClient>> listener=new CacheListener();
            cache.setListenter(listener);
            while(true)
            {
              
                WeakReference<judpClient> tmp= (WeakReference<judpClient>) gcQueue.poll();
                if(tmp==null)
                {
                    //说明没有数据
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                       
                    }
                    continue;
                }
                tmp.get().close();//先有回收消失，则逻辑关闭，由监听控制关闭
                long id=tmp.get().getSessionID();
                if(tmp.get().isOutTime())
                {
                    //如果消失时已经超时，则要真正关闭；
                    Session session= hashMap.get(id);
                    session.close();
                    hashMap.remove(id);
                    
                }
               
              
            }
            
        }
        
    });
   
}
}
