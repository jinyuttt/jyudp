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
    
    private static ReferenceQueue<judpClient> gcQueue=new ReferenceQueue<judpClient>();
    private static ConcurrentHashMap<Long,Session> hashMap=new ConcurrentHashMap<Long,Session>();
    private static CacheData<Long,Session> cache=new CacheData<Long,Session>(20000, 30, false);
    private static  volatile boolean isStart=false;
    private static ConcurrentHashMap<Long,WeakClient<judpClient>> hashMapClient=new ConcurrentHashMap<Long,WeakClient<judpClient>>();
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
            CacheTimeListenter<Long,Session> listener=new CacheListener();
            cache.setListenter(listener);
            while(true)
            {
              
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
