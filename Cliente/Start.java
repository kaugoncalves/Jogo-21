public class Start extends Comunicado
{     
    private int       msg;
    private int       numeroPlayers;    

    public Start (int  numeroPlayers, int msg)
    {       
        this.msg = msg;
        this.numeroPlayers = numeroPlayers;
        
       
    }
    
    public int getPlayers(){
        return this.numeroPlayers;
    } 

     public int getMsg(){
        return this.msg;
    }  
    

}
