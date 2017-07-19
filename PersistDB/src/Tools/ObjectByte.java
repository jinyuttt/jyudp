/**    
 * �ļ�����ObjectByte.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��19��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�ObjectByte    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��19�� ����10:01:02    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��19�� ����10:01:02    
 * �޸ı�ע��    
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
