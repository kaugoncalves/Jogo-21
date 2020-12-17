public class PedidoPlayAgain extends Comunicado
{        
    private int       msgPlayAgain;
    private int       voto;

    public PedidoPlayAgain (int msgPlayAgain, int voto)
    {       
        this.voto = voto;        
        this.msgPlayAgain = msgPlayAgain;  
    }
    
    public int getVoto(){
        return this.voto;
    } 

    
    
    public int getmsgPlayAgain(){
        return this.msgPlayAgain;
    } 
    

}