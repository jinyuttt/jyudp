/**    
 * �ļ�����DataModel.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��6��10��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package NetModel;

/**    
 *     
 * ��Ŀ���ƣ�CommonModel    
 * �����ƣ�DataModel    
 * ���������������ݴ��ݶ���model
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��6��10�� ����1:23:23    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��6��10�� ����1:23:23    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class DataModel {
    /*
     * ��ԴIp
     */
public String srcIP;
/*
 * ��Դ�˿�
 */
public int srcPort;

/**
 * ����IP
 */
public String  localIP;

/**
 * ���ض˿�
 */
public int  localPort;
/*
 * ����
 */
public byte[]data;

/**
 * ͨѶЭ������ 0 tcp  1 udp  2 �鲥 3 �㲥
 */
public int  netType;

/*
 * ��ҪsocketͨѶʱ����
 * һ��ֱ�ӱ��ַ���˵�socket����tcp�ͻ���
 */
public Object socket;//
}
