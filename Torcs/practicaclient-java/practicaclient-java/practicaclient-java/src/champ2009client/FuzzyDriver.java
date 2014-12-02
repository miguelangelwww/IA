package champ2009client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import jxl.read.biff.BiffException;

public class FuzzyDriver implements Controller{

	/* Gear Changing Constants*/
	final int[]  gearUp={5000,6000,6000,6500,7000,0};
	final int[]  gearDown={0,2500,3000,3000,3500,3500};

	/* Stuck constants*/
	final int  stuckTime = 25;
	final float  stuckAngle = (float) 0.523598775; //PI/6

	/* Accel and Brake Constants*/
	final float maxSpeedDist=70;
	final float maxSpeed=400;//150;
	final float sin10 = (float) 0.17365;
	final float cos10 = (float) 0.98481;

	/* Steering constants*/
	final float steerLock=(float) 0.785398;
	final float steerSensitivityOffset=(float) 80.0;
	final float wheelSensitivityCoeff=1;

	/* ABS Filter Constants */
	final float wheelRadius[]={(float) 0.3179,(float) 0.3179,(float) 0.3276,(float) 0.3276};
	final float absSlip=(float) 2.0;
	final float absRange=(float) 3.0;
	final float absMinSpeed=(float) 3.0;
	
	
	private int stuck=0;
	
	private  Controlador trayectoria;
	private Controlador velocidad;
	private Controlador aceleracion;
	private Controlador colisiones;
	
	private static final int MAX=100;
	private int contador=0;
	private String stateFile = "log_estado.csv";
	
	public FuzzyDriver() throws BiffException, IOException{
		System.out.println("<FuzzyDriver preparado, ejecute wtorcs.exe>");
		/*
		 * Leer base de conocimientos
		 */
       trayectoria=new Controlador("Trayectoria","FuzzySystem.xls","Configuracion"); //Lectura BD conocimiento y reglas
	   colisiones=new Controlador("Colisiones","FuzzySystem.xls","Configuracion");
	   velocidad=new Controlador("Velocidad","FuzzySystem.xls","Configuracion"); 
	   aceleracion=new Controlador("Aceleracion","FuzzySystem.xls","Configuracion"); 
	   createLogger();
	   
	}
	
	/**
	 * Crea el fichero csv donde se irá escribiendo el estado del coche cada vez que el contador llegue a 70
	 */
	private void createLogger() throws IOException
	    {
		 BufferedWriter bw = new BufferedWriter(new FileWriter(stateFile,false));
	     bw.write("Position;Laptime;Speed;Position_track;Acel;Gear;RPM;Steer;Damage");
	     bw.close();
	    }
	

	public void reset() {
		System.out.println("Restarting the race!");
		
	}

	public void shutdown() {
		System.out.println("Bye bye!");		
	}
	
	
	private int getGear(SensorModel sensors){
	    int gear = sensors.getGear();
	    double rpm  = sensors.getRPM();

	    // if gear is 0 (N) or -1 (R) just return 1 
	    if (gear<1)
	        return 1;
	    // check if the RPM value of car is greater than the one suggested 
	    // to shift up the gear from the current one     
	    if (gear <6 && rpm >= gearUp[gear-1])
	        return gear + 1;
	    else
	    	// check if the RPM value of car is lower than the one suggested 
	    	// to shift down the gear from the current one
	        if (gear > 1 && rpm <= gearDown[gear-1])
	            return gear - 1;
	        else // otherwhise keep current gear
	            return gear;
	}

