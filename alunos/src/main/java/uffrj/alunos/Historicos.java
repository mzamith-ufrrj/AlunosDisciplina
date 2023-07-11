/**
 * 
 */
package uffrj.alunos;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import io.github.jonathanlink.PDFLayoutTextStripper;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

/**
 * 
 */
public class Historicos {
	public List<Aluno> mHistoricos = null;
	
	private String mConteudo[] = null;
	private int mPAtualAno = 0, 
			    mPAtualSemestre = 0;
	public Historicos(String dir, String periodoAtual) {
		String m_dir = dir;
		
		mPAtualAno = Integer.parseInt(periodoAtual.substring(0, 4));
		mPAtualSemestre = Integer.parseInt(periodoAtual.substring(5, 6));
		
		
		File folder = new File(m_dir);
		File[] listOfFiles = folder.listFiles(new FilenameFilter() {public boolean accept(File dir, String name) {return name.toLowerCase().endsWith(".pdf");}});
		mHistoricos = new Vector<Aluno>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
		    System.out.println("\tArquivo: " + listOfFiles[i].getName());
		    open_formatted(m_dir+listOfFiles[i].getName());
		    //open_formatted(m_dir+"historico_20230025741.pdf");
		    //open_formatted(m_dir+"historico_20210011696.pdf");
			//open_formatted(m_dir+"historico_20230017900.pdf");    
			if (mConteudo == null) {
				System.err.printf("Conteúdo do arquivo não foi carregado");
				System.exit(-1);
			}
			parse();
			System.out.println("\t parse [OK]");
			
			
		  }//if (listOfFiles[i].isFile()) { 
		}//for (int i = 0; i < listOfFiles.length; i++) {
		
		
		
		
		
		

		
	}//public Historico() {
	
	private void parse() {
		Aluno aluno = new Aluno();
		int estado = 0;
		for (int i = 0; i < mConteudo.length; i++) {
			String linha = mConteudo[i];
			if (estado  == 0) {
				if (mConteudo[i].contains("Nome:") && aluno.mNome == null) {
					boolean hasMatr = true;
					int i1 = mConteudo[i].indexOf("Nome:");
					int i2 = mConteudo[i].indexOf("Matrícula:");
					if (i2 < i1) {
						i2 = mConteudo[i].length();
						hasMatr  = false;
					}
					
					String s = mConteudo[i].substring(i1 + 5, i2);
					aluno.mNome = new String(s.trim());
					
					if (hasMatr) {
						s = mConteudo[i].substring(i2 + 10);
						aluno.mMatr = new String(s.trim());	
					}
					
					
					//System.out.println(aluno.mNome);
					//System.out.println(aluno.mMatr);
				}//if (mConteudo[i].contains("Nome:")) {	
				
				if (mConteudo[i].contains("Ano /  Período  Letivo  Inicial:") && aluno.mEntrada == null) {
					int i1 = mConteudo[i].indexOf("Ano /  Período  Letivo  Inicial:");
					int i2 = mConteudo[i].indexOf("Perfil  Inicial:");
					
					String s = mConteudo[i].substring(i1 + 32, i2);
					aluno.mEntrada = new String(s.trim());
					
					if (aluno.mEntrada.length()>0) {
						if (aluno.mMatr.compareTo("20230000107")==0) {
							System.out.println("Heelo");
						}
						int ano = Integer.parseInt(aluno.mEntrada.substring(0, 4));
						int semestre = Integer.parseInt(aluno.mEntrada.substring(5, 6));
						/*
						semestre--;
						if (semestre < 0) {
							semestre = 0;
							System.err.println("Warning: linha 116");
						} 
						*/
						
						
						int semestre1 = mPAtualSemestre;
						if (mPAtualAno == ano) { 
							ano = 0;
							semestre1 = mPAtualSemestre - semestre;
						}
						else {
							int ano2 = mPAtualAno;
							int ano1 = ano+1;
							ano = ano2 - ano1;	
							semestre1 += (3-semestre);
							if (ano < 0) {
								System.err.println("Warning: ENTRADA:" + aluno.mEntrada);
							}
						}
						
						
						
						int p = (ano * 2) +  semestre1;
						aluno.mPeriodos = p;
					}//					if (aluno.mEntrada.length()>0) {
						
					
					//System.out.println(aluno.mEntrada);
				}
				
				if (mConteudo[i].contains("Componentes    Curriculares  Cursados/Cursando")) {
					estado = 1;
				}
			}//if (estado  == 0) {
			if (estado == 1) { //ler dados sobre: Componentes    Curriculares  Cursados/Cursando
				String s1 = mConteudo[i];
				s1 = s1.substring(22, 29).trim();
				
				if (s1.length() > 2) {
					String s = s1.substring(0, 2);
					if ((s.compareTo("TM")== 0) || (s.compareTo("IM")== 0) || (s.compareTo("AA")== 0) || (s.compareTo("TN")== 0) || (s.compareTo("AB")== 0) || (s.compareTo("AC")== 0)){
						String codigo = mConteudo[i].substring(22, 29).trim();
						String situacao =  mConteudo[i].substring(127, 135).trim();
						String carga =  mConteudo[i].substring(101, 104).trim(); 
						//System.out.println(codigo + " - " + situacao + " - "+ carga);
						aluno.addDisciplina(codigo, carga, situacao);
					}//if ((s.compareTo("TM")== 0) || (s.co...	
				}//if (estado == 1) {
				
				
				
				
				if (mConteudo[i].contains("Carga  Horária   Integralizada/Pendente")) estado = 2;
			}//if (estado == 1) {
			
			if (estado == 2) {//Carga  Horária   Integralizada/Pendente
				//quando for pendente, muda o estado
				String s = mConteudo[i].trim();
				if (s.length() > 0) {
					if (s.contains("Exigido") && s.contains(" h ")) {
						String s1 = s.substring(14, 46);
						String s2 = s.substring(47, 76);
						String s3 = s.substring(77, 103);
						aluno.setCargaHoraria("Exigido", s1.trim(), s2.trim(), s3.trim());
					}else if (s.contains("Integralizado") && s.contains(" h ")) {
						String s1 = s.substring(14, 46);
						String s2 = s.substring(47, 76);
						String s3 = s.substring(77, 103);
						aluno.setCargaHoraria("Integralizado", s1.trim(), s2.trim(), s3.trim());
					}else if (s.contains("Pendente") && s.contains(" h ")) {
						String s1 = s.substring(14, 46);
						String s2 = s.substring(47, 76);
						String s3 = s.substring(77, 103);
						aluno.setCargaHoraria("Pendente", s1.trim(), s2.trim(), s3.trim());
					}	
				}//if (s.length() > 0) {
				if (mConteudo[i].contains("Componentes       Curriculares   Obrigatórios    Pendentes:")) estado = 3;
			}//if (estado == 2)
			
			if (estado == 3) {//Carga  Horária   Integralizada/Pendente
				String s1 = mConteudo[i].trim();
				if (s1.length() > 2) {
					String s = s1.substring(0, 2);
					if ((s.compareTo("TM")== 0) || (s.compareTo("IM")== 0) || (s.compareTo("AA")== 0) || (s.compareTo("TN")== 0) || (s.compareTo("AB")== 0) || (s.compareTo("AC")== 0)){
						String codigo = s1.substring(0, 4);
						aluno.addPendente(codigo);
					}//if ((s.compareTo("TM")== 0) || (s.co...	
				}//if (estado == 1) {
				
				
				//quando for pendente, muda o estado
				/*
				System.out.println(mConteudo[i]);
				if (mConteudo[i].contains("Equivalências:")) {
					estado = 4;
				}
				*/
			}//if (estado == 2)
			
			/*
			if (estado == 4) {
				System.out.println(mConteudo[i]);
			}//if (estado == 3) {
			*/
		}//for (int i = 0; i < mConteudo.length; i++) {
		mHistoricos.add(aluno);

	}//private void parse() {
	


	private void open_formatted(String filename) {
	   mConteudo = null;
	   String texto = null;
	   FileInputStream inputStream = null;
	   RandomAccessBufferedFileInputStream strm = null;
	   PDFParser pdfParser = null;
	   
	   try {inputStream = new FileInputStream(filename);} catch (FileNotFoundException e) {e.printStackTrace();}

	   try { strm = new RandomAccessBufferedFileInputStream(inputStream); } catch (IOException e) {e.printStackTrace();	}
	   
	   try {
		   pdfParser = new PDFParser(strm);
		   pdfParser.parse();
           PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
           PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
           texto = pdfTextStripper.getText(pdDocument);
           //System.out.println(texto);        
           //System.exit(0);
           mConteudo = texto.split("\n");
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
	   
	   /*
	    * 
	        try {
	        	
	            PDFParser pdfParser = new PDFParser();
	            pdfParser.parse();
	            PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
	            PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
	            string = pdfTextStripper.getText(pdDocument);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        };
	        System.out.println(string);
	        */
	}//private void open_by_pages(String filename) {

}//public class Historico {
