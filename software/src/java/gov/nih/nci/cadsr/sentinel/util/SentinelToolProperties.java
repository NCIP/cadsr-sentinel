package gov.nih.nci.cadsr.sentinel.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * class to read a properties file
 * 
 * @author Archana Sahu
 */
public class SentinelToolProperties implements Serializable {

	InputStream is = null;
	private Properties props = null;
	private static SentinelToolProperties SentinelToolProperties = null;

	private SentinelToolProperties() throws IOException {
		props = loadProps();
	}

	public static SentinelToolProperties getFactory() {
		try {
			if (SentinelToolProperties == null) {
				SentinelToolProperties = new SentinelToolProperties();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return SentinelToolProperties;
	}
/*
	private Properties loadProps() throws IOException {
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			is = cl.getResourceAsStream("project.properties");
			props = new Properties();
			props.load(is);
		} catch (Exception e) {
			throw new IOException("Unable to get properties in loadProps() : "
					+ e);
		} finally {
			if (is != null)
				is.close();
		}

		return props;
	}
*/
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
	private Properties loadProps() throws IOException {
		try {
			FileInputStream propFile = new FileInputStream("project.properties");
			props = new Properties(System.getProperties());
			props.load(propFile);
		} catch (Exception e) {
			throw new IOException("Unable to get properties in loadProps() : " + e);
		} 
		
		return props;
	}
	
	public static void main(String[] args)
	        throws Exception {

	        // set up new properties object
	        FileInputStream propFile =
	            new FileInputStream("project.properties");
	        Properties p =
	            new Properties(System.getProperties());
	        p.load(propFile);

	        // set the system properties
	        System.setProperties(p);
	        // display new properties
	        System.getProperties().list(System.out);
	}
}
