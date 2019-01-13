package de.dfki.sonogram;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.plaf.*;
import java.awt.image.BufferedImage;
import de.dfki.maths.*;
/**
 * @(#)BioTechSys.VoiceAnalizer.java
 *
 * BioTechSys.VoiceAnalizer application
 *
 * @author 
 * @version 1.00 2009/7/22
 */

public class Sonogram extends JFrame implements ActionListener {
    static final int         build            = 2900;
    static final String      version          = "3.01";
    boolean                  infovisible      = false;
    boolean                  spektrumExist    = false;  // Flag that prevent painting bevore Transformation
    boolean                  updateimageflag  = true;
    boolean                  openingflag      = false;  // is set while file opening is make
    boolean                  transformflag    = false;  // is set during transformation is make
    boolean                  energyflag       = false;  // Is set by Trafo from gad
    boolean                  openenedbysvg    = false;
    boolean                  autoopened       = false;  // set when file is autoopened and unset from DataSourceReader
    boolean                  playbuttonpressed= false;
    boolean                  stopbuttonpressed= false;
    static boolean           java3disinstalled= true;
    public boolean           d3ison           = false;
    short                    timewindowlength = 0 ;     // length of Timewindow to Transform
    byte[]                   timeline         = new byte[2000];
    int                      samplestotal     = 0;      // Number of used samples
    int                      samplesall       = 0;      // Number of all samples obtain from DataSourceReader
    int                      peakx            = 0;      // Peak in Time in Spec-Units
    int                      peaky            = 0;      // Peak in Frequency in Frequ-Diff Units;
    int                      samplerate       = 0;      // Set from GAD
    int                      zoompreviousindex= 0;      // For Zoomback
    int                      gadx             = 20;
    int                      gady             = 20;
    int                      gadw             = 434;
    int                      gadh             = 432;
    String                   url;
    String                   selectedFilter;
    String                   filepath;
    String                   sysopt           = new String(); // Systemoptions generated in Construktor
    String                   storedurl        = "http://free.pages.at/clauer/demo.wav";
    double                   selectedstart    = 0.0;
    double                   selecedwidth     = 1.0;
    double                   selectedstartold = 0.0;
    double                   selecedwidthold  = 1.0;
    EFileChooser             chooser          = new EFileChooser();     // File-open-chooser
    public  DataSourceReader reader           = new DataSourceReader(); // To read Mediafiles
    private Surface          surplot;
    SonoProgressMonitor      progmon;
    HelpDialog               hd               = new HelpDialog             (this);
    LicenseDialog            ld               = new LicenseDialog             (this);
    CepstrumView             cv               = new CepstrumView           (this);
    WaveletView              av               = new WaveletView            (this);
    FormatView               fv               = new FormatView             (this);
    LinearPredictionView     lv               = new LinearPredictionView   (this);
    WaveformView             wv               = new WaveformView           (this);
    AutoCorrelationView      kv               = new AutoCorrelationView    (this);
    PitchDetectorView        pv               = new PitchDetectorView      (this);
    InfoDialog               infod            = new InfoDialog             (this);
    SelectLookAndFeelDialog  slaf             = new SelectLookAndFeelDialog(this);
    GeneralAdjustmentDialog  gad;
    ExportSpectrumSVG        svg              = new ExportSpectrumSVG      (this);
    public  Vector           spektrum         = new Vector();                 // Vector for FFT Transformations
    public  Vector           filehistory      = new Vector();                 // vector for file history
    Vector                   selectedstartpre = new Vector();                 // For Zoomback
    Vector                   selectedwidthpre = new Vector();                 // For Zoomback
    ButtonGroup              bg               = new ButtonGroup();            // To change Color and B/W view
    ButtonGroup              bgwf             = new ButtonGroup();            // For Submenu WinFunkt
    PaintPanel               pp               = new PaintPanel(this);
    PlaySound                player;
    JButton                  openbutton,adjbutton,helpbutton,quitbutton       // Toolbar Buttons
    ,playbutton,stopbutton,revbutton,d3button,cepbutton
    ,zinbutton,zbabutton,forbutton,infobutton,lpcbutton
	,zprebutton,svgbutton,wavbutton,walbutton,autocorrelationbutton,pitchbutton,arrangebutton,fullbutton;
    JMenuItem                quitItem, openItem, aboutItem, adjItem,playItem,revItem,d3Item // Menueitems - MainWindow
    ,stopItem, helpItem,cepItem,zinItem,zbaItem
    ,forItem,infoItem,lpcItem,zpreItem,lafItem
    ,delhistItem,printItem,saveItem,logfrItem
    ,svgItem,sysItem,memItem,impItem,defaultItem,webItem
    ,wavItem,fulItem,walItem,arrItem,closeItem,autocorrelationItem
	,pitchItem,feedbackItem,licenseItem;
    JMenuItem[]              hotlist;
    JMenu                    menuFile;
    JRadioButtonMenuItem     hamItem, rectItem, blaItem,hanItem,triItem
    ,welItem,gauItem;                                 // Menueitems - submenue WinFunkt
    JRadioButtonMenuItem     colItem, bwItem, fireItem,rainItem,greenItem;    // Menueitems - Radiobattons MW
    JCheckBoxMenuItem        negItem;                                         // toogle negative View
    JCheckBoxMenuItem        gridItem;                                        // toogle Gridview
    JCheckBoxMenuItem        logItem;                                         // logartihm view
    Sonogram                 reftosonogram    = this;
    JToolBar                 toolBar          = new JToolBar();

    // For Fullscreen
    Dimension                normaldimension  = new Dimension(0,0);           // used in fullscreen to atore old dimension
    Point                    normalpoint      = new Point (0,0);              // used in Fullscreen to store old pos
    boolean                  fullscreen       = false;                        // FullScreen flag
    static SplashScreen      splash;                                          // Splash Screen
    String                   filename         = "no name";                    // used in Splash Screen
    static boolean           plugin           = false;                        // Plugin Flag
    public boolean           fileisfromurl    = false;

    // settings initalized from the SonogramConfig.xml file for the GAD
    boolean        iinv;
    boolean        iloga;
    boolean        igrid;
    boolean        iautowinl;
    boolean        inorsi;
    boolean        ismof;
    boolean        ismosi;
    boolean        ienergy;
    boolean        iloglpc;
    boolean        ilogfour;
    boolean        iopen8;
    boolean        ismot;
    boolean        ipide;
    boolean        ipifrlim;
    boolean        ienov;
    boolean        isavehi;
    boolean        ipitchfog;
    boolean        ipitchblack;
    boolean        ipitchsm;
    boolean        ispecpl;
    boolean        ilooppl;
    boolean        imonoso;
    boolean        ilogf;
    boolean        isascpo;
    boolean        isaco;
    boolean        iffttrans;
    boolean        iopenlast;
    boolean        ilastwithzoom;
    boolean        iceplog;
    boolean        iwavelines;
    boolean        imute;
    boolean        ilocalpeak;
    boolean        iuniverse;
    boolean        iwallog;
    boolean        isarr;
    boolean        iacdl; //autocorrelation connect to points
    boolean        iacsmooth;
    boolean        iacpitch;
    boolean        iantialise;
    boolean        iwfnorm;
    boolean        icepsmooth;
    boolean        irotate;
    boolean        iperantialias;
    boolean        ipercoord;
    boolean        ipraway;
    boolean        ipsmo;
    boolean        iprsil;
    boolean 	   ipclin;
    boolean 	   iptrack;
    boolean 	   ipfog;

    int            iswalsel;
    int            iswaloct;
    int            islws;
    int            islwf;
    int            islov;
    int            islsdr;
    int            islff;
    int            islfl;
    int            isllc;
    int            islf;
    int            islls;
    int            isllff;
    int            islsy;
    int            islsx;
    int            islpf;
    int            islla;
    int            isllf;
    int            islcep;
    int            islwavetime;
    int            isldb;
    int            isc;
    int            isr;
    int            ilaf;
    int            isacwl; //autocorrelation window length
    int            isacws; //autocorrelation window shift
    int            isacpwl;
    int            isacpws;
    int            isacpmax;
       
