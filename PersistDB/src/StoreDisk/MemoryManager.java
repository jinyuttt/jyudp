/**    
 * �ļ�����MemoryManager.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��16��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package StoreDisk;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�MemoryManager    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��16�� ����5:13:12    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��16�� ����5:13:12    
 * �޸ı�ע��    
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
