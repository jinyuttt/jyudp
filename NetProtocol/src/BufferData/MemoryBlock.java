/**    
 * �ļ�����block.java    
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
 * �����ƣ�block    
 * ��������   �ڴ�� 
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����11:44:14    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����11:44:14    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class MemoryBlock {
    //16�ֽ�
    public long sessionid=0;
    public long packagetid=0;
    public MemoryBlock(int size)
    {
        blockData=new byte[size];
    }
    public int dataLen=0;
    public byte[] blockData=null;

}