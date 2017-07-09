/**    
 * 文件名：SubPackaget.java    
 *    
 * 版本信息：    
 * 日期：2017年6月11日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FactoryPackaget;

import java.nio.ByteBuffer;
import java.util.Arrays;

import RecviceData.AckPackaget;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：SubPackaget    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年6月11日 下午11:13:54    
 * 修改人：jinyu    
 * 修改时间：2017年6月11日 下午11:13:54    
 * 修改备注：    
 * @version     
 *     
 */
public class SubNetPackaget {
    private  final static byte[] myFlage="judp".getBytes();
    /*
     * 创建网络发送包
     */
public static byte[] createNetPackaget(long sessionid,long initseq,long packagetid, int packagetNum,byte[] data)
{
    if(data==null)
    {
        return null;
    }
    byte[] bytes=new byte[28+data.length+1];//一个字节类型包
    ByteBuffer buf=ByteBuffer.wrap(bytes);
    buf.put((byte) 0);
    buf.putLong(sessionid);
    buf.putLong(initseq);
    buf.putInt(packagetNum);
    buf.putLong(packagetid);
    buf.put(data);
    return  buf.array();
}

/**
 * 创建ack包byte[]
 * 类型1
 */
public static byte[] createAckPackaget(AckPackaget ack)
{
  int len=0;
  if(ack.ackType==2)
  {
      len=22;
  }
  else
  {
      len=14;
  }
    byte[] bytes=new byte[len];//一个字节网络包类型包;ack类型
    ByteBuffer buf=ByteBuffer.wrap(bytes);
    buf.put((byte) 1);
    buf.put(ack.ackType);
    buf.putLong(ack.sessionid);
    buf.putInt(ack.packagetNum);
    if(ack.ackType==2)
    {
        buf.putLong(ack.packagetID);
    }
    return  buf.array();
}


/*
 * 解析网络接收包
 */
public static ReturnCode  AnalysisNetPackaget(byte[]netdata)
{
    ReturnCode code=new ReturnCode();
    if(netdata==null)
    {
        return code;
    }
    if(netdata.length>=myFlage.length)
    {
        byte[] head=new byte[myFlage.length];
        System.arraycopy(netdata, 0, head, 0, head.length);
        if(Arrays.equals(myFlage, head))
        {
            byte[] tmp=new byte[netdata.length-head.length];
            System.arraycopy(netdata, head.length, tmp, 0,tmp.length);//去除标记，只要数据
            netdata=tmp;
        }
        else
        {
            code.data=netdata;
            return code;
        }
        
    }
    try
    {
    ByteBuffer buf=ByteBuffer.wrap(netdata);
    byte type=buf.get();
    if(type==0)
    {
    long sessionid=buf.getLong();
    long initseq=buf.getLong();
    int  num=buf.getInt();
    long packagetid=buf.getLong();
    byte[] data=new byte[netdata.length-28-1];
    buf.get(data);
    code.data=data;
    code.InitSeq=initseq;
    code.PackagetID=packagetid;
    code.packagetNum=num;
    code.SessionID=sessionid;
    code.isAck=false;
    return code;
    }
    else
    {
        //说明是ack,没有真正数据
        byte acktype=buf.get();
        long sessionid=buf.getLong();
        int  num=buf.getInt();
        AckPackaget ack=new AckPackaget();
        ack.ackType=acktype;
        ack.packagetNum=num;
        ack.sessionid=sessionid;
        if(acktype==2)
        {
            ack.packagetID=buf.getLong();
        }
        code.isAck=true;
        code.ackPackaget=ack;
    }
    }
    catch(Exception ex)
    {
        code.ErrorCode=ex.getMessage();
    }
    return code;
    
}

/**
 * 封装端标记
 * 由于使用封装接口已经打包sessionid;
 * 与直接接收的数据差别；
 * 打上标记方便接收端解析数据
 */
public static byte[] createNetData(byte[]sendData)
{
    byte[] netData=new byte[myFlage.length+sendData.length];
    System.arraycopy(myFlage, 0, netData, 0, myFlage.length);
    System.arraycopy(sendData, 0, netData, myFlage.length, sendData.length);
    return netData;
}

}
