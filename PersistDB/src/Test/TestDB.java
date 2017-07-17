/**    
 * �ļ�����TestDB.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��16��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Test;

import java.io.IOException;
import java.util.Random;

import StoreDisk.MemoryManager;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�TestDB    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��16�� ����8:10:17    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��16�� ����8:10:17    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TestDB {


    public static void main(String[] args) {
        MemoryManager.fileDB="E:\\Study\\java\\jyudp\\dbdata\\mapdb.db";
        MemoryManager<String,byte[]> obj=new  MemoryManager<String,byte[]>();
        byte[]data="hello".getBytes();
        byte[]data1="word".getBytes();
        long v1=System.currentTimeMillis();
        Random rdm=new Random();
        for(int i=0;i<10000;i++)
        {
            obj.put(String.valueOf(rdm.nextLong()), data);;
//        obj.put("12", data);
//        obj.put("34", data1);
//         byte[]value=obj.get("12");
//         System.out.println(new String(value));
//         value=obj.get("34");
//         System.out.println(new String(value));
        }
        long v2=System.currentTimeMillis();
        System.out.println(v2-v1);
        obj.close();
       try {
        System.in.read();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    }

}
