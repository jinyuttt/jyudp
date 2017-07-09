/**    
 * �ļ�����ClientPools.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Sessions;

import java.util.concurrent.ConcurrentHashMap;

import BufferData.MemoryPool;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ClientSessionsPools    
 * ��������   �����������ݷ��͵�seesion
 * 1.һ��session(host+srp+srport)
 * 2.�󶨱��أ�localIP+port+srp+srport)
 * 3.���ر��ض˿�(port+srp+srport)
 * 4.��ǰ2.3����
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����5:44:18    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����5:44:18    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ClientSessionsPools {
 private static int maxClientNum=1000;
 private static boolean isInitMemory=true;
 public static ConcurrentHashMap<String,ControlSession> map=new ConcurrentHashMap<String,ControlSession>();

 /**
 * ��ȡsession
 * 
 */
public static synchronized ClientSession getSession(int port,String srcIP,int srcPort)
{
   if(isInitMemory)
   {
       isInitMemory=false;
       MemoryPool.initMemoryBlock();
   }
    String key="";
    if(port==0)
    {
        //˵����һ��session;
         key=srcIP+srcPort;
    }
    else
    {
         key=port+srcIP+srcPort;
    }
    ControlSession ctrsession=  map.get(key);
    if(ctrsession==null)
    {
        ctrsession=new ControlSession();
        ClientSession session= SessionFactory.createObj();
        ctrsession.setSession(session);
        ctrsession.setInitMax(maxClientNum);
        map.put(key, ctrsession);
    }
    if(ctrsession.getNum()==0)
    {
        //���£�
        map.remove(key);
    }
    ClientSession client=ctrsession.getSession();
    client.setClientIncrement();//���ͻ���ʹ��
    return client;
}

/**
 * ���������
 */
public static void setSessionMax(int num)
{
    maxClientNum=num;
}

/*
 * ɾ�����ڷ���session
 */
public static void removeKey(String key)
{
    map.remove(key);
}
}