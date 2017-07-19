/**    
 * 文件名：ObjectByte.java    
 *    
 * 版本信息：    
 * 日期：2017年7月19日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：ObjectByte    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月19日 下午10:01:02    
 * 修改人：jinyu    
 * 修改时间：2017年7月19日 下午10:01:02    
 * 修改备注：    
 * @version     
 *     
 */
public class ObjectByte {
public static Object readStream(byte[]data)
{
    ByteArrayInputStream  input=new ByteArrayInputStream(data);
    try {
        ObjectInputStream  objinput=new ObjectInputStream(input);
         try {
            return  objinput.readObject();
        } catch (ClassNotFoundException e) {
          
   return null;
        }
    } catch (IOException e) {
        return null;
    }
}
public static byte[] writeObject(Object obj)
{
    ByteArrayOutputStream output=new ByteArrayOutputStream();
    ObjectOutputStream objOutput = null;
    try {
        objOutput = new ObjectOutputStream(output);
        objOutput.writeObject(obj);
    } catch (IOException e) {
       return null;
    }
    return output.toByteArray();
}
}