    Color          ibgcol;

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for the  Songram application.
     * Handles the WindowClosing event and make initialisations for the Filechooser,
     * the Toolbar and the Menubar.
     */
    public Sonogram (String openpath) {

        splash.setProgress(10,"10% ID de llamada de verificación de Plugin");
	setSize (748,483);
	int scw  = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int sch = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	setLocation(scw/2-getWidth()/2,sch/2-getHeight()/2);

        // Check if Sonogram is called from Plugin
	// the plugin functionality may be outdated...
        try {
            // Check Class for exist
            Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
            System.out.println("Voice Analyser comenzó como PLUGIN.");
            plugin = true;
            // Generate Signature for funktion to get Funktion
            Class[] signature  = new Class[1];
            signature[0] = this.getClass();
            // get funktion
            java.lang.reflect.Method function = sap.getMethod("sonogramCallBackSetReference",signature);
            // Generate parameter for funktion
            Object[] parameter = new Object[1];
            parameter[0] = this;
            // call static funktion, therefore null as class object
            function.invoke(null,parameter);
        } catch (Throwable throwable) {
            plugin = false;
        }

        // read startup settings from the SonogramConf.xml configuration file
        splash.setProgress(12,"10% Lectura de Archivo de la configuración de VoiceAnalyzer.xml ");
        ReadConfig rf  = new ReadConfig(this);
        if (isaco==false) {
            splash.setProgress(15,"15% Inicializar ajustes predeterminados .");
            initGadToDefault();
        }
        // instanciate the GUI elements
        splash.setProgress(20,"20% Crear el Menú de Bara");
        createMenu ();
        splash.setProgress(25,"25% Crear diálogo de ajuste General");
        gad = new GeneralAdjustmentDialog(this);
        // change the icon
        splash.setProgress(30,"30% Set Window Icons.");
        Toolkit tk = Toolkit.getDefaultToolkit();
        setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));
	

        // set the Tooltip colors and the window title
        splash.setProgress(35,"35% Cambiar Colores del ToolTip ");
        UIManager.put("ToolTip.foreground",
                      new ColorUIResource(Color.red));
        UIManager.put("ToolTip.background",
                      new ColorUIResource(new Color(194,251,0)));
        splash.setProgress(35,"35% Añadir a la pintura Grupo Ventana Principal.");
        setTitle("BioTechSys Voice Analyzer Visible Speech verción " + version);
        getContentPane().add(pp);

        // instanciate further  GUI elements
        splash.setProgress(40,"40% Instanciate the Tool Bar Buttons");
        toolBar.setBorderPainted(true);
        addButtons(toolBar);
        enableItems(false);

	// disable some toolbar elements
        splash.setProgress(45,"45% Disable some Toolbar Buttons");
        zprebutton.setEnabled(false);
        zpreItem.setEnabled(false);
        stopItem.setEnabled(false);
        closeItem.setEnabled(false);
        stopbutton.setEnabled(false);

        toolBar.putClientProperty("JToolBar.isRollover",new Boolean (true));

        splash.setProgress(50,"50% Add Toolbar to BioTechSys.VoiceAnalyzer's Main Window");
        getContentPane().add(toolBar, BorderLayout.NORTH);

        splash.setProgress(55,"55% Crete the File-Filters for the File Chooser.");
        chooser.setApproveButtonText("Okay");                              // Initsequence for Filecooser
        chooser.setDialogTitle("Seleccione un archivo de audio o vídeo");
        MediaFileFilter ffwave = new MediaFileFilter("wav","Wave Audio");
        MediaFileFilter ffau   = new MediaFileFilter("au","Sun Audio");
        MediaFileFilter ffaiff = new MediaFileFilter("aiff","Apple Audio");
        MediaFileFilter ffavi  = new MediaFileFilter("avi","AVI Video");
        MediaFileFilter ffswf  = new MediaFileFilter("swf","Flash SWF Video");
        MediaFileFilter ffspl  = new MediaFileFilter("spl","Flash SPL Video");
        MediaFileFilter ffgsm  = new MediaFileFilter("gsm","GSM Audio");
        MediaFileFilter ffmvr  = new MediaFileFilter("mvr","MVR IBM HotMedia");
        MediaFileFilter ffmpg  = new MediaFileFilter("mpg","MPEG-1 Layer I");
        MediaFileFilter ffmp2  = new MediaFileFilter("mp2","MPEG-1 Layer II");
        MediaFileFilter ffmov  = new MediaFileFilter("mov","MOV QuickTime Video");
        MediaFileFilter ffall  = new MediaFileFilter("","All Mediafiles");

        splash.setProgress(60,"60% Agregue el archivo a la Filtros-Selector de archivos.");
        ffall.aceptAllMediaFiles(true);
        chooser.addChoosableFileFilter(ffwave);
        chooser.addChoosableFileFilter(ffau);
        chooser.addChoosableFileFilter(ffaiff);
        chooser.addChoosableFileFilter(ffavi);
        chooser.addChoosableFileFilter(ffswf);
        chooser.addChoosableFileFilter(ffspl);
        chooser.addChoosableFileFilter(ffgsm);
        chooser.addChoosableFileFilter(ffmvr);
        chooser.addChoosableFileFilter(ffmpg);
        chooser.addChoosableFileFilter(ffmp2);
        chooser.addChoosableFileFilter(ffmov);
        chooser.addChoosableFileFilter(ffall);

        splash.setProgress(65,"65% Añadir la ventana de cierre del evento");
        WindowListener wndCloser = new WindowAdapter()                        // Windowlistener
                                   {
                                       public void windowClosing(WindowEvent e) {
                                           System.out.println("--> Bye");
                                           saveConfig();
                                           if (plugin == true) {
                                               try     // Call Plugin exit class
                                               {
                                                   // Check Class for exist
                                                   Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
                                                   // Generate Signature for funktion to get Funktion
                                                   Class[] signature  = new Class[0];
                                                   // get funktion
                                                   java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd",signature);
                                                   // Generate parameter for funktion
                                                   Object[] parameter = new Object[0];
                                                   // call static funktion, therefore null as class object
                                                   function.invoke(null,parameter);
                                               } catch (Throwable throwable) {
                                                   System.out.println("--> No Plugin Class not found.");
                                                   plugin = false;
                                               }
                                               dispose();
                                               return;
                                           }
                                           System.exit(0);
                                       }
                                   };
        addWindowListener(wndCloser);
        reader.setMainRef(this);
        // Set Look and Feel
        splash.setProgress(70,"70% Permitir el analizador de voz\"Look And Feel\"");
        setLookAndFeel(ilaf);
        slaf.slider.setValue(ilaf);
        // Check for AutoOpen
        splash.setProgress(80,"80% Compruebe si cualquier archivo debe ser abierto automaticamente");
        if (openpath!=null) {     // If Argument is given to main as parameter
            splash.setProgress(85,"80% Trate de abrir el archivo, mientras que los medios de comunicación de inicio");
            openFile(openpath);                   // from Konsole
        } else {

            if (filepath != null && iopenlast == true) {
                if (filepath.substring(0,2).equals("ft")==true || filepath.substring(0,2).equals("ht")==true) {
                    splash.setProgress(90,"90% Pregunte si el usuario remoto de archivos de red debe estar abierto");
                    int confirm = JOptionPane.showOptionDialog(this,
                                  "Last time BioTechSys.VoiceAnalyzer finished a remote Network file was open:\n" + filepath +
                                  "\nContinue open this remote File over the Network ?", "Open last Remote Network File ?"
                                  ,JOptionPane.YES_NO_OPTION
                                  ,JOptionPane.QUESTION_MESSAGE
                                  ,null,null,null);
                    if (confirm == 0) {
                        splash.setProgress(100,"100% Abrir el archivo remoto a través de la red");
                        setVisible (true);
                        openFile(filepath);               // Open from Network
                        System.out.println("--> Abre el archivo automáticamente a través de la Red.");
                    }
                } else {
                    File file = new File(filepath.substring(5));
                    if (file.exists()==true) {          // Check for file if existsy
                        splash.setProgress(100,"100% Automatical open the last File");
                        setVisible (true);
                        openFile(filepath);            // Open from local file system
                        System.out.println("--> File automatical opened on local System");
                    } else {                             // If file do
                        messageBox("Automatical Opening","You have selected automatical open in general adjustment dialog,\nbut stored file does not exist !\n"+filepath,1);
                        splash.setProgress(95,"95% The last opened File does not Exist !");
                        zoompreviousindex = 0;
                        selectedstartold  = 0.0;
                        selecedwidthold   = 1.0;
                        selecedwidth      = 1.0;
                        selectedstart     = 0.0;
                        filepath = null;
                    }
                }
            }
        }
        splash.setProgress(100,"100% BioTechSys.VoiceAnalyzer is coming up now !!!");
        setVisible (true);
        if (isarr == true && iopenlast == true) {
            arrangeWindows();
        }
    }



    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * The main function allocates the Sonogram class and intialise the backbone system.
     * Splash screen in called here and the JMF and Java3D classes are checked here for existing.
     */
    public static void main(String[] args) {

        String openpath;
        splash = new SplashScreen("Splash.jpg",350,200,30,new Color(0,0,0),new Color(255,0,0));
        splash.setProgress(5,"1% This is BioTechSys.VoiceAnalyzer " + version + " BUILD" + build);
        System.out.println("\n    ****************************************");
        System.out.println(" * BioTechSys.VoiceAnalyzer " + version + "   *");
        System.out.println(" *   BUILD " + build + "       		 *");
        System.out.println(" *  orangel@biometrics-solutions.com *");
        System.out.println(" ******************************************");
        openpath = null;
        //java windows borderes enabled (since java 1.4)
        try {
            JDialog.setDefaultLookAndFeelDecorated(true);
            JFrame.setDefaultLookAndFeelDecorated(true);
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
        } catch (Throwable throwable) {
            System.out.println("--> NO JAVA 1.6 (NO Dynamic GUI)");
        }
        // check for java media Framework
        try {
            Class.forName("javax.media.Player");
        } catch (Throwable throwable) {
            JOptionPane.showMessageDialog(null,"Java Media Framework no está instalado.\n Please look at www.java.sun.com.","JMF ERROR",0);
            System.exit(0);
        }	// Check for kunststoff.jar
        try {
            Class.forName("com.incors.plaf.kunststoff.KunststoffTheme");
        } catch (Throwable throwable) {
            System.out.println("--> Kunststoff.jar is not aviable !!!");
            JOptionPane.showMessageDialog(null," No es posible crear BioTechSys.VoiceAnalyzer maincllass\n Please include Sonogram.jar in Classpath.","BioTechSys.Voice Analyzer clase no se encuentra !",0);
            if (plugin == true) {
                try     // Call Plugin exit class
                {
                    // Check Class for exist
                    Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
                    // Generate Signature for funktion to get Funktion
                    Class[] signature  = new Class[0];
                    // get funktion
                    java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd",signature);
                    // Generate parameter for funktion
                    Object[] parameter = new Object[0];
                    // call static funktion, therefore null as class object
                    function.invoke(null,parameter);
                } catch (Throwable t) {
                    System.out.println("--> No Plugin Class not found.");
                    plugin = false;
                }
                return;
            }
            System.exit(0);
            return;
        }
        splash.setProgress(3,"3% Comprobar si el Java Media Framework está disponible");
        openpath = null;
        // Check if JMF is installed
        try {
            Class.forName("javax.media.Player");
        } catch (Throwable throwable) {
            System.out.println("--> Java Media Framework is not installed !!!");
            JOptionPane.showMessageDialog(null,"Java Media Framework is not installed !\nSonogram can not start.\nSee at www.java.sun.com\nand install it.\nhttp://java.sun.com/products/java-media/jmf/","JMF not found",0);
            if (plugin == true) {
                try     // Call Plugin exit class
                {
                    // Check Class for exist
                    Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
                    // Generate Signature for funktion to get Funktion
                    Class[] signature  = new Class[0];
                    // get funktion
                    java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd",signature);
                    // Generate parameter for funktion
                    Object[] parameter = new Object[0];
                    // call static funktion, therefore null as class object
                    function.invoke(null,parameter);
                } catch (Throwable t) {
                    System.out.println("BioTechSys.VoiceAnalyzer CALLBACK CLASS NOT FOUND.");
                    plugin = false;
                }
                return;
            }
            System.exit(0);
            return;
        }
        splash.setProgress(6,"6% Check if Java3D is available");
        String OS = System.getProperties().getProperty("os.name");
        System.out.println("--> Operating System: " + OS);
        // 	if (OS.equals("Mac OS X") == false) {
        try {
            Class.forName("javax.media.j3d.Canvas3D");
        } catch (Throwable throwable) {
            System.out.println("--> Java3D is not installed !!!");
            JOptionPane.showMessageDialog(null,"The Java3D extension is not installed ! In order to generate\n3D Perspectograms you must have Java3D installed. Java3D\ncan be obtained from www.java.sun.com . BioTechSys.VoiceAnalyzer will\nstart up tought normal without Java3D.","Please install Java3D",1);
            java3disinstalled = false;
        }



        // 	}
        // 	else // in case of Mac OSX
        // 	    java3disinstalled = false;
        splash.setProgress(8,"8% Check for PlugIn or Start Mode");
        if (args.length == 0)
            System.out.println("--> No argument given. Start in normal mode.");
        else if (args.length == 1) {
            openpath = args[0];
            System.out.println("--> Try to open File: " + openpath);
        } else if (args.length == 2) {
            // Here SMARTKOM handling
        } else if (args.length > 1)
            System.out.println("--> If you want to open a File please give ONE filename as argument.");
        splash.setProgress(9,"9% Instanciate BioTechSys.VoiceAnalyzer's main Application Class");
        try {
            Sonogram theApp = new Sonogram(openpath);
        } catch (Exception e) {
            System.out.println("--> Unknown Error in overall MAIN " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Unhandled Error\n" + e,"Unhandled Error",0);
        }
        System.out.println("--> main Class terminated.");
    }
    //-------------------------------------------------------------------------------------------------------------------------
    // Set Look and Feel
    public void setLookAndFeel(int which) {
	try {
	    //PLAF-Klasse auswaehlen
	    if (which == 0) {
		javax.swing.plaf.metal.MetalLookAndFeel mlaf = new javax.swing.plaf.metal.MetalLookAndFeel();
		mlaf.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
		    UIManager.setLookAndFeel(mlaf);
		    System.out.println("--> Cambiar LooAndFeel a METAL");
	    } else if (which == 1) {
		com.sun.java.swing.plaf.motif.MotifLookAndFeel olaf = new com.sun.java.swing.plaf.motif.MotifLookAndFeel();
		UIManager.setLookAndFeel(olaf);
		System.out.println("--> Cambiar LooAndFeel a MOTIV");
	    } else if (which == 2) {
		com.sun.java.swing.plaf.windows.WindowsLookAndFeel wlaf = new com.sun.java.swing.plaf.windows.WindowsLookAndFeel();
		UIManager.setLookAndFeel(wlaf);
		System.out.println("--> Cambiar LooAndFeel a WINDOWS");
	    } else if (which == 3) {
		com.incors.plaf.kunststoff.KunststoffLookAndFeel kunststoffLF = new com.incors.plaf.kunststoff.KunststoffLookAndFeel();
		kunststoffLF.setCurrentTheme(new com.incors.plaf.kunststoff.KunststoffTheme());
		UIManager.setLookAndFeel(kunststoffLF);
		System.out.println("--> Cambiar LooAndFeel a PLASTIC");
	    } else if (which == 4) {
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		System.out.println("--> Cambiar LookAndFeel a nativ OS");
	    } else if (which == 5) {
		javax.swing.plaf.metal.MetalLookAndFeel mlaf = new javax.swing.plaf.metal.MetalLookAndFeel();
		mlaf.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
		    UIManager.setLookAndFeel(mlaf);
		    System.out.println("--> Cambiar LooAndFeel a METAL");
	    }
	    ilaf = which;
 	    SwingUtilities.updateComponentTreeUI(this);
	    SwingUtilities.updateComponentTreeUI(infod);
	    SwingUtilities.updateComponentTreeUI(hd);
	    SwingUtilities.updateComponentTreeUI(slaf);
	    SwingUtilities.updateComponentTreeUI(chooser);
	    SwingUtilities.updateComponentTreeUI(svg);
	    UIManager.getLookAndFeelDefaults().put("ClassLoader", getClass().getClassLoader());
             if (gad != null)
                 SwingUtilities.updateComponentTreeUI(gad);
         } catch (Exception e) {
             messageBox("LookAndFeel","Error while change Look and Feel",2);
         }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This function is called from DataSourceReader when all reading-Threads
     * are back of from other functions. It cuts the audio-samplevector in 
     * pieces and transformet them with the  FFT Transformation. 
     * Also it generates a time or energy View for the bottom of mainwindow.
     * this function catch all Exceptions and display a MessageBox.
     */
    public void readerIsBack() {
        try {
            // Initialize some stuf for spectrum, transformation...
            float[]                   spekbuffer           = null;
            Byte                      b;                                               // Tempü
            short                     sqrtsize;                                        // Squareroot of Vector-length
            FastFourierTransform      fft                  = new FastFourierTransform();
            LinearPredictionTransform lpt                  = new LinearPredictionTransform();
            byte                      logdualis            = 1;
            int                       offset               = 0;
            int                       transformationlength = 0;
            boolean                   ffttrans             = gad.rfft.isSelected();    // Which Transformation is Selected
            // Initialize stuff for TimeLineArray
            double timetostreamfakt;
            int    streampoint;                                                        // Point in Samples-Array
            byte   timelinemax                             = -128;
            byte   timelinemin                             = 127;
            Byte   b1,b2;
            int    energywinlength                         = 50;
            double sum;
            // Progressdialog and Initalize some stuff
            if (gad.highlightedbutton < 3)
                gad.highLightButton(0);
            progmon = new SonoProgressMonitor(reftosonogram,"","",0,100);
            progmon.setProgress(1);
            progmon.setNote("Initialize for generating BioTechSys.VoiceAnalyzer for File: " + filename);
            transformflag = true;
            setCursor(Cursor.WAIT_CURSOR);
            openingflag = false;
            System.out.println("--> Begin Transformation.");
            spektrum.removeAllElements();
            // Store Old Numbers to calculate inner zooms
            selectedstartold = selectedstart;
            selecedwidthold  = selecedwidth;
            samplesall       = reader.audioStream.size();
            samplestotal     = (int)((double)samplesall * selecedwidth);
            timetostreamfakt = samplesall/2000.0;                                     // Faktor to read the Timeline-Vektor
            // if Auto-length is selected
            if (gad.cauto.isSelected()==true) {
                timewindowlength = 1;
                sqrtsize = (short)Math.sqrt(samplestotal);
                do {
                    timewindowlength*=2;
                    logdualis ++;
                } while (timewindowlength < sqrtsize);
                timewindowlength *= 2;
                gad.sliderwinsize.setValue(logdualis);
            } else
                timewindowlength = (short)Math.pow(2.0,gad.sliderwinsize.getValue());
            // allokate Windowbuffer
            float windowbuffer[] = null;
            // For LPC-Transformation Transformationlength is "previous Samples" in GAD
            if (ffttrans==false) {                                                 // LPC
                progmon.setNote("Calcular el espectro lineal con-predictivo-Codificación-Transformación del Fichero: " + filename);
                transformationlength = (short)gad.sliderlpcsamfutur.getValue();
                System.out.println("--> transformation length for LPC: " + transformationlength);
            } else {
                progmon.setNote("Calcular el espectro con rápida de Fourier-Transformación del Fichero: " + filename);
                transformationlength = timewindowlength;                           // FFT
            }
            System.out.println("--> Use " + transformationlength + " transformationlegth");
            // Then calculate Windowingbuffers which is selected in GeneralAdjustmentDialog
            int winfunclen = 0;
            if (ffttrans==true)
                winfunclen = transformationlength;
            else
                winfunclen = timewindowlength;
            if (hamItem.isSelected() == true) {
                windowbuffer = WindowFunktion.hammingWindow(winfunclen);
                selectedFilter = "Hamming";
            }
            if (hanItem.isSelected() == true) {
                windowbuffer = WindowFunktion.hanningWindow(winfunclen);
                selectedFilter = "Hanning";
            }
            if (blaItem.isSelected() == true) {
                windowbuffer = WindowFunktion.blackmanWindow(winfunclen);
                selectedFilter = "Blackman";
            }
            if (rectItem.isSelected() == true) {
                windowbuffer = WindowFunktion.rectangleWindow(winfunclen);
                selectedFilter = "Rectangle";
            }
            if (triItem.isSelected() == true) {
                windowbuffer = WindowFunktion.triangleWindow(winfunclen);
                selectedFilter = "Triangle";
            }
            if (gauItem.isSelected() == true) {
                windowbuffer = WindowFunktion.gaussWindow(winfunclen);
                selectedFilter = "Gauss";
            }
            if (welItem.isSelected() == true) {
                windowbuffer = WindowFunktion.welchWindow(winfunclen);
                selectedFilter = "Welch";
            }
            progmon.setProgress(10);
            // Main Transformation-loop
            double overlapping = (double)gad.sliderwinspeed.getValue();
            if (gad.coverlapping.isSelected()==false)
                overlapping = 1.0;
            for (double i=0;i<(samplestotal-transformationlength);i+=((double)timewindowlength/overlapping)) {
                if(progmon.isCanceled() == true) {
                    System.out.println("--> CANCEL Button is pressed while Transformation.");
                    setCursor(Cursor.DEFAULT_CURSOR);
                    progmon.close();
                    setTitle("BioTechSys.VoiceAnalyzer " + version );
                    spektrumExist = false;
                    updateimageflag = true;
                    repaint();
                    return;
                }
                progmon.setProgress(10+(int)((double)i/(double)(samplestotal-timewindowlength)*70.0));
                Thread.sleep(1);
                float timebuffer[]   = new float[transformationlength];           // allokate the buffer for timeSignal
                offset = (int)((double)samplesall*selectedstart);                 // offset for marked selection
                for(int v=0;v<transformationlength;v++) {                          // Copy from DataSourceReader
                    b = (Byte)reader.audioStream.get(v+(int)i+offset);
                    if (ffttrans == true)
                        timebuffer[v] = b.floatValue() * windowbuffer[v];
                    else
                        timebuffer[v] = b.floatValue();
                }
                if(ffttrans == false)                                             // Select Transformtion
                    spekbuffer = lpt.doLinearPredictionTransform(timebuffer,timewindowlength,gad.sliderlpccoef.getValue(),windowbuffer);
                else
                    spekbuffer = fft.doFFT(timebuffer);	                	  // Transformation Call for FFT
                // Smooth over Frequency
                if (gad.csmooth.isSelected() == true) {                           // if Smooth Spektrum ower frequency is set
                    float [] spekbuffertmp = new float[timewindowlength/2];
                    for(int k=2;k<(timewindowlength/2-2);k++)
                        spekbuffertmp[k] =
                            (spekbuffer[k-2] + spekbuffer[k-1] + spekbuffer[k]
                             + spekbuffer[k+1] + spekbuffer[k+2])/5.0f;
                    spekbuffertmp[1]= (spekbuffertmp[0] + spekbuffertmp[1] + spekbuffertmp[2])/3.0f;  // BOUNDS
                    spekbuffertmp[0]= (spekbuffertmp[0] + spekbuffertmp[1])/2.0f;
                    spekbuffertmp[timewindowlength/2-2] = (spekbuffertmp[timewindowlength/2-1]+spekbuffertmp[timewindowlength/2-2]+spekbuffertmp[timewindowlength/2-3])/3.0f;
                    spekbuffertmp[timewindowlength/2-1] = (spekbuffertmp[timewindowlength/2-1]+spekbuffertmp[timewindowlength/2-2])/2.0f;
                    spektrum.addElement(spekbuffertmp);	                          // Adding Transformvector to Spektrum
                } else
                    spektrum.addElement(spekbuffer);	                          // Adding Transformvector to Spektrum
            }
            System.out.println("--> Transformation endet  " + spektrum.size() + " Spektrums");
            System.out.println("--> Windowlentgh: " +  timewindowlength);
            // Smooth ower time is set
            if (gad.csmoothx.isSelected()==true) {
                float[] spekbuffersuba;                                           // b,a,0,a,b
                float[] spekbuffersubb;
                float[] spekbufferadda;
                float[] spekbufferaddb;
                float[] spekbuffertmp;
                for (int time=2;time<spektrum.size()-2;time++) {
                    spekbuffersubb = (float[])spektrum.get(time-2);
                    spekbuffersuba = (float[])spektrum.get(time-1);
                    spekbuffer     = (float[])spektrum.get(time);
                    spekbufferadda = (float[])spektrum.get(time+1);
                    spekbufferaddb = (float[])spektrum.get(time+2);
                    spekbuffertmp  = new float[timewindowlength/2];
                    for (int frequency = 0;frequency<timewindowlength/2;frequency++) {
                        spekbuffertmp[frequency]     = (
                                                           spekbuffer[frequency]
                                                           +spekbufferadda[frequency]
                                                           +spekbufferaddb[frequency]
                                                           +spekbuffersuba[frequency]
                                                           +spekbuffersubb[frequency] ) / 5.0f;
                    }
                    spektrum.setElementAt(spekbuffertmp,time);
                }
                for (int frequency = 0;frequency<timewindowlength/2;frequency++) { // Bounds
                    spekbuffer     = (float[])spektrum.get(0);
                    spekbuffersuba = (float[])spektrum.get(1);
                    spekbuffersubb = (float[])spektrum.get(2);
                    spekbuffersuba[frequency] = (spekbuffersubb[frequency]+spekbuffer[frequency]+spekbuffersuba[frequency])/3.0f;
                    spekbuffer[frequency]     = (spekbuffer[frequency]+spekbuffersuba[frequency])/2.0f;
                    spekbuffer     = (float[])spektrum.get(spektrum.size()-1);
                    spekbuffersuba = (float[])spektrum.get(spektrum.size()-2);
                    spekbuffersubb = (float[])spektrum.get(spektrum.size()-3);
                    spekbuffersuba[frequency] = (spekbuffersubb[frequency]+spekbuffer[frequency]+spekbuffersuba[frequency])/3.0f;
                    spekbuffer[frequency]     = (spekbuffer[frequency]+spekbuffersuba[frequency])/2.0f;
                }
            }
            System.out.println("--> Smoothed over time");
            // Logarithm Amplitude
            if (logItem.isSelected() == true) {
                int logscale = 0;
                if (gad.sliderlog.getValue() == 1)
                    logscale = 1;
                if (gad.sliderlog.getValue() == 2)
                    logscale = 4;
                if (gad.sliderlog.getValue() == 3)
                    logscale = 10;
                if (gad.sliderlog.getValue() == 4)
                    logscale = 30;
                if (gad.sliderlog.getValue() == 5)
                    logscale = 100;
                double  logkonst = 255.0/Math.log(256.0);
                for (int time=0;time<spektrum.size();time++) {
                    spekbuffer     = (float[])spektrum.get(time);
                    for (int frequency = 0;frequency<timewindowlength/2;frequency++) {
                        spekbuffer[frequency] = (int)(Math.log(spekbuffer[frequency]*logscale+1)*logkonst);
                    }
                }
            }
            System.out.println("--> Logarithm amplitude");
            // Generate Timelinearray
            if (gad.cenergy.isSelected()== true)
                energyflag = true;               // Check in GAD
            else
                energyflag = false;
            for (int t=0;t<2000;t++) {                                             // Copy and find min/max of timeline
                streampoint = (int)((double)t*timetostreamfakt);
                if (energyflag == false) {                                         // If Energytimesignal is not Selected
                    if (streampoint > 0 && streampoint < samplesall) {
                        b = (Byte)reader.audioStream.get(streampoint);
                        timeline[t] = b.byteValue();
                    }
                }
                if (energyflag == true) {                                          // If Energytimesignal is Selected
                    if ((streampoint > energywinlength) && (streampoint < samplesall-energywinlength)) {
                        sum = 0.0f;
                        for (int i=0;i<energywinlength;i++) {
                            b1   = (Byte)reader.audioStream.get(streampoint+i);
                            b2   = (Byte)reader.audioStream.get(streampoint-i);
                            sum += Math.pow(b1.doubleValue(),2.0);
                            sum += Math.pow(b2.doubleValue(),2.0);
                        }
                        timeline[t] = (byte)Math.sqrt(sum/energywinlength/2);
                    } else
                        timeline[t] = 0;
                }
                if (timeline[t] < timelinemin)
                    timelinemin = timeline[t];
                if (timeline[t] > timelinemax)
                    timelinemax = timeline[t];
            }
            // Normalize Timeline
            progmon.setProgress(85);
            Thread.sleep(1);
            float peak;
            if (timelinemax > -timelinemin)
                peak = (float)timelinemax;
            else
                peak = (float)-timelinemin;
            for (int t=0;t<2000;t++) {
                timeline[t]=(byte)((float)timeline[t]/peak*127.0f);
            }
            // Some Stuff at end of This Routine
            System.out.println("--> begin normalize");
            normalizeSpekt();
            progmon.setProgress(88);
            Thread.sleep(1);
            spektrumExist = true;
            if (player != null) {
                //player.close();
                player.timeThread.stop();
            }
            player = new PlaySound(url,this);
            updateimageflag = true;
            openingflag     = false;
            pp.plstart  = 0.0;
            pp.plstop   = 1.0;
            pp.plbutton = 0.0;
            if (true)
                progmon.setProgress(90);
            Thread.sleep(1);
	    // Update here the file history
            // filehistory = VECTOR with STRING elements
            boolean alwaysinlist = false;
            for (int i=0;i<filehistory.size();i++) {                     // Check if Element exist
                String tmpstr = (String)filehistory.get(i);
                if (tmpstr.equals(filepath))
                    alwaysinlist = true;
            }
            if (alwaysinlist == false) {                                  // Add Menue entry
                JMenuItem[] hotlisttmp = new JMenuItem[filehistory.size()];
                for (int i=0;i<filehistory.size();i++) {                 // Store old historylist
                    hotlisttmp[i] = hotlist[i];
                }
                filehistory.addElement(filepath);                        // add String to Vector
                hotlist = new JMenuItem[filehistory.size()];             // Allocate new bigger MenueItem-array
                int newlen = filehistory.size();                         // New Historysize
                for (int i=0;i<newlen-1;i++) {                           // Restore old Historylist
                    hotlist[i] = hotlisttmp[i];
                }
                String tmp = (String)filehistory.get(newlen-1);          // Get New String from new Path

                if (tmp.substring(0,4).equals("http")==true) {
                    if (tmp.length()>20)                                     // Length limitation
                        hotlist[newlen-1] = new JMenuItem(newlen-1 + "  HTTP..." + tmp.substring(tmp.length()-16), new ImageIcon(Sonogram.class.getResource("hin.gif")));
                    else
                        hotlist[newlen-1] = new JMenuItem(newlen-1 + "  " + tmp, new ImageIcon(Sonogram.class.getResource("hin.gif")));
                }
                if (tmp.substring(0,3).equals("ftp")==true) {
                    if (tmp.length()>20)                                     // Length limitation
                        hotlist[newlen-1] = new JMenuItem(newlen-1 + "  FTP..." + tmp.substring(tmp.length()-17), new ImageIcon(Sonogram.class.getResource("hin.gif")));
                    else
                        hotlist[newlen-1] = new JMenuItem(newlen-1 + "  " + tmp, new ImageIcon(Sonogram.class.getResource("hin.gif")));
                }
                if (url.substring(0,4).equals("file")==true) {
                    if (tmp.length()>20)                                     // Length limitation
                        hotlist[newlen-1] = new JMenuItem(newlen-1 + " ....." + tmp.substring(tmp.length()-20), new ImageIcon(Sonogram.class.getResource("his.gif")));
                    else
                        hotlist[newlen-1] = new JMenuItem(newlen-1 + "  " + tmp, new ImageIcon(Sonogram.class.getResource("hin.gif")));
                }
                menuFile.add(hotlist[newlen-1],7+newlen);
                hotlist[newlen-1].addActionListener(this);
		int num = newlen+1;
		hotlist[newlen-1].setToolTipText("<html>Path to file"+num+":<br><b>"+(String)filehistory.get(newlen-1)+"</b>");

                KeyStroke key = KeyStroke.getKeyStroke('0');
                if (newlen-1==0)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0,java.awt.Event.CTRL_MASK);
                if (newlen-1==1)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1,java.awt.Event.CTRL_MASK);
                if (newlen-1==2)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2,java.awt.Event.CTRL_MASK);
                if (newlen-1==3)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3,java.awt.Event.CTRL_MASK);
                if (newlen-1==4)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4,java.awt.Event.CTRL_MASK);
                if (newlen-1==5)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5,java.awt.Event.CTRL_MASK);
                if (newlen-1==6)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6,java.awt.Event.CTRL_MASK);
                if (newlen-1==7)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7,java.awt.Event.CTRL_MASK);
                if (newlen-1==8)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8,java.awt.Event.CTRL_MASK);
                if (newlen-1==9)
                    key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_9,java.awt.Event.CTRL_MASK);
                hotlist[newlen-1].setAccelerator(key);
            }
            //Garabage Collection and Some Stuff
            gad.highLightButton(0);
            progmon.setProgress(95);
            Thread.sleep(1);
            System.gc();
            progmon.setProgress(100);
            Thread.sleep(100);
            repaint();
            enableItems(true);
            if (zoompreviousindex==0)
                zprebutton.setEnabled(false);
            else
                zprebutton.setEnabled(true);
            zpreItem.setEnabled(false);
            stopItem.setEnabled(false);
            stopbutton.setEnabled(false);
            if (java3disinstalled==false) {
                d3button.setEnabled(false);
                d3Item.setEnabled(false);
            }
            if (openenedbysvg==true) {
                openenedbysvg = false;
                gad.btro.setEnabled(true);
                gad.sliderwinfunktion.setEnabled(true);
                gad.clog.setEnabled(true);
                gad.sliderlog.setEnabled(true);
                gad.csmoothx.setEnabled(true);
                gad.csmooth.setEnabled(true);
                gad.cenergy.setEnabled(true);
                gad.coverlapping.setEnabled(true);
                gad.sliderwinspeed.setEnabled(true);
                gad.sliderwinsize.setEnabled(true);
                gad.cauto.setEnabled(true);
                gad.csampl.setEnabled(true);
            }
            transformflag = false;
            progmon.close();
            // And then EXCEPTIONHANDLING for all EXCEPTIONS
        } catch (Exception e) {
            progmon.close();
            transformflag = false;
            setCursor(Cursor.DEFAULT_CURSOR);
            spektrumExist = false;
            updateimageflag = true;
            repaint();
            System.out.println(e);
            e.printStackTrace();
            messageBox("Error in Routine READERISBACK","Error while Transformation !!!\n See konsole for more Details !!!",0);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This Method adds the Buttons to Toolbar and give them Eventlisteners.
     * toolbar - Reference to Toolbar-Object
     */
    protected void addButtons(JToolBar toolBar) {

// 	toolBar.setMargin(new Insets(0,0,0,0));
//	toolBar.setBorderPainted(false);
//	toolBar.setFloatable(false);

	Dimension buttonSize = new Dimension(34,34);

        openbutton = new JButton(new ImageIcon(Sonogram.class.getResource("open.gif")));
	openbutton.setPreferredSize(buttonSize);
	openbutton.setMaximumSize(buttonSize);
	openbutton.getInsets().set(0,0,0,0);
	openbutton.setIconTextGap(0);
        toolBar.add(openbutton);
        openbutton.setToolTipText("<html>Open local <b>Audio</b> or <b>Video</b> File      <font color=black size=-2>Ctrl-O");
        adjbutton = new JButton(new ImageIcon(Sonogram.class.getResource("adj.gif")));
	adjbutton.setPreferredSize(buttonSize);
	adjbutton.setMaximumSize(buttonSize);
        toolBar.add(adjbutton);
        fullbutton = new JButton(new ImageIcon(Sonogram.class.getResource("full.gif")));
	fullbutton.setPreferredSize(buttonSize);
	fullbutton.setMaximumSize(buttonSize);
        fullbutton.setToolTipText("<html>Zoom the main sonogram<br>Window to <b>Fullscreen</b>      <font color=black size=-2>F11");
        toolBar.add(fullbutton);
        adjbutton.setToolTipText("<html>All BioTechSys.VoiceAnalyzer settings are justified<br> in the <b>General Adjustment Dialog</b>      <font color=black size=-2>Ctrl-A");
        arrangebutton = new JButton(new ImageIcon(Sonogram.class.getResource("arr.gif")));
	arrangebutton.setPreferredSize(buttonSize);
	arrangebutton.setMaximumSize(buttonSize);
        arrangebutton.setToolTipText("<html>Open <b>all analyze windows</b> and arrange the <br>automatically to the optimal screen position      <font color=black size=-2>F12");
        toolBar.add(arrangebutton);
        infobutton = new JButton(new ImageIcon(Sonogram.class.getResource("info.gif")));
	infobutton.setPreferredSize(buttonSize);
	infobutton.setMaximumSize(buttonSize);
        infobutton.setToolTipText("<html>Parameter, values and statistics<br> about the Signal and the Sonogram      <font color=black size=-2>Ctrl-Y");
        toolBar.add(infobutton);
        svgbutton = new JButton(new ImageIcon(Sonogram.class.getResource("svg.gif")));
	//svgbutton.setPreferredSize(buttonSize);
	//svgbutton.setMaximumSize(buttonSize);
        //	toolBar.add(svgbutton);
        //svgbutton.setToolTipText("Save spectrum as Scalable Vector Graphic (SVG file) Crtl-V");
        // 	javax.swing.JToolBar.Separator sep1 = new javax.swing.JToolBar.Separator();
        // 	toolBar.add(sep1);
        playbutton = new JButton(new ImageIcon(Sonogram.class.getResource("play.gif")));
	playbutton.setPreferredSize(buttonSize);
	playbutton.setMaximumSize(buttonSize);
        toolBar.add(playbutton);
        playbutton.setToolTipText("<html><b>Play</b> the selected timespan      <font color=black size=-2>Ctrl-P");
        revbutton = new JButton(new ImageIcon(Sonogram.class.getResource("rev.gif")));
	revbutton.setPreferredSize(buttonSize);
	revbutton.setMaximumSize(buttonSize);
        toolBar.add(revbutton);
        revbutton.setToolTipText("<html><b>Reselect</b> the entire timespan      <font color=black size=-2>Ctrl-R");
        stopbutton = new JButton(new ImageIcon(Sonogram.class.getResource("stop.gif")));
	stopbutton.setPreferredSize(buttonSize);
	stopbutton.setMaximumSize(buttonSize);
        toolBar.add(stopbutton);
        stopbutton.setToolTipText("<html><b>Stop</b> playing sound      <font color=black size=-2>Ctrl-T");
        // 	javax.swing.JToolBar.Separator sep2 = new javax.swing.JToolBar.Separator();
        // 	toolBar.add(sep2);
        d3button = new JButton(new ImageIcon(Sonogram.class.getResource("3d.gif")));
	d3button.setPreferredSize(buttonSize);
	d3button.setMaximumSize(buttonSize);
        toolBar.add(d3button);
        d3button.setToolTipText("<html>Open the <b>3D Perspectogram</b><br>for the diplayed Sonograma      <font color=black size=-2>Ctrl-S");
        pitchbutton = new JButton(new ImageIcon(Sonogram.class.getResource("pitch.gif")));
	pitchbutton.setPreferredSize(buttonSize);
	pitchbutton.setMaximumSize(buttonSize);
        toolBar.add(pitchbutton);
        pitchbutton.setToolTipText("<html>Shows the <b>Pitch</b> tracking  Window      <font color=black size=-2>Ctrl-P");
        forbutton = new JButton(new ImageIcon(Sonogram.class.getResource("for.gif")));
	forbutton.setPreferredSize(buttonSize);
	forbutton.setMaximumSize(buttonSize);
        toolBar.add(forbutton);
        forbutton.setToolTipText("<html>Shows the <b>FFT</b> Window      <font color=black size=-2>Ctrl-F");
        wavbutton = new JButton(new ImageIcon(Sonogram.class.getResource("wav.gif")));
	wavbutton.setPreferredSize(buttonSize);
	wavbutton.setMaximumSize(buttonSize);
        toolBar.add(wavbutton);
        wavbutton.setToolTipText("<html>Shows the <b>Waveform</b> dialog.<br>Displays the Amplitude in the 1time aligned domain      <font color=black size=-2>CTRL-W");
        autocorrelationbutton = new JButton(new ImageIcon(Sonogram.class.getResource("auc.gif")));
	autocorrelationbutton.setPreferredSize(buttonSize);
	autocorrelationbutton.setMaximumSize(buttonSize);
        toolBar.add(autocorrelationbutton);
        autocorrelationbutton.setToolTipText("<html>Shows the <b>Autocorrelation</b> Window      <font color=black size=-2>Ctrl-A");
        lpcbutton = new JButton(new ImageIcon(Sonogram.class.getResource("lpc.gif")));
	lpcbutton.setPreferredSize(buttonSize);
	lpcbutton.setMaximumSize(buttonSize);
        toolBar.add(lpcbutton);
        lpcbutton.setToolTipText("<html>Shows the <b>LPC</b> Window      <font color=black size=-2>Ctrl-L");
        cepbutton = new JButton(new ImageIcon(Sonogram.class.getResource("cep.gif")));
	cepbutton.setMaximumSize(buttonSize);
        toolBar.add(cepbutton);
        cepbutton.setToolTipText("<html>Shows the <b>Cepstrum</b> Window      <font color=black size=-2>Ctrl-C");
        walbutton = new JButton(new ImageIcon(Sonogram.class.getResource("wal.gif")));
	walbutton.setPreferredSize(buttonSize);
	walbutton.setMaximumSize(buttonSize);
        toolBar.add(walbutton);
        walbutton.setToolTipText("<html>Shows the <b>Wavelet</b> Window      <font color=black size=-2>Ctrl-H");
        // 	javax.swing.JToolBar.Separator sep3 = new javax.swing.JToolBar.Separator();
        // 	toolBar.add(sep3);
        zbabutton = new JButton(new ImageIcon(Sonogram.class.getResource("zba.gif")));
	zbabutton.setPreferredSize(buttonSize);
	zbabutton.setMaximumSize(buttonSize);
        toolBar.add(zbabutton);
        zbabutton.setToolTipText("<html><b>Zoom OUT</b> to the entire Signal      <font color=black size=-2>Ctrl-B");
        zinbutton = new JButton(new ImageIcon(Sonogram.class.getResource("zin.gif")));
	zinbutton.setPreferredSize(buttonSize);
	zinbutton.setMaximumSize(buttonSize);
        toolBar.add(zinbutton);
        zinbutton.setToolTipText("<html><b>Zoom IN</b> the selected Time Span      <font color=black size=-2>Ctrl-Z");
        zprebutton = new JButton(new ImageIcon(Sonogram.class.getResource("zpre.gif")));
	zprebutton.setPreferredSize(buttonSize);
	zprebutton.setMaximumSize(buttonSize);
        toolBar.add(zprebutton);
        zprebutton.setToolTipText("<html><b>Zoom BACK</b> to previous selection<br>in the zoom history      <font color=black size=-2>Ctrl-V");
        // 	javax.swing.JToolBar.Separator sep4 = new javax.swing.JToolBar.Separator();
        // 	toolBar.add(sep4);
        helpbutton = new JButton(new ImageIcon(Sonogram.class.getResource("help.gif")));
	helpbutton.setPreferredSize(buttonSize);
	helpbutton.setMaximumSize(buttonSize);
        //toolBar.add(helpbutton);
        helpbutton.setToolTipText("<html>Open the BioTechSys.VoiceAnalyzer <b>Online Help</b>      <font color=black size=-2>Ctrl-H");
        quitbutton = new JButton(new ImageIcon(Sonogram.class.getResource("quit.gif")));
	quitbutton.setPreferredSize(buttonSize);
	quitbutton.setMaximumSize(buttonSize);
        toolBar.add(quitbutton);
        quitbutton.setToolTipText("<html><b>Exit</b> BioTechSys.VoiceAnalyzer without saving anything      <font color=black size=-2>Ctrl-Q");
        playbutton.addActionListener(this);
        arrangebutton.addActionListener(this);
        fullbutton.addActionListener (this);
        svgbutton.addActionListener (this);
        openbutton.addActionListener(this);
        adjbutton.addActionListener (this);
        helpbutton.addActionListener(this);
        infobutton.addActionListener(this);
        quitbutton.addActionListener(this);
        stopbutton.addActionListener(this);
        revbutton.addActionListener (this);
        d3button.addActionListener  (this);
        cepbutton.addActionListener (this);
        lpcbutton.addActionListener (this);
        zinbutton.addActionListener (this);
        zbabutton.addActionListener (this);
        zprebutton.addActionListener(this);
        forbutton.addActionListener (this);
        wavbutton.addActionListener (this);
        walbutton.addActionListener (this);
        autocorrelationbutton.addActionListener (this);
        pitchbutton.addActionListener (this);
	// this prevents resizing Sonogram to the the width of the Toolbar while resizing Sonogram event...
	toolBar.setMinimumSize(new Dimension(10,10));
	
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This funktion builds the Menuebar and his single items.
     * Keyboard shortcuts are defined here.
     */
    private void createMenu () {
        menuFile = new JMenu ("Archivos");
        quitItem = new JMenuItem ("Exit VoiceAnalyzer",new ImageIcon(Sonogram.class.getResource("quit.gif")));
        impItem  = new JMenuItem ("Abrir Sonograma from SVG",new ImageIcon(Sonogram.class.getResource("imp.gif")));
        svgItem  = new JMenuItem ("Guardar Sonograma as SVG",new ImageIcon(Sonogram.class.getResource("svg.gif")));
        saveItem = new JMenuItem ("Exportar Sonograma as Bitmap",new ImageIcon(Sonogram.class.getResource("save.gif")));
        printItem= new JMenuItem ("Imprir Sonograma",new ImageIcon(Sonogram.class.getResource("print.gif")));
        openItem = new JMenuItem ("Abrir Archivo",    new ImageIcon(Sonogram.class.getResource("open.gif")));
        webItem  = new JMenuItem ("Abrir archivo remoto de red",new ImageIcon(Sonogram.class.getResource("web.gif")));
        closeItem  = new JMenuItem ("Cerrar File",new ImageIcon(Sonogram.class.getResource("close.gif")));

        menuFile.setMnemonic  ('F');
        quitItem.setMnemonic  ('Q');
        svgItem.setMnemonic   ('V');
        openItem.setMnemonic  ('O');
        printItem.setMnemonic ('P');
        saveItem.setMnemonic  ('B');
        impItem.setMnemonic   ('I');
        webItem.setMnemonic   ('W');
        webItem.setMnemonic   ('C');

        menuFile.add (openItem);
        menuFile.add (webItem);
        menuFile.add (impItem);
        menuFile.add (closeItem);
        menuFile.add (svgItem);
        menuFile.add (saveItem);
        menuFile.add (printItem);
        menuFile.addSeparator();
        hotlist = new JMenuItem[filehistory.size()];
        int     filecheck[] = new int [filehistory.size()];
        for (int i=0;i<filehistory.size();i++) {       // Test for File for exist
            filecheck[i] = 0;                          // 0 = not aviable; 1 = aviable; 2 = http; 3 = ftp
            String str = (String)filehistory.get(i);
            if (str.substring(0,4).equals("file")) {
                str = str.substring(5);
                File testfile = new File(str);
                if (testfile.exists()==true)
                    filecheck[i] = 1;
            }
            if (str.substring(0,4).equals("http")==true) {
                filecheck[i] = 2;
                continue;
            }
            if (str.substring(0,3).equals("ftp")==true) {
                filecheck[i] = 3;
                continue;
            }
        }
        for (int i=0;i<filehistory.size();i++) {
            System.out.println("--> " + filehistory.get(i));
            String tmp = (String)filehistory.get(i);
            if (tmp.length()>20) {
                if (filecheck[i]==0)
                    hotlist[i] = new JMenuItem( i + "....." + tmp.substring(tmp.length()-20), new ImageIcon(Sonogram.class.getResource("hid.gif")));
                if (filecheck[i]==1)
                    hotlist[i] = new JMenuItem( i + "....." + tmp.substring(tmp.length()-20), new ImageIcon(Sonogram.class.getResource("his.gif")));
                if (filecheck[i]==2)
                    hotlist[i] = new JMenuItem( i + "  http..." + tmp.substring(tmp.length()-16), new ImageIcon(Sonogram.class.getResource("hin.gif")));
                if (filecheck[i]==3)
                    hotlist[i] = new JMenuItem( i + "  ftp..." + tmp.substring(tmp.length()-17), new ImageIcon(Sonogram.class.getResource("hin.gif")));
            } else {
                if (filecheck[i]==0)
                    hotlist[i] = new JMenuItem( i + "  " + tmp, new ImageIcon(Sonogram.class.getResource("hid.gif")));
                if (filecheck[i]==1)
                    hotlist[i] = new JMenuItem( i + "  " + tmp, new ImageIcon(Sonogram.class.getResource("his.gif")));
                if (filecheck[i]==2)
                    hotlist[i] = new JMenuItem( i + "  " + tmp, new ImageIcon(Sonogram.class.getResource("hin.gif")));
                if (filecheck[i]==3)
                    hotlist[i] = new JMenuItem( i + "  " + tmp, new ImageIcon(Sonogram.class.getResource("hin.gif")));
            }
            menuFile.add(hotlist[i]);
            hotlist[i].setToolTipText("<html>Path to file"+i+":<br><b>"+(String)filehistory.get(i)+"</b>");
            hotlist[i].addActionListener  (this);

            KeyStroke key = KeyStroke.getKeyStroke('0');
            if (i==0)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0,java.awt.Event.CTRL_MASK);
            if (i==1)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1,java.awt.Event.CTRL_MASK);
            if (i==2)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2,java.awt.Event.CTRL_MASK);
            if (i==3)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3,java.awt.Event.CTRL_MASK);
            if (i==4)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4,java.awt.Event.CTRL_MASK);
            if (i==5)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5,java.awt.Event.CTRL_MASK);
            if (i==6)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6,java.awt.Event.CTRL_MASK);
            if (i==7)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7,java.awt.Event.CTRL_MASK);
            if (i==8)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8,java.awt.Event.CTRL_MASK);
            if (i==9)
                key = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_9,java.awt.Event.CTRL_MASK);
            hotlist[i].setAccelerator(key);
        }
        delhistItem = new JMenuItem("  --- Delete Hotlist ---",new ImageIcon(Sonogram.class.getResource("rem.gif")));
        menuFile.add(delhistItem);
        menuFile.addSeparator();
        menuFile.add (quitItem);

        JMenu menuOther = new JMenu ("Opciones");
        adjItem         = new JMenuItem("Adjustes Generales", new ImageIcon(Sonogram.class.getResource("adj.gif")));
        defaultItem     = new JMenuItem("Configuración predeterminada"  , new ImageIcon(Sonogram.class.getResource("def.gif")));
        arrItem         = new JMenuItem("Organizar Ventanas",    new ImageIcon(Sonogram.class.getResource("arr.gif")));
        lafItem         = new JMenuItem("Seleccione LookAndFeel", new ImageIcon(Sonogram.class.getResource("laf.gif")));
        fulItem         = new JMenuItem("Pantalla Completa", new ImageIcon(Sonogram.class.getResource("ful.gif")));
        JMenu windFunkt = new JMenu("Window Funktion");
        hamItem  = new JRadioButtonMenuItem("Ventana de Hamming", new ImageIcon(Sonogram.class.getResource("ham.gif")));
        rectItem = new JRadioButtonMenuItem("Rectángulo de la ventana", new ImageIcon(Sonogram.class.getResource("rect.gif")));
        blaItem  = new JRadioButtonMenuItem("Ventana de Blackman", new ImageIcon(Sonogram.class.getResource("bla.gif")));
        hanItem  = new JRadioButtonMenuItem("Ventana de Hanning", new ImageIcon(Sonogram.class.getResource("hen.gif")));
        welItem  = new JRadioButtonMenuItem("Ventana de Welch", new ImageIcon(Sonogram.class.getResource("wel.gif")));
        triItem  = new JRadioButtonMenuItem("Ventana Triangle", new ImageIcon(Sonogram.class.getResource("tri.gif")));
        gauItem  = new JRadioButtonMenuItem("Ventana Gaussian", new ImageIcon(Sonogram.class.getResource("gau.gif")),true);
        menuOther.setMnemonic  ('P');
        windFunkt.setMnemonic  ('W');
        adjItem.setMnemonic    ('A');
        lafItem.setMnemonic    ('L');
        defaultItem.setMnemonic('D');
        menuOther.setMnemonic  ('P');
        hamItem.setMnemonic    ('H');
        rectItem.setMnemonic   ('R');
        blaItem.setMnemonic    ('B');
        hanItem.setMnemonic    ('E');
        triItem.setMnemonic    ('T');
        welItem.setMnemonic    ('W');
        gauItem.setMnemonic    ('G');
        defaultItem.setMnemonic('F');
        fulItem.setMnemonic('D');
        bgwf.add(hamItem);
        bgwf.add(rectItem);
        bgwf.add(blaItem);
        bgwf.add(hanItem);
        bgwf.add(triItem);
        bgwf.add(welItem);
        bgwf.add(gauItem);
        windFunkt.add(hamItem);
        windFunkt.add(blaItem);
        windFunkt.add(hanItem);
        windFunkt.add(rectItem);
        windFunkt.add(triItem);
        windFunkt.add(welItem);
        windFunkt.add(gauItem);
        fireItem        = new JRadioButtonMenuItem("Fire-Colors",   new ImageIcon(Sonogram.class.getResource("fire.gif")),true);
        colItem         = new JRadioButtonMenuItem("Extended Fire-Colors", new ImageIcon(Sonogram.class.getResource("col.gif")) );
        rainItem        = new JRadioButtonMenuItem("Rainbow-Colors",new ImageIcon(Sonogram.class.getResource("rai.gif")) );
        greenItem       = new JRadioButtonMenuItem("Classical green",new ImageIcon(Sonogram.class.getResource("gre.gif")) );
        bwItem          = new JRadioButtonMenuItem("Black/White",   new ImageIcon(Sonogram.class.getResource("bw.gif"))  );
        bg.add(fireItem);
        bg.add(colItem);
        bg.add(rainItem);
        bg.add(greenItem);
        bg.add(bwItem);
        negItem         = new JCheckBoxMenuItem   ("Paint Inverse",        new ImageIcon(Sonogram.class.getResource("neg.gif")));
        gridItem        = new JCheckBoxMenuItem   ("Show Grid",            new ImageIcon(Sonogram.class.getResource("grid.gif")));
        logItem         = new JCheckBoxMenuItem   ("Logartihm Amplitude",  new ImageIcon(Sonogram.class.getResource("log.gif")),true);
        logfrItem       = new JCheckBoxMenuItem   ("Logartihm Frequency",  new ImageIcon(Sonogram.class.getResource("logfr.gif")));
        fulItem       = new JCheckBoxMenuItem   ("Fullscreen",  new ImageIcon(Sonogram.class.getResource("ful.gif")));
        negItem.setMnemonic   ('I');
        menuOther.setMnemonic ('M');
        colItem.setMnemonic   ('C');
        bwItem.setMnemonic    ('B');
        fireItem.setMnemonic  ('F');
        greenItem.setMnemonic ('E');
        rainItem.setMnemonic  ('R');
        gridItem.setMnemonic  ('G');
        logItem.setMnemonic   ('L');

        menuOther.add  (adjItem);
        menuOther.add  (defaultItem);
        menuOther.add  (arrItem);
        menuOther.add  (lafItem);
        menuOther.add  (fulItem);
        menuOther.addSeparator();
        menuOther.add  (windFunkt);
        menuOther.addSeparator();
        menuOther.add  (fireItem);
        menuOther.add  (colItem);
        menuOther.add  (rainItem);
        menuOther.add  (greenItem);
        menuOther.add  (bwItem);
        menuOther.addSeparator();
        menuOther.add  (negItem);
        menuOther.addSeparator();
        menuOther.add  (gridItem);
        menuOther.addSeparator();
        menuOther.add  (logItem);
        menuOther.add  (logfrItem);
        menuOther.addSeparator();
        menuOther.add  (fulItem);

        JMenu menuSound = new JMenu ("Señal");
        infoItem = new JMenuItem ("Info",    new ImageIcon(Sonogram.class.getResource("small_info.gif")));
        playItem = new JMenuItem ("Play",    new ImageIcon(Sonogram.class.getResource("small_play.gif")));
        revItem  = new JMenuItem ("Rewind",  new ImageIcon(Sonogram.class.getResource("small_rev.gif")));
        stopItem = new JMenuItem ("Stop",    new ImageIcon(Sonogram.class.getResource("small_stop.gif")));
        cepItem  = new JMenuItem ("Cepstrum",new ImageIcon(Sonogram.class.getResource("small_cep.gif")));
        forItem  = new JMenuItem ("Fast Fourier" ,new ImageIcon(Sonogram.class.getResource("small_for.gif")));
        wavItem  = new JMenuItem ("Waveform", new ImageIcon(Sonogram.class.getResource("small_wav.gif")));
        lpcItem  = new JMenuItem ("Linear Predictive Coding", new ImageIcon(Sonogram.class.getResource("small_lpc.gif")));
        zinItem  = new JMenuItem ("Zoom Marked",new ImageIcon(Sonogram.class.getResource("zin.gif")));
        zbaItem  = new JMenuItem ("Zoom Full",new ImageIcon(Sonogram.class.getResource("zba.gif")));
        zpreItem = new JMenuItem ("Zoom Previous",new ImageIcon(Sonogram.class.getResource("zpre.gif")));
        d3Item   = new JMenuItem ("Perspectogram", new ImageIcon(Sonogram.class.getResource("small_3d.gif")));
        walItem  = new JMenuItem ("Wavelet", new ImageIcon(Sonogram.class.getResource("small_wal.gif")));
        autocorrelationItem = new JMenuItem ("Autocorrelation", new ImageIcon(Sonogram.class.getResource("small_auc.gif")));
        pitchItem = new JMenuItem ("Pitch", new ImageIcon(Sonogram.class.getResource("small_pitch.gif")));
        d3Item.setMnemonic   ('S');
        menuSound.setMnemonic('S');
        infoItem.setMnemonic ('I');
        playItem.setMnemonic ('P');
        revItem.setMnemonic  ('R');
        stopItem.setMnemonic ('T');
        cepItem.setMnemonic  ('C');
        lpcItem.setMnemonic  ('L');
        forItem.setMnemonic  ('F');
        zinItem.setMnemonic  ('Z');
        zbaItem.setMnemonic  ('B');
        zpreItem.setMnemonic ('V');
        wavItem.setMnemonic  ('Y');
        autocorrelationItem.setMnemonic ('A');
        pitchItem.setMnemonic('h');
        menuSound.add (infoItem);
        menuSound.addSeparator();
        menuSound.add (playItem);
        menuSound.add (revItem);
        menuSound.add (stopItem);
        menuSound.addSeparator();
        menuSound.add (d3Item);
        menuSound.addSeparator();
        menuSound.add (pitchItem);
        menuSound.add (forItem);
        menuSound.add (wavItem);
        menuSound.add (lpcItem);
        menuSound.add (cepItem);
        menuSound.add (walItem);
        menuSound.add (autocorrelationItem);
        menuSound.addSeparator();
        menuSound.add (zinItem);
        menuSound.add (zbaItem);
        menuSound.add (zpreItem);

        JMenu menuHelp = new JMenu     ("Ayuda");
        aboutItem      = new JMenuItem ("Sobre BioTechSys.VoiceAnalyzer", new ImageIcon(Sonogram.class.getResource("about.gif")));
        //helpItem       = new JMenuItem ("Ayuda en Linea", new ImageIcon(Sonogram.class.getResource("help.gif")));
        //licenseItem       = new JMenuItem ("License", new ImageIcon(Sonogram.class.getResource("license.gif")));
        sysItem        = new JMenuItem ("Información del sistema", new ImageIcon(Sonogram.class.getResource("sys.gif")));
        memItem        = new JMenuItem ("Monitor de Memoria", new ImageIcon(Sonogram.class.getResource("Mem.png")));
        //feedbackItem        = new JMenuItem ("Submit Feedback", new ImageIcon(Sonogram.class.getResource("mail.gif")));
        menuHelp.setMnemonic('H');
        aboutItem.setMnemonic('A');
        //helpItem.setMnemonic('H');
        //helpItem.setMnemonic('S');
        //helpItem.setMnemonic('F');
        menuHelp.add (aboutItem);
        //menuHelp.add (licenseItem);
        //menuHelp.add (helpItem);
        menuHelp.addSeparator();
        menuHelp.add (sysItem);
        menuHelp.add (memItem);
        menuHelp.addSeparator();
        //menuHelp.add (feedbackItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add (menuFile);
        menuBar.add (menuOther);
        menuBar.add (menuSound);
        menuBar.add (menuHelp);
        setJMenuBar (menuBar);
        //feedbackItem.setToolTipText("<html><table border=0><colgroup><col width=30><col width=800></colgroup><tr><td><img src="+Sonogram.class.getResource("chris.jpg")+"></td><td>Pease provide me with a <b>Feedback</b> <br>message or a <b>Bug</b> report.<br></td></tr></table></html>");



        quitItem.setToolTipText("<html><b>Exit</b> BioTechSys.VoiceAnalyzer without saving anything");
        openItem.setToolTipText("<html>Abrir <b>Video</b> or <b>Audio</b> File");
        closeItem.setToolTipText("<html><b>Close</b> the present open File");
        adjItem.setToolTipText("<html>Open <b>General Adjustment Dialog</b>");
        lafItem.setToolTipText("<html>Change the <b>Look And Feel");
        negItem.setToolTipText("<html>Pintar el principal Sonograma<br>in <b>Inverse Colors");
        gridItem.setToolTipText("<html>Pintar the main Sonograma<br>with <b>Grid");
        logItem.setToolTipText("<html>Display the Somogram in <b>Logarithm Amplitude</b> Scale");
        fireItem.setToolTipText("<html>Paint BioTechSys.VoiceAnalyzer in <b>Fire</b> Colors");
        rainItem.setToolTipText("<html>Paint BioTechSys.VoiceAnalyzer in <b>Rainbow</b> Colors");
        colItem.setToolTipText("<html>Paint BioTechSys.VoiceAnalyzer in <b>Extended Fire</b> Colors");
        bwItem.setToolTipText("<html>Paint BioTechSys.VoiceAnalyzer in <b>black/white</b> Colors");
        greenItem.setToolTipText("<html>Paint BioTechSys.VoiceAnalyzer in <b>Classical Green</b> Colors");
        infoItem.setToolTipText("<html>The Information Dialog holds <b>Values</b> and<br><b>Statistics</b> about the Signal and the Sonograma");
        d3Item.setToolTipText("<html>Generate the 3D <b>Perspectogram");
        playItem.setToolTipText("<html><b>Start</b> playing selected time span");
        revItem.setToolTipText("<html><b>Rewind</b> selection to entire time");
        stopItem.setToolTipText("<html><b>Stop</b> playing");
        cepItem.setToolTipText("<html>Shows the <b>Cepstrum</b> Window");
        lpcItem.setToolTipText("<html>Shows the <b>LPC</b>Window<br>(Linear Predictive Coding)");
        forItem.setToolTipText("<html>Shows <b>FFT</b> Window<br>(Fast Fourier Transformation)");
        zinItem.setToolTipText("<html><b>Zoom II<b> the selected time span");
        zbaItem.setToolTipText("<html><b>Zoom OUT</b> to the entire time span");
        zpreItem.setToolTipText("<html><b>Zoom BACK</b> to previous zoom<br>in the zoom history");
        //helpItem.setToolTipText("<html>Show <b>Online Help");
        saveItem.setToolTipText("<html>Export BioTechSys.VoiceAnalyzer as <b>Bitmap Image");
        printItem.setToolTipText("<html><b>Print</b> current displayed Sonograma");
        delhistItem.setToolTipText("<html><b>Delete</b> whole file history");
        aboutItem.setToolTipText("<html><b>Information</b> about Sonograma");
        logfrItem.setToolTipText("<html>Display the BioTechSys.VoiceAnalyzer in <b>Logarithm Frequency</b> Scale");
        svgItem.setToolTipText("<html><b>Save</b> BioTechSys.VoiceAnalyzer as <b>SVG</b><br>(Scalable Vector Graphic)");
        sysItem.setToolTipText("<html>System and Java VM <b>Information");
        memItem.setToolTipText("<html><b>Memory</b> usage of the Java VM");
        defaultItem.setToolTipText("<html>Set all the <b>General Adjustment Dialog</b><br> parameters back to the default <b>Initial</b> settings");
        arrItem.setToolTipText("<html><b>Arrange</b> and open all analyze windows and<br>place them to the optimal screen position");
        impItem.setToolTipText("<html><b>Import</b> Spektrum stored in SVG file");
        webItem.setToolTipText("<html>Open a <b>Remote</b> media file<br>over the Network via an <b>URL</b>");
        wavItem.setToolTipText("<html>Shows the <b>Waveform</b> Window.<br>Displays the Amplitude in the time aligned domain");
        walItem.setToolTipText("<html>Shows the <b>Wavelet</b> Window");
        fulItem.setToolTipText("<html>Zoom the main BioTechSys.VoiceAnalyzer<br>Window to <b>Fullscreen</b>");
        autocorrelationItem.setToolTipText("<html>Shows the <b>Autocorrelation</b> Window");
        pitchItem.setToolTipText("<html>Shows the <b>Pitch</b> tracking  Window");
        //licenseItem.setToolTipText("<html>Sonogram is licensed under the<br><b>Academic Free License (ADF)</b> in version 3.0");



     



        quitItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q,java.awt.Event.CTRL_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,java.awt.Event.CTRL_MASK));
        adjItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,java.awt.Event.CTRL_MASK));
        lafItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,java.awt.Event.CTRL_MASK));
        negItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,java.awt.Event.CTRL_MASK));
        gridItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,java.awt.Event.CTRL_MASK));
        logItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K,java.awt.Event.CTRL_MASK));
        infoItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I,java.awt.Event.CTRL_MASK));
        d3Item.setAccelerator  (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3,java.awt.Event.CTRL_MASK));
        playItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,java.awt.Event.CTRL_MASK));
        revItem.setAccelerator (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,java.awt.Event.CTRL_MASK));
        stopItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,java.awt.Event.CTRL_MASK));
        cepItem.setAccelerator (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,java.awt.Event.CTRL_MASK));
        lpcItem.setAccelerator (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,java.awt.Event.CTRL_MASK));
        forItem.setAccelerator (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,java.awt.Event.CTRL_MASK));
        zinItem.setAccelerator (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,java.awt.Event.CTRL_MASK));
        zbaItem.setAccelerator (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B,java.awt.Event.CTRL_MASK));
        zpreItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,java.awt.Event.CTRL_MASK));
        //helpItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1,0));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E,java.awt.Event.CTRL_MASK));
        printItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,java.awt.Event.CTRL_MASK));
        svgItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,java.awt.Event.CTRL_MASK));
        logfrItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L,java.awt.Event.CTRL_MASK));
        impItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y,java.awt.Event.CTRL_MASK));
        webItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U,java.awt.Event.CTRL_MASK));
        walItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H,java.awt.Event.CTRL_MASK));
        wavItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W,java.awt.Event.CTRL_MASK));
        fulItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11,0));
	arrItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12,0));
        autocorrelationItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,java.awt.Event.CTRL_MASK));
        pitchItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,java.awt.Event.CTRL_MASK));

        infoItem.addActionListener  (this);
        quitItem.addActionListener  (this);
        closeItem.addActionListener  (this);
        adjItem.addActionListener   (this);
        lafItem.addActionListener   (this);
        d3Item.addActionListener    (this);
        openItem.addActionListener  (this);
        colItem.addActionListener   (this);
        bwItem.addActionListener    (this);
        greenItem.addActionListener (this);
        negItem.addActionListener   (this);
        gridItem.addActionListener  (this);
        logItem.addActionListener   (this);
        fireItem.addActionListener  (this);
        rainItem.addActionListener  (this);
        hamItem.addActionListener   (this);
        hanItem.addActionListener   (this);
        blaItem.addActionListener   (this);
        rectItem.addActionListener  (this);
        triItem.addActionListener   (this);
        welItem.addActionListener   (this);
        gauItem.addActionListener   (this);
        playItem.addActionListener  (this);
        revItem.addActionListener   (this);
        aboutItem.addActionListener (this);
        //helpItem.addActionListener  (this);
        //licenseItem.addActionListener  (this);
        stopItem.addActionListener  (this);
        cepItem.addActionListener   (this);
        lpcItem.addActionListener   (this);
        forItem.addActionListener   (this);
        zinItem.addActionListener   (this);
        zbaItem.addActionListener   (this);
        zpreItem.addActionListener  (this);
        delhistItem.addActionListener(this);
        printItem.addActionListener (this);
        saveItem.addActionListener  (this);
        logfrItem.addActionListener (this);
        svgItem.addActionListener   (this);
        sysItem.addActionListener   (this);
        memItem.addActionListener   (this);
        impItem.addActionListener   (this);
        defaultItem.addActionListener(this);
        arrItem.addActionListener(this);
        webItem.addActionListener(this);
        wavItem.addActionListener(this);
        walItem.addActionListener(this);
        fulItem.addActionListener(this);
        autocorrelationItem.addActionListener(this);
        pitchItem.addActionListener(this);
        //feedbackItem.addActionListener(this);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Enable/disable toolbar and menueitems
     * @param boolean - enable Buttons and Items on/off (true = on)
     */
    public void enableItems(boolean enable) {

        //Menueitems
        saveItem.setEnabled(enable);
        printItem.setEnabled(enable);
        playItem.setEnabled(enable);
        revItem.setEnabled(enable);
        d3Item.setEnabled(enable);
        forItem.setEnabled(enable);
        cepItem.setEnabled(enable);
        lpcItem.setEnabled(enable);
        zbaItem.setEnabled(enable);
        zinItem.setEnabled(enable);
        svgItem.setEnabled(enable);
        walItem.setEnabled(enable);
        wavItem.setEnabled(enable);
        arrItem.setEnabled(enable);
        closeItem.setEnabled(enable);
        autocorrelationItem.setEnabled(enable);
        pitchItem.setEnabled(enable);
        //Buttons
        infobutton.setEnabled(enable);
        fullbutton.setEnabled(enable);
        arrangebutton.setEnabled(enable);
        playbutton.setEnabled(enable);
        d3button.setEnabled(enable);
        revbutton.setEnabled(enable);
        forbutton.setEnabled(enable);
        cepbutton.setEnabled(enable);
        lpcbutton.setEnabled(enable);
        zbabutton.setEnabled(enable);
        zinbutton.setEnabled(enable);
        svgbutton.setEnabled(enable);
        wavbutton.setEnabled(enable);
        walbutton.setEnabled(enable);
        autocorrelationbutton.setEnabled(enable);
        pitchbutton.setEnabled(enable);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Eventhandling for all Actionlistener
     * @param ActionEvent e - Event for Actionlistener.
     */
    public void actionPerformed (ActionEvent e) {
        if (e.getSource()==closeItem) {
            setCursor(Cursor.DEFAULT_CURSOR);
            setTitle("BioTechSys.VoiceAnalyzer Visible Speech Version:"+version);
            spektrumExist = false;
            updateimageflag = true;
            repaint();
        }
        if (e.getSource()==hamItem) {
            gad.sliderwinfunktion.setValue(1);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource()==blaItem) {
            gad.sliderwinfunktion.setValue(2);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource()== hanItem) {
            gad.sliderwinfunktion.setValue(3);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource()==rectItem) {
            gad.sliderwinfunktion.setValue(4);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource()==triItem) {
            gad.sliderwinfunktion.setValue(5);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource()==welItem) {
            gad.sliderwinfunktion.setValue(6);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource()==gauItem) {
            gad.sliderwinfunktion.setValue(7);
            if (spektrumExist == true)
                readerIsBack();
        }
        if (e.getSource () == logItem) {
            gad.clog.setSelected(logItem.isSelected());
            if (spektrumExist==true)
                readerIsBack();
        }
        if (e.getSource () == gridItem) {
            gad.cgrid.setSelected(gridItem.isSelected());
            gad.cgrid2.setSelected(gridItem.isSelected());
            updateimageflag=true;
            repaint();
        }
        if (e.getSource () == fireItem) {
            updateimageflag=true;
            repaint();
            gad.r1.setSelected(true);
        }
        if (e.getSource () == colItem)  {
            updateimageflag=true;
            repaint();
            gad.r2.setSelected(true);
        }
        if (e.getSource () == rainItem) {
            updateimageflag=true;
            repaint();
            gad.r3.setSelected(true);
        }
        if (e.getSource () == greenItem) {
            updateimageflag=true;
            repaint();
            gad.r4.setSelected(true);
        }
        if (e.getSource () == bwItem)   {
            updateimageflag=true;
            repaint();
            gad.r5.setSelected(true);
        }
        if (e.getSource () == negItem)  {
            updateimageflag=true;
            repaint();
            gad.cinv.setSelected(negItem.isSelected());
        }
        if (e.getSource () == quitItem || e.getSource()==quitbutton) {
	    int confirm = JOptionPane.showOptionDialog(this,"You want to close BioTechSys.VoiceAnalyzer ?","Exit BioTechSys.VoiceAnalyzer ?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,null,null);
            if (confirm == 0){
		System.out.println("--> Bye");
		saveConfig();
		if (plugin == true) {
		    try     // Call Plugin exit class
			{
			    // Check Class for exist
			    Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
			    // Generate Signature for funktion to get Funktion
			    Class[] signature  = new Class[0];
			    // get funktion
			    java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd",signature);
			    // Generate parameter for funktion
			    Object[] parameter = new Object[0];
			    // call static funktion, therefore null as class object
			    function.invoke(null,parameter);
			} catch (Throwable throwable) {
			System.out.println("--> No Plugin Class not found.");
			plugin = false;
		    }
		    dispose();
		    
		    return;
		}
		System.exit(0);
	    }
        }
        if (e.getSource()==aboutItem) {                                       // AboutItem
            messageBox("BioTechSys.VoiceAnalyzer SPEECH version " + version + " BUILD " + build + "\n\n",
                       "Esta versión soporta los siguientes algoritmos de análisis de señal:\n"+
                       "FFT, LPC, Wavelet Transformation, Cepstral Analyze, Autocorrelation,\n"+
                       "© Oscar A. Rangel - orangel@biometrics-solutions.com - www.biometrics-solutions.com"
                       ,JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource()==openItem || e.getSource()==openbutton)            // File-Open Item, generates a File Open Dialog
            openFile(null);
        if (e.getSource()==adjItem || e.getSource()==adjbutton) {             // General Adjustment Button, shows Adj.Dialog
            gad.aktualize();
            gad.show();
        }
        if (e.getSource () == playbutton || e.getSource () == playItem) {
            if (spektrumExist == true) {
                playbuttonpressed = true;
                System.out.println("--> START playing");
                if (pp.plstop == pp.plstart)
                    pp.plstop = 1.0;
		player.springTo((selectedstartold+pp.plstart*selecedwidthold)*(double)samplesall/(double)samplerate);
                player.play();
                stopItem.setEnabled(true);
                stopbutton.setEnabled(true);
                playItem.setEnabled(false);
                playbutton.setEnabled(false);
            }
        }
        if (e.getSource () == stopbutton || e.getSource () == stopItem) {
            if (spektrumExist == true) {
                System.out.println("--> STOP playing");
                stopbuttonpressed = true;
                player.stop();
            }
        }
        if (e.getSource () == revbutton || e.getSource () == revItem) {      // Play Rewindbutton
            if (spektrumExist == true) {
                player.springTo(0.0);
                pp.plstart    = 0.0;
                pp.plbutton   = 0.0;
                pp.plstop     = 1.0;
                selectedstart = selectedstartold;
                selecedwidth  = selecedwidthold;
                pp.paintTimeSlider(null);
                infod.update();
            }
        }
        if (e.getSource()==helpItem || e.getSource()==helpbutton) {           // Helpitem
            hd.show();
        }
        if (e.getSource()==licenseItem) {   
            ld.show();
        }
        if (e.getSource()==d3button || e.getSource()==d3Item )   {           // Surfaceplot
            if (spektrumExist==false)
                messageBox("Perspectogram","Please open Mediafile first.",2);
            else
                try {
                    surplot = new Surface(this);
                } catch (Throwable thro) {
                    System.out.println("--> Error while Perspectogram");
                    System.out.println(thro);
                    messageBox("Perspectogram","Error while generate Perspectogram.\nHave you installed Java3D or\nselected a very large plot?\nSee konsole for details.",0);
                }
        }
        if (e.getSource()==cepbutton || e.getSource()==cepItem )   {         // Cepstrum View
            if (spektrumExist==false)
                messageBox("Cepstrum","Please open Mediafile first.",1);
            else
                if (samplesall> cv.len) {
                    System.out.println("--> Show Cepstrum View");
                    cv.show();
                } else
                    messageBox("Cepstrum","Signal to short !!!.",1);
        }
        if (e.getSource()==zinbutton || e.getSource()==zinItem )   {         // Zoomin
            if (spektrumExist == true) {
                selectedstartpre.add(new Double(selectedstartold));
                selectedwidthpre.add(new Double(selecedwidthold ));
                zoompreviousindex ++;
                readerIsBack();
                zprebutton.setEnabled(true);
                zpreItem.setEnabled(true);
            }
        }
        if (e.getSource()==zbabutton || e.getSource()==zbaItem )   {         // Zoomfull
            if (spektrumExist == true) {
                selectedstart = 0.0;
                selecedwidth  = 1.0;
                selectedstartpre.removeAllElements();
                selectedwidthpre.removeAllElements();
                zoompreviousindex = 0;
                readerIsBack();
                zprebutton.setEnabled(false);
                zpreItem.setEnabled(false);
                infod.update();
            }
        }
        if (e.getSource()==zprebutton || e.getSource()==zpreItem )   {       // Zoomback
            if (spektrumExist == true) {
                if (zoompreviousindex != 0) {
                    selectedstart = ((Double)selectedstartpre.get(zoompreviousindex-1)).doubleValue();
                    selecedwidth  = ((Double)selectedwidthpre.get(zoompreviousindex-1)).doubleValue();
                    selectedstartpre.remove(zoompreviousindex-1);
                    selectedwidthpre.remove(zoompreviousindex-1);
                    zoompreviousindex --;
                    readerIsBack();
                    zprebutton.setEnabled(true);
                    zpreItem.setEnabled(true);
                }
                if (zoompreviousindex == 0) {
                    zprebutton.setEnabled(false);
                    zpreItem.setEnabled(false);
                }
            }
        }
        if (e.getSource()==forbutton || e.getSource()==forItem )   {         // FFT View
            if (spektrumExist==false)
                messageBox("Fast Fourier","Please open Mediafile first.",1);
            else
                if (samplesall> fv.len) {
                    System.out.println("--> Fast Fourier Transform View");
                    fv.show();
                    gad.p1.setSelectedIndex(6);
                } else
                    messageBox("FFT","Signal to short !!!.\nReduce number of samples used for FFT.",1);
        }
        if (e.getSource()==autocorrelationbutton || e.getSource()==autocorrelationItem) { // Autocorrelation
            if (spektrumExist==false)
                messageBox("Autocorrelation","Please open Mediafile first.",1);
            else {
                System.out.println("--> Autocorrelation View");
                kv.show();
            }
        }
        if (e.getSource()==pitchbutton || e.getSource()==pitchItem) { // PitchWindow
            if (spektrumExist==false)
                messageBox("Pitch Tracking","Please open Mediafile first.",1);
            else {
                System.out.println("--> Pitch Tracking View");
                pv.placePitchwindowUnderTheMainWindow();
                pv.show();
            }
        }
        if (e.getSource()==infobutton || e.getSource()==infoItem )   {       // Information Dialog
            if (infovisible==false) {
                infod .show();
                infovisible = true;
                infod.update();
                System.out.println("--> Show Informationtable-View");
            }
        }
        if (e.getSource()==lpcbutton || e.getSource()==lpcItem )   {         // Linear Prediction  Dialog
            if (spektrumExist==false)
                messageBox("LPC","Please open Mediafile first.",1);
            else
                if (samplesall> lv.len) {
                    System.out.println("--> Show Linear Prediction View");
                    lv.show();
                    gad.p1.setSelectedIndex(7);
                } else
                    messageBox("LPC","Signal to short !!!.\n Reduce number of previous samples.",1);
        }
        if (e.getSource()==lafItem) {                                        // Change Look and Feel
            slaf.show();
        }
        for (int i=0;i<filehistory.size();i++) {                             // Filehistory
            if (e.getSource() == hotlist[i]) {
                openFile((String)filehistory.get(i));
            }
        }
        if (e.getSource()==delhistItem) {                                    // Delte Hotlist
            for (int i=0;i<filehistory.size();i++)
                menuFile.remove(7);
            filehistory.removeAllElements();
        }
        if (e.getSource()==printItem) {
            PrintableComponent pc = new PrintableComponent(pp);
            try {
                pc.print();
                System.out.println("--> Sonograma printed.");
            } catch(Throwable ex) {
                messageBox("Error","Error while printing",0);
            };

        }
        if (e.getSource()==saveItem) {
            if (spektrumExist == true)
                try {
                    EFileChooser chooser = new EFileChooser();
                    chooser.setApproveButtonText("Okay");
                    chooser.setDialogTitle("Save");
                    MediaFileFilter ffbmp = new MediaFileFilter("bmp","Bitmap Image");
                    chooser.addChoosableFileFilter(ffbmp);
                    int returnVal = 0;
                    do {
                        returnVal = chooser.showOpenDialog(this);
                        if (returnVal == JFileChooser.CANCEL_OPTION)
                            return;
                    } while
                    (returnVal != JFileChooser.APPROVE_OPTION);

                    SaveImage si = new SaveImage();
                    si.saveBitmap(chooser.getSelectedFile().getAbsolutePath(),pp.doublebufferimage,pp.getSize().width-60,pp.getSize().height);
                    System.out.println("--> Sonograma image saved: " + chooser.getSelectedFile().getAbsolutePath());
                } catch(Throwable ex) {
                    messageBox("Error","Error while saving to bitmap file.",0);
                };
        }
        if (e.getSource()==logfrItem) {
            gad.cslogfr.setSelected(logfrItem.isSelected());
        }
        if (e.getSource()==svgItem || e.getSource()==svgbutton) {
            svg.show();
        }
        if (e.getSource()==memItem) {
            final PerformanceMonitor perfmon = new PerformanceMonitor();
            final JFrame f = new JFrame("Performance Monitor");
            Toolkit tk = Toolkit.getDefaultToolkit();
            f.setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));
            perfmon.surf.start();
            WindowListener l = new WindowAdapter() {
                                   public void windowClosing(WindowEvent e)     {
                                       f.dispose();
                                   }
                                   public void windowDeiconified(WindowEvent e) {
                                       perfmon.surf.start();
                                   }
                                   public void windowIconified(WindowEvent e)   {
                                       perfmon.surf.stop();
                                   }
                               };
            f.addWindowListener(l);
            f.getContentPane().add("Center",perfmon);
            f.pack();
            f.setSize(new Dimension(150,140));
            perfmon.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
            f.setVisible(true);
        }
        if (e.getSource()==sysItem) {
            // Get System Properties
            Properties prop = System.getProperties();
            sysopt = "BioTechSys.VoiceAnalyzer VERCIÓN: " + version;
            sysopt += "\nBUILD: " + build;
            sysopt += "\nOS name: " + prop.getProperty("os.name");
            sysopt += "\nOS version: " + prop.getProperty("os.version");
            sysopt += "\nAchitecture: " + prop.getProperty("os.arch");
            sysopt += "\nProcessor: " + prop.getProperty("sun.cpu.isalist");
            sysopt += "\nJava runtime name: " + prop.getProperty("java.runtime.name");
            sysopt += "\nJava VM version: " + prop.getProperty("java.vm.version");
            sysopt += "\nJava VM name: " + prop.getProperty("java.vm.name");
            sysopt += "\nJava vendor: " + prop.getProperty("java.vendor");
            sysopt += "\nJava path: " + prop.getProperty("sun.boot.library.path");
            sysopt += "\nJava TEMP: " + prop.getProperty("java.io.tmpdir");
            sysopt += "\nJava class version: " + prop.getProperty("java.class.version");
            sysopt += "\nUser name: " + prop.getProperty("user.name");
            sysopt += "\nUser home: " + prop.getProperty("user.home");
            sysopt += "\nJava Media Framework Installed: YES";
            sysopt += "\nJava3D installed: ";
            if (java3disinstalled == true)
                sysopt += "YES";
            else
                sysopt += "NO";
            sysopt += "\nCurrently free aviable memory (MB) : " + (double)Runtime.getRuntime().freeMemory()/1024/1024;
            sysopt += "\nCurrently total aviable memory (MB): " + (double)Runtime.getRuntime().totalMemory()/1024/1024;
            messageBox("System Properies",sysopt,1);
        }
        if (e.getSource()==impItem) {
            ImportFromSVG ifs = new ImportFromSVG(this);
        }
        if (e.getSource()==arrItem) {
            arrangeWindows();
        }
        if (e.getSource()==defaultItem) {
            if (openenedbysvg == true) {
                messageBox("Not Aviable","This file is opened by SVG.\nThis feature is onely aviable, if\nyou open this file new by his\norginal source",2);
                return;
            }
            int confirm = JOptionPane.showOptionDialog(this,
                          "Reset all BioTechSys.VoiceAnalyzer Parameters and\n" +
                          "Settings back to the initial Values ?", "Reset Confirmation ?"
                          ,JOptionPane.YES_NO_OPTION
                          ,JOptionPane.QUESTION_MESSAGE
                          ,null,null,null);
            if (confirm == 0) {
		initGadToDefault();
                setLookAndFeel(ilaf);
                boolean wasvisible;
                wasvisible = gad.isVisible();
		Dimension dimension = gad.getSize();
                Point location      = gad.getLocation();
                gad.dispose();
                gad = new GeneralAdjustmentDialog(this);
                gad.setLocation(location);
		gad.setSize(dimension);
                //gad.pack();
                gad.setVisible(wasvisible);
                fireItem.setSelected (true);
                gauItem.setSelected  (true);
                negItem.setSelected  (false);
                gridItem.setSelected (true);
                logItem.setSelected  (true);
                logfrItem.setSelected(false);
                if (spektrumExist==true)
                    openFile(filepath);
            }
            int confirmplacemet = JOptionPane.showOptionDialog(this,
                                  "Arrange all Windows to the\n" +
                                  "default positions and sizes ?", "Reset Placement"
                                  ,JOptionPane.YES_NO_OPTION
                                  ,JOptionPane.QUESTION_MESSAGE
                                  ,null,null,null);
            if (confirmplacemet == 0) {
                setSize          (748,483);
 		int scw  = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
 		int sch = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
 		setLocation(scw/2-getWidth()/2,sch/2-getHeight()/2);
                //gad.setSize      (434,432);
                gad.setLocation  (20,20);
                cv.setLocation   (100,100);
                cv.setSize       (400,200);
                fv.setSize       (500,250);
                fv.setLocation   (100,100);
                lv.setLocation   (100,100);
                lv.setSize       (500,270);
                infod.setLocation(40,40);
                hd.setLocation   (5,10);
                hd.setSize       (615,700);
                wv.setLocation   (100,100);
                wv.setSize       (440,200);
                av.setLocation   (100,100);
                av.setSize       (400,300);
                dispatchEvent(new java.awt.event.ComponentEvent(this,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                dispatchEvent(new java.awt.event.ComponentEvent(gad,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                dispatchEvent(new java.awt.event.ComponentEvent(cv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                dispatchEvent(new java.awt.event.ComponentEvent(fv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                dispatchEvent(new java.awt.event.ComponentEvent(hd,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                dispatchEvent(new java.awt.event.ComponentEvent(wv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                dispatchEvent(new java.awt.event.ComponentEvent(infod,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
            }
            gad.highLightButton(0);
        }
        if (e.getSource()==webItem) {
            OpenFromUrl ou = new OpenFromUrl(this);
        }
        if (e.getSource()==wavItem || e.getSource()==wavbutton) {
            if (spektrumExist==false)
                messageBox("Waveform","Please open Mediafile first.",1);
            else {
                wv.getLen();
                if (samplesall > wv.len) {
                    System.out.println("--> Show Linear Prediction View");
                    wv.show();
                    gad.p1.setSelectedIndex(9);
                } else
                    messageBox("Waveform","Signal to short !!!.\nReduce time division.",1);
            }
        }
        if (e.getSource()==fulItem || e.getSource()==fullbutton) {
            if (fullscreen != fulItem.isSelected() || e.getSource()==fullbutton)  {
		if (e.getSource()==fullbutton)
		    fulItem.setSelected(true);
                fullscreen = fulItem.isSelected();
                if (fullscreen == true) {
                    normaldimension = getSize();
                    normalpoint     = getLocation();
                    remove
                        (toolBar);
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    Dimension scd = tk.getScreenSize();
                    setLocation(0,0);
                    resize(scd);
                    setResizable(false);
                    dispatchEvent(new java.awt.event.ComponentEvent(this,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                    toFront();
                } else {
                    getContentPane().add(toolBar, BorderLayout.NORTH);
                    setLocation(normalpoint);
                    setResizable(true);
                    resize(normaldimension);
                    dispatchEvent(new java.awt.event.ComponentEvent(this,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                }
            }
        }
        if (e.getSource()==walbutton || e.getSource()==walItem )   {         // Cepstrum View
            if (spektrumExist==false)
                messageBox("Wavelet","Please open Mediafile first.",1);
            else
                if (samplesall> gad.walwindowlength) {
                    System.out.println("--> Show Wavelet View");
                    av.show();
                } else
                    messageBox("Wavelet","Signal to short !!!.",1);
        }
        if (e.getSource()==arrangebutton)   {     
	    arrangeWindows();
	}    
        if (e.getSource()==feedbackItem) { 
	    FeedbackMessage message = new FeedbackMessage(this);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This fumction arranges the windows to a callculated position. 
     * The Positions are calculated with the rule of the "Golden Cut"
     */
    public void arrangeWindows() {
        double goldencut = (Math.sqrt(5)+1.0)/2.0;
        int sw  = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int sh = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int s16sh = sh*1/6;
        int s26sh = sh*2/6;
        int s36sh = sh*3/6;
        int s46sh = sh*4/6;
        int s56sh = sh*5/6;
        int ssw   = sw-350; // window width for the main window
        int ssh   = (int)((double)ssw/goldencut)+94; // 94 is the offset for the border the toolbar and the menuebar
        int sww   = sw-ssw; // window width for the anayse windows
        int swh   = s16sh;
        setSize             (ssw,ssh);
        setLocation         (0,0);
        fv.setSize          (sww,swh);
        fv.setLocation      (ssw,0);
        wv.setSize          (sww,swh);
        wv.setLocation      (ssw,s16sh);
        lv.setSize          (sww,swh);
        lv.setLocation      (ssw,s26sh);
        cv.setSize          (sww,swh);
        cv.setLocation      (ssw,s36sh);
        kv.setSize          (sww,swh);
        kv.setLocation      (ssw,s46sh);
        av.setSize          (sww,swh);
        av.setLocation      (ssw,s56sh);
        pv.placePitchwindowUnderTheMainWindow();
        fv.show();
        wv.show();
        lv.show();
        cv.show();
        av.show();
        kv.show();
        pv.show();
        dispatchEvent(new java.awt.event.ComponentEvent(this,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(gad,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(cv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(fv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(hd,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(av,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(kv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(new java.awt.event.ComponentEvent(pv,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Open the DataSourceReader.
     * param fp Filepath to Mediafile. Is fp null so this Funktion opened an Filechooser.
     */
    public void openFile(String fp) {
        if (fp == null) {
            // If no File is given Filechooser is activated
            System.out.println("--> FileChooser opened");
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {                    // this when Valid Fileselection
                java.io.File file = chooser.getSelectedFile();
                fp = file.getAbsolutePath();
                System.out.println("--> Choosed File: "+ fp);
            } else {
                repaint();
                System.out.println("--> Filechooser Caneled");
                return;
            }
        } else {
            // Set File in FileChooser
            File file = new File(fp);
            System.out.println("--> Open from File given without choosing: " + file);
            chooser.setSelectedFile(file);
        }
        if (player != null) {
            player.close();
            player.timeThread.stop();
        }
        File file = new File(fp);
        setTitle("BioTechSys.VoiceAnalyzer " + version+ "  " + file.getName());
        openingflag = true;
        filepath    = fp;
        // Some Stuff
        repaint();
        if (fp.substring(0,3).equals("ftp")==true || fp.substring(0,4).equals("http")==true)
            url = fp;                               // network
        else if (fp.substring(0,4).equals("file")==true)
            url = fp;                               // local
        else
            url = "file:" + fp;                     // local

        System.out.println("--> Opening URL: " + url);
        reader.generateSamplesFromURL(url);
        fileisfromurl = false;
        gad.highLightButton(0);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This Funktion is caled from 'readerIsBack' method and normalized
     * the komplette Spektrum from 0 to 255.
     */
    private void normalizeSpekt() {
        float[] tempSpektrum;
        float min =   Float.MAX_VALUE;
        float max =  -Float.MAX_VALUE;
        for (int x=0;x<spektrum.size();x++) {                                  // find min/max Points of Spektrum
            tempSpektrum = (float[])spektrum.get(x);
            for(int y=0;y<(timewindowlength/2);y++) {
                if (max<tempSpektrum[y]) {
                    max=tempSpektrum[y];
                    peaky = y;
                    peakx = x;
                }
                if (min>tempSpektrum[y])
                    min=tempSpektrum[y];
            }
        }
        System.out.println("--> Normalize: MAX = " + max + ", MIN = " + min);
        float diff = max - min;
        for (int x=0;x<spektrum.size();x++) {                                   // Normalizes the Spektrum to 0..255 and test the range
            tempSpektrum = (float[])spektrum.get(x);
            for(int y=0;y<(timewindowlength/2);y++) {
                tempSpektrum[y] =(tempSpektrum[y]/diff*255.0f);               //Normalisation
                if (tempSpektrum[y] < 0.0f)
                    tempSpektrum[y] = 0.0f;
                if (tempSpektrum[y]>255.0f)
                    tempSpektrum[y] = 255.0f;
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * A litle Procedure to shows a Messagebox on Screen
     * last parameter must be the Icontype for Messagebox.
     * Valid Numbers are -1,0,1,2,3.
     * -1 = No ICON
     * 0  = STOP (RED)
     * 1  = INFORMATION (GRAY)
     * 2  = ATTENTION (ORANGE)
     * 3  = QUESTION (GREEN)
     * @param title is the Title of messagebox
     * @param message is the message to display on screen
     * @param type specified the type of messagebox (sse above).
     */
    public void messageBox(String title,String message,int type) {
        JOptionPane.showMessageDialog(this,message,title,type);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This Funktion saves the current Sonogram Settings
     * in SonogramConfig.xml file, by starting the Constructor
     * of the SaveConfig CLASS.
     */
    public void saveConfig() {
        SaveConfig sc = new SaveConfig(this);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * This Class initalize the GAD params to 
     * his default values. It is called by 
     * Reset to dafault Values, or while beginning 
     * when "save config at end" 
     * is not enabled.
     */
    public void initGadToDefault() {

        iinv       = false;
        iloga      = true;
        igrid      = true;
        iautowinl  = true;
        inorsi     = false;
        ismof      = false;
        ismosi     = true;
        ienergy    = false;
        iloglpc    = false;
        ilogfour   = true;
        iopen8     = false;
        ismot      = false;
        ipide      = false;
        ipifrlim   = false;
        ienov      = true;
        isavehi    = true;
        ipitchfog  = true;
        ipitchblack= false;
        ipitchsm   = true;
        ispecpl    = true;
        ilooppl    = false;
        imonoso    = false;
        ilogf      = false;
        isascpo    = true;
        isaco      = true;
        iffttrans  = true;
        iopenlast  = false;
        ilastwithzoom = true;
        isarr      = false;
        iceplog    = false;
        iwavelines = true;
        imute      = false;
        ilocalpeak = true;
        iuniverse  = false;
        iwallog    = true;
        iacdl      = true;
        iacsmooth  = true;
        iacpitch   = true;
        iantialise = true;
        iwfnorm    = false;
        icepsmooth = true;
	irotate    = true;
	iperantialias = false;
	ipercoord   = true;
	ipraway     = true;
	ipsmo       = true;
	iprsil      = true;
	ipclin      = true;
	iptrack     = false;
	ipfog       = true;

	isacpwl    = 2;
	isacpws    = 4;
	isacpmax   = 400;
        iswaloct   = 9;
        iswalsel   = 0;
        islws      = 9;
        islwf      = 1;
        islov      = 4;
        islsdr     = 1;
        islff      = 4;
        islfl      = 2;
        isllc      = 30;
        islf       = 10;
        islls      = 500;
        isllff     = 4;
        islsy      = 10;
        islsx      = 10;
        islpf      = 500;
        islla      = 3;
        isllf      = 5;
        islcep     = 9;
        islwavetime= 3;
        isldb      = 0;
        ilaf       = 3;
        isacwl     = 3;
        isacws     = 3;
        isc        = 1;
        isr        = 2;
        ibgcol     = new Color(0,0,0);
    }
}
