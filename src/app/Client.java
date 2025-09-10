package app; //Em Java, pacotes organizam classes em namespaces para evitar conflitos de nomes e estruturar projetos

import java.io.*; // Entrada e saída
import java.net.*; // Usar Sockets
import java.util.*; // Para Scanner, ler entrada do usuário

//Classe com a lógica do Cliente:
public class Client {
   public static void main(String[] args) { //Ponto de entrada do programa
        String host = "localhost"; //Servidor roda na mesma máquina
        int port = 5000; //Porta do servidor
        try (Socket socket = new Socket(host, port)) { //Conecta ao servidor na porta 5000, fecha automaticamente devido ao try
            System.out.println("Conectado ao servidor!"); //Feedback de conexão
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Leitura de mensagens do servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //Envio de mensagens ao servidor
            Scanner scanner = new Scanner(System.in); //Leitura de entrada do usuário (jogadas)

            while (true) { //Loop principal do cliente
                String mensagem = in.readLine(); //Lê mensagem do servidor
                if (mensagem == null) { //Se servidor desconectar
                    System.out.println("Servidor desconectado.");
                    break;
                }
                System.out.println(mensagem); //Mostra mensagem do servidor (ex.: tabuleiro, "Sua vez!", "Fim de jogo!")
                if (mensagem.startsWith("Sua vez!")) { //Se for a vez do jogador
                    System.out.print("Digite sua jogada (linha,coluna ex: 0,0): "); //Pede jogada ao usuário
                    String jogada = scanner.nextLine(); //Lê jogada do usuário
                    out.println(jogada); //Envia jogada ao servidor
                } else if (mensagem.startsWith("Fim de jogo!")) { //Se jogo terminou
                    break; //Sai do loop
                }
            }
            scanner.close(); //Fecha o Scanner
        } catch (IOException e) {
            e.printStackTrace(); //Mostra erros de conexão ou I/O
        }
    }
}