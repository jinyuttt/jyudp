/**    
 * �ļ�����NetDataAddress.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��4��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetModel;

/**    
 *     
 * ��Ŀ���ƣ�NetModel    
 * �����ƣ�NetDataAddress    
 * ��������   ��ȡ�������ݣ����ݣ���Դ��ַ
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��4�� ����11:35:43    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��4�� ����11:35:43    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class NetDataAddress {
    /**
     * ��ԴIP
     */
    public String srcIP;
    /**
     * ��Դ�˿�
     */
    public int srcPort;
    
    /**
    * ����IP
    */
   public String  localIP;

   /**
    * ���ض˿�
    */
   public int  localPort;
    
    /**
     * ����
     */
    public byte[] netData;
    
    /**
     * ��ȡ���ݳ���
     */
    public int len=-1;
    
    public long srcid=-1;
}
