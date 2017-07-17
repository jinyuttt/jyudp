/**    
 * �ļ�����MemoryPalDB.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��16��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package StoreDisk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.linkedin.paldb.api.PalDB;
import com.linkedin.paldb.api.StoreReader;
import com.linkedin.paldb.api.StoreWriter;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�MemoryPalDB    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��16�� ����6:05:20    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��16�� ����6:05:20    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class MemoryPalDB<K,V> extends MemoryDisk<K,V> {
  
    private final String fileDb="PalDB.DB";
    HashMap<Long,StoreReader> reader=new  HashMap<Long,StoreReader>();
    HashMap<Long,StoreWriter> writer=new  HashMap<Long,StoreWriter>();
    HashMap<Long,Long> fileIndex=new  HashMap<Long,Long>();
    @Override
    void init() {
        
    }

    @Override
    void putMemory(K key, V value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    void putFile(K key, V value) {
        StoreWriter writer = PalDB.createWriter(new File(fileDb));
        writer.put("foo", "bar");
        writer.put(1213, new int[] {1, 2, 3});
        writer.close();
        
    }

    @Override
    void removeMemory(K key) {
     
        
    }

    @Override
    void removeFile(K key) {
    }

    @Override
    void clearMemoryAll() {
        // TODO Auto-generated method stub
        
    }

    @Override
    void cleaFileAll() {
        // TODO Auto-generated method stub
        
    }

    @Override
    V getMemory(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    V getFile(K key) {
        StoreReader reader = PalDB.createReader(new File(fileDb));
        V val= reader.get(key);
        reader.close();
        return val;
    }

    @Override
    void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    Map<K, V> getAll() {
        HashMap<K,V> hash=new  HashMap<K,V> ();
        StoreReader reader = PalDB.createReader(new File(fileDb));
        Iterable<Map.Entry<K, V>> iterable = reader.iterable();
        for (Map.Entry<K, V> entry : iterable) {
          K key = entry.getKey();
          V value = entry.getValue();
          hash.put(key, value);
        }
        
        reader.close();
        return hash;
    }

}