	private float getSteer(SensorModel sensors){
		// steering angle is compute by correcting the actual car angle w.r.t. to track 
		// axis [sensors.getAngle()] and to adjust car position w.r.t to middle of track [sensors.getTrackPos()*0.5]
	    float targetAngle=(float) (sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
	    // at high speed reduce the steering command to avoid loosing the control
	    if (sensors.getSpeed() > steerSensitivityOffset)
	        return (float) (targetAngle/(steerLock*(sensors.getSpeed()-steerSensitivityOffset)*wheelSensitivityCoeff));
	    else
	        return (targetAngle)/steerLock;

	}
	
	private float getAccel(SensorModel sensors)
	{
	    // checks if car is out of track
	    if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1)
	    {
	        // reading of sensor at +10 degree w.r.t. car axis
	        float rxSensor=(float) sensors.getTrackEdgeSensors()[10];
	        // reading of sensor parallel to car axis
	        float sensorsensor=(float) sensors.getTrackEdgeSensors()[9];
	        // reading of sensor at -10 degree w.r.t. car axis
	        float sxSensor=(float) sensors.getTrackEdgeSensors()[8];

	        float targetSpeed;

	        // track is straight and enough far from a turn so goes to max speed
	        if (sensorsensor>maxSpeedDist || (sensorsensor>=rxSensor && sensorsensor >= sxSensor))
	            targetSpeed = maxSpeed;
	        else
	        {
	            // approaching a turn on right
	            if(rxSensor>sxSensor)
	            {
	                // computing approximately the "angle" of turn
	                float h = sensorsensor*sin10;
	                float b = rxSensor - sensorsensor*cos10;
	                float sinAngle = b*b/(h*h+b*b);
	                // estimate the target speed depending on turn and on how close it is
	                targetSpeed = maxSpeed*(sensorsensor*sinAngle/maxSpeedDist);
	            }
	            // approaching a turn on left
	            else
	            {
	                // computing approximately the "angle" of turn
	                float h = sensorsensor*sin10;
	                float b = sxSensor - sensorsensor*cos10;
	                float sinAngle = b*b/(h*h+b*b);
	                // estimate the target speed depending on turn and on how close it is
	                targetSpeed = maxSpeed*(sensorsensor*sinAngle/maxSpeedDist);
	            }

	        }

	        // accel/brake command is expontially scaled w.r.t. the difference between target speed and current one
	        return (float) (2/(1+Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
	    }
	    else
	        return (float) 0.3; // when out of track returns a moderate acceleration command

	}
	
    double minimo(double a,double b)
    {
    	if (a<=b) return a;
    	else return b;
    }


	public Action control(SensorModel sensors) throws IOException, BiffException{
		// check if car is currently stuck
		if ( Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle )
	    {
			// update stuck counter
	        stuck++;
	    }
	    else
	    {
	    	// if not stuck reset stuck counter
	        stuck = 0;
	    }

		// after car is stuck for a while apply recovering policy
	    if (stuck > stuckTime)
	    {
	    	/* set gear and sterring command assuming car is 
	    	 * pointing in a direction out of track */
	    	
	    	// to bring car parallel to track axis
	        float steer = (float) (- sensors.getAngleToTrackAxis() / steerLock); 
	        int gear=-1; // gear R
	        
	        // if car is pointing in the correct direction revert gear and steer  
	        if (sensors.getAngleToTrackAxis()*sensors.getTrackPosition()>0)
	        {
	            gear = 1;
	            steer = -steer;
	        }
	        // build a CarControl variable and return it
	        Action action = new Action ();
	        action.gear = gear;
	        action.steering = steer;
	        action.accelerate = 1.0;
	        action.brake = 0;
	        return action;
	    }

	    else // car is not stuck
	    {

	    
	    	
	    
	    	// compute accel/brake command
	        float accel_and_brake = getAccel(sensors); //El controlador difuso  sobreescribe la variable accel_and_brake
	        // compute gear 
	        int gear = getGear(sensors);
	        // compute steering
	        float steer = getSteer(sensors); //El controlador difuso sobreescribe la variable steer
	        

	        /*Lectura de los sensores*/
	        double angulo=sensors.getAngleToTrackAxis();//angulo al eje de la pista
	        double bordes[]=sensors.getTrackEdgeSensors();//dist bordes
	        double oponentes[]=sensors.getOpponentSensors();//dist oponentes
	        float speed=(float)sensors.getSpeed();//velocidad
	        float damage=(float)sensors.getDamage();//daños
	        double trackP=(float)sensors.getTrackPosition();
	        double rpm=sensors.getRPM();
	        double laptime=sensors.getCurrentLapTime ();
	        int position=sensors.getRacePosition();
	        double[] vrueda=sensors.getWheelSpinVelocity();
	        
	       /*Controlador difuso de trayectoria*/ 
	        
	       //0.5
	        float accel=(float)1; // valor fijo para primera entrega
	        
	        float brake=(float)0;
	        
	        
	        
	        
	        /* *********************************Controlador de trayectoria**************/
	        if(speed<0) speed=0;
	        if (bordes[4]==-1) bordes[4]=0;
	        if (bordes[9]==-1) bordes[9]=0;
	        if (bordes[14]==-1) bordes[14]=0;
	        
	        float []entradasTray=new float[4];
	        entradasTray[0]=(float)speed;
	        entradasTray[1]=(float)bordes[4];
	        entradasTray[2]=(float) bordes[9];
	        entradasTray[3]=(float) bordes[14];
	        
	       
	        //Es necesario controlar directamente cuando el coche está dentro de uno de los arcenes, ya que los sensores de distancia devuelven todos 0 en ese caso
	        //por lo que es imposible determinar en que arcen estamos, y por tanto qué giro de volante aplicar.
	        if (trackP<-1)steer=0.3f; //coche dentro de arcen derecho
	        else if(trackP>1) steer=-0.3f;//coche dentro de arcen izquierdo
	        else steer=trayectoria.calcularY0(entradasTray); //Calcular salida dado los valores de entrada
	        /* ***********************************************************************************/
	        
	        /* *****************       Controlador Colisiones ************************/
	        float entradasCol[]=new float[4];
	        entradasCol[0]=steer;
	        entradasCol[1]=/*(float) oponentes[16];*/(float)minimo(minimo(oponentes[14],oponentes[15]),oponentes[16]);
	        entradasCol[2]=/*(float)oponentes[17];*/(float)minimo(minimo(oponentes[16],oponentes[17]),oponentes[18]);
	        entradasCol[3]=/*(float)oponentes[18];*/(float)minimo(minimo(oponentes[18],oponentes[19]),oponentes[20]);
	        
	        //entradasCol[1]=/*(float) oponentes[16];*/(float)minimo(minimo(oponentes[10],oponentes[13]),oponentes[16]);
	        //entradasCol[2]=/*(float)oponentes[17];*/(float)minimo(minimo(oponentes[16],oponentes[17]),oponentes[18]);
	        //entradasCol[3]=/*(float)oponentes[18];*/(float)minimo(minimo(oponentes[18],oponentes[21]),oponentes[24]);
	        //if(entradasCol[1] < 10 || entradasCol[2] < 10 || entradasCol[3] < 10)
	        	steer=colisiones.calcularY0(entradasCol);
	        /* ************************************************************************/
	        
	        /* *******************Controlador velocidad************************/
	        float entradas_v[]=new float[3];
	        entradas_v[0]=(float)bordes[4];
	        entradas_v[1]=(float)bordes[9];
	        entradas_v[2]=(float)bordes[14];
	       // float v_ideal=5;
	        float v_ideal = velocidad.calcularY0(entradas_v);	
	        /* ************************************************************/

	        
	        /* **************Contolador aceleración*********************************/
	        float entradas_a[]=new float[3];
	        entradas_a[0]=(float)speed;
	        entradas_a[1]=(float)v_ideal;
	        entradas_a[2]=(float)vrueda[0]*wheelRadius[0]*3.6f; //conversion de rad/s a km/h
	     
	        accel_and_brake=aceleracion.calcularY0(entradas_a);
	        //accel_and_brake = 20;
	        if(accel_and_brake<0)
	        {
	        	brake=-accel_and_brake;
	        	accel=0;
	        }else
	        {
	        	accel=accel_and_brake;
	        	brake=0;
	        }
	        /* ******************************************************************/

	       	        	        
	        
	        //LOG DE ESTADO GENERAL
	        if (contador==MAX)
	        {
	        contador=0;
	        	BufferedWriter bw = new BufferedWriter(new FileWriter(stateFile, true));
	 	        bw.newLine();
	 	        
	 	        String sposition=Float.toString(position).replace('.', ',');
	 	        String slaptime=Double.toString(laptime).replace('.', ',');
	 	        String sspeed=Float.toString(speed).replace('.', ',');
	 	        String strackP=Double.toString(trackP).replace('.', ',');
	 	        String saccel=Float.toString(position).replace('.', ',');
	 	        String sgear=Float.toString(gear).replace('.', ',');
	 	        String srpm=Double.toString(rpm).replace('.', ',');
	 	        String ssteer=Float.toString(steer).replace('.', ',');
	 	        String sdamage=Float.toString(damage).replace('.', ',');
		 	    
	 	        bw.write(sposition+";"+slaptime+";"+sspeed+";"+strackP+";"+saccel+";"+sgear+";"+srpm+";"+ssteer+";"+sdamage);
	 	        bw.close();
	        }
	        contador++;

	        
	        // build a CarControl variable and return it+-
	        //accel = (float) 0;
	        //brake= 0;
	        
	        Action action = new Action ();
	        action.gear = gear;
	        action.steering = steer;
	        action.accelerate = accel;
	        action.brake = brake;
	        return action;
	    }
	}
	
	
	

	private float filterABS(SensorModel sensors,float brake){
		// convert speed to m/s
		float speed = (float) (sensors.getSpeed() / 3.6);
		// when spedd lower than min speed for abs do nothing
	    if (speed < absMinSpeed)
	        return brake;
	    
	    // compute the speed of wheels in m/s
	    float slip = 0.0f;
	    for (int i = 0; i < 4; i++)
	    {
	        slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
	    }
	    // slip is the difference between actual speed of car and average speed of wheels
	    slip = speed - slip/4.0f;
	    // when slip too high applu ABS
	    if (slip > absSlip)
	    {
	        brake = brake - (slip - absSlip)/absRange;
	    }
	    
	    // check brake is not negative, otherwise set it to zero
	    if (brake<0)
	    	return 0;
	    else
	    	return brake;
	}
	
	

}
