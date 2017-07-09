/**    
 * 文件名：block.java    
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
 * 类名称：block    
 * 类描述：   内存块 
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 下午11:44:14    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 下午11:44:14    
 * 修改备注：    
 * @version     
 *     
 */
public class MemoryBlock {
    //16字节
    public long sessionid=0;
    public long packagetid=0;
    public MemoryBlock(int size)
    {
        blockData=new byte[size];
    }
    public int dataLen=0;
    public byte[] blockData=null;

}
