/**    
 * 文件名：NetDataPackaget.java    
 *    
 * 版本信息：    
 * 日期：2017年6月12日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetPackaget;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：NetDataPackaget    
 * 类描述：   网络数据包 
 * 创建人：jinyu    
 * 创建时间：2017年6月12日 上午3:01:12    
 * 修改人：jinyu    
 * 修改时间：2017年6月12日 上午3:01:12    
 * 修改备注：    
 * @version     
 *     
 */
public class NetDataPackaget {
    /*
     * 来源Ip
     */
  public String srcIP;
  /*
  * 来源端口
  */
  public int srcPort;

  /**
   * 本地IP
   */
  public String localIP;

  /*
   * 本地IP
   */
  public int localPort;
  /*
  * 通讯协议类型 0 tcp  1 udp  2 组播 3 广播
  */
  public int  netType;

  /*
  * 需要socket通讯时保持
  * 一般直接保持服务端的socket或者tcp客户端
  */
  public Object socket;//
  
  /**
   *  完整的网络数据
   */
  public byte[] netPackaget;
}
