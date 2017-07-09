/**    
 * �ļ�����memory_chunk.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package BufferData;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�memory_chunk    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����11:47:05    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����11:47:05    
 * �޸ı�ע��    
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