package uniandes.isis2304.alohandes.interfazApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.lang.reflect.Method;

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

import uniandes.isis2304.alohandes.negocio.Cliente;
import uniandes.isis2304.alohandes.negocio.OfertaAlojamiento;
import uniandes.isis2304.alohandes.negocio.Operador;
import uniandes.isis2304.alohandes.negocio.NegocioAlohandes;
import uniandes.isis2304.alohandes.negocio.Reserva;

/**
 * Clase principal de la interfaz
 * @author Germán Bravo
 */
@SuppressWarnings("serial")

public class InterfazAlohandesApp extends JFrame implements ActionListener
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(InterfazAlohandesApp.class.getName());
	
	/**
	 * Ruta al archivo de configuración de la interfaz
	 */
	private static final String CONFIG_INTERFAZ = "./src/main/resources/config/interfaceConfigApp.json"; 
	
	/**
	 * Ruta al archivo de configuración de los nombres de tablas de la base de datos
	 */
	private static final String CONFIG_TABLAS = "./src/main/resources/config/TablasBD.json"; 
	
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
    private NegocioAlohandes negocioAlohandes;
    
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
    public InterfazAlohandesApp( )
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
        negocioAlohandes = new NegocioAlohandes (tableConfig);
        
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
			JOptionPane.showMessageDialog(null, "No se encontró un archivo de configuración de interfaz válido: " + tipo, "Alohandes App", JOptionPane.ERROR_MESSAGE);
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
			titulo = "Alohandes APP Default";
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
        		Operador tb = negocioAlohandes.crearOperador(nombre, correo, contrasena, tipo_operador);
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
        		OfertaAlojamiento tb = negocioAlohandes.crearOfertaAlojamiento(tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador);
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
    			
    			Cliente tb = negocioAlohandes.crearCliente(cedula, nombre, correo, contrasena, tipo);
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
    			Reserva tb = negocioAlohandes.crearReserva(creacion, inicio, periodos, costo, cliente, oferta);
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
    			long tbEliminados = negocioAlohandes.borrarReserva(id_reserva);

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
    			long tbEliminados = negocioAlohandes.borrarOfertaAlojamiento(id_oferta);

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
    
    public void crearReservaColectiva() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores TIPO y CANTIDAD: ", "Crear Reserva Colectiva", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			String tipo = inputArray[0];
    			int cantidad = Integer.parseInt(inputArray[1]);
  
    			Reserva[] tb = negocioAlohandes.crearReservaColectiva(tipo, cantidad);
        		if (tb == null)
        		{
        			throw new Exception ("No se pudo crear un Reserva Colectiva con datos: " + input);
        		}
        		String resultado = "En crear Reserva Colectiva\n\n";
        		resultado += "Reservas adicionadas exitosamente: ";
        		for(Reserva r : tb) {
        			resultado += "\n" + r;
        		}
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
    
    public void borrarReservaColectiva() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese id de reserva colectiva: ", "Borrar Reserva Colectiva", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long id_reserva_colectiva = Long.parseLong(input);
    			long eliminados = negocioAlohandes.borrarReservaColectiva(id_reserva_colectiva);
        		String resultado = "En borar Reserva Colectiva\n\n";
        		resultado += "Reservas eliminadas exitosamente: " + eliminados;
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
    
    public void deshabilitarOferta() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese id de la oferta: ", "Deshabilitar Oferta Alojamiento", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long id_oferta = Long.parseLong(input);
    			Reserva[] tb = negocioAlohandes.deshabilitarOferta(id_oferta);
        		if (tb == null)
        		{
        			throw new Exception ("No se pudo deshabilitar oferta con datos: " + input);
        		}
        		String resultado = "En deshabilitar oferta\n\n";
        		resultado += "Reservas reasignadas exitosamente: ";
        		for(Reserva r : tb) {
        			resultado += "\n" + r;
        		}
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
    
    public void habilitarOferta() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese id de la oferta: ", "Habilitar Oferta Alojamiento", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long id_oferta = Long.parseLong(input);
    			long rows = negocioAlohandes.habilitarOferta(id_oferta);
        		String resultado = "En habilitar Oferta\n\n";
        		resultado += "Ofertas habilitadas exitosamente: " + rows;
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
    		String rpta = negocioAlohandes.ingresosRecibidos();
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
    		String rpta = negocioAlohandes.ofertasPopulares();
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
    		String rpta = negocioAlohandes.indiceOcupacion();
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
        		String rpta = negocioAlohandes.alojamientosDisponibles(servicios);
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
    		String rpta = negocioAlohandes.usoTipoUsuario();
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
        		String rpta = negocioAlohandes.usoUsuario(cedula);
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
    
    public void analizarOperacion() {
    	try
    	{
    		String tipo = JOptionPane.showInputDialog (this, "Ingrese el tipo de oferta", "Análisis de Operación", JOptionPane.QUESTION_MESSAGE);
    		if (tipo != null)
    		{
        		String rpta = negocioAlohandes.analizarOperacion(tipo);
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
    
    public void clientesFrecuentes() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese el id de la oferta", "Clientes Frecuentes", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			long id_oferta = Long.valueOf (input);
        		String rpta = negocioAlohandes.clientesFrecuentes(id_oferta);
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
    
    public void ofertasBajaDemanda() {
    	try
    	{
    		String rpta = negocioAlohandes.ofertasBajaDemanda();
    		panelDatos.actualizarInterfaz(rpta);
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void consultarConsumo1() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores id_oferta, fecha1, fecha2 (yyyy-mm-dd): ", "Consumo 1", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			Long id_oferta = Long.parseLong(inputArray[0]);
    			String date1 = inputArray[1];
    			String date2 = inputArray[2];
    			String rpta = negocioAlohandes.consultarConsumo1(id_oferta, date1, date2);
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

    public void consultarConsumo2() {
    	try
    	{
    		String input = JOptionPane.showInputDialog (this, "Ingrese separados por ';' los valores id_oferta, fecha1, fecha2 (yyyy-mm-dd): ", "Consumo 2", JOptionPane.QUESTION_MESSAGE);
    		if (input != null)
    		{
    			String[] inputArray = input.split(";");
    			Long id_oferta = Long.parseLong(inputArray[0]);
    			String date1 = inputArray[1];
    			String date2 = inputArray[2];
    			String rpta = negocioAlohandes.consultarConsumo2(id_oferta, date1, date2);
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
    
    public void consultarFuncionamiento() {
    	try
    	{
    		String rpta = negocioAlohandes.consultarFuncionamiento();
    		panelDatos.actualizarInterfaz(rpta);
    	}
    	catch (Exception e) 
    	{
//			e.printStackTrace();
			String resultado = generarMensajeError(e);
			panelDatos.actualizarInterfaz(resultado);
		}
    }
    
    public void consultarBuenosClientes() {
    	try
    	{
    		String rpta = negocioAlohandes.consultarBuenosClientes();
    		panelDatos.actualizarInterfaz(rpta);
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
		resultado += "\n\nRevise datanucleus.log y alohandes.log para más detalles";
		return resultado;
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
			Method req = InterfazAlohandesApp.class.getMethod ( evento );			
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
            InterfazAlohandesApp interfaz = new InterfazAlohandesApp( );
            interfaz.setVisible( true );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
}
