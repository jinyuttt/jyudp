/**    
 * �ļ�����TestNet.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��29��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package JNetSocket;



/**    
 *     
 * ��Ŀ���ƣ�NetSocket    
 * �����ƣ�TestNet    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��29�� ����10:03:22    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��29�� ����10:03:22    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TestNet {

    /**    
       
     * main(������һ�仰�����������������)    
       
     * TODO(����������������������� �C ��ѡ)    
     * @param   name    
     * @return 
       
     * @Exception �쳣����        
       
    */
    public static void main(String[] args) {
        MulticastClient client =new MulticastClient();
        byte[] data="hello!".getBytes();
        client.sendData("224.0.1.21", 4444, data);
        
//        MulticastServerSocket server =new MulticastServerSocket();
//        server.InitServer("224.0.1.21", 4444);
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }

}
