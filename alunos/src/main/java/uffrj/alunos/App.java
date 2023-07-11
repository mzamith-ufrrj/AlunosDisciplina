package uffrj.alunos;

import java.util.Iterator;

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
        mHistorico = new Historicos("/home/mzamith/Documents/Works/UFRRJ/UFRRJ.Disciplinas/Downloads.historicos/", "2023.1");
        System.out.println("\tHistóricos carregados: " + Integer.toString(mHistorico.mHistoricos.size()));
        System.out.println("---------------------------------");
        Iterator itr = mHistorico.mHistoricos.iterator();
        while (itr.hasNext()) {
        	Aluno a = (Aluno) itr.next();
        	if (a.mPeriodos == 0)
        		System.out.println("Entrada: " + a.mEntrada + " - " + a.mMatr);
        }
    }
}
