/**    
 * �ļ�����DirFilter.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��17��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Tools;

import java.io.File;
import java.io.FilenameFilter;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�DirFilter    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��17�� ����10:45:41    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��17�� ����10:45:41    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class DirFilter implements FilenameFilter {

    private String type;
    public DirFilter(String tp){
        this.type=tp;
       }
    @Override
    public boolean accept(File fl,String path) {
      File file=new File(path);
      String filename=file.getName();
      return filename.indexOf(type)!=-1;
    }

}
