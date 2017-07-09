/**    
 * �ļ�����WeakClient.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��9��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package BufferData;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**    
 *     
 * ��Ŀ���ƣ�NetProtocol    
 * �����ƣ�WeakClient    
 * ��������  ���ڴ洢���ͻ���  
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��9�� ����7:14:07    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��9�� ����7:14:07    
 * �޸ı�ע��    
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