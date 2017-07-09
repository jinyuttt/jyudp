/**    
 * 文件名：IRecData.java    
 *    
 * 版本信息：    
 * 日期：2017年5月26日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package JNetSocket;
/**    
 *     
 * 项目名称：JNetSocket    
 * 类名称：IRecData    
 * 类描述：    接受数据
 * 创建人：jinyu    
 * 创建时间：2017年5月26日 下午11:20:26    
 * 修改人：jinyu    
 * 修改时间：2017年5月26日 下午11:20:26    
 * 修改备注：    
 * @version     
 *     
 */
public interface IRecData {
   
    /*
     * 返回接收的数据
     * 
     */
public void   recviceData(String src,byte[]data);

/**
 * 
   
 * setServer 返回socket
 * @param   name    
   
 * @param  @return    设定文件    
   
 * @return String    DOM对象    
   
 * @Exception 异常对象    
   
 * @since  CodingExample　Ver(编码范例查看) 1.1
 */
public void   setServer(Object socket);
}
