package champ2009client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.io.*;

import com.sun.org.apache.bcel.internal.generic.NEW;

import sun.management.Sensor;
import jxl.*;
import jxl.read.biff.BiffException;


/**
 * Crea una instancia de controlador e incializa las variables relacionadas
 * Muestra las variables etiquetas y las reglas leidas de la base de conocimiento.
 * 
 * Las etiquetas se encuentran en tablas hash y el controlador debe tener un fichero de configuración con las reglas,
 * La base de conocimiento y la definición de los antecedentes
 *
 * El controlador tendrá varios modos para trabajar como :
 * - Fuzzy 
 * - Crisp - Mejor Regla y valor el valor medio de la etiqueta.
 */
public class Controlador {
	
	private String nombre;
	private ArrayList<Regla> reglas;
	private Hashtable<String, Etiqueta> argumento1;
	private Hashtable<String, Etiqueta> argumento2;
	private Hashtable<String, Etiqueta> argumento3;
	private Hashtable<String, Etiqueta> argumento4;	
	private Hashtable<String, Etiqueta> consecuente;
	
	private double contador=0;
	private static final int MAXIMO=100; //valor máxmimo del contador para loggers
	
	
	public Controlador(String nombre,String filename,String hoja) throws BiffException, IOException
	{
		this.nombre=nombre;
		reglas = new ArrayList<Regla>();
		argumento1=new Hashtable<String, Etiqueta>();
		argumento2=new Hashtable<String, Etiqueta>();
		argumento3=new Hashtable<String, Etiqueta>();
		argumento4=new Hashtable<String, Etiqueta>();
		consecuente = new Hashtable<String, Etiqueta>();
		
		leerConfiguracion(nombre,filename,hoja);
		createLogger();
		
			
	}	
	
	public void leerConfiguracion(String nombre,String filename,String hoja) throws BiffException, IOException
	{
		Workbook wb = Workbook.getWorkbook(new File(filename));
		Sheet sheet = wb.getSheet(hoja);
		
		if (nombre=="Trayectoria")
		{	
			//Lectura de las etiquetas asociadas a las variables que se usan en las reglas de trayectoria
			
			argumento1=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B2").getContents()),Integer.parseInt(sheet.getCell("C2").getContents()));
			argumento2=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B3").getContents()),Integer.parseInt(sheet.getCell("C3").getContents()));
			argumento3=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B4").getContents()),Integer.parseInt(sheet.getCell("C4").getContents()));
			argumento4=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B5").getContents()),Integer.parseInt(sheet.getCell("C5").getContents()));
			consecuente=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B6").getContents()),Integer.parseInt(sheet.getCell("C6").getContents()));
						
			leerReglas(filename,sheet.getCell("A1").getContents());

		}
		else if (nombre=="Colisiones")
		{
			argumento1=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B9").getContents()),Integer.parseInt(sheet.getCell("C9").getContents()));
			argumento2=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B10").getContents()),Integer.parseInt(sheet.getCell("C10").getContents()));
			argumento3=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B11").getContents()),Integer.parseInt(sheet.getCell("C11").getContents()));
			argumento4=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B12").getContents()),Integer.parseInt(sheet.getCell("C12").getContents()));
			consecuente=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B13").getContents()),Integer.parseInt(sheet.getCell("C13").getContents()));
						
	
			leerReglas(filename,sheet.getCell("A8").getContents());
			
		
		}
		else if(nombre=="Velocidad")
		{
			
			argumento1=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B16").getContents()),Integer.parseInt(sheet.getCell("C16").getContents()));
			argumento2=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B17").getContents()),Integer.parseInt(sheet.getCell("C17").getContents()));
			argumento3=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B18").getContents()),Integer.parseInt(sheet.getCell("C18").getContents()));
			//argumento4=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B19").getContents()),Integer.parseInt(sheet.getCell("C19").getContents()));
			consecuente=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B20").getContents()),Integer.parseInt(sheet.getCell("C20").getContents()));
						
			leerReglas(filename,sheet.getCell("A15").getContents());
			
		}
		else if(nombre=="Aceleracion")
		{
			
			argumento1=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B23").getContents()),Integer.parseInt(sheet.getCell("C23").getContents()));
			argumento2=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B24").getContents()),Integer.parseInt(sheet.getCell("C24").getContents()));
			argumento3=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B25").getContents()),Integer.parseInt(sheet.getCell("C25").getContents()));
			//argumento4=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B26").getContents()),Integer.parseInt(sheet.getCell("C26").getContents()));
			consecuente=leerEtiqueta(filename,sheet.getCell("F1").getContents(),Integer.parseInt(sheet.getCell("B27").getContents()),Integer.parseInt(sheet.getCell("C27").getContents()));
						
			leerReglas(filename,sheet.getCell("A22").getContents());
			
		}
		
		
		mostrarVariables();
		mostrarReglas();
		
		
	}
	
	
	private Hashtable<String, Etiqueta> leerEtiqueta(String filename,String hoja,int filaInicio,int filaFin) throws BiffException, IOException
	{
		Hashtable<String, Etiqueta> etiquetas = new Hashtable<String, Etiqueta>();
		Workbook wb = Workbook.getWorkbook(new File(filename));
		Sheet sheet = wb.getSheet(hoja);
		
		int filaExcel=filaInicio;
		


		while(filaExcel<=filaFin){
			String name=sheet.getCell("B"+filaExcel).getContents();
			float x0=Float.parseFloat(sheet.getCell("C"+filaExcel).getContents().replace(',', '.'));
			float x1=Float.parseFloat(sheet.getCell("D"+filaExcel).getContents().replace(',', '.'));
			float x2=Float.parseFloat(sheet.getCell("E"+filaExcel).getContents().replace(',', '.'));
			float x3=Float.parseFloat(sheet.getCell("F"+filaExcel).getContents().replace(',', '.'));
			
			Etiqueta e=new Etiqueta(name,x0,x1,x2,x3);
			etiquetas.put(e.getNombre(),e);
			filaExcel++;
		}
		return etiquetas;
		
		
	}
	
	
	
