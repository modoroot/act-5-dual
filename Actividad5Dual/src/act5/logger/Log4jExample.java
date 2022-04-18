package act5.logger;

import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * 
 * @author amna
 *
 */
public class Log4jExample {
	   /* Get actual class name to be printed on */
	 static Logger logger = Logger.getLogger(Log4jExample.class.getName());
	
	   public static void main(String[] args)throws IOException,SQLException{
		   BasicConfigurator.configure();
		   logger.debug("debug");
		   logger.info("info");
		  
	   }
	
}
