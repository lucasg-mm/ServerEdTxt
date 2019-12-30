package Servidor;

import Mensagem.Mensagem;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * classe responsável por receber uma mensagem (ação efetuada por um cliente) e
 * repassá-la para todos os outros
 *
 * @author Lucas Gabriel Mendes Miranda
 * @author 10265892
 */
public class Repassador implements Runnable {

    @SuppressWarnings("FieldMayBeFinal")

    /**
     * Vetor de repassadores de mensagens associados a clientes conectados ao
     * mesmo arquivo.
     */
    private Vector<Repassador> clientes;
    /**
     * Conexãoo com o cliente associado com esse repassador.
     */
    private Socket conexaoCliente;
    /**
     * Fluxo de entrada.
     */
    private ObjectInputStream input;
    /**
     * Fluxo de saída.
     */
    private ObjectOutputStream output;
    /**
     * Mensagem e recebida pelo cliente associado e que deve ser repassada para
     * todos os outros clientes conectados ao mesmo arquivo.
     */
    private Mensagem msg;  //ação repassada para os outros clientes
    /**
     * Nome do arquivo sendo editado.
     */
    private String nomeDoc;  //nome do documento sendo editado

    public Repassador(Vector<Repassador> clientes, Socket conexaoCliente, ObjectInputStream input, ObjectOutputStream output, String nomeDoc) throws IOException {
        this.clientes = clientes;
        this.conexaoCliente = conexaoCliente;
        this.input = input;
        this.output = output;
        this.nomeDoc = nomeDoc;
        this.msg = new Mensagem();
        msg.setTexto("");
    }

    public String getNomeDoc() {
        return this.nomeDoc;
    }

    public void setNomeDoc(String nomeDoc) {
        this.nomeDoc = nomeDoc;
    }

    /**
     * Espera uma mensagem de um cliente.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void recebeMensagem() throws IOException, ClassNotFoundException {  //o repassador recebe uma mensagem do seu respectivo cliente  
        msg = (Mensagem) input.readObject();
    }

    /**
     * Manda uma mensagem recebida para todos os repassadores associados a
     * clientes conectados ao mesmo arquivo.
     *
     * @throws IOException se tentar mandar a mensagem para um cliente já
     * desconectado.
     */
    public void repassaMensagem() throws IOException {  //repassa a mensagem para todos os outros clientes
        for (Repassador i : clientes) {
            if (i != this) {
                i.output.writeObject(msg);
                i.output.flush();
            }
        }
    }

    @Override
    /**
     * Lida com o recebimento de mensagens e posterior repassamento.
     */
    public void run() {
        try {
            output.writeInt(clientes.size());  //manda a quantidade de clientes conectados
            output.flush();
        } catch (IOException ex) {
        }

        do {
            try {
                //recebe uma ação e depois a repassa:
                recebeMensagem();
                if (msg.getComando().equals("s")) {  //se o comando for para salvar
                    Thread t = new Thread(new Salvamento((LinkedList<Character>) msg.getDocumentoASalvar().clone(), nomeDoc));
                    t.start();
                } else {
                    repassaMensagem();
                }
            } catch (IOException | ClassNotFoundException ex) {  //se cair na exceção, fecha o programa
                try {
                    //fecha a conexão e os fluxos de i/o:
                    conexaoCliente.close();
                    input.close();
                    output.close();
                    clientes.removeElement(this);
                    System.out.println(">Um cliente se desconectou do arquivo " + nomeDoc + ".");
                    return;
                } catch (IOException ex2) {
                    System.out.println("#Erro durante o encerramento da conexão com o arquivo " + nomeDoc + ".");
                }
            }
        } while (true);
    }
}
