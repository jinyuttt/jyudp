/**    
 * �ļ�����IRecData.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��5��26��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package JNetSocket;
/**    
 *     
 * ��Ŀ���ƣ�JNetSocket    
 * �����ƣ�IRecData    
 * ��������    ��������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��5��26�� ����11:20:26    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��5��26�� ����11:20:26    
 * �޸ı�ע��    
 * @version     
 *     
 */
public interface IRecData {
   
    /*
     * ���ؽ��յ�����
     * 
     */
public void   recviceData(String src,byte[]data);

/**
 * 
   
 * setServer ����socket
 * @param   name    
   
 * @param  @return    �趨�ļ�    
   
 * @return String    DOM����    
   
 * @Exception �쳣����    
   
 * @since  CodingExample��Ver(���뷶���鿴) 1.1
 */
public void   setServer(Object socket);
}
