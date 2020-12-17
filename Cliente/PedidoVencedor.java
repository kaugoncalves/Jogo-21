public class PedidoVencedor extends Comunicado
{     
    private int       vencedor;   
    private int       msg;
    private int       valor;
    

    public PedidoVencedor (int  vencedor, int msg, int valor)
    {       
        this.vencedor = vencedor;
        this.msg = msg;   
        this.valor = valor; 
        
    }
    
    public int getVencedor(){
        return this.vencedor;
    } 

    public int getMsg(){
        return this.msg;
    }
    
    public int getValor(){
        return this.valor;
    }     
    
    

}