	/**
	 *Lee todas las reglas disponibles en un fichero xls.
	 *Se le pasa por parámetro el nombre del fichero y el nombre de la hoja 
	 * @param filename
	 * @param hoja
	 * @throws IOException 
	 * @throws BiffException 
	 */
	private void leerReglas(String filename, String hoja) throws BiffException, IOException
	{
		//Leer reglas
		int id=1;
		String ant1="_",ant2="_",ant3="_",ant4="_",c="_";
		
		
		
		int filaExcel=2;
		
		Workbook wb = Workbook.getWorkbook(new File(filename));
		Sheet sheet = wb.getSheet(hoja);
		int nfilas=sheet.getRows();
		while(filaExcel<=nfilas)
		{
			ant1=sheet.getCell("A"+filaExcel).getContents();
			ant2=sheet.getCell("B"+filaExcel).getContents();
			ant3=sheet.getCell("C"+filaExcel).getContents();
			ant4=sheet.getCell("D"+filaExcel).getContents();
			c=sheet.getCell("E"+filaExcel).getContents();
			
			Regla r=new Regla(id,ant1,ant2,ant3,ant4,c); //creamos nueva regla con los antecedentes y consecuente leidos
			reglas.add(r); //añadimos la nueva regla a nuestra coleccion de reglas
			
			id++;
			filaExcel++;		
		}
	}
	
