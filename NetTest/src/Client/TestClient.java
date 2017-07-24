package Client;


import java.util.concurrent.TimeUnit;

import NetProtocol.judpClient;

public class TestClient {

    public static void main(String[] args) {
       long id=-1;
       long sum=0;
while(true)
{

   int num=100;
   while(num>0)
    {
     StringBuffer  str=new StringBuffer();
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");  
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append("sjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdgsjfhslghsdjsdfgsgsdg");
     str.append(id);
    judpClient client=new judpClient();
    byte[]senddata=str.toString().trim().getBytes();
    client.sendData("192.168.3.139", 5555, senddata);
    //client.close();
    id++;
    sum+=senddata.length;
    num--;
   }

  try {
      System.out.println("senddata:"+sum);
    TimeUnit.SECONDS.sleep(60);
} catch (InterruptedException e) {
    e.printStackTrace();
}
   
}
    }

}
