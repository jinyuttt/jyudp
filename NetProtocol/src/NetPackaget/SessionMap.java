/**    
 * �ļ�����SessionMap.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��12��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetPackaget;

import java.util.concurrent.ConcurrentHashMap;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�SessionMap    
 * �������������洢
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��12�� ����2:11:49    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��12�� ����2:11:49    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class SessionMap<K,V> {
   /**
    * ���ݻ���
    */
 private   ConcurrentHashMap<K,V> hashMap=new ConcurrentHashMap<K,V>();
 /**
  * �������
  */
public void put(K key, V value)
{
    hashMap.put(key, value);
}

/**
 * ��ȡ����
 */
public V get(K key)
{
  return  hashMap.get(key);
}

/**
 * ɾ������
 */
public void remove(K key)
{
    hashMap.remove(key);
}

/**
 * �����������
 */
public void clear()
{
    hashMap.clear();
}
public boolean isEmpty()
{
    return hashMap.isEmpty();
}
}
