package act5.dual;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Clase que define las propiedades de la clase 
 * y que se conecta a una base de datos y envía logs
 * a partir de un XML y viceversa
 * @author amna
 * @version 1.0
 */
public class XmlLogs {
	private Document doc;
	private Connection conn;
	private DOMSource domSrc;
	private Statement stats;
	private DocumentBuilder docBuilder;
	private DocumentBuilderFactory docBuilderFactory;
	private TransformerFactory transFact;
	private Transformer trans;
	private StreamResult strmResult;
	private ResultSet resultSet;
	/**
	 * Constructor principal de la clase que configura
	 * la conexión a una DB
	 * @param ruta archivo de conexión
	 */
	public XmlLogs(String ruta) {
		try {
			//Configuración del documento
			String url = "";
			String username = "";
			String pwd = "";
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document documento = dBuilder.parse(new File(ruta));
			//Lista de nodos y sus subnodos
			NodeList nList = documento.getDocumentElement().getChildNodes();
			for(int i = 0; i < nList.getLength(); i++) {
				Node nodo = nList.item(i);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nodo;
					//Busca en cada elemento el nombre para mandar a un string las configuraciones
					switch (element.getTagName()){
					case "url":
						url = element.getTextContent();
						break;
					case "username":
						username = element.getTextContent();
						break;
					case "pwd":
						pwd =  element.getTextContent();
						break;
					}
				}
			}
			//Conexión
			this.conn = DriverManager.getConnection(url,username,pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Método que guarda los datos en un XML parar
	 * pasarlos a una DB
	 * @param file fichero XML con los datos
	 * @return false si no funcionó bien el método true
	 * si ha ido correctamente
	 */
	public boolean XmlADb(File file) {
		boolean  result = false;
		Logger log;
		StringTokenizer st;
		StringBuffer sb;
		sb = new StringBuffer();
		try {
			//Árbol XML
			this.docBuilderFactory = DocumentBuilderFactory.newInstance();
			this.docBuilder = docBuilderFactory.newDocumentBuilder();
			this.doc = this.docBuilder.parse(file);
			if(this.conn!=null) {
				this.stats = this.conn.createStatement();
				NodeList nodelist = this.doc.getElementsByTagName("registro");
				for(int i = 0; i<nodelist.getLength();i++) {
					if(nodelist.item(i).hasChildNodes()) {
						NodeList nodeL = nodelist.item(i).getChildNodes();
						for(int j = 0;j<nodeL.getLength(); j++) {
							if(nodeL.item(j).getNodeType() == Node.ELEMENT_NODE) {
								Element elemento = (Element) nodeL.item(j);
								sb.append(elemento.getTextContent()+",");
							}
						}
						sb.deleteCharAt(sb.lastIndexOf(","));
						st = new StringTokenizer(sb.toString(),",");
						log = new Logger(st.nextToken(), st.nextToken(), st.nextToken(),
								st.nextToken(), st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
						this.stats.execute("INSERT INTO logs (date,logger,level,clase,metodo,msg,num_orden) VALUES ("
								+"'"+log.getFecha()+"'"+","+"'"+log.getLogger()+"'"+","+"'"+log.getLvl()
								+"'"+","+"'"+log.getClase()+"'"+","+"'"+log.getMet()+"'"+","+"'"+log.getMsg()
								+"'"+","+log.getNumOrden()+");");
						//Vacía el buffer para insertar el siguiente registro
						sb.replace(0, sb.length(),"");
					}
				}
			}
			result = true;
		}catch(Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Método que recoge los registros de la DB conectada y los
	 * inserta en un fichero XML
	 * @return si ha funcionado 'true', si ha habido algún error 'false'
	 */
	public boolean DBaXml() {
		boolean result;
		ArrayList<Logger> logList = new ArrayList<Logger>();
		try {
			this.docBuilderFactory = DocumentBuilderFactory.newInstance();
			this.docBuilder = docBuilderFactory.newDocumentBuilder();
			this.doc = this.docBuilder.newDocument();
			if(this.conn!=null) {
				this.stats = this.conn.createStatement();
				this.resultSet = this.stats.executeQuery("SELECT * FROM logs");
				while(this.resultSet.next()) {
				logList.add(new Logger(resultSet.getString("date"),resultSet.getString("logger"),
						resultSet.getString("level"),resultSet.getString("clase"),resultSet.getString("metodo")
						,resultSet.getString("msg"),resultSet.getInt("num_orden")));
				}
				Element root = this.doc.createElement("log");
				this.doc.appendChild(root);
				for(int i = 0; i<logList.size(); i++) {
					Element registro = this.doc.createElement("registro");
					Element fecha = this.doc.createElement("date");
					fecha.setTextContent(logList.get(i).getFecha());
					registro.appendChild(fecha);
					Element logger = this.doc.createElement("logger");
					logger.setTextContent(logList.get(i).getLogger());
					registro.appendChild(logger);
					Element nivel = this.doc.createElement("level");
					nivel.setTextContent(logList.get(i).getLvl());
					registro.appendChild(nivel);
					Element clase = this.doc.createElement("clase");
					clase.setTextContent(logList.get(i).getClase());
					registro.appendChild(clase);
					Element metodo = this.doc.createElement("metodo");
					metodo.setTextContent(logList.get(i).getMet());
					registro.appendChild(metodo);
					Element msg = this.doc.createElement("msg");
					msg.setTextContent(logList.get(i).getMsg());
					registro.appendChild(msg);
					Element numOrden = this.doc.createElement("num_orden");
					numOrden.setTextContent(Integer.toString(logList.get(i).getNumOrden()));
					registro.appendChild(numOrden);
					root.appendChild(registro);
				}
				//Paso de datos al XML
				this.transFact = TransformerFactory.newInstance();
				this.trans = transFact.newTransformer();
				this.trans.setOutputProperty(OutputKeys.INDENT, "yes");
				domSrc = new DOMSource(doc);
				strmResult = new StreamResult(new File("logs.xml"));
				trans.transform(domSrc, strmResult);
			}
			result = true;
		}catch(Exception e) {
			result = false;
		}
		return result;
	}

}
