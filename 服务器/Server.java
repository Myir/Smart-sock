import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter; 
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.io.PrintWriter;
import java.util.TimerTask;
import java.io.PrintStream;
import java.io.DataOutputStream;  

public class Server {
    public static void main(String args[])throws Exception {
        System.out.println("Server On��");
        Thread db = new DataBase();
        db.start();
        Thread ad = new android();
        ad.start();
        Thread sc = new sock();
        sc.start();
    }
}

class DataBase extends Thread {
    public void run()
    {
        try {
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new DB_insert(), 60000, 15000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DB_insert extends TimerTask {
    public void run()
    {
        Connection conn = null;
        Statement stmt = null;
        String connectionString = "jdbc:mysql://localhost:3306/sockma?user=root&password=123456";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(connectionString);
            stmt = conn.createStatement();
            String sql = "INSERT INTO POWERX(TIMEx,VOLTAGE,CURRENT) VALUES(\""+Timer.get_time()+"\",\""+ Power.get_voltage()+"\",\""+Power.get_current()+"\")";
            int i = stmt.executeUpdate(sql);
            if (i > 0) {
                System.out.println("�ɹ��������ݿ�!");
            } else {
                System.out.println("may failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



class android extends Thread {
    public void run() {
        try{
            ServerSocket server = new ServerSocket(13450);
            while (true) {
                Multi_android mc = new Multi_android(server.accept());
               // System.out.println("App ����");
                mc.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

class Multi_android extends Thread {
    private Socket client;

    public Multi_android(Socket c) {
        this.client = c;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String str = in.readLine();
                if(str==null)
                {
                    str="0";
                    continue;
                }
                int result = Integer.parseInt(str);
                if (result==1) {
                    System.out.println("�յ�App����\n");
                    Multi_sock.turn_on();
                }
                else if (result==2) {
                    System.out.println("�յ�App����\n");
                    Multi_sock.turn_off();
                }
            }
        } catch (IOException ex) {
        }
    }
}

 class sock extends Thread{
     public void run() {
         try{
             ServerSocket server = new ServerSocket(11000);
             while (true) {
                 Multi_sock ms = new Multi_sock(server.accept());
                 //System.out.println("Sock ����");
                 ms.start();
             }
         }catch(IOException e){
             e.printStackTrace();
         }
     }
 }

class Multi_sock extends Thread
{
    private  static Socket client;
    private static PrintWriter out;
    public Multi_sock(Socket c)throws IOException
    {
        this.client=c;
    }
    public void run()
    {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
            while(true) {
                String str = in.readLine();
                System.out.println("Get message from sock��"+str);
                int open,current,voltage;
                int i = Integer.parseInt(str);
                open = i%10;
                if(open==1) {
                   // System.out.println("������Դ");
                    i=i/10;
                    i=i/10;
                    current =i%1000;
                    //System.out.print("����"+current);
                    i=i/1000;
                    voltage=i;
                    //System.out.println("��ѹ"+voltage);
                    Power.add_power(current,voltage);
                }
                else
                {
                   // System.out.println("�رյ�Դ");
                }
            }
            } catch (Exception e) {
                System.out.println("�����ѶϿ�����");
                client=null;
            }
    }
    public static void turn_on()throws IOException
    {
        if(client==null)
        {
            //System.out.println("����δ����\n");
        }
        else{
            //System.out.println("���ڿ���������Դ");
            out.println("1");
            System.out.println("������Դ�ѿ���\n");
            out.flush();
            //DataOutputStream out = new DataOutputStream(client.getOutputStream()); 
            //out.writeUTF("1");          
            //out.close();    
            
        }
    }
    public static void turn_off()throws IOException
    {
        if(client==null)
        {
            //System.out.println("����δ����\n");
        }
        else{
            //System.out.println("���ڹرղ�����Դ");
            out.println("2");
            System.out.println("������Դ�ѹر�\n");
            out.flush();
        }
    }
}

class Power
{
    private static int  power_today;
    private static int voltage_x;
    private static int current_x;
    public static void add_power(int ma,int v)
    {
        power_today=power_today+(ma*v);
        voltage_x=v;
        current_x=ma;
        //System.out.println("�����õ����Ѿ�����");
    }
    public static int get_voltage()
    {
        //int power_yesterday=power_today/10;
        //power_today=0;
        int voltage_xn=voltage_x;
        return voltage_xn;
        //int current_xn=current_x;
        
    }
   public static int get_current()
    {
        int current_xn=current_x;
        return current_xn;
     }
}

class Timer {
    public static String get_time() throws ParseException {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }
}