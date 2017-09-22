import java.io.*; //Importado porque precisamos das classes InputStream e OutputStream
import java.net.*; // Importado porque a classe Socket é necessária
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
 
public class Cliente {
    
    public static void main(String args[]) throws Exception {  
 
        // A porta padrão    
        int clientport = 7777;
        String host = "localhost";
 
        if (args.length < 1) {
           System.out.println("Em uso: UDPClient " + "agora está usando o servidor = " + host + ", porta# = " + clientport);
        } 
        
        // Pega o número da porta para usar nas linhas de comando
        else {      
           //host = args[0];
           clientport = Integer.valueOf(args[0]).intValue();
           System.out.println("Em uso: UDPClient " + "agora está usando a porta = " + host + ", porta# = " + clientport);
        } 
 
        // Pega o endereço IP da máquina local - usaremos isso como endereço para enviar os dados para seu destino
        InetAddress ia = InetAddress.getByName(host);
 
        SenderThread sender = new SenderThread(ia, clientport);
        sender.start();
        ReceiverThread receiver = new ReceiverThread(sender.getSocket());
        receiver.start();
    }
}      
 
class SenderThread extends Thread {
 
    private InetAddress serverIPAddress;
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
    private int serverport;
 
    public SenderThread(InetAddress address, int serverport) throws SocketException {
        this.serverIPAddress = address;
        this.serverport = serverport;
        // Cria um DatagramSocket para o cliente
        this.udpClientSocket = new DatagramSocket();
        this.udpClientSocket.connect(serverIPAddress, serverport);
    }
    public void halt() {
        this.stopped = true;
    }
    public DatagramSocket getSocket() {
        return this.udpClientSocket;
    }
 
    public void run() {       
        String nomeUsuario;
                
                //comandos 
                String quit = "/quit";
                Scanner sc = new Scanner(System.in);
                System.out.println("Digite seu nick: ");
                nomeUsuario = sc.nextLine();
                
        
        try {    
        	//Envia uma mensagem em branco
        	byte[] data = new byte[1024];
        	data = "".getBytes();
        	DatagramPacket blankPacket = new DatagramPacket(data,data.length , serverIPAddress, serverport);
            udpClientSocket.send(blankPacket);
            
                // Cria um inputstream (Stream de entrada)
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            while (true) 
            {
                if (stopped)
                    return;
 
                // Mensagem para enviar
                String clientMessage = inFromUser.readLine();
 
                if (clientMessage.equals("."))
                    break;
 
                // Cria um buffer de bytes para conter a mensagem à enviar
                byte[] sendData = new byte[1024];
                
                // bota essa mensagem dentro do buffer/array de bytes vazio
                sendData = criarString(nomeUsuario, clientMessage);
 
                // Cria um DatagramPacket com o dado, endereço IP e número da porta
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport);
                if (clientMessage.equals(quit)){
                    udpClientSocket.send(sendPacket);
                    System.out.println("Desligando sistema");
                    System.exit(0);
                } else {
                // Envia o pacote UDP ao server
                System.out.println("["+horaAtual()+"] "+nomeUsuario+" diz: "+clientMessage);
                }
                udpClientSocket.send(sendPacket);
 
                Thread.yield();
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    public static byte[] criarString(String nomeUsuario, String mensagem) {
        byte[] mensagemConsole = new byte[1024];
        String quit = "/quit";
        /*
         * Montando uma string buffer para conter a mensagem
         */
        StringBuffer stringMensagem = new StringBuffer();
        if (mensagem.equals(quit)) {     
            stringMensagem.append("[");
            stringMensagem.append(horaAtual());
            stringMensagem.append("] ");
            stringMensagem.append(nomeUsuario);
            stringMensagem.append(" saiu da sala!");
        } else {
        stringMensagem.append("[");
        stringMensagem.append(horaAtual());
        stringMensagem.append("] ");
        stringMensagem.append(nomeUsuario);
        stringMensagem.append(" diz: ");
        stringMensagem.append(mensagem.toString());
        
        }
 
        mensagemConsole = stringMensagem.toString().getBytes();
 
        return mensagemConsole;
    }
    
     public static String horaAtual() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}   
 
class ReceiverThread extends Thread {
 
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
 
    public ReceiverThread(DatagramSocket ds) throws SocketException {
        this.udpClientSocket = ds;
    }
 
    public void halt() {
        this.stopped = true;
    }
 
    public void run() {

        // Cria um array/buffer de bytes para receber o Datagram packet
        byte[] receiveData = new byte[1024];
        String vazio = "";
 
        while (true) {            
            if (stopped)
            return;
 
            // Configura o DatagramPacket para receber os dados 
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                // Recebe o pacote do servidor (bloqueia até que os pacotes sejam recebidos)
                udpClientSocket.receive(receivePacket);

                // Extrai a resposta do DatagramPacket
                String serverReply =  new String(receivePacket.getData(), 0, receivePacket.getLength());
                // Se a mensagem retornada pelo servidor estiver vazia, significa que um  cliente entrou na sala
                if (serverReply.equals(vazio)) {
                    System.out.println("Um cliente entrou na sala");
                } else {
                    // Do contrário, a mensagem é exibida para todos
                    System.out.println(serverReply + "\n");
                }
                
                
 
                Thread.yield();
            } 
            catch (IOException ex) {
            System.err.println(ex);
            }
        }
    }
    
}