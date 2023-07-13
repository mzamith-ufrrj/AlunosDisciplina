package uffrj.alunos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Aluno {
	public String mNome = null;
	public String mMatr = null;
	public String mCurr = null;
	public String mEntrada = null;
	public String mLimite = null;
	public int mPeriodos = 0;
/*	
	public List<Disciplina> mCumpridas = null;
	public List<Disciplina> mPendentes = null;
	public List<Disciplina> mMatriculadas = null;
	*/
	public HashMap<String, Disciplina> mCumpridas = null;
	public HashMap<String, Disciplina> mPendentes = null;
	public HashMap<String, Disciplina> mMatriculadas = null;
	
	public int [][]mCargaHoraria;
	
	public Aluno() {
		mCumpridas = new HashMap<String, Disciplina>();
		mPendentes = new HashMap<String, Disciplina>();
		mMatriculadas = new HashMap<String, Disciplina>();
		mCargaHoraria = new int[3][3];
		for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) mCargaHoraria[i][j] = 0;
		
	}//public void Aluno() {
	
	public void addDisciplina(String codigo, String carga, String situacao) {
		Disciplina d = new Disciplina(codigo, "", carga);
		
		if (situacao.contains("APR") || situacao.contains("DISP") ||  situacao.contains("TRANS") || situacao.contains("CUMP") || situacao.contains("INCORP")) {
			mCumpridas.put(codigo, d);
			return;
		}
		
		
		if (situacao.contains("MATR")) {
			mMatriculadas.put(codigo, d);
			return;
		}
		
	}//public void addDisciplina(String codigo, String carga, String situacao) {

	public void addPendente(String codigo) {
		Disciplina d = new Disciplina(codigo, "", "");
		mPendentes.put(codigo, d);
	}
	public void setCargaHoraria(String titulo, String v1, String v2, String v3) {
		int linha = -1;
		if (titulo.compareTo("Exigido")==0) 
			linha = 0;
		else if (titulo.compareTo("Integralizado")==0)
			linha = 1;
		else if (titulo.compareTo("Pendente")==0)
			linha = 2;
		mCargaHoraria[linha][0] = Integer.parseInt(v1);
		mCargaHoraria[linha][1] = Integer.parseInt(v2);
		mCargaHoraria[linha][2] = Integer.parseInt(v3);
	}
	
	public double getPeriodoFloat() {
		if (mEntrada.length() == 0)
			mEntrada = new String("-1");
		double v = Double.parseDouble(mEntrada);
		return v;		
	}
}//public class Aluno {