	private void mostrarVariablesTrayectoria()
	{
		System.out.println("<-----------Etiquetas Controlador Trayectoria---------->");
		System.out.println("A1: Variable velocidad");
		Etiqueta e1= argumento1.get("very_slow");
		e1.imprimir();
		Etiqueta e2= argumento1.get("slow");
		e2.imprimir();
		Etiqueta e3= argumento1.get("medium");
		e3.imprimir();
		Etiqueta e4=argumento1.get("fast");
		e4.imprimir();
		Etiqueta e5=argumento1.get("very_fast");
		e5.imprimir();
		
		System.out.println("A2: Distancia izq");
		e1= argumento2.get("short");
		e1.imprimir();
		e2= argumento2.get("medium");
		e2.imprimir();
		e3= argumento2.get("far");
		e3.imprimir();
		
		
		System.out.println("A3:Distancia centro");
		e1= argumento3.get("short");
		e1.imprimir();
		e2= argumento3.get("medium");
		e2.imprimir();
		e3= argumento3.get("far");
		e3.imprimir();
		
		System.out.println("A4:Distancia der");
		e1= argumento4.get("short");
		e1.imprimir();
		e2= argumento4.get("medium");
		e2.imprimir();
		e3= argumento4.get("far");
		e3.imprimir();
		
		
		System.out.println("Variable Giro volante");
		e1= consecuente.get("left");
		e1.imprimir();
		e2= consecuente.get("small_left");
		e2.imprimir();
		e3= consecuente.get("zero");
		e3.imprimir();
		e4= consecuente.get("small_right");
		e4.imprimir();
		e5= consecuente.get("right");
		e5.imprimir();
	}
	private void mostrarVariablesColisiones()
	{
		System.out.println("A1: Variable Giro volante");
		Etiqueta e1= consecuente.get("left");
		e1.imprimir();
		Etiqueta e2= consecuente.get("small_left");
		e2.imprimir();
		Etiqueta e3= consecuente.get("zero");
		e3.imprimir();
		Etiqueta e4= consecuente.get("small_right");
		e4.imprimir();
		Etiqueta e5= consecuente.get("right");
		e5.imprimir();
		
		System.out.println("A2:Distancia izq");
		e1= argumento2.get("short");
		e1.imprimir();
		e2= argumento2.get("medium");
		e2.imprimir();
		e3= argumento2.get("far");
		e3.imprimir();
		
		System.out.println("A3:Distancia centro");
		e1= argumento2.get("short");
		e1.imprimir();
		e2= argumento2.get("medium");
		e2.imprimir();
		e3= argumento2.get("far");
		e3.imprimir();
		
		System.out.println("A4: Dist der");
		e1= argumento2.get("short");
		e1.imprimir();
		e2= argumento2.get("medium");
		e2.imprimir();
		e3= argumento2.get("far");
		e3.imprimir();
		

		System.out.println("CONS: Variable Giro volante");
		e1= consecuente.get("left");
		e1.imprimir();
		 e2= consecuente.get("small_left");
		e2.imprimir();
		 e3= consecuente.get("zero");
		e3.imprimir();
		 e4= consecuente.get("small_right");
		e4.imprimir();
		 e5= consecuente.get("right");
		e5.imprimir();
		
	}
	private void mostrarVariablesVelocidad()
	{
System.out.println("<-----------ETIQUETAS CONTROLADOR VELOCIDAD---------->");
		
		System.out.println("ANT1:Distancia izq");
		Etiqueta e1= argumento1.get("short");
		e1.imprimir();
		Etiqueta e2= argumento1.get("medium");
		e2.imprimir();
		Etiqueta e3= argumento1.get("far");
		e3.imprimir();
		
		System.out.println("ANT2:Distancia centro");
		e1= argumento2.get("short");
		e1.imprimir();
		e2= argumento2.get("medium");
		e2.imprimir();
		e3= argumento2.get("far");
		e3.imprimir();
		
		
		System.out.println("ANT3:Distancia derecha");
		e1= argumento3.get("short");
		e1.imprimir();
		e2= argumento3.get("medium");
		e2.imprimir();
		e3= argumento3.get("far");
		e3.imprimir();
		
		System.out.println("CONS:Variable velocidad");
		e1= consecuente.get("very_slow");
		e1.imprimir();
		e2= consecuente.get("slow");
		e2.imprimir();
		e3= consecuente.get("medium");
		e3.imprimir();
		Etiqueta e4=consecuente.get("fast");
		e4.imprimir();
		Etiqueta e5=consecuente.get("very_fast");
		e5.imprimir();
	}
	private void mostrarVariablesAceleracion()
	{
		System.out.println("<-----------ETIQUETAS CONTROLADOR ACELERACION---------->");			System.out.println("Variable velocidad");
		System.out.println("A1: Velocidad inicial");
		Etiqueta e1= argumento1.get("very_slow");
		e1.imprimir();
		Etiqueta e2= argumento1.get("slow");
		e2.imprimir();
		Etiqueta e3= argumento1.get("medium");
		e3.imprimir();
		Etiqueta e4=argumento1.get("fast");
		e4.imprimir();
		Etiqueta e5=argumento1.get("very_fast");
		e5.imprimir();
		
		System.out.println("A2: Velocidad ideal");
		e1= argumento2.get("very_slow");
		e1.imprimir();
		e2= argumento2.get("slow");
		e2.imprimir();
		e3= argumento2.get("medium");
		e3.imprimir();
		e4=argumento2.get("fast");
		e4.imprimir();
		e5=argumento2.get("very_fast");
		e5.imprimir();
		
		System.out.println("A3: Velocidad rueda");
		e1= argumento2.get("very_slow");
		e1.imprimir();
		e2= argumento2.get("slow");
		e2.imprimir();
		e3= argumento2.get("medium");
		e3.imprimir();
		e4=argumento2.get("fast");
		e4.imprimir();
		e5=argumento2.get("very_fast");
		e5.imprimir();
		
		
		
		System.out.println("CONS: Aceleracion");
		e1= consecuente.get("strong_brake");
		e1.imprimir();
		e2= consecuente.get("soft_brake");
		e2.imprimir();
		e3= consecuente.get("soft_accelerate");
		e3.imprimir();
		e4=consecuente.get("medium_accelerate");
		e4.imprimir();
		e5=consecuente.get("high_accelerate");
		e5.imprimir();
		Etiqueta e6=consecuente.get("strong_accelerate");
		e6.imprimir();
	
		
	}
	
