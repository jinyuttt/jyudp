/**    
 * 文件名：NetDataAddress.java    
 *    
 * 版本信息：    
 * 日期：2017年7月4日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetModel;

/**    
 *     
 * 项目名称：NetModel    
 * 类名称：NetDataAddress    
 * 类描述：   获取返回数据：数据，来源地址
 * 创建人：jinyu    
 * 创建时间：2017年7月4日 下午11:35:43    
 * 修改人：jinyu    
 * 修改时间：2017年7月4日 下午11:35:43    
 * 修改备注：    
 * @version     
 *     
 */
public class NetDataAddress {
    /**
     * 来源IP
     */
    public String srcIP;
    /**
     * 来源端口
     */
    public int srcPort;
    
    /**
    * 本地IP
    */
   public String  localIP;

   /**
    * 本地端口
    */
   public int  localPort;
    
    /**
     * 数据
     */
    public byte[] netData;
    
    /**
     * 读取数据长度
     */
    public int len=-1;
    
    public long srcid=-1;
}
