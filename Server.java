import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server extends Thread {
    private int port;
    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        //Для первого порта (клиента)
        BufferedReader in1 = null;
        PrintWriter out1= null;
        //Для второго порта (клиента)
        BufferedReader in2 = null;
        PrintWriter out2= null;
        //Создаем два сокета для приема и перенаправления
        ServerSocket serverSocket = null;
        Socket socketAccept = null;
        try {
            serverSocket = new ServerSocket(port);
            int n = 0;
            HashMap<Integer, Socket> mapClients = new HashMap<>();


            while(true) {
                socketAccept = serverSocket.accept();
                if (!mapClients.containsValue(socketAccept)) {
                    n += 1;
                    mapClients.put(n, socketAccept);
                }
                System.out.println("A client is connected to the server: " + socketAccept);
                if (n == 2) {
                    System.out.println("Number of customers exceeded\t:(");
                    break;
                }
            }

            in1 = new BufferedReader(new InputStreamReader(mapClients.get(1).getInputStream()));
            out1 = new PrintWriter(mapClients.get(2).getOutputStream(), true);

            in2 = new BufferedReader(new InputStreamReader(mapClients.get(2).getInputStream()));
            out2 = new PrintWriter(mapClients.get(1).getOutputStream(), true);

            String input1;
            String input2;
            while (true) {
                input1 = in1.readLine();
                input2 = in2.readLine();
                System.out.println(input1);
                if ((input1.contains("exit")) || (input2.contains("exit"))) {
                    System.out.println("Shutting down...");
                    break;
                }

                if (input1 != null) {
                    System.out.println(input1);
                    out1.println(input1);
                }
                if (input2 != null) {
                    out2.println(input2);
                    System.out.println(input2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out2.close();
                out1.close();
                in2.close();
                in1.close();
                serverSocket.close();
            } catch (IOException ex) {}
        }
    }

    public static void main(String[] args) {
        Thread globalServer = new Server(2000);
        globalServer.start();
    }
}
