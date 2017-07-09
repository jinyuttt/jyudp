/**    
 * �ļ�����MemoryPool.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package BufferData;

import java.util.ArrayList;
import java.util.HashMap;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�MemoryPool    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����11:37:41    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����11:37:41    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class MemoryPool {
    /**
     * ��С(M)
     */
private static int memorySize=300;
/**
 * �Լ�
 */
private static int blockSize=30;

private static MemoryBlock[] memory=null;
private static int readindex=0;
//�����ǵ����ݰ�
public static HashMap<Long,ArrayList<Long>> hashMap=new HashMap<Long,ArrayList<Long>>();
public  static void initMemoryBlock()
{
    int num=memorySize*1000/(blockSize+16);
    memory=new MemoryBlock[num];
    for(int i=0;i<num;i++)
    {
        MemoryBlock tmp=new MemoryBlock(blockSize);
        memory[i]=tmp;
    }
}
public static MemoryChunk getBlock(int size)
{
    int num=size/blockSize;
    int left=size%blockSize;
    int bsize=left>0?1:0;
    MemoryChunk chunk=new MemoryChunk(num+bsize,size);
    for(int i=0;i<num;i++)
    {
        MemoryBlock tmp=memory[(readindex++)%memory.length];
        tmp.dataLen=blockSize;
        chunk.add(tmp);
        remove(tmp);
    }
    if(left>0)
    {
        MemoryBlock tmp=memory[(readindex++)%memory.length];
        tmp.dataLen=left;
        chunk.add(tmp);
        remove(tmp);
    }
    return chunk;
}
public static void remove(MemoryBlock block)
{
   
        ArrayList<Long> list=  hashMap.get(block.sessionid);
        if(list==null)
        {
            list=new ArrayList<Long>();
            hashMap.put(block.sessionid, list);
        }
        list.add(block.packagetid);
    
}
public static ArrayList<Long> getPackaget(long id)
{
   return hashMap.remove(id);
}
public static void setChunk(byte[]data,MemoryChunk chunk)
{
    
}
}