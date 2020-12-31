public class PedidoVencedor extends Comunicado
{     
    private int       vencedor;   
    private int       msg;
    
    

    public PedidoVencedor (int  vencedor, int msg)
    {       
        this.vencedor = vencedor;
        this.msg = msg;   
      
        
    }
    
    public int getVencedor(){
        return this.vencedor;
    } 

    public int getMsg(){
        return this.msg;
    }
    
      
    
    

}