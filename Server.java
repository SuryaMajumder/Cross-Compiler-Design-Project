import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

class Server 
{
    private ServerSocket serversocket;
    static boolean isWindows = false;

    public Server(int port) throws IOException 
    {
        serversocket = new ServerSocket(port);
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        if (osName.startsWith("Windows") && (osArch.endsWith("64") || osArch.endsWith("86"))) 
        {
            isWindows = true;
        }
    }

    public void connect() 
    {
        System.out.println("Server started on port " + serversocket.getLocalPort() + "...");
        
        while (true) 
        {
            try 
            {
                Socket clientsocket = serversocket.accept();
                System.out.println("Client accepted from " + clientsocket.getRemoteSocketAddress());
                Thread obj = new Thread(() -> compile(clientsocket));
                obj.start();
            } 
            catch (Exception e) 
            {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private void compile(Socket clientsocket)
    {
        try
        {
            final DataInputStream in = new DataInputStream(new BufferedInputStream(clientsocket.getInputStream()));
            final PrintWriter out = new PrintWriter(clientsocket.getOutputStream(), true);
            
            try
            {
                final String filename = in.readUTF();
                final String code = in.readUTF();
                if(filename.endsWith(".java"))
                {
                    Thread thread = new Thread(() -> {
                        try 
                        {
                            compileJava(code, filename, in, out);
                        } 
                        catch (IOException | InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                }
                else if(filename.endsWith(".c"))
                {
                    Thread thread = new Thread(() -> {
                        try 
                        {
                            compileC(code, filename, in, out);
                        } 
                        catch (IOException | InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                }
                else if(filename.endsWith(".cpp"))
                {
                    Thread thread = new Thread(() -> {
                        try 
                        {
                            compileCpp(code, filename, in, out);
                        } 
                        catch (IOException | InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                }
                else if(filename.endsWith(".py"))
                {
                    Thread thread = new Thread(() -> {
                        try 
                        {
                            executePy(code, filename, in, out);
                        } 
                        catch (IOException | InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                }
                else
                {
                    Thread thread = new Thread(() -> {
                        try 
                        {
                            compileJava(code, filename, in, out);
                        } 
                        catch (IOException | InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                }
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
         
        finally 
        {
            if (clientsocket != null) 
            {
                try 
                {
                    clientsocket.close();
                    System.out.println("Closing connection");
                } 
                catch (IOException e) 
                {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
    
    private static void compileJava(String code, String fName, DataInputStream in, PrintWriter out) throws IOException, InterruptedException 
    {
        
        // creating directory
        String dirName = "javafiles_" + fName;
        File dir = new File(dirName);
        dir.mkdir();
        
        // creating file
        String fileName = fName;
        File file = new File(dir, fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
        
        // compiling file
        ProcessBuilder processBuilder = new ProcessBuilder("javac", fileName);
        processBuilder.directory(dir);
        Process process = processBuilder.start();
        process.waitFor();
        
        // reading compilation message
        /* String compilationMessage = new String(process.getErrorStream().readAllBytes());   // From Java 9 
                or we can use the below buffered reader version with the while loop */
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line, compilationMessage="";
        while ((line = reader.readLine()) != null) 
        {
            compilationMessage+=line+"\n";
        }
        if (compilationMessage.isEmpty()) 
        {
            System.out.println("Successfully compiled .java file");
            
            // running the code
            processBuilder = new ProcessBuilder("java", fName.substring(0, fName.length()-5));
            processBuilder.directory(dir);
            process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            PrintWriter input = new PrintWriter(process.getOutputStream(), true);
            
            while ((line = reader.readLine()) != null) 
            {
                if (line.contains("Enter")) 
                { // If the code asks for an input
                    out.println(line); // Pass the input prompt to the client
                    String userInput = in.readLine(); // Get the input from the client
                    input.println(userInput); // Pass the input back to the server execution
                } 
                else 
                {
                    out.println(line+"\n");
                }
            }
            process.waitFor();
            
            // Checking if there are any errors
            String error = "";
            while ((line = errorReader.readLine()) != null) 
            {
                error += line + "\n";
            }
            
            if (error.isEmpty()) 
            {
                System.out.println("Successfully Executed");
            } 
            else 
            {
                out.println("Error:\n" + error);
            }
        } 
        else 
        {
            out.println("Compilation error: \n" + compilationMessage);
        }
        
        // deleting directory and its contents
        deleteDirectory(dir);
    }
    
    private static void compileC(String code, String fileName, DataInputStream in, PrintWriter out) throws IOException, InterruptedException 
    {
        // creating directory
        String dirName = "cfiles_" + fileName;
        File dir = new File(dirName);
        dir.mkdir();
        
        // creating file
        File file = new File(dir, fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
        
        // compiling file
        ProcessBuilder processBuilder = new ProcessBuilder("gcc", fileName);
        processBuilder.directory(dir);
        Process process = processBuilder.start();
        process.waitFor();
        
        // reading compilation message
        /* String compilationMessage = new String(process.getErrorStream().readAllBytes());   // From Java 9 
                or we can use the below buffered reader version with the while loop */
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line, compilationMessage="";
        while ((line = reader.readLine()) != null) 
        {
            compilationMessage+=line+"\n";
        }
        if (compilationMessage.isEmpty()) 
        {
            System.out.println("Successfully compiled .c file");
            
            // running the code
            if (isWindows)
            {
                processBuilder = new ProcessBuilder(dirName+"/a.exe");
            }
            else
            {
                processBuilder = new ProcessBuilder("./a.out");
            }
            processBuilder.directory(dir);
            process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            PrintWriter input = new PrintWriter(process.getOutputStream(), true);
            
            while ((line = reader.readLine()) != null) 
            {
                if (line.contains("Enter")) 
                { // If the code asks for an input
                    out.println(line); // Pass the input prompt to the client
                    String userInput = in.readLine(); // Get the input from the client
                    input.println(userInput); // Pass the input back to the server execution
                } 
                else 
                {
                    out.println(line+"\n");
                }
            }
            process.waitFor();
            
            // Checking if there are any errors
            String error = "";
            while ((line = errorReader.readLine()) != null) 
            {
                error += line + "\n";
            }
            
            if (error.isEmpty()) 
            {
                System.out.println("Successfully Executed");
            } 
            else 
            {
                out.println("Error:\n" + error);
            }
        } 
        else 
        {
            out.println("Compilation error: \n" + compilationMessage);
        }
        
        // deleting directory and its contents
        deleteDirectory(dir);
    }
    
    private static void compileCpp(String code, String fileName, DataInputStream in, PrintWriter out) throws IOException, InterruptedException 
    {
        // creating directory
        String dirName = "cppfiles_" + fileName;
        File dir = new File(dirName);
        dir.mkdir();
        
        // creating file
        File file = new File(dir, fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
        
        // compiling file
        ProcessBuilder processBuilder = new ProcessBuilder("g++", fileName);
        processBuilder.directory(dir);
        Process process = processBuilder.start();
        process.waitFor();
        
        // reading compilation message
        /* String compilationMessage = new String(process.getErrorStream().readAllBytes());   // From Java 9 
                or we can use the below buffered reader version with the while loop */
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line, compilationMessage="";
        while ((line = reader.readLine()) != null) 
        {
            compilationMessage+=line+"\n";
        } 
        if (compilationMessage.isEmpty()) 
        {
            System.out.println("Successfully compiled .cpp file");
            
            // running the code
            if (isWindows)
            {
                processBuilder = new ProcessBuilder(dirName+"/a.exe");
            }
            else
            {
                processBuilder = new ProcessBuilder("./a.out");
            }
            processBuilder.directory(dir);
            process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            PrintWriter input = new PrintWriter(process.getOutputStream(), true);
            
            while ((line = reader.readLine()) != null) 
            {
                if (line.contains("Enter")) 
                { // If the code asks for an input
                    out.println(line); // Pass the input prompt to the client
                    String userInput = in.readLine(); // Get the input from the client
                    input.println(userInput); // Pass the input back to the server execution
                } 
                else 
                {
                    out.println(line+"\n");
                }
            }
            process.waitFor();
            
            // Check if there are any errors
            String error = "";
            while ((line = errorReader.readLine()) != null) 
            {
                error += line + "\n";
            }
            
            if (error.isEmpty()) 
            {
                System.out.println("Successfully Executed");
            } 
            else 
            {
                out.println("Error:\n" + error);
            }
        } 
        else 
        {
            out.println("Compilation error: \n" + compilationMessage);
        }
        
        // deleting directory and its contents
        deleteDirectory(dir);
    }
    
    private static void executePy(String code, String fileName, DataInputStream in, PrintWriter out) throws IOException, InterruptedException 
    {
        // creating directory
        String dirName = "pyfiles_" + fileName;
        File dir = new File(dirName);
        dir.mkdir();
        
        // creating file
        File file = new File(dir, fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
        
        // running the code
        String line;
        ProcessBuilder processBuilder = new ProcessBuilder("py", fileName);
        processBuilder.directory(dir);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        PrintWriter input = new PrintWriter(process.getOutputStream(), true);
    
        while ((line = reader.readLine()) != null) 
        {
            if (line.contains("Enter")) 
            { // If the code asks for an input
                out.println(line); // Pass the input prompt to the client
                String userInput = in.readLine(); // Get the input from the client
                input.println(userInput); // Pass the input back to the server execution
            } 
            else 
            {
                out.println(line+"\n");
            }
        }
        process.waitFor();
        
        // Check if there are any errors
        String error = "";
        while ((line = errorReader.readLine()) != null) 
        {
            error += line + "\n";
        }
        
        if (error.isEmpty()) 
        {
            System.out.println("Successfully Executed");
        } 
        else 
        {
            out.println("Error:\n" + error);
        }
        
        // deleting directory and its contents
        deleteDirectory(dir);
    }
    
    private static void deleteDirectory(File dir) 
    {
        if (dir.isDirectory()) 
        {
            File[] files = dir.listFiles();
            for (File file : files) 
            {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }

    public void stop() throws IOException 
    {
        serversocket.close();
    }

    public static void main(String[] args) 
    {
        try 
        {
            Server server = new Server(5000);
            server.connect();
        } 
        catch (IOException e) 
        {
            System.err.println("Error starting concurrent TCP server: " + e.getMessage());
        }
    }
}