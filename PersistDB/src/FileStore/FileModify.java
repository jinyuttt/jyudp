/**    
 * �ļ�����FileModify.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��21��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileStore;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileModify    
 * ��������    �����ļ����� 
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��21�� ����9:59:30    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��21�� ����9:59:30    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileModify {
    private  final int maxCopy=100*1204*1024;//100M
   private final long maxSize=1*1024*1024*1024;//����
   private   long sum=0;
   private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
  private HashMap<String,ArrayList<DataDeleteIndex>> hash=new HashMap<String,ArrayList<DataDeleteIndex>>();
  public void addFile(DataDeleteIndex index)
   {
       ArrayList<DataDeleteIndex> tmp= hash.get(index.fileid);
       if(tmp==null)
       {
           tmp=new ArrayList<DataDeleteIndex>();
           hash.put(index.fileid, tmp);
           if(!tmp.contains(index))
           tmp.add(index);
       }
       else
       {
           if(!tmp.contains(index))
               tmp.add(index);
       }
       //
       sum+=index.len;
      if(sum>maxSize)
      {
          //��������1G��ʼ�����ļ�
          sum=0;
          startThread();
      }
   }
  
  /*
   * �����߳�
   */
   private void startThread()
   {
       cachedThreadPool.execute(new Runnable() {

        @Override
        public void run() {
            Thread.currentThread().setName("�����ļ�");
            start();
        }
           
       });
   }
  /*
   * �������ݴ���
   */
 public void start()
{
  
    for(Entry<String, ArrayList<DataDeleteIndex>> entry:hash.entrySet())
    {
        String fileid=entry.getKey();
        String tmpfile=fileid+".tmp";
        ArrayList<DataDeleteIndex> data=entry.getValue();
        DataDeleteIndex[] moidydata= sortIndex(data);
        long offset=0;
        FileRead read=new FileRead();
        read.path=fileid;
        FileWrite sw=new FileWrite();
        sw.path=tmpfile;
        for(int i=0;i<moidydata.length;i++)
        {
           DataDeleteIndex index=moidydata[i];
           long curPosition= index.position;
           int curLen=index.len;
           long bytesnum=curPosition-offset-1;
           offset=copyData(read,sw,offset,bytesnum,curLen);
           String sql="update indexFile set position=position-"+curLen+" where fileid='"+fileid+"'";
           //�޸ĺ��������
           FileDBManager.getObj().exeSql(sql);
        }
        //����ʣ������
        File f=new File(fileid);
        long leftBytes=f.length()-offset;
        offset=copyData(read,sw,offset,leftBytes,0);
       
        //�ٴαȽ�����
        File ff=new File(fileid);
        
        if(ff.length()!=sw.getFile())
        {
            //����д����ļ�
            FileModifyManager.fileName=fileid;
            //�ٴο�����������
             leftBytes=ff.length()-sw.getFile();
             offset=copyData(read,sw,offset,leftBytes,0);
        }
        //��������
        //������������
        //�޸��ļ���
        FileModifyManager.hashindex.putAll(getIndex(fileid));
        ff.delete();
        File newFile=new File(tmpfile);
        newFile.renameTo(ff);
        data.clear();
        //
        FileModifyManager.fileName="";
    }
    //�ٴα�����������
    Iterator<Entry<String, ArrayList<DataDeleteIndex>>> it = hash.entrySet().iterator();  
    while(it.hasNext()){  
        Entry<String, ArrayList<DataDeleteIndex>> entry=it.next();  
        if(entry.getValue().isEmpty())
        {
            it.remove();
        }
    }  
    
}
   
  /*
   * ��������
   */
  
private long copyData(FileRead frd,FileWrite fsw,long position,long num,int len)
{
    byte[]data=null;
    
    if(num<maxCopy)
    {
       data= frd.read(position,(int) num);
       fsw.writeFile(data);
       position+=num;
    }
    else
    {
        long count=num/maxCopy;
        long left=num%maxCopy;
        for(int i=0;i<count;i++)
        {
            data= frd.read(position, maxCopy);
            fsw.writeFile(data);
            position+=maxCopy;
        }
        if(left>0)
        {
            data= frd.read(position,(int) left);
            fsw.writeFile(data);
            position+=left;
        }
    }
    position+=len;
    return position;
    
}

/*
 * ����
 */
private DataDeleteIndex[] sortIndex( ArrayList<DataDeleteIndex> record)
{
    //��򵥵�ð������
    DataDeleteIndex[] tmp=new DataDeleteIndex[record.size()];
    for (int i = 0; i < tmp.length -1; i++){    //�����n-1������
            for(int j = 0 ;j < tmp.length - i - 1; j++){    //�Ե�ǰ��������score[0......length-i-1]��������(j�ķ�Χ�ܹؼ��������Χ��������С��)
             if(tmp[j].position > tmp[j + 1].position){    //��С��ֵ����������
                 DataDeleteIndex temp = tmp[j];
                       tmp[j] = tmp[j + 1];
                           tmp[j + 1] = temp;
                    }
                 }            
       }
    return tmp;
}

/*
 * ��ȡ���µ�index
 */
private  HashMap<String,FileIndex<String>>  getIndex(String fileid)
{
    HashMap<String,FileIndex<String>> hash=new HashMap<String,FileIndex<String>>();
    String sql="selest * from indexFile where fileid='"+fileid+"'";
    ResultSet rs= FileDBManager.getObj().exeSelect(sql);
    FileIndex<String> tmp=new FileIndex<String>();
    tmp.fileid=fileid;
    try {
        tmp.key=rs.getString(2);
        tmp.position=rs.getLong(4);
        tmp.len=rs.getInt(5);
        hash.put(tmp.key, tmp);
    } catch (SQLException e) {
     
        e.printStackTrace();
    }
   return hash;
}
}
