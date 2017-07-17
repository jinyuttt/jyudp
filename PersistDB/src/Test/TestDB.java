/**    
 * 文件名：TestDB.java    
 *    
 * 版本信息：    
 * 日期：2017年7月16日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Test;

import java.io.IOException;
import java.util.Random;

import StoreDisk.MemoryManager;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：TestDB    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月16日 下午8:10:17    
 * 修改人：jinyu    
 * 修改时间：2017年7月16日 下午8:10:17    
 * 修改备注：    
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
