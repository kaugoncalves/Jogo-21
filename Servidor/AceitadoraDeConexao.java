import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket                 pedido;
    protected static ArrayList<Parceiro> usuarios;
    protected static Vector<Integer>     descartada; 
    protected static Vector<Integer>     baralho;
    protected static int                 idUsuario;
    protected static boolean             temVencedor;
    protected static int                 jogarNovamente;
    protected static int                 vencedor;
    protected static int                 seuId;
    
    
    public AceitadoraDeConexao
    (String porta, ArrayList<Parceiro> usuarios)
    throws Exception
    {
        if (porta==null)
            throw new Exception ("Porta ausente");

        try
        {
            this.pedido =
            new ServerSocket (Integer.parseInt(porta));
            
        }
        catch (Exception  erro)
        {
            throw new Exception ("Porta invalida");
        }

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

            baralho = new Vector<Integer>();

            //criando o vetor de cartas, pois a Classe vector nao permite adicionar direto
            int alterna = 4;
            for(int i = 1; i <= 10; i++)
            {
                alterna=4;
                if(i==10)
                alterna=16; //caso seja algumca carta com valor 10, existem as cartas 10, k, j, q

                for(int j = 0; j < alterna; j++) //todas as cartas precisam aparecer minimamente 4 vezes
                {
                    baralho.add(i);
                }
                
            }
        this.descartada = new Vector<Integer>(); 
        this.descartada.add(0);
        this.seuId = 0;
        this.jogarNovamente = 0;
        this.temVencedor=false;
        this.idUsuario = 0;
        this.usuarios = usuarios;
        System.out.println("O baralho é: "+baralho.toString()); 
        
        
    }

    public void run ()
    {
        for(;;)
        {
            Socket conexao=null;
            try
            {
                 
                conexao = this.pedido.accept();
                
            }
            catch (Exception erro)
            {
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao=null;
            try
            {           
                  
                supervisoraDeConexao =
                new SupervisoraDeConexao (conexao, usuarios);
                
            }
            catch (Exception erro)
            { erro.printStackTrace();}

                     
            supervisoraDeConexao.start();
            
           
        }
    }
}
