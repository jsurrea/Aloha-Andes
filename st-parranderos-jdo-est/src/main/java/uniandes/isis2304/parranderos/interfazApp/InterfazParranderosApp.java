/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Universidad	de	los	Andes	(Bogotá	- Colombia)
 * Departamento	de	Ingeniería	de	Sistemas	y	Computación
 * Licenciado	bajo	el	esquema	Academic Free License versión 2.1
 * 		
 * Curso: isis2304 - Sistemas Transaccionales
 * Proyecto: Parranderos Uniandes
 * @version 1.0
 * @author Germán Bravo
 * Julio de 2018
 * 
 * Revisado por: Claudia Jiménez, Christian Ariza
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package uniandes.isis2304.parranderos.interfazApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import java.sql.Timestamp;

import javax.jdo.JDODataStoreException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import uniandes.isis2304.parranderos.negocio.Cliente;
import uniandes.isis2304.parranderos.negocio.OfertaAlojamiento;
import uniandes.isis2304.parranderos.negocio.Operador;
import uniandes.isis2304.parranderos.negocio.Parranderos;
import uniandes.isis2304.parranderos.negocio.Reserva;

/**
 * Clase principal de la interfaz
 * @author Germán Bravo
 */
@SuppressWarnings("serial")

public class InterfazParranderosApp extends JFrame implements ActionListener
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(InterfazParranderosApp.class.getName());
	
	/**
	 * Ruta al archivo de configuración de la interfaz
	 */
	private static final String CONFIG_INTERFAZ = "./src/main/resources/config/interfaceConfigApp.json"; 
	
	/**
	 * Ruta al archivo de configuración de los nombres de tablas de la base de datos
	 */
	private static final String CONFIG_TABLAS = "./src/main/resources/config/TablasBD_A.json"; 
	
	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
    /**
     * Objeto JSON con los nombres de las tablas de la base de datos que se quieren utilizar
     */
    private JsonObject tableConfig;
    
    /**
     * Asociación a la clase principal del negocio.
     */
    private Parranderos parranderos;
    
	/* ****************************************************************
	 * 			Atributos de interfaz
	 *****************************************************************/
    /**
     * Objeto JSON con la configuración de interfaz de la app.
     */
    private JsonObject guiConfig;
    
    /**
     * Panel de despliegue de interacción para los requerimientos
     */
    private PanelDatos panelDatos;
    
    /**
     * Menú de la aplicación
     */
    private JMenuBar menuBar;

	/* ****************************************************************
	 * 			Métodos
	 *****************************************************************/
    /**
     * Construye la ventana principal de la aplicación. <br>
     * <b>post:</b> Todos los componentes de la interfaz fueron inicializados.
     */
    public InterfazParranderosApp( )
    {
        // Carga la configuración de la interfaz desde un archivo JSON
        guiConfig = openConfig ("Interfaz", CONFIG_INTERFAZ);
        
        // Configura la apariencia del frame que contiene la interfaz gráfica
        configurarFrame ( );
        if (guiConfig != null) 	   
        {
     	   crearMenu( guiConfig.getAsJsonArray("menuBar") );
        }
        
        tableConfig = openConfig ("Tablas BD", CONFIG_TABLAS);
        parranderos = new Parranderos (tableConfig);
        
    	String path = guiConfig.get("bannerPath").getAsString();
        panelDatos = new PanelDatos ( );

        setLayout (new BorderLayout());
        add (new JLabel (new ImageIcon (path)), BorderLayout.NORTH );          
        add( panelDatos, BorderLayout.CENTER );        
    }
    
	/* ****************************************************************
	 * 			Métodos de configuración de la interfaz
	 *****************************************************************/
    /**
     * Lee datos de configuración para la aplicació, a partir de un archivo JSON o con valores por defecto si hay errores.
     * @param tipo - El tipo de configuración deseada
     * @param archConfig - Archivo Json que contiene la configuración
     * @return Un objeto JSON con la configuración del tipo especificado
     * 			NULL si hay un error en el archivo.
     */
    private JsonObject openConfig (String tipo, String archConfig)
    {
    	JsonObject config = null;
		try 
		{
			Gson gson = new Gson( );
			FileReader file = new FileReader (archConfig);
			JsonReader reader = new JsonReader ( file );
			config = gson.fromJson(reader, JsonObject.class);
			log.info ("Se encontró un archivo de configuración válido: " + tipo);
		} 
		catch (Exception e)
		{
//			e.printStackTrace ();
			log.info ("NO se encontró un archivo de configuración válido");			
			JOptionPane.showMessageDialog(null, "No se encontró un archivo de configuración de interfaz válido: " + tipo, "Parranderos App", JOptionPane.ERROR_MESSAGE);
		}	
        return config;
    }
    
    /**
     * Método para configurar el frame principal de la aplicación
     */
    private void configurarFrame(  )
    {
    	int alto = 0;
    	int ancho = 0;
    	String titulo = "";	
    	
    	if ( guiConfig == null )
    	{
    		log.info ( "Se aplica configuración por defecto" );			
			titulo = "Parranderos APP Default";
			alto = 300;
			ancho = 500;
    	}
    	else
    	{
			log.info ( "Se aplica configuración indicada en el archivo de configuración" );
    		titulo = guiConfig.get("title").getAsString();
			alto= guiConfig.get("frameH").getAsInt();
			ancho = guiConfig.get("frameW").getAsInt();
    	}
    	
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setLocation (50,50);
        setResizable( true );
        setBackground( Color.WHITE );

        setTitle( titulo );
		setSize ( ancho, alto);        
    }

    /**
     * Método para crear el menú de la aplicación con base em el objeto JSON leído
     * Genera una barra de menú y los menús con sus respectivas opciones
     * @param jsonMenu - Arreglo Json con los menùs deseados
     */
    private void crearMenu(  JsonArray jsonMenu )
    {    	
    	// Creación de la barra de menús
        menuBar = new JMenuBar();       
        for (JsonElement men : jsonMenu)
        {
        	// Creación de cada uno de los menús
        	JsonObject jom = men.getAsJsonObject(); 

        	String menuTitle = jom.get("menuTitle").getAsString();        	
        	JsonArray opciones = jom.getAsJsonArray("options");
        	
        	JMenu menu = new JMenu( menuTitle);
        	
        	for (JsonElement op : opciones)
        	{       	
        		// Creación de cada una de las opciones del menú
        		JsonObject jo = op.getAsJsonObject(); 
        		String lb =   jo.get("label").getAsString();
        		String event = jo.get("event").getAsString();
        		
        		JMenuItem mItem = new JMenuItem( lb );
        		mItem.addActionListener( this );
        		mItem.setActionCommand(event);
        		
        		menu.add(mItem);
        	}       
        	menuBar.add( menu );
        }        
        setJMenuBar ( menuBar );	
    }

	/* ****************************************************************
	 * 			Requerimientos funcionales
	 *****************************************************************/
    
    public void crearOperador( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores: ", "Crear Operador", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			String nombre = inputArray[0];
    			String correo = inputArray[1];
    			String contrasena = inputArray[2];
    			String tipo_operador = inputArray[3];
        		Operador tb = parranderos.crearOperador(nombre, correo, contrasena, tipo_operador);
        		if (tb == null)
        		{
        			throw new Exception ("No se pudo crear un Operador con datos: " + input);
        		}
        		String resultado = "En crear Operador\n\n";
        		resultado += "Operador adicionado exitosamente: " + tb;
    			resultado += "\n Operación terminada";
    			panelDatos.actualizarInterfaz(resultado);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void crearOferta( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores: ", "Crear Oferta Alojamiento", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			String tipo = inputArray[0];
    			int costo = Integer.parseInt(inputArray[1]);
    			int capacidad = Integer.parseInt(inputArray[2]);
    			String periodicidad = inputArray[3];
    			int tiempo_minimo = Integer.parseInt(inputArray[4]);
    			String ubicacion = inputArray[5];
    			int tamano = Integer.parseInt(inputArray[6]);
    			Timestamp retiro = Timestamp.valueOf(inputArray[7]);
    			long operador = Long.parseLong(inputArray[8]);
        		OfertaAlojamiento tb = parranderos.crearOfertaAlojamiento(tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador);
        		if (tb == null)
        		{
        			throw new Exception ("No se pudo crear un Oferta con datos: " + input);
        		}
        		String resultado = "En crear Oferta\n\n";
        		resultado += "Oferta adicionado exitosamente: " + tb;
    			resultado += "\n Operación terminada";
    			panelDatos.actualizarInterfaz(resultado);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void crearCliente( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores: ", "Crear Cliente", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			long cedula = Long.valueOf(inputArray[0]);
    			String nombre = inputArray[1];
    			String correo = inputArray[2];
    			String contrasena = inputArray[3];
    			String tipo = inputArray[4];
    			
    			Cliente tb = parranderos.crearCliente(cedula, nombre, correo, contrasena, tipo);
        		if (tb == null)
        		{
        			throw new Exception ("No se pudo crear un Cliente con datos: " + input);
        		}
        		String resultado = "En crear Cliente\n\n";
        		resultado += "Cliente adicionado exitosamente: " + tb;
    			resultado += "\n Operación terminada";
    			panelDatos.actualizarInterfaz(resultado);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void crearReserva( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores: ", "Crear Reserva", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			Timestamp creacion = Timestamp.valueOf(inputArray[0]);
    			Timestamp inicio = Timestamp.valueOf(inputArray[1]);
    			int periodos = Integer.parseInt(inputArray[2]);
    			int costo = Integer.parseInt(inputArray[3]);
    			long cliente = Long.parseLong(inputArray[4]);
    			long oferta = Long.parseLong(inputArray[5]);
    			Reserva tb = parranderos.crearReserva(creacion, inicio, periodos, costo, cliente, oferta);
        		if (tb == null)
        		{
        			throw new Exception ("No se pudo crear un Reserva con datos: " + input);
        		}
        		String resultado = "En crear Reserva\n\n";
        		resultado += "Reserva adicionado exitosamente: " + tb;
    			resultado += "\n Operación terminada";
    			panelDatos.actualizarInterfaz(resultado);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void borrarReserva( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese el ID de la reserva", "Borrar Reserva", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long id_reserva = Long.valueOf (input);
    			long tbEliminados = parranderos.borrarReserva(id_reserva);

    			String resultado = "En eliminar Reserva\n\n";
    			resultado += tbEliminados + " Reservas eliminadas\n";
    			resultado += "\n Operación terminada";
    			panelDatos.actualizarInterfaz(resultado);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void borrarOfertaAlojamiento( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese el ID de la oferta de alojamiento", "Borrar Oferta Alojamiento", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long id_oferta = Long.valueOf (input);
    			long tbEliminados = parranderos.borrarOfertaAlojamiento(id_oferta);

    			String resultado = "En eliminar OfertaAlojamiento\n\n";
    			resultado += tbEliminados + " OfertasAlojamiento eliminadas\n";
    			resultado += "\n Operación terminada";
    			panelDatos.actualizarInterfaz(resultado);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
	/* ****************************************************************
	 * 			Requerimientos de consulta
	 *****************************************************************/
    
    public void ingresosRecibidos( )
    {
    	try
    	{
    		String rpta = parranderos.ingresosRecibidos();
    		panelDatos.actualizarInterfaz(rpta);
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void ofertasPopulares( )
    {
    	try
    	{
    		String rpta = parranderos.ofertasPopulares();
    		panelDatos.actualizarInterfaz(rpta);
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void indiceOcupacion( )
    {
    	try
    	{
    		String rpta = parranderos.indiceOcupacion();
    		panelDatos.actualizarInterfaz(rpta);
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void alojamientosDisponibles( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese servicios separados por ';'", "Alojamientos Disponibles", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] servicios = input.split(";");
        		String rpta = parranderos.alojamientosDisponibles(servicios);
        		panelDatos.actualizarInterfaz(rpta);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void usoTipoUsuario( )
    {
    	try
    	{
    		String rpta = parranderos.usoTipoUsuario();
    		panelDatos.actualizarInterfaz(rpta);
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void usoUsuario( )
    {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese la cédula", "Uso usuario específico", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long cedula = Long.valueOf (input);
        		String rpta = parranderos.usoUsuario(cedula);
        		panelDatos.actualizarInterfaz(rpta);
    		}
    		else
    		{
    			panelDatos.actualizarInterfaz("Operación cancelada por el usuario");
    		}
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }

	/* ****************************************************************
	 * 			Métodos administrativos
	 *****************************************************************/

	/**
     * Muestra la información acerca del desarrollo de esta apicación
     */
    public void acercaDe ()
    {
		String resultado = "Autores: Lina Ojeda y Sebastian Urrea :)";
		panelDatos.actualizarInterfaz(resultado);		
    }

    /**
     * Genera una cadena de caracteres con la descripción de la excepcion e, haciendo énfasis en las excepcionsde JDO
     * @param e - La excepción recibida
     * @return La descripción de la excepción, cuando es javax.jdo.JDODataStoreException, "" de lo contrario
     */
	private String darDetalleException(Exception e) 
	{
		String resp = "";
		if (e.getClass().getName().equals("javax.jdo.JDODataStoreException"))
		{
			JDODataStoreException je = (javax.jdo.JDODataStoreException) e;
			return je.getNestedExceptions() [0].getMessage();
		}
		return resp;
	}

	/**
	 * Genera una cadena para indicar al usuario que hubo un error en la aplicación
	 * @param e - La excepción generada
	 * @return La cadena con la información de la excepción y detalles adicionales
	 */
	private String generarMensajeError(Exception e) 
	{
		String resultado = "************ Error en la ejecución\n";
		resultado += e.getLocalizedMessage() + ", " + darDetalleException(e);
		resultado += "\n\nRevise datanucleus.log y parranderos.log para más detalles";
		return resultado;
	}

	/**
	 * Limpia el contenido de un archivo dado su nombre
	 * @param nombreArchivo - El nombre del archivo que se quiere borrar
	 * @return true si se pudo limpiar
	 */
	private boolean limpiarArchivo(String nombreArchivo) 
	{
		BufferedWriter bw;
		try 
		{
			bw = new BufferedWriter(new FileWriter(new File (nombreArchivo)));
			bw.write ("");
			bw.close ();
			return true;
		} 
		catch (IOException e) 
		{
//			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Abre el archivo dado como parámetro con la aplicación por defecto del sistema
	 * @param nombreArchivo - El nombre del archivo que se quiere mostrar
	 */
	private void mostrarArchivo (String nombreArchivo)
	{
		try
		{
			Desktop.getDesktop().open(new File(nombreArchivo));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/* ****************************************************************
	 * 			Métodos de la Interacción
	 *****************************************************************/
    /**
     * Método para la ejecución de los eventos que enlazan el menú con los métodos de negocio
     * Invoca al método correspondiente según el evento recibido
     * @param pEvento - El evento del usuario
     */
    @Override
	public void actionPerformed(ActionEvent pEvento)
	{
		String evento = pEvento.getActionCommand( );		
        try 
        {
			Method req = InterfazParranderosApp.class.getMethod ( evento );			
			req.invoke ( this );
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
		} 
	}
    
	/* ****************************************************************
	 * 			Programa principal
	 *****************************************************************/
    /**
     * Este método ejecuta la aplicación, creando una nueva interfaz
     * @param args Arreglo de argumentos que se recibe por línea de comandos
     */
    public static void main( String[] args )
    {
        try
        {
        	
            // Unifica la interfaz para Mac y para Windows.
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName( ) );
            InterfazParranderosApp interfaz = new InterfazParranderosApp( );
            interfaz.setVisible( true );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
}
