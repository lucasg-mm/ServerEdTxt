package Servidor;

import java.io.*;
import java.util.*;

/**
 * classe responsável por realizar o salvamento de um arquivo no servidor.
 *
 * @author Lucas Gabriel Mendes Miranda
 * @author 10265892
 */
public class Salvamento implements Runnable {
    /**
     * Documento a ser salvo em formato de lista encadeada.
     */
    private LinkedList<Character> doc;
    /**
     * Nome do documento a ser salvo.
     */
    private String nomeDoc;
    
    public Salvamento(LinkedList<Character> doc, String nomeDoc){
        this.doc = doc;
        this.nomeDoc = nomeDoc;
    }

    @Override
    /**
     * Salva o arquivo no diretório padrão do projeto.
     */
    public void run() {
        try (
                FileWriter escrever = new FileWriter(nomeDoc)) {
            ListIterator iterador;
            iterador = doc.listIterator();

            while (iterador.hasNext()) {
                escrever.write((char) iterador.next());
            }

        } catch (IOException ex) {
            System.out.println("#Erro durante o salvamento do arquivo.");
        }
    }

}
