package uffrj.alunos;

public class Disciplina {
	public String mCodigo = null;
	public String mNome = null;
	public String mCarga = null;
	
	
	public Disciplina() {
		mCodigo = new String();
		mNome = new String();
		mCarga = new String();
			
	}//public void Aluno() {
	
	public Disciplina(String codigo, String nome, String carga) {
		mCodigo = new String(codigo);
		mNome = new String(nome);
		mCarga = new String(carga);
			
	}//public void Aluno() {
}
