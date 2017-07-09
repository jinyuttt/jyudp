/**    
 * �ļ�����ListenerServer.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��11��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetProtocol;


import java.util.concurrent.ConcurrentHashMap;
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
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ListenerServer    
 * ��������    ����ȫ����������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��11�� ����11:03:32    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��11�� ����11:03:32    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ListenerData {
    Session client=null;
  public  ListenerData(Session clientSession)
  {
      client=clientSession;
  }
  public  ListenerData()
  {
      
  }
    //������Դ�洢
  private  ConcurrentHashMap<String,SessionMap<Long,Session>> hashOffer=new ConcurrentHashMap<String,SessionMap<Long,Session>>(); 
  public void remove(String key)
  {
      hashOffer.remove(key);
  }
    //private Lock lock = new ReentrantLock();// ������
    @Subscribe
    @AllowConcurrentEvents
public void  monitorServer(DataModel monitorData)
{
        ReturnCode returnCode=CreateNetPackaget.AnalysisNetPackaget(monitorData.data);
        if(returnCode.isAck)
        {
            //�����ack
            //if(returnCode.SessionID)
            //��ȡ�����ط�
            //�������
            if(client!=null)
            {
                //��ǰ�ǿͻ��˽��յ����ݴ�����
                client.addData(returnCode);
                return;
            }
           
           
        }
        if(returnCode.SessionID==0&&returnCode.packagetNum==0)
        {
            //˵��û�����ݻ�����udpֱ�ӷ��͵�����
            //����ʹ�õķ�װ����
            //������ֱ�ӷ���
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
        //�ȴ������
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
        
        Session session=single.get(returnCode.SessionID);
        if(session==null||session.isClose())
        {
            session=createSession(single,monitorData.srcIP,monitorData.srcPort,monitorData.localIP,monitorData.localPort,returnCode.SessionID,monitorData.netType);
            
        }
         session.addData(returnCode);
           
        } 
    
    /*
     * ����session
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
     * ����SessionMap
     */
    private synchronized  SessionMap<Long,Session> createcreateSessionMap(String key)
    {
        SessionMap<Long,Session> single=  hashOffer.get(key);
        if(single==null)
        {
          single=new  SessionMap<Long,Session>();
          hashOffer.put(key, single);
        }
        return single;
    }
   /*
    *��ȡ���Ͷ�����
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
 