	/**
	 * Muestra todas las etiquetas leidas
	 */
	public void mostrarVariables()
	{
		if (nombre=="Trayectoria")		mostrarVariablesTrayectoria();
		else if (nombre=="Colisiones") 	mostrarVariablesColisiones();
		else if (nombre=="Velocidad") 	mostrarVariablesVelocidad();
		else if (nombre=="Aceleracion") mostrarVariablesAceleracion();

	
	}
/**
 * Muestra todas las reglas leidas
 */
	public void mostrarReglas()
	{
		System.out.println("<-----------REGLAS---------->");
		int n=reglas.size();
		for (int i=0;i<n;i++)
			reglas.get(i).imprimir();
		System.out.println("-------------------------------");
	}
	
	/**
	 * Fuzzificar la entrada para cada etiqueta de cada regla
	 * Devuelve un array con los hmin para cada regla
	 * @throws IOException 
	 */
	
	/*
	public float calcularY0(float entradas[]) throws IOException{


		ArrayList<Float> hmin=new ArrayList<Float>(); // Lista de emparejamientos por regla
		ArrayList<Float> x1t =new ArrayList<Float>(); // lista de puntos de corte consecuente
		ArrayList<Float> x2t =new ArrayList<Float>(); // lista de puntos de corte consecuente
        
		float emparejamiento = 0;//emparejamiento de la regla activa
		float minGE = 0;
		Regla r = null;// regla activa en cada momento
		
/**			
*			Para cada una de las reglas
*				obtener regla (i)
*			    Obtener las etiquetas para cada uno de los argumentos (hay que tener en cuenta que hay reglas de 2,3 4 antecedentes)
*					ejemplo:
*							 Etiqueta ant1= argumento1.get(r.getArgumento1());	//get te devuelve la etiqueta asociada al nombre
*			    Para cada antecedente calcular el grado de emparejamiento (donde corta el ejemplo a  la etiqueta , si lo hace, y quedarnos con el mayor de ellos);
*           		ejemplo: 	
*           				float h1=calculaGE(ant1,entradas[0]);  // calcula el emparejamiento de la etiqueta del antecedente 1 con el valor del ejemplo en entradas [0]
*				Si corta a todos los antecedentes (si uno de ellos no lo corta es que esa regla no se cumple).
*					** Se puede tener reglas de tipo "no importa". Si se pone un valor especial en la regla considera que esa regla no es afectada por el antecedente y no se incluye en el calculo . disminuye el numero de reglas necesarias
*                   Añade a la lista de emparejamientos el emparejamiento de dicha regla  .
*					añade los puntos de corte del consecuente 
*
*      Con la lista de h0 y de valores de corte calcula el valor desfuzzificado y lo devuelve
*
*/
			
			 
//TODO CODIGO AQUI 
		
