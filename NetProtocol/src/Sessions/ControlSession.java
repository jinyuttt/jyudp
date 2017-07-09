/**    
 * �ļ�����ControlSession.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package Sessions;

import java.util.concurrent.atomic.AtomicInteger;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�ControlSession    
 * ��������    sessionʹ������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����6:02:51    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����6:02:51    
 * �޸ı�ע��    
 * @version     
 *     
 */
class ControlSession {
  private  ClientSession session=null;
  private AtomicInteger maxNum=new AtomicInteger(1000);
  
  /*
   * ��ȡsession
   * ��ȡһ����������
   * ��������һ��
   */
  public ClientSession getSession()
  {
      return session;
  }
  
  /*
   * ����sesion
   */
  public void setSession(ClientSession session)
  {
      this.session=session;
  }
  public void setInitMax(int num)
  {
      maxNum.set(num);
  }
  public int getNum()
  {
      return maxNum.decrementAndGet();
  }
    
}