/**    
 * 文件名：memory_chunk.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package BufferData;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：memory_chunk    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 下午11:47:05    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 下午11:47:05    
 * 修改备注：    
 * @version     
 *     
 */
public class MemoryChunk {
 public MemoryChunk(int num,int size)
    {
        data=new MemoryBlock[num];
        this.size=size;
    }
public MemoryBlock[] data=null;
private int index=0;
private int size=0;

public void  add(MemoryBlock block)
{
    data[index]=block;
    index++;
}
public byte[] getData()
{
    byte[] byteData=new byte[size];
    int offset=0;
    for(int i=0;i<data.length;i++)
    {
       System.arraycopy(data[i].blockData, 0, byteData, offset, data[i].dataLen); 
       offset+=data[i].dataLen;
    }
    return byteData;
}
public void setData(long id,long packagetid,byte[]databyte)
{
    int offset=0;
    for(int i=0;i<data.length;i++)
    {
        System.arraycopy(databyte,offset, data[i].blockData, 0, data[i].dataLen);
        offset+=data[i].dataLen;
        data[i].sessionid=id;
        data[i].packagetid=packagetid;
    }
}
}
