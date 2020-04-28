package main;

public class Linha{
	private String tipo;
	private String colum;
	private String nome;
	private Boolean pk;
	
	public Linha() {}
	public Linha(String tipo, String colum, String nome) {
		this.tipo = tipo;
		this.colum = colum;
		this.nome = nome;
		this.pk = false;
	}
	public Linha(String tipo, String colum, String nome, Boolean pk) {
		this.tipo = tipo;
		this.colum = colum;
		this.nome = nome;
		this.pk = pk;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getColum() {
		return colum;
	}
	public void setColum(String colum) {
		this.colum = colum;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Boolean getPk() {
		return pk;
	}
	public void setPk(Boolean pk) {
		this.pk = pk;
	}
}