
import java.net.*; // importado pq a classe do Socket é necessária
import java.util.HashSet;

public class Servidor {

    private static HashSet<Integer> portSet = new HashSet<Integer>();

    public static void main(String args[]) throws Exception {

        // porta padrão    
        int serverport = 7777;

        if (args.length < 1) {
            System.out.println("Em uso: UDPServer " + "agora está usando a porta:# = " + serverport);
        } // pega o número da porta e o host para usar nas linhas de comando
        else {
            serverport = Integer.valueOf(args[0]).intValue();
            System.out.println("Em uso: UDPServer " + "agora está usando a porta:# = " + serverport);
        }

        // Abre um novo socket datagrama na porta específica
        DatagramSocket udpServerSocket = new DatagramSocket(serverport);

        System.out.println("Servidor iniciado!\n");

        while (true) {
            // Cria buffers de byte para pegar as mensagens para enviar e receber
            byte[] receiveData = new byte[1024];

            // Cria um pacote DatagramPacket vazio
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Bloqueia até hourver um pacote a ser recebido, então recebe-o (dentro do nosso pacote vazio)
            udpServerSocket.receive(receivePacket);

            // Extrai a mensagem do pacote e coloca em uma string, então executa um método TRIM()
            String clientMessage = (new String(receivePacket.getData())).trim();

            // Retorna mensagens de status
            System.out.println("Cliente Conectado - Endereço do Socket: " + receivePacket.getSocketAddress());
            
            //Variável criada para comparar a mensagem do cliente com ela, caso as duas sejam "/quit" o cliente é desconectado do chat.
            String quit = "/quit";
            
            //Variável criada com 140 caracteres para ser comparada com a mensagem do cliente e poder utilizá-la como um limitador
            String maximo = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
            long tamanho = maximo.length();
            long tamanhocliente = clientMessage.length();
            
            //if's para comparar as strings, como citado acima
            if (clientMessage.equals(quit)) {
                System.out.println("Um cliente quer sair");
            //.length() é utilizado para conter a quantidade de caracteres de uma variável
            } else if (clientMessage.length() > maximo.length()) {
                System.out.println("Mensagem grande demais");
            } else {
                System.out.println("Mensagem do cliente: \"" + clientMessage + "\"");
            }
            // Pega o endereço de IP e o número da porta da qual veio a conexão recebida
            InetAddress clientIP = receivePacket.getAddress();

            // retorna uma mensagem de status
            System.out.println("Endereço IP e Hostname do cliente: " + clientIP + ", " + clientIP.getHostName() + "\n");

            // Pega o número da porta da qual a veio a conexão recebida
            int clientport = receivePacket.getPort();
            System.out.println("Adicionando " + clientport);
            portSet.add(clientport);

            // Mensagem de resposta         
            String returnMessage = clientMessage;
            System.out.println(returnMessage);
            // Cria um buffer/array de bytes vazio para enviar de volta
            byte[] sendData = new byte[1024];

            // Atribui a mensagem para o buffer de envio
            sendData = returnMessage.getBytes();

            for (Integer port : portSet) {
                //System.out.println(port != clientport);
                // Essa verificação faz com que o servidor redirecione as mensagens a todos os clientes
                // menos o próprio emissor
                if (port != clientport) {
                    // Cria um DatagramPacket para enviar, usando o buffer, o endereço ip dos clientes, e a porta dos clientes
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);
                    System.out.println("porta:" + port + " e porta do cliente: " + clientport);
                    System.out.println("Enviado");
                    // Envia uma mensagem ecoada
                    udpServerSocket.send(sendPacket);
                }
            }
        }
    }
}
