package champ2009client;
import java.util.ArrayList;



/**
 * @author Miguel
 *  Una Regla tiene hasta cuatro antecedentes y un consecuente . 
 *  Para crear una regla hay que darle valor a cada argumento. 
 *  un argumento es una variable que puede tomar los valores de las etiquetas definidas para cada variable.
 *   las etiquetas que servirán luego para buscar en el conjunto de valores que tenga definido para ella en el universo de discurso
 *   
 *   Ejemplo: 
 *   
 *    Regla 1: 
 *    		VARIABLES: BordeIzq BordeCentro BordeDerecho NoUsado Giro
 *          VALOR:    short		medium		medium 				Zero
 *          
 *          Representa : Si BordeIzq = Short Y BordeCentro = medium Y bordeDerecho = medium
 *          			 Entonces Gira a angulo Zero
 *          
 *          	
 */
public class Regla {


	private int id;
	private String Argumento1;
	private String Argumento2;
	private String Argumento3;
	private String Argumento4;
	
	private String Consecuente;
	

	public Regla(int id,String argumento1, String argumento2, String argumento3,
			String argumento4, String consecuente) {
		this.id=id;
		Argumento1 = argumento1;
		Argumento2 = argumento2;
		Argumento3 = argumento3;
		Argumento4 = argumento4;
		Consecuente = consecuente;
	}

	public String getConsecuente() {
		return Consecuente;
	}
	public void setConsecuente(String consecuente) {
		Consecuente = consecuente;
	}


	public String getArgumento1() {
		return Argumento1;
	}


	public void setArgumento1(String argumento1) {
		Argumento1 = argumento1;
	}


	public String getArgumento2() {
		return Argumento2;
	}


	public void setArgumento2(String argumento2) {
		Argumento2 = argumento2;
	}


	public String getArgumento3() {
		return Argumento3;
	}


	public void setArgumento3(String argumento3) {
		Argumento3 = argumento3;
	}


	public String getArgumento4() {
		return Argumento4;
	}


	public void setArgumento4(String argumento4) {
		Argumento4 = argumento4;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	public void imprimir()
	{
		System.out.println("Regla "+id+": "+getArgumento1()+" "+getArgumento2()+" "+getArgumento3()+" "+getArgumento4()+" -> "+getConsecuente());
	}
	
}
