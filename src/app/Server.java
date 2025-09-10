package app; //Em Java, pacotes organizam classes em namespaces para evitar conflitos de nomes e estruturar projetos
import java.io.*; // Entrada e saída
import java.net.*; // Usar Sockets
import java.util.*; // armazenar jogadores

//Classe com a lógica do Servidor:
public class Server {
    private static char[][] =  {{" ", " ", " "}, {" ", " ", " "}, {" ", " ", " "}};
    private static List<PrintWriter>; players = new ArrayList<>(); // Lista de players PrintWriter eu consigo mandar mensagem à ambos.
    private static char[] symbols = {'X', 'O'};
    private static int turn = 0; // 0   zzpara X, 1 para O
    public static void main(String args){
        int port = 5000;
       try (ServerSocket servidor = new ServerSocket(port)){ // Abre servidor na porta 5000, fecha automaticamete devido ao try.
            System.out.println("Servidor aguardando conexão..."); //Manda atualização sobre o estado atual ao user.
            try (Socket socket = servidor.accept()){ //Chama accet no server socket apenas quando um user for conectado.
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Buffer para ler as jogadas enviadas pelo cliente
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Indicar que o Client X ou O pode fazer sua jogada;
                 BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) { // Receber inpout teclado;

                System.out.println("Cliente conectado!");
                
                }        
            }

        }
    }
}