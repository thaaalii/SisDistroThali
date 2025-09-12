package app;

import java.io.*; // Entrada e saída
import java.net.*; // Usar Sockets
import java.util.*; // armazenar jogadores

//Classe com a lógica do Servidor:
public class Server {
    private static char[][] tabuleiro =  {{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};
    private static List<PrintWriter> players = new ArrayList<>(); // Lista de players PrintWriter eu consigo mandar mensagem à ambos.
    private static char[] simbolos = {'X', 'O'};
    private static int turn = 0; // 0 para X, 1 para O
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args){
        int port = 5000;
        try (ServerSocket servidor = new ServerSocket(port)){ // Abre servidor na porta 5000, fecha automaticamete devido ao try.
            System.out.println("Servidor aguardando conexão..."); //Manda atualização sobre o estado atual ao user.
            //Aceitar dois Clientes:
            for(int i = 0; i < 2; i++){
                Socket socket = servidor.accept();
                System.out.println("Jogador " + (i + 1) + " conectado!"); //feedback
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Indicar que o Client X ou O pode fazer sua jogada;
                players.add(out); //Permite que o servidor mande msg para ambos os jogadores quando necessario.
                Thread playerThread = new Thread(new Gerente(socket, i)); //Gerencia o cliente tal no socket tal em sua própria thread
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static class Gerente implements Runnable { //Classe interna
        private Socket socket;
        private int playerId;
        private BufferedReader in;
        private PrintWriter out;
        public Gerente(Socket socket, int playerId) {
            this.socket = socket; //armazena socket
            this.playerId = playerId; //armazena id do player
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Leitura de msg dos clientes
                out = players.get(playerId); //Recupera o PrintWriter correspondente ao jogador da lista estática players usando o playerId.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() { //Define o método run, exigido pela interface Runnable, que contém a lógica executada pela thread.
            try { //capturar exceções de I/O durante a execução da thread.
                out.println("Você é " + simbolos[playerId]); //Informa ao jogador qual é seu símbolo no início do jogo.
                AtualizaTabuleiro();
                while (true) { //Até o jogo acabar
                    synchronized (Server.class) { //Garante que apenas uma thread (Gerenciador de um jogador) acesse a lógica do turno de cada vez, evitando que ambos os jogadores joguem simultaneamente.
                        if (turn == playerId) { //Se a vez for do player
                            out.println("Sua vez! Envie a jogada (linha,coluna ex: 0,0)");
                            String move = in.readLine(); //Lê uma linha de texto enviada pelo cliente.
                            if (move != null && MakeMove(move)) {
                                AtualizaTabuleiro(); //Atualiza o tabuleiro aos players
                                String vencedor = ChecaGanhador(); // Checa se alguem ganhou
                                if (vencedor != null) { //Se houver ganhador
                                    broadcast("Fim de jogo! " + (vencedor.equals("Tie") ? "Empate!" : "Vencedor: " + vencedor));
                                    break;
                                }
                                turn = 1 - turn;//Se não acabou - Alterna turno
                            } else {
                                out.println("Jogada inválida! Tente novamente.");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private boolean MakeMove(String move) {
            try {
                String[] parts = move.split(","); //Separa a linha e coluna em duas partes
                int row = Integer.parseInt(parts[0]); //Converte a primeira parte em um inteiro
                int col = Integer.parseInt(parts[1]);//Converte a outra parte p/ int
                if (row >= 0 && row < 3 && col >= 0 && col < 3 && tabuleiro[row][col] == ' ') { //Verifica se a jogada é válida
                    tabuleiro[row][col] = simbolos[playerId]; //Aplica a jogado do player
                    return true; //Jogada válida e aplicada
                }
            } catch (Exception e) {
                return false; //jogada não válida
            }
            return false;
        }
        private boolean AtualizaTabuleiro(){ //formatar e enviar o estado do tabuleiro aos clientes
            StringBuilder tabuleiroStr = new StringBuilder(); //StringBuilder é uma classe eficiente para concatenar strings dinamicamente
            for (char[] row : tabuleiro) {  //Itera sobre cada linha do tabuleiro
                tabuleiroStr.append(row[0]).append(" | ").append(row[1]).append(" | ").append(row[2]).append("\n");
            }
            broadcast(tabuleiroStr.toString());
            return true; // Deu certo
        }
        private void broadcast(String message) {
            for (PrintWriter writer : players) { //For each writer dos clientes 
                try {
                    writer.println(message); //Manda msg para todos
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        private String ChecaGanhador() {
            for (int i = 0; i < 3; i++) {
                if (tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][1] == tabuleiro[i][2] && tabuleiro[i][0] != ' ') {
                    return String.valueOf(tabuleiro[i][0]); // Corrigido: Indica o vencedor com base no símbolo da linha.
                }
                if (tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[1][i] == tabuleiro[2][i] && tabuleiro[0][i] != ' ') {
                    return String.valueOf(tabuleiro[0][i]); //Indica o vencedor com base no símbolo da coluna.
                }
            }
            //Verifica diagonal
            if (tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][2] && tabuleiro[0][0] != ' ') {
                return String.valueOf(tabuleiro[0][0]);
            }
            //Verifica diagonal
            if (tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][0] && tabuleiro[0][2] != ' ') {
                return String.valueOf(tabuleiro[0][2]);
            }
            boolean full = true;
            for (char[] row : tabuleiro) { //verifica se o tabuleiro está cheio
                for (char cell : row) {
                    if (cell == ' ') {
                        full = false;
                    }
                }
            }
            return full ? "Tie" : null; // retona o estado do jogo tie = empate, null continua
        }
    }
}