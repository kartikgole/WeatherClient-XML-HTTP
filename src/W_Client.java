/*DISTRIBUTED SYSTEMS LAB 3
 * Name: Karteek Gole
 * UTA ID: 1001553522
 * netID: kpg3522
 * 
 * References:	Github, Stack Overflow
*/

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.weather.graphical.xml.DWMLgen.wsdl.ndfdXML_wsdl.NdfdXMLLocator;
import gov.weather.graphical.xml.DWMLgen.wsdl.ndfdXML_wsdl.NdfdXMLPortType;
import gov.weather.graphical.xml.DWMLgen.wsdl.ndfdXML_wsdl.NdfdXMLPortTypeProxy;
import gov.weather.graphical.xml.DWMLgen.wsdl.ndfdXML_wsdl.WeatherParametersType;

public class W_Client {

	public static void main(String[] args) throws ServiceException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		// TODO Auto-generated method stub
		
		String refresh="y";
        Scanner sc = new Scanner(System.in);
        NdfdXMLLocator locator = new NdfdXMLLocator();			//This is the object of the service locator present in the second package

		NdfdXMLPortTypeProxy portproxy = new NdfdXMLPortTypeProxy();	//This is the object of the port type present in the second package
		NdfdXMLPortType porttype = portproxy.getNdfdXMLPortType();		//This is the object of the port proxy present in the second package

		Scanner scanner = new Scanner(System.in);     					//Taking latitude and longitude from the user
    	System.out.print("Enter Latitude (32.73 for local):");			//local = Arlington, TX
    	BigDecimal latitude=(BigDecimal)scanner.nextBigDecimal();
    	System.out.print("Enter Longitude (-97.01 for local):");
    	BigDecimal longitude=(BigDecimal)scanner.nextBigDecimal();		
    	System.out.println("Latitude:"+latitude+" Longitude:"+longitude);
    	//Converting latitude and longitude to big decimal so that target method (portproxy) understands
    	do{
	    	WeatherParametersType wtp= new WeatherParametersType();		//This is the object of the WeatherParametersType present in the second package
	    
			wtp.setMint(true);			//Retrieving weather details
			wtp.setDew(true);
	    	wtp.setPop12(true);
			wtp.setWdir(true);
			
			String productType="time-series";
			String unit ="e";
			Calendar  time = new GregorianCalendar(2017,12,03);		//Setting date
			time.setTime(new Date());
			
			
			String xml = portproxy.NDFDgen(latitude, longitude, productType,time, time, unit , wtp);	//Result of the method is stored in this string variable
	    	
	    	
	    	Document document = stringtodocument(xml);		//This method means that the return value from stringtodocument is stored in the document object
	    	DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
	        fact.setNamespaceAware(true);				//the factory will support XML namespaces (so parsers understand about XML)
	      
	        
	        XPathFactory xpathfact = XPathFactory.newInstance();		//factory object for xpath and then setting it on new path
	        XPath path = xpathfact.newXPath();
	        
	                     
	        String minimumTemperature = getmintemp(document, path);			//Call getmintemp, retrieve and store it in a string
	        System.out.println("\nMin temperature:  " + minimumTemperature);		//and then print to the user
	        
	        String dewPointTemperature = getdptemp(document, path);		//Call getdptemp, retrieve and store it in a string
	        System.out.println("\nDew point temperature: " + dewPointTemperature);	//and then print to the user
	        
	        String probabilityOfPrecipitation = getpop(document, path);		//Call getpop, retrieve and store it in a string
	        System.out.println("\nProbability of precipitation: " + probabilityOfPrecipitation);		//and then print to the user
	        
	        String waved = getwdir(document, path);				//Call getwdir, retrieve and store it in a string
	        System.out.println("\nWave direction: " + waved);		//and then print to the user
	        
	        System.out.println("\nDo you want to refresh?(y/n):- ");		//For the loop, to ask if the user wants to refresh the data
			refresh=sc.next();
       }
       while(refresh.equals("y")||refresh.equals("Y"));

	}
	
	/*
	 * In the following method, we are passing an String xml as input so that the Document Builder can convert it into an object of type document
	 * because that is a parameter type to getmintemp and the rest of the retrieval methods
	 */
	public static Document stringtodocument(String xml) throws SAXException, IOException, ParserConfigurationException 
	{
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder docbuilder = docbf.newDocumentBuilder();
		return docbuilder.parse(new InputSource(new StringReader(xml)));
	}
	
	/*
	 * Method to get minimum temp from the xml doc and return a string value so that it can be printed to the user
	 */
 	private static String getmintemp(Document doc, XPath xpath) throws XPathExpressionException {
        String mintemp = null;
        
        	//retrieving min temp from the xml file
            XPathExpression minimumtemperature = xpath.compile("/dwml/data/parameters/temperature[@type='minimum']/value/text()");
    
            mintemp = (String) minimumtemperature.evaluate(doc, XPathConstants.STRING);
     

        return mintemp; //returns string min temp
    }
    
 	/*
	 * Same method except this is for getting dew point temperature
	 */
    private static String getdptemp(Document doc, XPath xpath) throws XPathExpressionException {
        String dptemp = null;
       

            XPathExpression dptemperature = xpath.compile("/dwml/data/parameters/temperature[@type='dew point']/value/text()");
            
            dptemp = (String) dptemperature.evaluate(doc, XPathConstants.STRING);
     
        return dptemp;		//returns string dp temp
    }
    
    /*
     * getting probability of precipitation
     */
    private static String getpop(Document doc, XPath xpath) throws XPathExpressionException {
	 	String pop = null;
        
        	
            XPathExpression probofprep = xpath.compile("/dwml/data/parameters/probability-of-precipitation/value/text()");
          
            pop = (String) probofprep.evaluate(doc, XPathConstants.STRING);
      
        return pop;		//returns string pop
    }
    
    /*
     * getting wave direction
     */
    private static String getwdir(Document doc, XPath xpath) throws XPathExpressionException {
        String waveD = null;
       
 
            XPathExpression wavedirection = xpath.compile("/dwml/data/parameters/direction/value/text()");
 
            waveD = (String) wavedirection.evaluate(doc, XPathConstants.STRING);
    
        return waveD;
    }

}
