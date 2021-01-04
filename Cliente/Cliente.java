import java.net.*;

import javax.naming.spi.DirStateFactory.Result;

import java.io.*;


public class Cliente
{
	public static final String HOST_PADRAO  = "localhost";
	public static final int    PORTA_PADRAO = 3000;

	public static void main (String[] args) throws Exception
	{
        if (args.length>2)
        {
            System.err.println ("***Uso esperado: java Cliente [HOST [PORTA]]***\n");
            return;
		}
		Parceiro servidor=null;
		

		
 do{
        Socket conexao=null;
        try
        {
            String host = Cliente.HOST_PADRAO;
            int    porta= Cliente.PORTA_PADRAO;

            if (args.length>0)
                host = args[0];

            if (args.length==2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket (host, porta);
        }
        catch (Exception erro)
        {
            System.err.println ("***NÃO FOI POSSIVEL SE CONECTAR AO SERVIDOR, TENTE NOVAMENTE***\n");
            return;
        }
		



        ObjectOutputStream transmissor=null;
        try
        {
            transmissor =
            new ObjectOutputStream(
            conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("***NÃO FOI POSSIVEL SE CONECTAR AO SERVIDOR, TENTE NOVAMENTE***\n");
            return;
        }

        ObjectInputStream receptor=null;
        try
        {
            receptor =
            new ObjectInputStream(
            conexao.getInputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("***NÃO FOI POSSIVEL SE CONECTAR AO SERVIDOR, TENTE NOVAMENTE***\n");
            return;
        }

        servidor=null;
        try
        {
            servidor =
            new Parceiro (conexao, receptor, transmissor);
        }
        catch (Exception erro)
        {
            System.err.println ("***NÃO FOI POSSIVEL SE CONECTAR AO SERVIDOR, TENTE NOVAMENTE***\n");
            return;
        }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
        try
        {
			tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento (servidor);
		}
		catch (Exception erro)
		{} // sei que servidor foi instanciado
		tratadoraDeComunicadoDeDesligamento.start();

		




		char 					opcao=' ';
		Start 					start;
		boolean 				jaPrintou = false;
		boolean 				jaPrintouVez = false;
		int 					seuId= -1;
		Resultado 				resultado;
		SuaVez     				pedido;
		PedidoVencedor 			vencedor = null;				
		PedidoPlayAgain 		pedidoPlayAgain = null;
		int 					print = 0;
		boolean 				jaPrintou2 = false;
		Comunicado 				comunicadoInicial = null;
		Comunicado 				comunicado = null;
		

		
        do
        {	

			
			do
			{	
				
				servidor.receba (new PedidoStart());
				
				do
				{
					comunicadoInicial = (Comunicado)servidor.espie ();
				}
				while (!(comunicadoInicial instanceof Start));
				start = (Start)servidor.envie ();

				
				if(start.getMsg() == 3 && !jaPrintou2)
				{
					System.out.println("***Esperando players se conectarem: "+start.getPlayers()  + "/3***");
					print++;
					jaPrintou2 = true;
					jaPrintou = true;
				}

				if(start.getMsg() == 2 && !jaPrintou)
				{
					System.out.println("***Esperando players se conectarem: "+start.getPlayers()  + "/3***");
					print++;
					jaPrintou = true;	
				}
				

				if(start.getMsg() == 1 && print == 2 && jaPrintou)
				{
					System.out.println("\n***Todos jogadores conectados: "+start.getPlayers()  + "/3***");
					jaPrintou = false;
				}
				

	
			}
			while(start.getPlayers() < 3);

			
			do
			{
				servidor.receba (new PedidoDaVez());
				
				comunicado = null;

				do
				{
					comunicado = (Comunicado)servidor.espie ();
					

				}
				while (!(comunicado instanceof SuaVez));
				pedido = (SuaVez)servidor.envie ();

				if(seuId == -1) //para gerenciar o sistema de rodadas
				{	
					seuId = pedido.getUsuario();					
					System.out.println("\n***Você será o " + (seuId + 1) + "° a jogar***\n");

				}			
					
					if(!jaPrintouVez)
					{					
						
						System.out.println("\n===========[Espere sua vez para jogar*]===========\n");
						jaPrintouVez = true;						

					}		
						
			}
			while(pedido.getUsuarioServidor() != seuId);

			
			
			
			servidor.receba (new PedidoDeResultado());

			
			comunicado = null;
			do
			{				
					
				comunicado = (Comunicado)servidor.espie ();
				
					
			}
			while (!(comunicado instanceof Resultado));
			resultado = (Resultado)servidor.envie (); 

			
			System.out.println("--Suas cartas são: "+resultado.toString() + "--");
			System.out.println("--Seu valor total é: "+resultado.getValorTotal() + "--");
			int descarte=resultado.getDescartada();
			
				if(descarte == 0)
				System.out.print ("\n--Suas opcões ([N]ova carta, [D]escarta): ");

				if(descarte != 0)
				{
				System.out.println ("\n--Suas opcões ([N]ova carta, [D]escarta, [C]omprar carta descartada) ");
				System.out.println ("--Descartada: " +descarte + "--");		
				}
			

			
            try
            {
				opcao = Character.toUpperCase(Teclado.getUmChar());
		    }
		    catch (Exception erro)
		    {
				System.err.println ("\n***Opcao invalida!***\n");
				jaPrintou = true;
				jaPrintouVez = true;
				continue;
			}

			if ("NDC".indexOf(opcao)==-1)
		    {
				System.err.println ("\n***Opcao invalida!***\n");
				jaPrintou = false;
				jaPrintouVez = true;
				continue;
			}

			
			try
			{

				if (opcao == 'N')
				{			
					servidor.receba (new PedidoDeOperacao (opcao,0));					
					System.out.println ("\nCarta comprada\n");	
									
					servidor.receba (new PedidoDeResultado());
					comunicado = null;
					do
					{
						comunicado = (Comunicado)servidor.espie ();
					}
					while (!(comunicado instanceof Resultado));
					resultado = (Resultado)servidor.envie ();
					
					
					System.out.println("--Suas NOVAS cartas são: "+resultado.toString() + "--");
					System.out.println("--Seu NOVO valor total é: "+resultado.getValorTotal() + "--");
					jaPrintouVez = false;					
					
					
				}
				
				
				if (opcao == 'D')
				{					
					System.out.print ("Qual carta deseja descartar? ");
						descarte = Teclado.getUmInt();
						if(descarte > 10 || descarte <= 0)
						{
							System.out.println("\n***Opcao invalida!***\n");
							jaPrintouVez = true;
							jaPrintou = true;
							print = 1;
							continue;
						}
						
						servidor.receba (new PedidoDeOperacao (opcao, descarte));
						
						servidor.receba (new PedidoDeResultado());
						comunicado = null;
						do
						{
							comunicado = (Comunicado)servidor.espie ();
						}
						while (!(comunicado instanceof Resultado));
						resultado = (Resultado)servidor.envie ();



						if(resultado.getErro() == -10)
						{
							System.out.println("\n***Seu baralho nao possui essa carta!***\n");
							jaPrintouVez = true;
							jaPrintou = true;								
							continue;
						}

						
						System.out.println("\ncarta " + descarte + " descartada\n");
						System.out.println("--Suas NOVAS cartas são: "+resultado.toString() + "--");
						System.out.println("--Seu NOVO valor total é: "+resultado.getValorTotal() + "--");						

						jaPrintouVez = false;
						jaPrintou = false;
				}


				if (opcao == 'C')
				{
					if(descarte == 0)
					{
						System.err.println ("\n***Opcao invalida!***\n");
						jaPrintouVez = true;
						jaPrintou = true;
						continue;
					}

					try{
						servidor.receba (new PedidoDeOperacao (opcao,descarte));							
						System.err.println ("\nCompra realizada da carta: " + descarte);
						
						servidor.receba (new PedidoDeResultado());
						comunicado = null;
						do
						{
							comunicado = (Comunicado)servidor.espie ();
						}
						while (!(comunicado instanceof Resultado));
						resultado = (Resultado)servidor.envie ();

						System.out.println("--Suas NOVAS cartas são: "+resultado.toString() + "--");
						System.out.println("--Seu NOVO valor total é: "+resultado.getValorTotal() + "--");
						
						jaPrintouVez = false;						
					}
					catch(Exception erro){
						System.err.println ("\n***Opcao invalida!***\n");
						jaPrintouVez = true;
						continue;
					}
				}		

					comunicado = null;

					if(resultado.getValorTotal() == 21)
					{
						servidor.receba (new Ganhei());
						System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n===============[FIM DE JOGO]===============\n");
						System.out.println("===============[PARABÉNS, VOCÊ ATINGIU 21 E GANHOU]===============");
						break;
					}
					
					else
					{
						servidor.receba (new VerificaGanhei());
						comunicado = null;
						do
						{
							comunicado = (Comunicado)servidor.espie ();
						}
						while (!(comunicado instanceof PedidoVencedor));
						vencedor = (PedidoVencedor)servidor.envie ();

						if(vencedor.getMsg() == 2)
							continue;

						else
						{
							System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n===============[FIM DE JOGO]===============\n");
							System.out.println ("===============[VOCÊ PERDEU, O GANHADOR FOI O " + (vencedor.getVencedor() + 1)  + "° JOGADOR]===============");
							break;
						}
					}
					
						

			}
			catch (Exception erro)
			{
				System.err.println ("Erro de comunicacao com o servidor;");
				System.err.println ("Tente novamente!");
				System.err.println ("Caso o erro persista, termine o programa");
				System.err.println ("e volte a tentar mais tarde!\n");
			}
        }
		while (true);

							
		Comunicado 	comunicadoFinal = null;	
		
		if(seuId == 0)
		{
			System.err.println ("===============[O JOGO IRÁ REINICIAR? (S ou N)]===============");

			do
			{

				try
				{
					opcao = Character.toUpperCase(Teclado.getUmChar());
					
				}
				catch (Exception erro)
				{
					System.err.println ("\n***Opcao invalida!***\n");
					continue;
					
				}

				if ("NS".indexOf(opcao)==-1)
				{
					System.err.println ("\n***Opcao invalida!***\n");
					continue;
					
				}
			
			}while("NS".indexOf(opcao)==-1);


			


			if(opcao == 'N')
			{
				servidor.receba (new NaoJogarNovamente());	
					
			}

			if(opcao == 'S')
			{
				servidor.receba (new JogarNovamente());
				
				
			}
		}
		
		System.out.println("\n===============[ESPERANDO O LIDER DECIDIR SE O JOGO REINICIARÁ]===============");

		
		do{		
			servidor.receba (new EsperandoVotacao());
			comunicadoFinal = null;
			do
			{
				comunicadoFinal = (Comunicado)servidor.espie ();
					
			}
			while (!(comunicadoFinal instanceof PedidoPlayAgain));
			pedidoPlayAgain = (PedidoPlayAgain)servidor.envie ();		
			
			

		  }while(pedidoPlayAgain.getVoto() == 0 );
	
		

		if(pedidoPlayAgain.getVoto() == 1)
		{
			System.out.println("\n===============[O LÍDER DECIDIU QUE JOGAREMOS NOVAMENTE]===============");
			System.out.println("============================[RECOMEÇANDO]============================\n");
			continue;
		}

		  if(pedidoPlayAgain.getVoto() == 2)
		  {
			System.out.println("\n===============[O LÍDER DECIDIU QUE NÃO JOGAREMOS NOVAMENTE]===============");
			System.out.println("============================[ENCERRANDO JOGO]============================\n");
			break;
		  }



	}while(true);

		try
		{
			servidor.receba (new PedidoParaSair ());
		}
		catch (Exception erro)
		{}
		
		System.out.println ("\n***Obrigado por usar este programa!***");
		System.exit(0);
	}
}

