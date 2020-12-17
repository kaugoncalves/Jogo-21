public class Resultado extends Comunicado
{
      
    private String    cartas; 
    private int       erro;
    private int       valorTotal;
    private int       descartada;
    

    public Resultado (String  cartas, int erro, int valorTotal, int descartada)
    {        
        this.cartas     = cartas; 
        this.erro       = erro;
        this.valorTotal = valorTotal;
        this.descartada = descartada;
        
    }
    
    public int getValorTotal(){
        return this.valorTotal;
    } 
    
    public int getDescartada(){
        return this.descartada;
    }

     public int getErro(){
        return this.erro;
    }       
  
    public String toString ()
    {
        String ret = cartas.toString();
        return ret;
    }

}
