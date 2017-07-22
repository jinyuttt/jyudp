/**    
 * �ļ�����FileIndexManager.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��22��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileIndexManager    
 * ��������    �����ڴ�����
 * ���ڸ���
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��22�� ����1:10:02    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��22�� ����1:10:02    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileDBManager {
    private static FileDBManager instance=null;
    private  Connection conn=null;
    private FileDBManager()
    {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e1) {
         
            e1.printStackTrace();
        }
         try {
            conn = DriverManager.
                    getConnection("jdbc:h2:tcp://localhost/mem:indextable", "sa", "");
        } catch (SQLException e) {
           
            e.printStackTrace();
        }
    }
    public static FileDBManager getObj()
    {
        if(instance==null)
        {
            instance=new FileDBManager();
        }
        return instance;
    }
    public boolean exeSql(String sql)
    {
        boolean r=true;
     Statement stmt = null;
    try {
        stmt = conn.createStatement();
    } catch (SQLException e) {
        r=false;
        e.printStackTrace();
    }
     try {
        stmt.executeUpdate(sql);
    } catch (SQLException e) {
       r=false;
        e.printStackTrace();
    }
    
     return r;
    }
    public ResultSet exeSelect(String sql)
    {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
           
            e.printStackTrace();
        }
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
           
            e.printStackTrace();
        }   
        return rs;
    }
}
