/**    
 * 文件名：ServerManager.java    
 *    
 * 版本信息：    
 * 日期：2017年7月8日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import NetProtocol.judpServer;


/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：ServerManager    
 * 类描述：    接收端管理
 * 创建人：jinyu    
 * 创建时间：2017年7月8日 下午5:37:06    
 * 修改人：jinyu    
 * 修改时间：2017年7月8日 下午5:37:06    
 * 修改备注：    
 * @version     
 *     
 */
public class ServerManager {
    /*
     * 每个端口对应的每次通讯
     */
    private static ConcurrentHashMap<Integer,judpServer> map=new ConcurrentHashMap<Integer,judpServer>();
    /*
     * 有删除session的key
     */
    private static ArrayList<String> handQueue=new  ArrayList<String>();
    
    /*
     * 待删除的sesssion
     */
    private static ConcurrentHashMap<String, ArrayList<Long>> mapSession=new ConcurrentHashMap<String,ArrayList<Long>>();

    /*
     * 控制线程名称进行调整
     */
     private static AtomicLong threadName=new AtomicLong(0);
     
     /**
      * 控制线程名称
      */
     public static String getThreadName()
     {
         return "serverSession_"+threadName.getAndIncrement();
     }
    /**
     * 全局保存judpServer
     */
    public static void addSocket(int port,judpServer socket)
{
    map.put(port, socket);
}
    /**
     * 获取对应的服务端judpServer
     */
public static judpServer getSocket(int port)
{
   return  map.get(port);
}

/**
 * 移除judpServer
 */
public static void remove(int port)
{
    map.remove(port);
}

/**
 * 添加删除过session的key
 */
public static void addSessionMap(String key)
{
    handQueue.add(key);
}

/*
 * 获取准备删除session的key
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
 * 移除key
 * 
 */
public static void removeKey(String key)
{
    handQueue.remove(key);
}

/**
 * 添加准备移除的key
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
 * 获取要移除的sessionid
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
