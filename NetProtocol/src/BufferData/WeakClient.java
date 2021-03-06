/**    
 * 文件名：WeakClient.java    
 *    
 * 版本信息：    
 * 日期：2017年7月9日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package BufferData;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**    
 *     
 * 项目名称：NetProtocol    
 * 类名称：WeakClient    
 * 类描述：  用于存储外层客户端  
 * 创建人：jinyu    
 * 创建时间：2017年7月9日 下午7:14:07    
 * 修改人：jinyu    
 * 修改时间：2017年7月9日 下午7:14:07    
 * 修改备注：    
 * @version     
 * @param <T>
 *     
 */
public class WeakClient<T> extends WeakReference<T> {
    public long key;
    public long okey;
    public WeakClient(long key,T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
        this.key = key;
    }
}
