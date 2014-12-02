package champ2009client;

import java.io.IOException;

import jxl.read.biff.BiffException;

public class Prueba {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws BiffException, IOException {
		// TODO Auto-generated method stub

		 Controlador trayectoria=new Controlador("Trayectoria","FuzzySystem.xls","Configuracion"); //Lectura BD conocimiento y reglas
		   Controlador colisiones=new Controlador("Colisiones","FuzzySystem.xls","Configuracion");
		   Controlador velocidad=new Controlador("Velocidad","FuzzySystem.xls","Configuracion"); 
		   Controlador aceleracion=new Controlador("Aceleracion","FuzzySystem.xls","Configuracion"); 
		
		float entradas_v[]=new float[3];
        entradas_v[0]=30;
        entradas_v[1]=12;
        entradas_v[2]=5;
        
        float v_ideal=velocidad.calcularY0(entradas_v);
        
        
        System.out.println("La velocidad ideal deberia ser:"+v_ideal);
    
        
        float entradas_a[]=new float[3];
        entradas_a[0]=(float)141.138;
        entradas_a[1]=116.121f;
        entradas_a[2]=(float)133.949; //conversion de rad/s a km/h
      
        float brake=0;
        float accel=0;
        
        float accel_and_brake=aceleracion.calcularY0(entradas_a);
        if(accel_and_brake<=0)
        {System.out.println("FRENANDO!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        	brake=accel_and_brake;
        	accel=0;
        }else
        {
        	accel=accel_and_brake;
        	brake=0;
        }
        
        System.out.println("br"+brake+"ac"+accel);

}
}
