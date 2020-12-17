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
   

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios)
    throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        j1 = new Jogo(AceitadoraDeConexao.baralho);  
        this.cartas =  j1.getCartas();              //cada carta adicionada e removida do vetor Baralho
        valorTotal = j1.somando();             

        idUsuario = AceitadoraDeConexao.usuarios.size(); //so é realizada uma conexao por vez, entao
                                                // o id do usuario sera sua posicao no vetor ""usuarios"
        
       
        this.conexao  = conexao;
        this.usuarios = usuarios;
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
                            
                            
                            
                            AceitadoraDeConexao.idUsuario = this.idUsuario +1; 
                            if(AceitadoraDeConexao.idUsuario == 3)
                            {
                                
                                AceitadoraDeConexao.idUsuario=0;
                            }

                        System.out.println("O ID AGORA É"+AceitadoraDeConexao.idUsuario);
						break;
						    
                        case 'D': //descarte

                            if(j1.descarte(pedidoDeOperacao.getValor()) == -1)
                            {
                                this.usuario.receba (new Resultado (this.cartas.toString(), 1, this.valorTotal, AceitadoraDeConexao.descartada )); 
                                return; 
                            }       
                            this.cartas = j1.getCartas();             
                            System.out.println("carta a ser descartada será: "+pedidoDeOperacao.getValor());

                           AceitadoraDeConexao.descartada = pedidoDeOperacao.getValor(); //variavel descartada do servidor
                            //nao conseguimos fazer um vetor de descartadas, estavamos tendo problema com LockSupport
                            //e nao sabiamos como resolver

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
                {          //retorna as informações especificas de cada jogador
                   this.usuario.receba (new Resultado (this.cartas.toString(), 0, this.valorTotal,  AceitadoraDeConexao.descartada));
                }

                else if (comunicado instanceof PedidoStart)
                {   

                    //sistema de validação de conexoes, só inicia se tiver 3 jogadores
                    synchronized(usuarios)
                    {
                    if(AceitadoraDeConexao.usuarios.size() == 2)
                    this.usuario.receba (new Start (AceitadoraDeConexao.usuarios.size(), 3)); //caso tenha 2 players

                    if(AceitadoraDeConexao.usuarios.size() == 3)
                    this.usuario.receba (new Start (AceitadoraDeConexao.usuarios.size(), 1)); //caso tenha 3 players

                    if(AceitadoraDeConexao.usuarios.size() == 1)
                    this.usuario.receba (new Start (AceitadoraDeConexao.usuarios.size(), 2)); //caso tenha 1 players
                    
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
                    // sistema de vez de cada player, onde retorna o id dele e o id da vez do jogador    
                   this.usuario.receba (new SuaVez (this.idUsuario, AceitadoraDeConexao.idUsuario));
                }

                else if (comunicado instanceof PedidoVencedor)
                {          
                    //sistema de vencer
                    PedidoVencedor PedidoVencedor = (PedidoVencedor)comunicado; 

                    //caso nao tenha ganhador, valida para tentar acha-lo
                    if(!AceitadoraDeConexao.temVencedor)
                    {
                        if(PedidoVencedor.getValor() == 21  ) 
                        {    
                            AceitadoraDeConexao.vencedor = this.idUsuario;
                            this.usuario.receba(new PedidoVencedor(AceitadoraDeConexao.vencedor, 2, 21));
                            AceitadoraDeConexao.temVencedor = true;
                            System.out.println("DECLAROU O VENCEDOR");
                        }
                        if(PedidoVencedor.getValor() != 21)
                        {
                            this.usuario.receba(new PedidoVencedor(AceitadoraDeConexao.idUsuario, 3, 0));
                            System.out.println("CONTINUE");
                        }
                    }                    
                    if(AceitadoraDeConexao.temVencedor)
                    {                       
                        this.usuario.receba(new PedidoVencedor(AceitadoraDeConexao.vencedor,1 , PedidoVencedor.getValor()));
                    }

                }
                else if (comunicado instanceof PedidoPlayAgain)
                {          
                    //sistema para jogar novamente          
                    PedidoPlayAgain pedidoPlayAgain = (PedidoPlayAgain)comunicado;                    
                    
                    //caso o servidor receba 2 significa que alguem votou SIM, entao tambem retorna 2
                    if( pedidoPlayAgain.getmsgPlayAgain() == 2 )
                    {                        
                        AceitadoraDeConexao.jogarNovamente++;
                        
                        this.usuario.receba(new PedidoPlayAgain(2, 2));
                        System.out.println("VOTOU SIM");
                        


                    }

                    //o jogo so iniciara novamente caso todos players queiram jogar de novo
                    if(AceitadoraDeConexao.jogarNovamente == 3)
                    {   
                        System.out.println("Td mundo quer jogar dnv");
                        this.usuario.receba(new PedidoPlayAgain(3, 3));
                        j1.reFazBaralho();
                        AceitadoraDeConexao.jogarNovamente = 0;
                        AceitadoraDeConexao.descartada = 0;
                        AceitadoraDeConexao.idUsuario = 0; 
                        AceitadoraDeConexao.temVencedor=false;
                        AceitadoraDeConexao.jogarNovamente = 0;
                        AceitadoraDeConexao.usuarios.clear();
                        return;
                    }

                    //caso o servidor receba 1 significa que alguem votou NAO, entao tambem retorna 1
                    if( pedidoPlayAgain.getmsgPlayAgain() == 1 )
                    {                        
                        AceitadoraDeConexao.jogarNovamente= -10;                        
                        System.out.println("VOTOU NAO");


                    }
                    //retorna -10 caso alguem nao queira jogar novamente, entao todos sao desconectados
                    if(AceitadoraDeConexao.jogarNovamente == -10)
                    {   
                        System.out.println("ALGUEM NAO QUER JOGAR DNV");
                        this.usuario.receba(new PedidoPlayAgain(-10, -10));  
                        
                    }    
                    //sistema de loop que espera a todos os jogadores votarem
                    if(pedidoPlayAgain.getVoto() == 0 || pedidoPlayAgain.getmsgPlayAgain() == 0)
                        this.usuario.receba (new PedidoPlayAgain (0, 0));
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
