package Servidor;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Trata-se do servidor, que gerencia as conexões dos clientes.
 *
 * @author lucas
 * @author Engenharia de Computação
 * @author 10265892
 */
public class Server {
    /**
     * Lista de documentos no servidor.
     */
    private static ArrayList<String> listaDocs = new ArrayList();
    
    /**
     * Preenche a listaDocs com os arquivos existentes no servidor.
     */
    private static void buscaArquivos() {  //busca arquivos no servidor
        String nomeArq;

        listaDocs.clear();

        File diretorio = new File(".");
        File[] arquivos = diretorio.listFiles();

        for (File f : arquivos) {
            nomeArq = f.getName();
            if (nomeArq.endsWith(".txt")) {
                listaDocs.add(nomeArq);
            }
        }
    }
    
    /**
     * Abre um arquivo no servidor e retorna uma versão dele em lista encadeada.
     * @param nome nome do arquivo a ser aberto.
     * @return versão desse arquivo em lista encadeada
     * @throws IOException
     * @throws FileNotFoundException 
     */
    private static LinkedList<Character> abrirDoc(String nome) throws IOException, FileNotFoundException {  //abre o documento (para a escrita) de nome informado pelo usuário
        File doc = new File(nome);
        FileReader leitor = new FileReader(doc);  //stream de leitura
        LinkedList<Character> lista;
        try (BufferedReader buffer = new BufferedReader(leitor) //para realizar a leitura 'bufferizada'
                ) {
            char caractere;
            lista = new LinkedList<>(); //Vamos criar uma lista encadeada de char
            while ((caractere = (char) buffer.read()) != (char) -1) {  //a ideia eh ler caractere a caractere do arquivo e inserir na lista encadeada
                lista.add(caractere);
            }
        }

        return lista;
    }

    /**
     * Cria um arquivo no diretório padrão.
     *
     * @param nome nome do arquivo que se deseja criar.
     * @throws IOException se um documento com esse nome já existir no
     * diretório. padrão
     */
    private static void criarDoc(String nome) throws IOException {  //cria novo documento de nome informado pelo usuário
        File doc = new File(nome);

        if (doc.createNewFile()) {  //retorna true se um doc com esse nome não existe
            System.out.println(">>O documento " + nome + " foi criado no diretorio padrao!\n");
        } else {  //retorna exceção se um doc com esse nome já existe
            System.out.println("##Um documento com o nome " + nome + " ja existe!\n");
        }
    }

    /**
     * Lógica principal do servidor.
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocket conexaoServidor = new ServerSocket(12345, 10);  //corresponde ao próprio servidor, sendo que 10 clientes podem se conectar a ele.    
        Vector<Vector<Repassador>> clientes = new Vector();  //cada cliente tem o seu próprio repassador de mensagens
        Socket conexaoCliente = null;  //armazena uma conexão com um cliente
        String nomeDoc;
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        int i;
        Repassador novoCliente;

        while (true) {
            nomeDoc = "#fechar#";
            while (nomeDoc.equals("#fechar#")) {
                System.out.println(">Esperando conexões...");
                conexaoCliente = conexaoServidor.accept();
                System.out.println(">Um novo cliente foi conectado.");

                //instancia fluxos de i/o:
                output = new ObjectOutputStream(conexaoCliente.getOutputStream());
                output.flush();
                input = new ObjectInputStream(conexaoCliente.getInputStream());

                //manda para o cliente uma lista de arquivos para ele escolher:
                buscaArquivos();
                output.writeObject(listaDocs);
                output.flush();

                //lê uma string com o nome do documento aberto:
                nomeDoc = input.readUTF();
                if (nomeDoc.equals("#fechar#")) {
                    System.out.println(">Cliente desconectado.");
                }
            }

            //manda a lista encadeada para o cliente:
            if (listaDocs.contains(nomeDoc)) {
                LinkedList<Character> l = abrirDoc(nomeDoc);
                output.writeObject(l);
                output.flush();
            } else {  //se o documento não existir, cria ele
                criarDoc(nomeDoc);
                output.writeObject(new LinkedList<Character>());
                output.flush();
            }

            //se o nome do documento aberto já for representado por um vetor
            //no vetor de vetores, adiciona um elemento ao seu respectivo vetor:
            for (i = 0; i < clientes.size(); i++) {
                if (clientes.elementAt(i).isEmpty()) {
                    break;
                }
                if (clientes.elementAt(i).elementAt(0).getNomeDoc().equals(nomeDoc)) {
                    break;
                }
            }

            if (i < clientes.size()) {
                novoCliente = new Repassador(clientes.elementAt(i), conexaoCliente, input, output, nomeDoc);
                clientes.elementAt(i).add(novoCliente);
            } else {  //senão cria um vetor no vetor de vetores, e aí adiciona um elemento:
                clientes.add(new Vector());
                novoCliente = new Repassador(clientes.elementAt(clientes.size() - 1), conexaoCliente, input, output, nomeDoc);
                clientes.elementAt(clientes.size() - 1).add(novoCliente);
            }

            //executa o repassador como uma nova thread:
            Thread t = new Thread(novoCliente);
            t.start();
        }
    }

}
