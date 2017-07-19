/**    
 * 文件名：MemoryManager.java    
 *    
 * 版本信息：    
 * 日期：2017年7月16日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package StoreDisk;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：MemoryManager    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月16日 下午5:13:12    
 * 修改人：jinyu    
 * 修改时间：2017年7月16日 下午5:13:12    
 * 修改备注：    
 * @version     
 *     
 */
public class MemoryManager<K,V> {
  private  MemoryDisk<K, V> objDB=null;
  //static MemoryManager instance=null;
  public static String fileDB="";
 // private String curType="MapDB";
 
  public MemoryManager()
  {
      objDB=new MemoryMapDB<K, V>(fileDB);
  }
  public void setType(String type)
  {
     // curType=type;
      if(type.equalsIgnoreCase("PalDB"))
      {
          objDB=new MemoryPalDB<K,V>();
      }
  }
//  public static MemoryManager getInstance()
//  {
//      if(instance==null)
//      {
//          instance=new MemoryManager();
//      }
//    return instance;
//      
//  }
  public void put(K key,V val)
  {
      objDB.putFile(key, val);
  }
  public V get(K key)
  {
     return objDB.getFile(key);
  }
  public void deleteByKey(K key)
  {
      objDB.removeFile(key);
  }
  public void clear()
  {
      objDB.cleaFileAll();
  }
  public void close()
  {
      objDB.close();
  }
}
