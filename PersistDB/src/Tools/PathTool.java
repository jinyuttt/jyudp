/**    
 * �ļ�����PathTool.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��26��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Tools;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

/**    
 *     
 * ��Ŀ���ƣ�Common    
 * �����ƣ�PathTool    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��26�� ����1:10:09    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��26�� ����1:10:09    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class PathTool {

/**
 * ��ȡ��ǰjar·��
 */
public static String getDirPath(Object obj)
{
    URL url = obj.getClass().getProtectionDomain().getCodeSource().getLocation();  
    String filePath = null;  
    try {  
        filePath = URLDecoder.decode(url.getPath(), "utf-8");// ת��Ϊutf-8����  
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
    if (filePath.endsWith(".jar")) {// ��ִ��jar�����еĽ�������".jar"  
        // ��ȡ·���е�jar����  
        filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);  
    }  
    File file = new File(filePath);  
      
    // /If this abstract pathname is already absolute, then the pathname  
    // string is simply returned as if by the getPath method. If this  
    // abstract pathname is the empty abstract pathname then the pathname  
    // string of the current user directory, which is named by the system  
    // property user.dir, is returned.  
    filePath = file.getAbsolutePath();//�õ�windows�µ���ȷ·��  
    return filePath;  
}

/**
 * ��ȡ��ǰjar·��
 */
public static String getFilePath(Object obj)
{
    URL url = obj.getClass().getProtectionDomain().getCodeSource().getLocation();  
    String filePath = null;  
    try {  
        filePath = URLDecoder.decode(url.getPath(), "utf-8");// ת��Ϊutf-8����  
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
    File file = new File(filePath);  
      
    // /If this abstract pathname is already absolute, then the pathname  
    // string is simply returned as if by the getPath method. If this  
    // abstract pathname is the empty abstract pathname then the pathname  
    // string of the current user directory, which is named by the system  
    // property user.dir, is returned.  
    filePath = file.getAbsolutePath();//�õ�windows�µ���ȷ·��  
    return filePath;  
}

}
