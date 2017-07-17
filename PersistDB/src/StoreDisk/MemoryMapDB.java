/**    
 * �ļ�����MemoryMapDB.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��11��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package StoreDisk;

import java.io.File;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�MemoryMapDB    
 * ��������    ��������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��11�� ����2:15:37    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��11�� ����2:15:37    
 * �޸ı�ע��    
 * @version     
 * @param <V>
 * @param <K>
 *     
 */
public  class MemoryMapDB<K,V> extends MemoryDisk<K,V>{
    private static final String MAP_NAME = "STAT_MAP";  
    private String filePath;  
  DB  memoryDB= null; 
  DB  fileDB=null;
    //��Ҫ�����ݸ�ʽ
    Map<K,V> statMap = null;
    Map<K,V>  memoryMap=null;
    DBMod type = null;  
    static enum DBMod  
    {  
        READ,  
        WRITE  
    }  
    //��Ĺ��췽��
    public MemoryMapDB(String filePath)  
    {  
        //��ʼ��
        this.filePath = filePath;  
        
        //this.type = type;  
        init();  
    } 
    //��ʼ��mapdb
    @Override
    void init()  
    {  
         File file = new File(filePath);  
         if(file.isDirectory())
         {
             if(!file.exists())
             {
                 file.mkdir();
                 
             }
           //
           String  fileName=filePath+"/mapdb.db";
           file = new File(fileName);
         }
         if(file.exists())  
         {  
             file.delete();  
             new File(filePath + ".p").delete();  
         }  
         memoryDB=DBMaker.newMemoryDirectDB().make();
         fileDB=DBMaker.newFileDB(file)
                 .closeOnJvmShutdown()
                 .transactionDisable()
                  .asyncWriteEnable()
                 .make();
             
             statMap = (Map<K, V>) fileDB.getHashMap(MAP_NAME);
             memoryMap=(Map<K, V>) memoryDB.getHashMap(MAP_NAME);
    }  
    public Map<K,V> memoryDB()  
    {  
        return this.statMap;  
    }  
    @Override
    public void putMemory(K key,V value)
    {
        memoryMap.put(key, value);
    }
    @Override
    public void putFile(K key,V value)
    {
        statMap.put(key, value);
    }
    @Override
    public void removeMemory(K key)
    {
        memoryMap.remove(key);
    }
    @Override
    public void removeFile(K key)
    {
        statMap.remove(key);
    }
    public void removeMemoryAll()
    {
        memoryMap.clear();
    }
    public void removeFileAll()
    {
        statMap.clear();
    }
    @Override
    public  V getMemory(K key)
    {
       return memoryMap.get(key);
    }
    
    @Override
    public  V getFile(K key)
    {
       return statMap.get(key);
    }
    public void loadFile()
    {
        
    }
    
    @Override
    public void close()  
    {  
        if(fileDB!=null){  
            fileDB.close();  
            fileDB = null;  
        } 
        if(memoryDB!=null)
        {
            memoryDB.close();
            memoryDB=null;
        }
    }
    @Override
    void clearMemoryAll() {
        removeMemoryAll();
        
    }
    @Override
    void cleaFileAll() {
     
        removeFileAll();
    }
    @Override
    Map<K, V> getAll() {
        // TODO Auto-generated method stub
        return null;
    }
  
 
}
