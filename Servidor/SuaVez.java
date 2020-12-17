public class SuaVez extends Comunicado
{
    private int       idUsuario;
    private int       idUsuarioServidor;

    public SuaVez (int idUsuario, int idUsuarioServidor)
    {        
        
        this.idUsuario = idUsuario;
        this.idUsuarioServidor = idUsuarioServidor;
    }  

    public int getUsuario(){
        return this.idUsuario;
    }

    public int getUsuarioServidor(){
        return this.idUsuarioServidor;
    }
  
    

}