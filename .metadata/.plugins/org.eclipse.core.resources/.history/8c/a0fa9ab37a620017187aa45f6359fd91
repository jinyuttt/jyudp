package DataBus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * 缓存数据
 *缓存内存数据
 */
public class CacheData<K,V> {
    boolean isLoadDB=false;
    Cache<K, V> cache=null;
    CacheTimeListenter<K,V> listener=null;
    public void setListenter(CacheTimeListenter<K, V> listener)
    {
        this.listener=listener;
    }
    
    /**
     * 构造函数
     * maxsize 缓存最大数量
     * time 缓存时间 秒
     * isLoadDB 是否从数据库加载
     */
   public  CacheData(long maxsize,int time,boolean isLoadDB)
  {
       this.isLoadDB=isLoadDB;
   cache = (Cache<K, V>) CacheBuilder.newBuilder()
                    .maximumSize(maxsize)
                    .initialCapacity(4)
                    .expireAfterAccess(time, TimeUnit.SECONDS)
                    .build(
                            new CacheLoader<K, V>() {
                         
                                                public V load(K key) { // no checked exception
                                                  if(isLoadDB)
                                                  {
                                                      return getDBKey( key);
                                                  }
                                                  else
                                                  {
                                                      return null;
                                                  }
                 
                                                }});

  }
  
  /**
   * 加载
   */
  public V getDBKey(K key)
  {
   
   return   listener.getDB(key);
  }
  /*
   * 直接存储
   */
 public void put(K key, V v)
 {
     cache.put(key, v);
 }
 
 /*
  * 获取值
  */
 public V getByKey(K key)
 {
        return cache.getIfPresent(key);
    
 }
 /*
  * 移除缓存
  */
 public void  remove(K key)
 {
     cache.invalidate(key);
 }
 
 /*
  * 清空
  */
 public void removeAll()
 {
     cache.cleanUp();
 }
}
