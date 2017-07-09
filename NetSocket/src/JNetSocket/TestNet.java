/**    
 * 文件名：TestNet.java    
 *    
 * 版本信息：    
 * 日期：2017年6月29日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package JNetSocket;



/**    
 *     
 * 项目名称：NetSocket    
 * 类名称：TestNet    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年6月29日 下午10:03:22    
 * 修改人：jinyu    
 * 修改时间：2017年6月29日 下午10:03:22    
 * 修改备注：    
 * @version     
 *     
 */
public class TestNet {

    /**    
       
     * main(这里用一句话描述这个方法的作用)    
       
     * TODO(这里描述这个方法适用条件 – 可选)    
     * @param   name    
     * @return 
       
     * @Exception 异常对象        
       
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
