/**    
 * �ļ�����ServerManager.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��8��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Sessions;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import NetProtocol.judpServer;


/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ServerManager    
 * ��������    ���ն˹���
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��8�� ����5:37:06    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��8�� ����5:37:06    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ServerManager {
    /*
     * ÿ���˿ڶ�Ӧ��ÿ��ͨѶ
     */
    private static ConcurrentHashMap<Integer,judpServer> map=new ConcurrentHashMap<Integer,judpServer>();
    /*
     * ��ɾ��session��key
     */
    private static ArrayList<String> handQueue=new  ArrayList<String>();
    
    /*
     * ��ɾ����sesssion
     */
    private static ConcurrentHashMap<String, ArrayList<Long>> mapSession=new ConcurrentHashMap<String,ArrayList<Long>>();

    /*
     * �����߳����ƽ��е���
     */
     private static AtomicLong threadName=new AtomicLong(0);
     
     /**
      * �����߳�����
      */
     public static String getThreadName()
     {
         return "serverSession_"+threadName.getAndIncrement();
     }
    /**
     * ȫ�ֱ���judpServer
     */
    public static void addSocket(int port,judpServer socket)
{
    map.put(port, socket);
}
    /**
     * ��ȡ��Ӧ�ķ����judpServer
     */
public static judpServer getSocket(int port)
{
   return  map.get(port);
}

/**
 * �Ƴ�judpServer
 */
public static void remove(int port)
{
    map.remove(port);
}

/**
 * ����ɾ����session��key
 */
public static void addSessionMap(String key)
{
    handQueue.add(key);
}

/*
 * ��ȡ׼��ɾ��session��key
 */
public static  String[] getSessionMap()
{
    if(handQueue.isEmpty())
    {
        return null;
    }
    else
    {
        int size=handQueue.size();
        String[] keys=new String[size];
        for(int i=0;i<size;i++)
        {
            keys[i]=handQueue.get(i);
        }
        removeKeys(keys);
        return keys;
        
    }
}

/*
 * 
 */
private static void removeKeys(String[] keys)
{
    Thread remove=new Thread(new Runnable() {

        @Override
        public void run() {
         
            for(int i=0;i<keys.length;i++)
            {
                handQueue.remove(keys[i]);
            }
            
        }
        
    });
    remove.setDaemon(true);
    remove.setName("removeSessionMap");
    remove.start();
}

/*
 * �Ƴ�key
 * 
 */
public static void removeKey(String key)
{
    handQueue.remove(key);
}

/**
 * ����׼���Ƴ���key
 * 
 */
public static void addSession(String key,long id)
{
      ArrayList<Long> s=  mapSession.get(key);
      if(s==null)
      {
          s=new ArrayList<Long>();
          mapSession.put(key, s);
      }
      else
      {
          s.add(id);
      }
}

/*
 * ��ȡҪ�Ƴ���sessionid
 */
public static long[] getSessions(String key)
{
    ArrayList<Long> s=  mapSession.get(key);
    if(s==null)
    {
        return null;
    }
    else
    {
        int num=s.size();
        long[] keys=new long[num];
        for(int i=0;i<num;i++)
        {
            keys[i]=s.get(i);
        }
        removeSession(key,keys);
    }
    return null;
    
}
private static void removeSession(String key,long[] keys)
{
    Thread remove=new Thread(new Runnable() {

        @Override
        public void run() {
            ArrayList<Long> s=  mapSession.get(key);
            for(int i=0;i<keys.length;i++)
            {
                s.remove(keys[i]);
            }
            
        }
        
    });
    remove.setDaemon(true);
    remove.setName("removeSession");
    remove.start();
}
}