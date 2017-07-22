/**    
 * 文件名：FileModify.java    
 *    
 * 版本信息：    
 * 日期：2017年7月21日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
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
 * 项目名称：PersistDB    
 * 类名称：FileModify    
 * 类描述：    启动文件处理 
 * 创建人：jinyu    
 * 创建时间：2017年7月21日 下午9:59:30    
 * 修改人：jinyu    
 * 修改时间：2017年7月21日 下午9:59:30    
 * 修改备注：    
 * @version     
 *     
 */
public class FileModify {
    private  final int maxCopy=100*1204*1024;//100M
   private final long maxSize=1*1024*1024*1024;//到达
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
          //满足总数1G开始清理文件
          sum=0;
          startThread();
      }
   }
  
  /*
   * 启动线程
   */
   private void startThread()
   {
       cachedThreadPool.execute(new Runnable() {

        @Override
        public void run() {
            Thread.currentThread().setName("整理文件");
            start();
        }
           
       });
   }
  /*
   * 启动数据处理
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
           //修改后面的数据
           FileDBManager.getObj().exeSql(sql);
        }
        //拷贝剩余数据
        File f=new File(fileid);
        long leftBytes=f.length()-offset;
        offset=copyData(read,sw,offset,leftBytes,0);
       
        //再次比较数据
        File ff=new File(fileid);
        
        if(ff.length()!=sw.getFile())
        {
            //不再写入该文件
            FileModifyManager.fileName=fileid;
            //再次拷贝所有数据
             leftBytes=ff.length()-sw.getFile();
             offset=copyData(read,sw,offset,leftBytes,0);
        }
        //更新索引
        //产生索引数据
        //修改文件；
        FileModifyManager.hashindex.putAll(getIndex(fileid));
        ff.delete();
        File newFile=new File(tmpfile);
        newFile.renameTo(ff);
        data.clear();
        //
        FileModifyManager.fileName="";
    }
    //再次遍历清理数据
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
   * 拷贝数据
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
 * 排序
 */
private DataDeleteIndex[] sortIndex( ArrayList<DataDeleteIndex> record)
{
    //最简单的冒泡排序
    DataDeleteIndex[] tmp=new DataDeleteIndex[record.size()];
    for (int i = 0; i < tmp.length -1; i++){    //最多做n-1趟排序
            for(int j = 0 ;j < tmp.length - i - 1; j++){    //对当前无序区间score[0......length-i-1]进行排序(j的范围很关键，这个范围是在逐步缩小的)
             if(tmp[j].position > tmp[j + 1].position){    //把小的值交换到后面
                 DataDeleteIndex temp = tmp[j];
                       tmp[j] = tmp[j + 1];
                           tmp[j + 1] = temp;
                    }
                 }            
       }
    return tmp;
}

/*
 * 获取最新的index
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
