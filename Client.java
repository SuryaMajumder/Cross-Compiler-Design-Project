import java.io.*;
import java.net.*;
 
public class Client 
{
    // initializing socket and input output streams
    Socket socket = null;
    DataInputStream inp = null;
    DataOutputStream out = null;
 
    // constructor to put ip address and port
    public Client(String address, int port, String filename, String code)
    {
        // establishing a connection
        try 
        {
            socket = new Socket(address, port);
            System.out.println("Connected");
            
            inp = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch (UnknownHostException u) 
        {
            System.out.println(u);
            return;
        }
        catch (IOException i) 
        {
            System.out.println(i);
            return;
        }
        
        try 
        {
            out.writeUTF(filename);
            out.writeUTF(code);
            
            String line;
            while ((line = inp.readLine()) != null) 
            {
                if (line.contains("Enter")) 
                {
                    System.out.println(line);
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    String userInput = br.readLine();
                    output.println(userInput);
                } 
                else 
                {
                    System.out.println(line);
                }
            }
        }
        catch (IOException i) 
        {
            System.out.println(i);
        }
        
        // closing the connection
        try 
        {
            //inp.close();
            out.close();
            socket.close();
        }
        catch (IOException i) 
        {
            System.out.println(i);
        }
    }
}