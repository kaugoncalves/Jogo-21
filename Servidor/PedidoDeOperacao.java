public class PedidoDeOperacao extends Comunicado
{
    private char   operacao;
    private int valor;
    
    public PedidoDeOperacao (char operacao, int valor)
    {
        this.operacao = operacao;
        this.valor    = valor;
    }
    
    public char getOperacao ()
    {
        return this.operacao;
    }
    
    public int getValor ()
    {
        return this.valor;
    }
    
    public String toString ()
    {
        return (""+this.operacao+this.valor);
    }
}
