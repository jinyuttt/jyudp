package Client;


import java.util.concurrent.TimeUnit;

import NetProtocol.judpClient;

public class TestClient {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
while(true)
{
  // System.out.println("�����룺");
    int num=100;
    while(num>0)
    {
   StringBuffer  str=new StringBuffer();
   str.append("sjfhslghsdjsdfgsgsdg");

//    char ch='0';
//    while(ch!='\n') {
//      ch = (char) System.in.read();
//      str.append(ch);
//    }
    judpClient client=new judpClient();
    byte[]senddata=str.toString().trim().getBytes();
    client.sendData("192.168.3.139", 5555, senddata);
    }
   // System.out.println("���ͣ�"+str.toString().trim());
  try {
    TimeUnit.SECONDS.sleep(2);
} catch (InterruptedException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
   
}
    }

}
