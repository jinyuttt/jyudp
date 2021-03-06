/**    
 * 文件名：ControlSession.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Sessions;

import java.util.concurrent.atomic.AtomicInteger;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：ControlSession    
 * 类描述：    session使用设置
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 下午6:02:51    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 下午6:02:51    
 * 修改备注：    
 * @version     
 *     
 */
class ControlSession {
  private  ClientSession session=null;
  private AtomicInteger maxNum=new AtomicInteger(1000);
  
  /*
   * 获取session
   * 获取一次则共享增加
   * 计数减少一次
   */
  public ClientSession getSession()
  {
      return session;
  }
  
  /*
   * 设置sesion
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
