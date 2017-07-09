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
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ClientManager    
 * ��������    �������Ͷ�
 * �������Ͷ���ʧ�ر�
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��8�� ����1:14:55    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��8�� ����1:14:55    
 * �޸ı�ע��    
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
     * �����߳����ƽ��е���
     */
     private static AtomicLong threadName=new AtomicLong(0);
     
     /**
      * �����߳�����
      */
     public static String getThreadName()
     {
         return "clientSession_"+threadName.getAndIncrement();
     }
    public static Session getSession(long id)
    {
        cache.remove(id);//session�����ˣ�����Ҳ��û������
      return     hashMap.remove(id);
    }
    /*
     * ���ӷ��Ͷ�session
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
   * ���ӷ��Ͷ�
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
                    //˵��û������
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                       
                    }
                    continue;
                }
                tmp.get().close();//���л�����ʧ�����߼��رգ��ɼ������ƹر�
                long id=tmp.get().getSessionID();
                if(tmp.get().isOutTime())
                {
                    //�����ʧʱ�Ѿ���ʱ����Ҫ�����رգ�
                    Session session= hashMap.get(id);
                    session.close();
                    hashMap.remove(id);
                    
                }
               
              
            }
            
        }
        
    });
   
}
}