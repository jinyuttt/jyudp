/**    
 * 文件名：SubPackaget.java    
 *    
 * 版本信息：    
 * 日期：2017年6月11日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package NetPackaget;

import java.util.LinkedList;

/**    
 *     
 * 项目名称：Common    
 * 类名称：SubPackaget    
 * 类描述：  网络数据分包
 * 可以设置传递的每包大小
 * 可以使用默认值（最大包）
 * 可以通过默认的设置（min,max,middle)来使用已经有的值
 * 创建人：jinyu    
 * 创建时间：2017年6月11日 下午4:17:18    
 * 修改人：jinyu    
 * 修改时间：2017年6月11日 下午4:17:18    
 * 修改备注：    
 * @version     
 *     
 */
public class SubPackaget {
    //548,65535,1472
    private static int  streamSize=65535-20;
    /**
     * 设置已有的值
     */
    public static void setSizeByDefault(String name)
    {
        String dname=name.toLowerCase();
        switch(dname)
        {
        case  "min":
            streamSize=548;
            break;
        case "middle":
            streamSize=1472;
            break;
        case "max":
            streamSize=65535;
            break;
            default:
                streamSize=65535;
                break;
            
        }
    }
    
    /**
     *  设置自定义大小(字节）
     */
    public static void setStreamSize(int size)
    {
        streamSize=size;
    }
   
    /*
     * 获取分包大小（字节）
     */
    public static  int getStreamSize()
    {
        return streamSize;
    }
   
    /* *
     * 数据分包
     */
public   static LinkedList<byte[]> subData(byte[]bytes)
{
    LinkedList<byte[]> list=new LinkedList<byte[]>();
    if(bytes==null)
    {
        return null;
    }
    else if(bytes.length<streamSize)
    {
        list.add(bytes);
        return list;
    }
    else
    {
        int  offset=0;//拷贝移位
        int len=streamSize;//初始化长度
        while(len>0)
        {
            byte[] tmp=new byte[len];
            System.arraycopy(bytes, offset, tmp, 0, tmp.length);
            list.addLast(tmp);
            offset+=len;//增长len;
            len=bytes.length-offset;//剩余长度
            if(len>streamSize)
            {
                //剩余长度置回大小
                len=streamSize;
            }
        }
      return  list;
    }
    
}
}
