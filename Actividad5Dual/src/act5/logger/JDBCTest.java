package act5.logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author amna
 *
 */
public class JDBCTest {
	private static NodeList nlist;
	public static void main(String[] args) {
		//Conexión a la DB y MySQL
		String dbUrl="jdbc:mysql://localhost/empleados";
		String user = "usuario";
		String pwd = "usuario";
		String xml = "src/empleados.xml";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(dbUrl, user, pwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(xml);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		Document xmlDoc = null;
		try {
			xmlDoc = builder.parse(file);
		} catch (SAXException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			Object res = xpath.evaluate("/empleados/empleado", xmlDoc,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO empleados(\n" +
					" id, first_name, last_name, email, gender,\n)" +
					"VALUES(?, ?, ?, ?, ?)");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0 ; i < nlist.getLength(); i++) {
			Node node = nlist.item(i);
			List<String> columns = Arrays.asList(
					getAttrValue(node, "id"),
					getTextContent(node, "first_name"),
					getTextContent(node, "last_name"),
					getTextContent(node, "email"),
					getTextContent(node, "gender"));
			for (int n = 0 ; n < columns.size() ; n++) {
				try {
					stmt.setString(n+1, columns.get(n));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				stmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	static private String getAttrValue(Node node,String attrName) {
		if ( ! node.hasAttributes() ) 
			return "";
		NamedNodeMap nmap = node.getAttributes();
		if ( nmap == null ) 
			return "";
		Node n = nmap.getNamedItem(attrName);
		if ( n == null ) 
			return "";
		return n.getNodeValue();
	}

	static private String getTextContent(Node parentNode,String childName) {
		nlist = parentNode.getChildNodes();
		for (int i = 0 ; i < nlist.getLength() ; i++) {
			Node n = nlist.item(i);
			String name = n.getNodeName();
			if ( name != null && name.equals(childName) )
				return n.getTextContent();
		}
		return "";
	}
}
