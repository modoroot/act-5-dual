package act5.dual;
/**
 * Clase que define las propiedades y constructor
 * principal de Log
 * @author amna
 * @version 1.0
 * 
 */
public class Logger {
	//Declaración atributos
	private String fecha, logger, lvl, clase, met, msg;
	private int numOrden;
	/**
	 * Constructor principal de toda la clase
	 * @param fecha fecha de la inserción de los datos
	 * @param logger Objeto logger que creó el registro
	 * @param lvl nivel de log (INFO, DEBUG, etc.)
	 * @param clase clase
	 * @param met método utilizado
	 * @param msg mensaje
	 * @param numOrden número de la orden
	 */
	public Logger(String fecha, String logger, String lvl, String clase, String met, 
			String msg, int numOrden) {
		this.fecha = fecha;
		this.logger = logger;
		this.lvl = lvl;
		this.clase = clase;
		this.met = met;
		this.msg = msg;
		this.numOrden = numOrden;
	}
	//Getters-Setters
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getLogger() {
		return logger;
	}
	public void setLogger(String logger) {
		this.logger = logger;
	}
	public String getLvl() {
		return lvl;
	}
	public void setLvl(String lvl) {
		this.lvl = lvl;
	}
	public String getClase() {
		return clase;
	}
	public void setClase(String clase) {
		this.clase = clase;
	}
	public String getMet() {
		return met;
	}
	public void setMet(String met) {
		this.met = met;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getNumOrden() {
		return numOrden;
	}
	public void setNumOrden(int numOrden) {
		this.numOrden = numOrden;
	}
	@Override
	public String toString() {
		return "Logger [fecha=" + fecha + ", logger=" + logger 
				+ ", lvl=" + lvl + ", clase=" + clase + ", met=" + met
				+ ", msg=" + msg + ", numOrden=" + numOrden+"]";
	}
	
}
