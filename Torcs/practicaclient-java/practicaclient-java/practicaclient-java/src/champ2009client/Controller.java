package champ2009client;

import java.io.IOException;

import jxl.read.biff.BiffException;



/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 12:19:22 PM
 */
public interface Controller {

    public Action control(SensorModel sensors) throws IOException, BiffException;

    public void reset(); // called at the beginning of each new trial
    
    public void shutdown();

}