		//hay 4 argumentos(que son los atributos de la clase), por tanto tendremos 4 antecedentes (cada antecendente es una etiqueta)
		//para cada antecendente calculamos el GE al que le pasamos una etiqueta(que es el antecedente) y un float (la entrada correspondiente de la tabla)
		//System.out.println("nº reglas: " + reglas.size());
		/*for(int i=0; i<reglas.size(); i++){		//para cada regla
			r = reglas.get(i);	//obtenemos la regla i
			
			Etiqueta a1, a2, a3, a4, cons, a_aux;
			a1 = argumento1.get(r.getArgumento1());
			a2 = argumento2.get(r.getArgumento2());
			a3 = argumento3.get(r.getArgumento3());
			
			cons = consecuente.get(r.getConsecuente());
			
			float h1, h2, h3, h4;	//aquí almacenamos los grados de emparejamiento
			//minGE = 0;//aqui almacenamos el max GE
			h1 = calculaGE(a1, entradas[0]);
			h2 = calculaGE(a2, entradas[1]);
			h3 = calculaGE(a3, entradas[2]);
			//h4 = calculaGE(a4, entradas[3]); 
			
			if(entradas.length == 4){
				a4 = argumento4.get(r.getArgumento4());
				h4 = calculaGE(a4, entradas[3]);
			}
				
			else 
				h4 = Float.MAX_VALUE;
			

			
			if(h1 != 0 && h2 != 0 && h3 != 0 && h4 != 0){
				minGE = Math.min(h1, h2); // descomentar en version completa
				minGE = Math.min(minGE, h3);
				//minGE = Math.min(h2, h3); // comentar en  version completa 
				
				minGE = Math.min(minGE, h4);
				hmin.add(minGE);
				float [] aux = calcularConsecuente(cons,minGE);
				x1t.add(aux[0]);
				x2t.add(aux[1]);
			}
			
		}	//fin del for


			
			contador++; // muestra cada 100 iteraciones cada regla que se activa en el logger para evitar sobrecargar el sistema  y que muestre cambios significativos
			if(contador==MAXIMO)
			{
				contador=0;
				logger(nombre+"Logger.csv",r,minGE);
			}

//TODO CODIGO AQUI
		
	return defuzzy(hmin,x1t,x2t);
	}*/
	
