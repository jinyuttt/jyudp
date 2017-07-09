/**    
 * �ļ�����AppData.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��10��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package BufferData;

/**    
 *     
 * ��Ŀ���ƣ�DataStromUtil    
 * �����ƣ�AppData    
 * ��������  �洢���ݸ�  
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��10�� ����11:11:46    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��10�� ����11:11:46    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class AppData {
    final long sequenceNumber;
    final long intSequenceNumber;
    final int packagetNum;
    public final byte[] data;
    public AppData(int num,long intSequenceNumber,long sequenceNumber, byte[]data){
        this.sequenceNumber=sequenceNumber;
        this.intSequenceNumber=intSequenceNumber;
        this.data=data;
        this.packagetNum=num;
    }
    public String toString(){
        return sequenceNumber+"["+data.length+"]";
    }

    public long getSequenceNumber(){
        return sequenceNumber;
    }
    public long getInitSequenceNumber(){
        return intSequenceNumber;
    }
    public long getDataLen(){
        return packagetNum;
    }
}