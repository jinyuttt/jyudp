/**    
 * �ļ�����SubPackaget.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��11��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetPackaget;

import java.util.LinkedList;

/**    
 *     
 * ��Ŀ���ƣ�Common    
 * �����ƣ�SubPackaget    
 * ��������  �������ݷְ�
 * �������ô��ݵ�ÿ����С
 * ����ʹ��Ĭ��ֵ��������
 * ����ͨ��Ĭ�ϵ����ã�min,max,middle)��ʹ���Ѿ��е�ֵ
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��11�� ����4:17:18    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��11�� ����4:17:18    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class SubPackaget {
    //548,65535,1472
    private static int  streamSize=65535-20;
    /**
     * �������е�ֵ
     */
    public static void setSizeByDefault(String name)
    {
        String dname=name.toLowerCase();
        switch(dname)
        {
        case  "min":
            streamSize=548;
            break;
        case "middle":
            streamSize=1472;
            break;
        case "max":
            streamSize=65535;
            break;
            default:
                streamSize=65535;
                break;
            
        }
    }
    
    /**
     *  �����Զ����С(�ֽڣ�
     */
    public static void setStreamSize(int size)
    {
        streamSize=size;
    }
   
    /*
     * ��ȡ�ְ���С���ֽڣ�
     */
    public static  int getStreamSize()
    {
        return streamSize;
    }
   
    /* *
     * ���ݷְ�
     */
public   static LinkedList<byte[]> subData(byte[]bytes)
{
    LinkedList<byte[]> list=new LinkedList<byte[]>();
    if(bytes==null)
    {
        return null;
    }
    else if(bytes.length<streamSize)
    {
        list.add(bytes);
        return list;
    }
    else
    {
        int  offset=0;//������λ
        int len=streamSize;//��ʼ������
        while(len>0)
        {
            byte[] tmp=new byte[len];
            System.arraycopy(bytes, offset, tmp, 0, tmp.length);
            list.addLast(tmp);
            offset+=len;//����len;
            len=bytes.length-offset;//ʣ�೤��
            if(len>streamSize)
            {
                //ʣ�೤���ûش�С
                len=streamSize;
            }
        }
      return  list;
    }
    
}
}
