import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Random;

public class SupervisoraDeConexao extends Thread
{ 
    private Jogo                j1; 
    private Vector<Integer>     cartas;
    private int                 valorTotal;    
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;
    private int                 idUsuario;
    private int                 erro;    
   

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios)
    throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        j1 =                            new Jogo(AceitadoraDeConexao.baralho);  
        this.cartas =                   j1.getCartas(); //cada carta adicionada e removida do vetor Baralho
        this.valorTotal =               j1.somando();             

        this.idUsuario =                AceitadoraDeConexao.seuId; //cada jogador recebe o id que e criado na aceitadora
        this.erro =                      0;
        this.conexao  =                  conexao;
        this.usuarios =                  usuarios;
        AceitadoraDeConexao.seuId++;            // o id aumenta cada vez que alguem se conecta
        AceitadoraDeConexao.jogarNovamente = 0;
    }

    public void run ()
    {

        ObjectOutputStream transmissor;
        try
        {
            transmissor =
            new ObjectOutputStream(
            this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }
        
        ObjectInputStream receptor=null;
        try
        {
            receptor=
            new ObjectInputStream(
            this.conexao.getInputStream());
        }
        catch (Exception err0)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread
            
            return;
        }

        try
        {
            this.usuario =
            new Parceiro (this.conexao,
                          receptor,
                          transmissor);
        }
        catch (Exception erro)
        {} // sei que passei os parametros corretos

        try
        {
            synchronized (this.usuarios)
            {
                this.usuarios.add (this.usuario);
            }
            

            for(;;)
            {
                Comunicado comunicado = this.usuario.envie ();

                if (comunicado==null)
                    return;
                else if (comunicado instanceof PedidoDeOperacao)
                {
					PedidoDeOperacao pedidoDeOperacao = (PedidoDeOperacao)comunicado;
					
					switch (pedidoDeOperacao.getOperacao())
					{
                        case 'N': //nova carta
                            
                            j1.novaCarta();         //cada carta adicionada e removida do vetor Baralho
                            this.cartas = j1.getCartas();

                            this.valorTotal = 0;                                                    
                            valorTotal = j1.somando();	        

                            AceitadoraDeConexao.idUsuario = this.idUsuario +1;  //sistema de vez
                            if(AceitadoraDeConexao.idUsuario == 3) 
                            AceitadoraDeConexao.idUsuario=0;
                            

                        System.out.println("O ID AGORA É"+AceitadoraDeConexao.idUsuario);
						break;
						    
                        case 'D': //descarte

                        if(!this.cartas.contains(pedidoDeOperacao.getValor()))
                        {
                            erro = -10; //esse erro é retornado mais tarde, quando é chamado o PedidoResultado
                            break;
                        }
                
                        j1.descarte(pedidoDeOperacao.getValor());
                              
                            this.cartas = j1.getCartas();             
                            System.out.println("carta a ser descartada será: "+pedidoDeOperacao.getValor());
                            

                            this.valorTotal = 0;
                            valorTotal = j1.somando();   
                            
                            AceitadoraDeConexao.idUsuario = (this.idUsuario +1); 
                            if(AceitadoraDeConexao.idUsuario == 3)
                            AceitadoraDeConexao.idUsuario=0;

						    break;
						    
                        case 'C': //comprar descartada 
                            j1.novaCartaDescarte(pedidoDeOperacao.getValor());  

                            System.out.println("A carta é: " + pedidoDeOperacao.getValor());
                            this.cartas = j1.getCartas();

                            this.valorTotal = 0;
                            valorTotal = j1.somando();

                            AceitadoraDeConexao.idUsuario = (this.idUsuario +1); 
                            if(AceitadoraDeConexao.idUsuario == 3)
                            AceitadoraDeConexao.idUsuario=0;

						    break;					    
						
                    }
                }
                else if (comunicado instanceof PedidoDeResultado)
                {  //retorna as informações especificas de cada jogador
                                      
                   this.usuario.receba (new Resultado (this.cartas.toString(), erro, this.valorTotal,  AceitadoraDeConexao.descartada.lastElement()));
                   this.erro = 0;
                }

                else if (comunicado instanceof PedidoStart)
                {   

                    //sistema de validação de conexoes, só inicia se tiver 3 jogadores
                   synchronized(usuarios) //synchronized pois ele ve o usuarios.size
                    {                   
                    if(AceitadoraDeConexao.seuId == 2)
                    this.usuario.receba (new Start (AceitadoraDeConexao.seuId, 3)); //caso tenha 2 players
                    }
                   synchronized(usuarios)
                    { 
                    if(AceitadoraDeConexao.seuId == 3)
                    this.usuario.receba (new Start (AceitadoraDeConexao.seuId, 1)); //caso tenha 3 players
                    }
                    synchronized(usuarios)
                    { 
                    if(AceitadoraDeConexao.seuId == 1)
                    this.usuario.receba (new Start (AceitadoraDeConexao.seuId, 2)); //caso tenha 1 players
                    }
                    
                    

                }

                else if (comunicado instanceof PedidoParaSair)
                {
                    synchronized (this.usuarios)
                    {
                        this.usuarios.remove (this.usuario);
                    }
                    this.usuario.adeus();
                }

                else if (comunicado instanceof PedidoDaVez)
                {       
                    // sistema de vez de cada player, onde retorna o id dele e o id da vez do proximo jogador    
                   this.usuario.receba (new SuaVez (this.idUsuario, AceitadoraDeConexao.idUsuario));
                }

                else if  (comunicado instanceof Ganhei)
                { //declara o ganhador
                    AceitadoraDeConexao.vencedor = this.idUsuario;                    
                    AceitadoraDeConexao.temVencedor = true;
                    System.out.println("DECLAROU O VENCEDOR");
                }

                else if(comunicado instanceof VerificaGanhei)
                { //verificando se voce ganhou, para decidir se o jogo continua ou para
                    if(AceitadoraDeConexao.temVencedor)
                    {                       
                        this.usuario.receba(new PedidoVencedor(AceitadoraDeConexao.vencedor,1));
                    }

                    else
                        this.usuario.receba(new PedidoVencedor(AceitadoraDeConexao.vencedor,2));
                }

                else if(comunicado instanceof NaoJogarNovamente)
                {
                    //caso o lider escolha nao jogar novamente
                    System.out.println("VOTOU NAO");
                    AceitadoraDeConexao.jogarNovamente = -10; // essa variavel para determinar o sim ou o não
                    
                }

                else if(comunicado instanceof JogarNovamente)
                {       
                    //caso o lider escolha jogar novamente      
                    //todas variaveis compartilhadas sao zeradas     
                    System.out.println("VOTOU SIM");
                    AceitadoraDeConexao.jogarNovamente = 1; // essa variavel para determinar o sim ou o não
                    AceitadoraDeConexao.descartada.clear();
                    AceitadoraDeConexao.descartada.add(0);
                    AceitadoraDeConexao.idUsuario = 0; 
                    AceitadoraDeConexao.temVencedor=false;
                    j1.reFazBaralho();
                    AceitadoraDeConexao.usuarios.clear(); 
                    AceitadoraDeConexao.seuId = 0;

                }
                
                else if(comunicado instanceof EsperandoVotacao)
                {
                   //sistema de espera da votação
                    if(AceitadoraDeConexao.jogarNovamente == 1) //caso seja 1, o lider votou sim
                    {   
                        System.out.println("VAI REINICIAR");  
                        this.usuario.receba(new PedidoPlayAgain(1));
                        
                    }

                    if(AceitadoraDeConexao.jogarNovamente == -10) //caso seja -10, o lider votou nao
                    {   
                        System.out.println("NAO JOGARemos NOVAMENTE");                      
                        this.usuario.receba(new PedidoPlayAgain(2));
                    }

                    if(AceitadoraDeConexao.jogarNovamente == 0) //apenas para funcionar o sistema de loop
                    this.usuario.receba(new PedidoPlayAgain(0));
                    
                }


                

            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}
