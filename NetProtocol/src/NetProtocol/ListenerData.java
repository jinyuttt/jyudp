/**    
 * 文件名：ListenerServer.java    
 *    
 * 版本信息：    
 * 日期：2017年6月11日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetProtocol;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import EventBus.MessageBus;
import NetModel.DataModel;
import NetModel.NetDataAddress;
import NetPackaget.CreateNetPackaget;
import NetPackaget.NetDataPackaget;
import NetPackaget.ReturnCode;
import NetPackaget.SessionMap;
import Sessions.ServerManager;
import Sessions.Session;
import Sessions.SessionFactory;


/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：ListenerServer    
 * 类描述：    监听全部接收数据
 * 创建人：jinyu    
 * 创建时间：2017年6月11日 下午11:03:32    
 * 修改人：jinyu    
 * 修改时间：2017年6月11日 下午11:03:32    
 * 修改备注：    
 * @version     
 *     
 */
public class ListenerData {
    Session client=null;
   private ConcurrentLinkedQueue<DataModel> queue=new ConcurrentLinkedQueue<DataModel>();
  //  private Lock lock = new ReentrantLock();// 锁对象  
   private volatile boolean isStart=false;
  public  ListenerData(Session clientSession)
  {
      client=clientSession;
  }
  public  ListenerData()
  {
      
  }
    //按照来源存储
  private  ConcurrentHashMap<String,SessionMap<Long,Session>> hashOffer=new ConcurrentHashMap<String,SessionMap<Long,Session>>(); 
  public void remove(String key)
  {
      hashOffer.remove(key);
  }
    //private Lock lock = new ReentrantLock();// 锁对象
    @Subscribe
    @AllowConcurrentEvents
public void  monitorServer(DataModel monitorData)
{
        queue.offer(monitorData);
        startThread();
}
 private void startThread()
 {
     if(isStart)
     {
         return;
     }
     isStart=true;
     Thread procedata=new Thread(new Runnable() {

        @Override
        public void run() {
           int sum=0;
           while(true)
           {
               if(sum>1000)
               {
                   sum=0;
                   try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                 
                    e.printStackTrace();
                }
               }
               DataModel data=queue.poll();
               if(data==null)
               {
                   sum=0;
                   try {
                    TimeUnit.SECONDS.sleep(1);
                    if(queue.isEmpty())
                    {
                        break;
                    }
                    else
                    {
                        continue;
                    }
                } catch (InterruptedException e) {
                }
               }
               sum++;
               processData(data);
           }
           isStart=false;
        }
         
     });
     procedata.setDaemon(true);
     procedata.setName("udpRecprocess");
     procedata.start();
 }
 private void processData(DataModel monitorData)
    {
        ReturnCode returnCode=CreateNetPackaget.AnalysisNetPackaget(monitorData.data);
        if(returnCode.isAck)
        {
            //如果是ack
            //if(returnCode.SessionID)
            //获取数据重发
            //完成数据
            if(client!=null)
            {
                //当前是客户端接收的数据处理；
                client.addData(returnCode);
                return;
            }
           
           
        }
        if(returnCode.SessionID==0&&returnCode.packagetNum==0)
        {
            //说明没有数据或者是udp直接发送的数据
            //不是使用的封装发送
            //该数据直接返回
            NetDataPackaget  netData=new NetDataPackaget();
            netData.localIP=monitorData.localIP;
            netData.localPort=monitorData.localPort;
            netData.netPackaget=returnCode.data;
            netData.netType=monitorData.netType;
            netData.socket=monitorData.socket;
            netData.srcIP=monitorData.srcIP;
            netData.srcPort=monitorData.srcPort;
            MessageBus.post(String.valueOf(monitorData.localPort), netData);
            return;
        }
        String key=monitorData.srcIP+monitorData.srcPort;
      //  ServerManager.removeKey(key);
        //先触发清除
        String[] keys=ServerManager.getSessionMap();
        SessionMap<Long,Session> single=null;
        if(keys!=null)
        {
            for(int i=0;i<keys.length;i++)
            {
               long[]ids= ServerManager.getSessions(keys[i]);
               single= hashOffer.get(keys[i]);
               if(ids!=null)
               {
                   for(int j=0;j<ids.length;j++)
                   {
                      single.remove(ids[j]);
                   }
               }
                if(single.isEmpty())
                {
                    hashOffer.remove(keys[i]);
                }
                ServerManager.removeKey(keys[i]);
            }
           
          
        }
        single=hashOffer.getOrDefault(key, null);
        if(single==null)
        {
            single= createcreateSessionMap(key);
        }
        
         Session session=null;
         if(returnCode.isAck)
         {
             session=single.get(returnCode.ackPackaget.sessionid);
         }
         else
         {
             session= single.get(returnCode.SessionID);
         }
        if(session==null||session.isClose())
        {
            session=createSession(single,monitorData.srcIP,monitorData.srcPort,monitorData.localIP,monitorData.localPort,returnCode.SessionID,monitorData.netType);
            session.setClientID(returnCode.clientid);
        }
       
         session.addData(returnCode);
           
        
    }
    
    /*
     * 
     * 创建session
     */
    private synchronized  Session  createSession(SessionMap<Long,Session> single,String srcIP,int srcPort,String localIP,int localPort,long sessionid,int netType)
    {
        Session session=single.get(sessionid);
        if(session==null)
        {
           session=SessionFactory.createObj(srcIP,srcPort,localIP,localPort);
           single.put(sessionid, session);
           session.setID(sessionid);
           session.setNetType(netType);   
        }
        return session;
    }
    
    /*
     * 
     * 创建SessionMap
     * 
     */
    private synchronized SessionMap<Long,Session> createcreateSessionMap(String key)
    {
        SessionMap<Long,Session> single=hashOffer.get(key);
        if(single==null)
        {
          single=new  SessionMap<Long,Session>();
          hashOffer.put(key, single);
        }
        return single;
    }
   /*
    *获取发送端数据
    */
    public NetDataAddress getData(String srcIP,int srcPort,String localIP,int localPort,long sessionid,int netType)
    {
        String key=srcIP+srcPort;
        SessionMap<Long,Session> single=hashOffer.getOrDefault(key, null);
        if(single==null)
            {
                single= createcreateSessionMap(key);
            }
            Session session=single.get(sessionid);
            if(session==null)
            {
                session=createSession(single,srcIP,srcPort,localIP,localPort,sessionid,netType);
            }
           
            NetDataAddress data= session.read();
            session.setCall();
            hashOffer.remove(key);
            return data;
        
    }
    public Session getDataLen(String srcIP,int srcPort,String localIP,int localPort,long sessionid,int netType)
    {
        String key=srcIP+srcPort;
        SessionMap<Long,Session> single=hashOffer.getOrDefault(key, null);
          if(single==null)
            {
                single= createcreateSessionMap(key);
            }
            Session session=single.get(sessionid);
            if(session==null)
            {
                session=createSession(single,srcIP,srcPort,localIP,localPort,sessionid,netType);
            }
           
         return session;
        
    }
}
 