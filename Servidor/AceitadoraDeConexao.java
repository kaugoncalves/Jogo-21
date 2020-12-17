import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket                 pedido;
    protected static ArrayList<Parceiro> usuarios;
    protected static int                 descartada = 0; 
    protected static Vector<Integer>     baralho;
    protected static int                 idUsuario = 0; 
    protected static boolean             temVencedor=false;
    protected static int                 jogarNovamente = 0;
    protected static int                 vencedor;
    
    
    
 

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
                    alterna=16; //caso seja a carta 10, existe a carta 10, k, j, q

                for(int j = 0; j < alterna; j++) //todas as cartas precisam aparecer minimamente 4 vezes
                {
                    baralho.add(i);
                }
            }
        System.out.println("O baralho é: "+baralho.toString()); 


        this.usuarios = usuarios;
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