	 public float calcularY0(float entradas[]) throws IOException{ 
		  
	        ArrayList<Float> hmin=new ArrayList<Float>(); 
	        ArrayList<Float> x1t =new ArrayList<Float>(); 
	        ArrayList<Float> x2t =new ArrayList<Float>(); 
	  
	          
	        switch(entradas.length) 
	        { 
	        case 2: 
	            for (int n=0;n<reglas.size();n++) 
	            { 
	            Regla r=reglas.get(n);                              //obtener regla 
	  
	              
	            Etiqueta ant1= argumento1.get(r.getArgumento1());   //obtener etiqueta antecedente1 
	            Etiqueta ant2= argumento2.get(r.getArgumento2());   //obtener etiqueta antecedente2 
	            Etiqueta cons= consecuente.get(r.getConsecuente()); //obtener etiqueta consecuente 
	            float h1=calculaGE(ant1,entradas[0]); 
	            float h2=calculaGE(ant2,entradas[1]); 
	  
	            /*Cálculo del consecuente inferido (puntos x1t, x2t) */
	            if(h1!=0 && h2!=0) 
	            { 
	                hmin.add(minimo(h1,h2)); //obtener hmin de la regla y añadirlo a colección 
	                float valores[]=calcularConsecuente(cons,minimo(h1,h2)); 
	                x1t.add(valores[0]); 
	                x2t.add(valores[1]); 
	                  
	                contador++; 
	                if(contador==MAXIMO) 
	                { 
	                    contador=0; 
	                    logger(nombre+"Logger.csv",r,minimo(h1,h2)); 
	                } 
	                //System.out.println(nombre+":Aplicando regla "+reglas.get(n).getId()+";resultados> %hmin: "+minimo(h1,h2)*100+" x1t:"+valores[0]+" x2t:"+valores[1]); 
	            } 
	            } 
	            break; 
	        case 3: 
	            for (int n=0;n<reglas.size();n++) 
	            { 
	            Regla r=reglas.get(n);                              //obtener regla 
	              
	            Etiqueta ant1= argumento1.get(r.getArgumento1());   //obtener etiqueta antecedente1 
	            Etiqueta ant2= argumento2.get(r.getArgumento2());   //obtener etiqueta antecedente2 
	            Etiqueta ant3= argumento3.get(r.getArgumento3()); //obtener etiqueta antecedente3 
	            Etiqueta cons= consecuente.get(r.getConsecuente()); //obtener etiqueta consecuente 
	            float h1=calculaGE(ant1,entradas[0]); 
	            float h2=calculaGE(ant2,entradas[1]); 
	            float h3=calculaGE(ant3,entradas[2]); 
	  
	            if(h1!=0 && h2!=0 && h3!=0) 
	            { 
	                float min_aux1=minimo(h1,h2); 
	                hmin.add(minimo(min_aux1,h3)); //obtener hmin de la regla y añadirlo a colección 
	                float valores[]=calcularConsecuente(cons,minimo(min_aux1,h3)); 
	                x1t.add(valores[0]); 
	                x2t.add(valores[1]); 
	                  
	                  
	                contador++; 
	                if(contador==MAXIMO) 
	                { 
	                    contador=0; 
	                    logger(nombre+"Logger.csv",r,minimo(min_aux1,h3)); 
	                } 
	                //if(nombre.equals("Aceleracion")) 
	                //System.out.println(nombre+":Aplicando regla "+reglas.get(n).getId()+";resultados> %hmin: "+minimo(min_aux1,h3)*100+" x1t:"+valores[0]+" x2t:"+valores[1]); 
	            } 
	            } 
	            break; 
	        case 4: 
	              
	            for (int n=0;n<reglas.size();n++) 
	            { 
	            Regla r=reglas.get(n);                              //obtener regla 
	  
	              
	            Etiqueta ant1= argumento1.get(r.getArgumento1());   //obtener etiqueta antecedente1 
	            Etiqueta ant2= argumento2.get(r.getArgumento2());   //obtener etiqueta antecedente2 
	            Etiqueta ant3= argumento3.get(r.getArgumento3()); //obtener etiqueta antecedente3 
	            Etiqueta ant4= argumento4.get(r.getArgumento4()); //obtener etiqueta antecedente4 
	            Etiqueta cons= consecuente.get(r.getConsecuente()); //obtener etiqueta consecuente 
	              
	              
	            float h1=calculaGE(ant1,entradas[0]); 
	            float h2=calculaGE(ant2,entradas[1]); 
	            float h3=calculaGE(ant3,entradas[2]); 
	            float h4=calculaGE(ant4,entradas[3]); 
	  
	            if(h1!=0 && h2!=0 && h3!=0 && h4!=0) 
	            { 
	            float min_aux1=minimo(h1,h2); 
	            float min_aux2=minimo(h3,h4); 
	            hmin.add(minimo(min_aux1,min_aux2)); //obtener hmin de la regla y añadirlo a colección 
	            float valores[]=calcularConsecuente(cons,minimo(min_aux1,min_aux2)); 
	            x1t.add(valores[0]); 
	            x2t.add(valores[1]); 
	              
	            contador++; 
	            if(contador==MAXIMO) 
	            { 
	                contador=0; 
	                logger(nombre+"Logger.csv",r,minimo(min_aux1,min_aux2)); 
	            } 
	  
	              
	            //System.out.println(nombre+":Aplicando regla "+reglas.get(n).getId()+";resultados> % hmin: "+minimo(min_aux1,min_aux2)*100+" x1t:"+valores[0]+" x2t:"+valores[1]); 
	            } 
	            } 
	            break; 
	        } 
	          
	   // return defuzzy(hmin,x1t,x2t); 
	    return crisp(hmin,x1t,x2t); 
	    }
	
	
	
	
	/**
	 * Calcula el grado de emparejamiento de un valor en una etiqueta (hj_x)
	 * Notas: 
	 * 	-Si la etiqueta es abierta por la izquierda o por la derecha hay que tenerlo en cuenta para devolver 1 si es menor o mayor que el trozo en el que vale 1
	 *   En otro caso devuelve el corte con la pendiente (grado de emparejamiento).
	 *   Si no corta devuelve 0
	 * @param e
	 * @param ej
	 * @return
	 */
	public float calculaGE(Etiqueta e, float ej)
    {       
		//TODO CODIGO AQUI
	
		if((e.getX0() == e.getX1()) && (ej <= e.getX1()))
			return 1;
		
		else if((e.getX2() == e.getX3()) && (ej >= e.getX3()))
			return 1;
		
		//else if(ej < e.getX0())
			//return 0;
		
		else if((e.getX0() < ej) && (ej <= e.getX1()))
			return (ej - e.getX0()) / (e.getX1() - e.getX0());
			//h=(ej - e.getX0()) / (e.getX1() - e.getX0());
		
		else if((e.getX1() <= ej) && (ej <= e.getX2()))
			return 1;
			//h=1;
		
		else if((e.getX2() < ej) && (ej <= e.getX3()))
			return (ej - e.getX3()) / (e.getX2() - e.getX3());
			//h=(ej - e.getX3()) / (e.getX2() - e.getX3());
		
		//else if(e.getX3() < ej)
			//return 0;
		
		else
			return 0;
		
    }
    
