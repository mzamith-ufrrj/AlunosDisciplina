package uffrj.alunos;

/**
 * Hello world!
 *
 */
public class App 
{
	private static Historicos mHistorico = null;
    public static void main( String[] args )
    {
        System.out.println( "Processando históricos:" );
        mHistorico = new Historicos("/home/mzamith/Documents/Works/UFRRJ/UFRRJ.Disciplinas/Downloads.historicos/");
        System.out.println("\tHistóricos carregados: " + Integer.toString(mHistorico.mHistoricos.size()));
    }
}
