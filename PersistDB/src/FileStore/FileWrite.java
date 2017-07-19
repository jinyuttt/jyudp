/**    
 * 文件名：FileWrite.java    
 *    
 * 版本信息：    
 * 日期：2017年7月19日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：FileWrite    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年7月19日 下午8:47:17    
 * 修改人：jinyu    
 * 修改时间：2017年7月19日 下午8:47:17    
 * 修改备注：    
 * @version     
 *     
 */
public class FileWrite {
 String path="";
 boolean append=true;
 private long fileMax=2*1024*1024*1024;//2G字节
public long writeSmall(byte[]data)
{
    File fout=new File(path);
    FileOutputStream fs=null;
    long len=0;
    try {
        fs=new FileOutputStream(fout,append);
        BufferedOutputStream buffer=new BufferedOutputStream(fs);
        buffer.write(data);
        len=fs.getChannel().position();
        buffer.close();
        fs.close();
    } catch (IOException e) {
      
        e.printStackTrace();
    }
    return len;
}
@SuppressWarnings("resource")
public long write(byte[]data)
{
    File fout=new File(path);
    if(fout.isDirectory())
    {
        return -1;
    }
    MappedByteBuffer outputBuf=null;
    try {
        outputBuf=new RandomAccessFile(fout,"rw").getChannel().map(FileChannel.MapMode.READ_WRITE, fout.length(),data.length);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    outputBuf.put(data);
  
    outputBuf.clear();
    outputBuf=null;
    System.gc();
    return fout.length()+data.length;
}
public long writeFile(byte[]data)
{
    File f=new File(path);
    if(f.isDirectory())
    {
        return -1;
    }
    if(f.exists())
    {
       if(!append)
       {
          f.delete();
          try {
            f.createNewFile();
        } catch (IOException e) {
          
            e.printStackTrace();
        }
       }
    }
    if(f.length()<=fileMax)
    {
       return writeSmall(data);
    }
    else
    {
      return  write(data);
    }
}
public long getFile()
{
    File f=new File(path);
    if(f.isDirectory())
    {
        return -1;
    }
    if(f.exists())
    {
        return f.length();
    }
    else
    {
        return 0;
    }
}
}
