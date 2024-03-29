import java.util.Random;
import java.util.Vector;

public class Jogo {
    private Vector<Integer> cartas;      


    public Jogo(Vector<Integer> baralho){       
        
        this.cartas = new Vector<Integer>(3);       
        Random gerador = new Random();

        for(int i = 0; i < this.cartas.capacity(); i++)
        {
            int posicao = (gerador.nextInt(AceitadoraDeConexao.baralho.size())); //so vai sortear uma poiscão q exista no vector baralho
            this.cartas.add(baralho.elementAt(posicao));
            AceitadoraDeConexao.baralho.removeElementAt(posicao); //logo apos adicionar a carta em determinada posição, aquela posicao é removida do vetor, e o vetor se adapta
        }
    }

    public void novaCarta(){     

        Random gerador = new Random();
        int posicao = (gerador.nextInt(AceitadoraDeConexao.baralho.size())); //so vai sortear uma poiscão q exista no vector baralho
        this.cartas.add(AceitadoraDeConexao.baralho.elementAt(posicao));
        AceitadoraDeConexao.baralho.removeElementAt(posicao);
         
    }

    public Vector<Integer> getCartas(){
        return this.cartas;
    }
    

    public void novaCartaDescarte(int novaCarta)throws Exception{
        
        if(novaCarta <= 0 || novaCarta >= 11)
        throw new Exception("Carta invalida");

        cartas.add(novaCarta);
        AceitadoraDeConexao.descartada.removeElement(novaCarta);
        

    }    

    public void descarte(int descartada){

        cartas.removeElement(descartada); //remove
        AceitadoraDeConexao.descartada.add(descartada); //e adiciona no vector de descartada
    }

    public int somando(){

        int ret= 0;

        for(int i = 0; i <  this.cartas.size(); i++)
        ret += this.cartas.get(i);

        return ret;

    }    

    @Override
    public String toString ()
    {
        String ret = cartas.toString();
        return ret;
    }
    public void reFazBaralho(){

            AceitadoraDeConexao.baralho.clear();
            int alterna = 4;
            for(int i = 1; i <= 10; i++)
            {
                alterna=4;
                if(i==10)
                    alterna=16; //caso seja alguma carta com valor 10, existem as cartas 10, k, j, q

                for(int j = 0; j < alterna; j++) //todas as cartas precisam aparecer minimamente 4 vezes
                {
                    AceitadoraDeConexao.baralho.add(i);
                }
            }
            
    }
    




}