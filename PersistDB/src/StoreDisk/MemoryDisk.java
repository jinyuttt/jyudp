/**    
 * 文件名：MemoryDisk.java    
 *    
 * 版本信息：    
 * 日期：2017年7月16日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package StoreDisk;

import java.util.Map;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：MemoryDisk    
 * 类描述：   管理存储
 * 创建人：jinyu    
 * 创建时间：2017年7月16日 下午5:18:41    
 * 修改人：jinyu    
 * 修改时间：2017年7月16日 下午5:18:41    
 * 修改备注：    
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