	/**
	 * Calcula el hji minimo, pasando como parametros hj1 hj2
	 * @param m1
	 * @param m2
	 * @return
	 */
    private float minimo(float hj1, float hj2)
    {
    	if(hj1 < hj2) return hj1;
    	else return hj2;
    }
    
    /**
     * Mecanismo de inferencia: cálculo del consecuente inferido
     * Toma como parámetros el string que representa la consecuencia y el hj minimo obtenido de los antecedentes, obtiene la etiqueta asociada
     * a la consecuencia y aplica una serie de fórmulas para calcular el consecuente inferido
     * @param conse
     * @param h
     * @return
     */
    public float[] calcularConsecuente(Etiqueta e, float h)
    {
    	// de la etiqueta calcula con los 4 puntos del trapecio x0,x1,x2 y x3 los puntos de corte con el consecuente.
    
    	//TODO CODIGO AQUI
    	float x4, x5;
    	float[] dev = new float[2];
    	
    	x4 = e.getX0() + ((e.getX1() - e.getX0()) * h);
    	x5 = e.getX3() - ((e.getX3() - e.getX2()) * h);
    	
    	dev[0] = x4;
    	dev[1] = x5;
    
		return dev;	//devolver los puntos de corte 	
    }
    
    /**
     * En bas a la liste de emparejamientos y los puntos de corte genera una salida en el universo de discurso de 
     * la etiqueta del consecuente calculada como una suma que simula el punto de gravedad de la agregación de todos los consecuentes
     * @param h lista de emparejamientos
     * @param x1t  lista de puntos de corte  
     * @param x2t
     * @return
     */
    public float defuzzy(ArrayList<Float> h,ArrayList<Float> x1t, ArrayList<Float> x2t)
    {
    //float resultado=0;
/*
*  para cada valor en h y x1,x2
*     numerador += emparejamiento*(x1+x2)/2  // grado de emparejamiento por la porción del antecedente que devolvió cada regla. Pondera cada propuesta por regla
     denominador +=emparejamiento
 OJO: si no se cumple ninguna reggla -- divide por cero y da error, siempre debe existir al menos una regla que se cumpla.
    resultado = numerador/denominador
*/
    /*for(int i = 0; i < h.size(); i++)
    {
    	resultado = resultado + (h.get(i) * ((x1t.get(i) + x2t.get(i)) / 2) ) / (h.get(i));
    }
    
    
    return resultado ;
	*/
    
    	float y0=0; 
        float numerador=0; 
        float denominador=0; 
          
        float subtotal1=0; 
        for (int k=0;k<h.size();k++) 
        { 
            subtotal1=h.get(k)*((x1t.get(k)+x2t.get(k))/2); 
            numerador+=subtotal1; 
            denominador+=h.get(k); 
              
        } 
        
        try
        { 
        y0=numerador/denominador; 
        if(denominador==0) return 0; 
        } 
        catch(ArithmeticException e){System.out.println("Division por cero");return 0;} 
        return y0; 
    
    }
    
