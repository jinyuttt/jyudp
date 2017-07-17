/**    
 * �ļ�����MemoryDisk.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��16��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package StoreDisk;

import java.util.Map;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�MemoryDisk    
 * ��������   ����洢
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��16�� ����5:18:41    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��16�� ����5:18:41    
 * �޸ı�ע��    
 * @version     
 *     
 */
public abstract class MemoryDisk<K,V> {
     abstract void init();
     abstract void putMemory(K key,V value);
     abstract void putFile(K key,V value);
     abstract void removeMemory(K key);
     abstract void removeFile(K key);
     abstract void   clearMemoryAll();
     abstract void cleaFileAll();
     abstract  V getMemory(K key);
     abstract  V getFile(K key);
     abstract Map<K,V> getAll();
     abstract void close();
}
