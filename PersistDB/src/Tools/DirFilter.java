/**    
 * 文件名：DirFilter.java    
 *    
 * 版本信息：    
 * 日期：2017年7月17日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Tools;

import java.io.File;
import java.io.FilenameFilter;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：DirFilter    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月17日 下午10:45:41    
 * 修改人：jinyu    
 * 修改时间：2017年7月17日 下午10:45:41    
 * 修改备注：    
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
