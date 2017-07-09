/**    
 * 文件名：DataModel.java    
 *    
 * 版本信息：    
 * 日期：2017年6月10日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetModel;

/**    
 *     
 * 项目名称：CommonModel    
 * 类名称：DataModel    
 * 类描述：网络数据传递对外model
 * 创建人：jinyu    
 * 创建时间：2017年6月10日 上午1:23:23    
 * 修改人：jinyu    
 * 修改时间：2017年6月10日 上午1:23:23    
 * 修改备注：    
 * @version     
 *     
 */
public class DataModel {
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
public String  localIP;

/**
 * 本地端口
 */
public int  localPort;
/*
 * 数据
 */
public byte[]data;

/**
 * 通讯协议类型 0 tcp  1 udp  2 组播 3 广播
 */
public int  netType;

/*
 * 需要socket通讯时保持
 * 一般直接保持服务端的socket或者tcp客户端
 */
public Object socket;//
}
