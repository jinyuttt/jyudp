/**    
 * 文件名：MemoryPool.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package BufferData;

import java.util.ArrayList;
import java.util.HashMap;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：MemoryPool    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 下午11:37:41    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 下午11:37:41    
 * 修改备注：    
 * @version     
 *     
 */
public class MemoryPool {
    /**
     * 大小(M)
     */
private static int memorySize=300;
/**
 * 自己
 */
private static int blockSize=30;

private static MemoryBlock[] memory=null;
private static int readindex=0;
//被覆盖的数据包
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
