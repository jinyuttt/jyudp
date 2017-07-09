/**    
 * 文件名：SessionMap.java    
 *    
 * 版本信息：    
 * 日期：2017年6月12日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetPackaget;

import java.util.concurrent.ConcurrentHashMap;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：SessionMap    
 * 类描述：二级存储
 * 创建人：jinyu    
 * 创建时间：2017年6月12日 上午2:11:49    
 * 修改人：jinyu    
 * 修改时间：2017年6月12日 上午2:11:49    
 * 修改备注：    
 * @version     
 *     
 */
public class SessionMap<K,V> {
   /**
    * 数据缓存
    */
 private   ConcurrentHashMap<K,V> hashMap=new ConcurrentHashMap<K,V>();
 /**
  * 添加数据
  */
public void put(K key, V value)
{
    hashMap.put(key, value);
}

/**
 * 获取数据
 */
public V get(K key)
{
  return  hashMap.get(key);
}

/**
 * 删除数据
 */
public void remove(K key)
{
    hashMap.remove(key);
}

/**
 * 清除所以数据
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
