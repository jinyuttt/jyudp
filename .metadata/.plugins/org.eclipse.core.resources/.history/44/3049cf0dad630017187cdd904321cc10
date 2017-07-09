/**    
 * 文件名：CacheTimeListenter.java    
 *    
 * 版本信息：    
 * 日期：2017年6月10日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package DataBus;

import com.google.common.cache.RemovalListener;

import DBAcess.BerkeleyDB;

/**    
 *     
 * 项目名称：Common    
 * 类名称：CacheTimeListenter    
 * 类描述： 内存缓存消失时触发器
 * 创建人：jinyu    
 * 创建时间：2017年6月10日 下午3:11:41    
 * 修改人：jinyu    
 * 修改时间：2017年6月10日 下午3:11:41    
 * 修改备注：    
 * @version     
 * @param <K>
 * @param <V>
 *     
 */
public abstract class CacheTimeListenter<Key, VCache> implements RemovalListener<Key , VCache>{
    BerkeleyDB db=null;
    public CacheTimeListenter()
    {
        db=new BerkeleyDB();
        db.setDir("cacheData");
        db.open();
        db.setConfig();
    }
    /*
     * 持久化
     */
    public void put(byte[] key,byte[]v)
    {
        db.insert(key, v);
    }
    
    /*
     * 获取数据
     */
    public byte[] get(byte[] key)
    {
     return   db.get(key);
    }
    public  abstract void  putDB(Key key,VCache v);
    public  abstract VCache  getDB(Key key);
    
}