    public ArrayList<Regla> getReglas()
    {
    	return reglas;
    }
    
    public void logger(String file,Regla r,float hmin) throws IOException
    {

        	BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
 	        bw.newLine();
 	        String id=Integer.toString(r.getId());
 	        String ant1=r.getArgumento1();
 	        String ant2=r.getArgumento2();
 	        String ant3=r.getArgumento3();
 	        String ant4=r.getArgumento4();
 	        String cons=r.getConsecuente();
 	        String h=(Float.toString(hmin*100)).replace('.', ',');

 	        bw.write(id+";"+ant1+";"+ant2+";"+ant3+";"+ant4+";"+cons+";"+h);

 	        bw.close();
        
    }
    
    private void createLogger() throws IOException
    {
    	  BufferedWriter bw = new BufferedWriter(new FileWriter(nombre+"Logger.csv",false));
          bw.write("Id;Antecedente1;Antecedente2;Antecedente3;Antecedente4;Consecuente;%h");
          bw.close();
    }

	/**
	     * En bas a la liste de emparejamientos y los puntos de corte genera una salida en el universo de discurso de 
	     * la etiqueta del consecuente calculada como una suma que simula el punto de gravedad de la agregación de todos los consecuentes
	     * @param h lista de emparejamientos
	     * @param x1t  lista de puntos de corte  
	     * @param x2t
	     * @return
	     */
	    public float crisp(ArrayList<Float> h,ArrayList<Float> x1t, ArrayList<Float> x2t)
	    {
	    	 int max = 0;
	    	 float min = 0;
	    
	    	
	          
	        float subtotal1=0; 
	        for (int k=0;k<h.size();k++) 
	        { 
	            if(min < h.get(k))
	            {
	            	min = h.get(k);
	            	max = k;
	            }
	              
	        } 
	        
	      
		return subtotal1=((x1t.get(max)+x2t.get(max))/2); 
	    
	    }

	/**
	 * @param args
	 *            is used to define all the options of the client.
	 *            <port:N> is used to specify the port for the connection (default is 3001)
	 *            <host:ADDRESS> is used to specify the address of the host where the server is running (default is localhost)  
	 *            <id:ClientID> is used to specify the ID of the client sent to the server (default is championship2009) 
	 *            <verbose:on> is used to set verbose mode on (default is off)
	 *            <maxEpisodes:N> is used to set the number of episodes (default is 1)
	 *            <maxSteps:N> is used to set the max number of steps for each episode (0 is default value, that means unlimited number of steps)
	 *            - 
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws IOException, BiffException {
		
		
		 float []entradasTray=new float[4];
	        entradasTray[0]=(float)0;
	        entradasTray[1]=(float)0;
	        entradasTray[2]=(float)0;
	        entradasTray[3]=(float)30;
	         Controlador trayectoria;
	        trayectoria=new Controlador("Trayectoria","FuzzySystem.xls","Configuracion"); //Lectura BD conocimiento y reglas
	      
	       System.out.println(""+ trayectoria.calcularY0(entradasTray)); //Calcular salida dado los valores de entrada
	
	}
}

