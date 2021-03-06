/**    
 * 文件名：AckPackaget.java    
 *    
 * 版本信息：    
 * 日期：2017年6月12日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package BufferData;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：AckPackaget    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年6月12日 下午8:39:19    
 * 修改人：jinyu    
 * 修改时间：2017年6月12日 下午8:39:19    
 * 修改备注：    
 * @version     
 *     
 */
public class AckPackaget {
    
    /**
     * 0发送方确认ack
     * 1接收方结束ack
     * 2丢包请求ack
     * 3发送方关闭
     */
    public byte  ackType=0;
    /**
     * session id
     */
public long sessionid=0;

/*
 * 当前session的网络包数
 */
public int  packagetNum=0;

/**
 * ackType=2时为丢失ID
 *  ackType=0，1时为包初始化ID
 * ,
 */
public long packagetID=-1;

public long clientID=-1;

}
