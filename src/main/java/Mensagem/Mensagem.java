package Mensagem;

import java.util.LinkedList;

/**
 * Representa mensagens que o servidor deve mandar para todos os clientes (menos
 * para aquele que gerou a mensagem).
 *
 * @author Lucas Gabriel Mendes Miranda
 * @author Engenharia de Computação
 * @author 10265892
 */
public class Mensagem implements java.io.Serializable {  

    /**
     * Comando que corresponde à ação.
     */
    private String comando;  //especificação da ação
    /**
     * Caso ocorra uma inserção, essa variável guarda o texto inserido
     */
    private String texto;
    /**
     * Caso ocorra uma deleção, indica quantos chars foram removidos
     */
    private int numDel;
    /**
     * Posição do cursor à qual a ação deverá ser aplicada.
     */
    private int aplicar_na_posi;
    /**
     * Documento a ser salvo, se a mensagem possuir o comando de salvamento.
     */
    private LinkedList<Character> documentoASalvar = null;  //só é usado no comando de salvar documento

    public LinkedList<Character> getDocumentoASalvar() {
        return documentoASalvar;
    }

    public void setDocumentoASalvar(LinkedList<Character> documentoASalvar) {
        this.documentoASalvar = documentoASalvar;
    }

    public int getAplicar_na_posi() {
        return aplicar_na_posi;
    }

    public void setAplicar_na_posi(int aplicar_na_posi) {
        this.aplicar_na_posi = aplicar_na_posi;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getTexto() {
        return texto;
    }

    public int getNumDel() {
        return numDel;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setNumDel(int numDel) {
        this.numDel = numDel;
    }
}
