/**    
 * �ļ�����CacheTimeListenter.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��10��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package DataBus;

import com.google.common.cache.RemovalListener;



/**    
 *     
 * ��Ŀ���ƣ�Common    
 * �����ƣ�CacheTimeListenter    
 * �������� �ڴ滺����ʧʱ������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��10�� ����3:11:41    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��10�� ����3:11:41    
 * �޸ı�ע��    
 * @version     
 * @param <K>
 * @param <V>
 *     
 */
public abstract class CacheTimeListenter<Key, VCache> implements RemovalListener<Key , VCache>{

    public VCache getDB(Key key) {
      
        return null;
    }
 
 
    
}