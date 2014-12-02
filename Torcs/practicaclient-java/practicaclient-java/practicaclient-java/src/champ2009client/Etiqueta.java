package champ2009client;

public class Etiqueta {


private String nombre;
private float x0;
private float x1;
private float x2;
private float x3;

public Etiqueta(String nombre, float x0, float x1, float x2, float x3) 
{
	this.nombre = nombre;
	this.x0 = x0;
	this.x1 = x1;
	this.x2 = x2;
	this.x3 = x3;
}


public String getNombre() {
	return nombre;
}
public void setNombre(String nombre) {
	this.nombre = nombre;
}
public float getX0() {
	return x0;
}
public void setX0(float x0) {
	this.x0 = x0;
}
public float getX1() {
	return x1;
}
public void setX1(float x1) {
	this.x1 = x1;
}
public float getX2() {
	return x2;
}
public void setX2(float x2) {
	this.x2 = x2;
}
public float getX3() {
	return x3;
}
public void setX3(float x3) {
	this.x3 = x3;
}

public void imprimir()
{
	System.out.println("Etiqueta "+nombre+": "+getX0()+" "+getX1()+" "+getX2()+" "+getX3());
}

}
