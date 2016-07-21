package ntz.drivers;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;

import ntz.drivers.modules.NavEventListener;
import ntz.drivers.modules.Nav;
import ntz.exceptions.BotException;
import ntz.exceptions.WebNavException;
import ntz.tests.errors.ITestErrorMessage;
/**
* @author netzulo.com
* @since 2013-01-1
* @version 0.5.1
*/
public abstract class ATrandasha implements ITrandasha{

	//COMMON properties
	private WebDriver currDriver;	
	private final String DRIVERSPATH = "libs\\drivers\\";
	
	//REMOTE properties
	private String serverUrl = "http://localhost:11000/wd/hub";

	// USABLE drivers
	private WebDriverBackedSelenium driverGridOne = null;
	private RemoteWebDriver driverRemote = null;
	private EventFiringWebDriver driverJs = null;
	private NavEventListener driverListener = null;
	private WebDriverWait driverWait = null;
	
	// Helpers browser classes
	public Nav webNav;
	
	//----------------------------------------------------------------------------------------
	
	/**
	 * Redirects to ABotBase(int browser) throws BotException CONSTRUCTOR
	 * @throws BotException 
	 * */
	public ATrandasha(BrowserMode browser) throws BotException {	
		open(browser.ordinal());
	}
	
	/**
	 * Primary Constructor: just a local server
	 * */
	public ATrandasha(int browser) throws BotException {	
		open(browser);
		
		isDriversReady(); // not neccessary on local
	}
	//--
	/**
	 * Redirects to ABotBase(int browser, int mode, "VALUE_DEFAULT") throws BotException CONSTRUCTOR
	 * @throws BotException 
	 * */
	public ATrandasha(int type, int browser) throws BotException {	
		this(type, browser, "VALUE_DEFAULT");		
	}
	/**
	 * Redirects to ABotBase(int browser, int mode) throws BotException CONSTRUCTOR
	 * @throws BotException 
	 * */
	public ATrandasha(DriverType type, BrowserMode browser) throws BotException {
		this(type.ordinal(),browser.ordinal());		
	}	
	//--
	
	/**
	 * Connects to LOCAL or REMOTE browser, if REMOTE and have custom url, use it, if not, use url passed by param
	 * Establise other drivers from currentDriver
	 * Checks all ready drivers , not null drivers	
	 * */
	public ATrandasha(int type, int browser,String _serverUrl) throws BotException {	
		if(_serverUrl.isEmpty() || _serverUrl.length() < 7){
			throw new BotException(ITestErrorMessage.ERROR_serverUrlNULL);
		}else{			
			//--
			if(!_serverUrl.contains("VALUE_DEFAULT")){
				this.serverUrl = _serverUrl;
			}			
			switch (type) {
			case 0://local
				open(browser);			
				break;
			case 1://remote
				//need url, hub active, node active, props
				openRemote(browser);				
				break;
			default:
				break;
			}
			//commons properties , except local			
			loadDrivers();
			loadModules();
			//TODO: pendiente mejorar, no se utiliza el int retornado en ningun sitio			
			//int driversReadyCount = isDriversReady();
		}
	}
	
	/**
	 * Redirects to ABotBase(int browser, int mode,String _serverUrl) throws BotException CONSTRUCTOR
	 * @throws BotException 
	 * */
	public ATrandasha(DriverType type, BrowserMode browser, String _serverUrl) throws BotException {	
		//throw new BotException(IExceptionMsg.DEV_notImplemented);				
		this(type.ordinal(),browser.ordinal(),_serverUrl);
	}
	//----------------------------------------------------------------------------------------
	
	@Override
	public void open(int num) throws BotException {		
		try {
			switch (num) {
			case 0 :
				openFirefox();				
				break;
			case 1 :
				openChrome();
				break;
			case 2 :
				openIExplorer();
				break;
			case 3 :
				openPhantomJS();
				break;
			case 4 :
				openOpera();
				break;
			case 5 :
				openEdge();
				break;				
			default :
				throw new BotException(ITestErrorMessage.ERROR_botNull);
			}		
		} catch (Exception e) {
			throw new BotException(ITestErrorMessage.ERROR_localBrowserNULL);
		}
	}
	
	@Override
	public void openRemote(int browser) throws BotException {		
		try {
			Capabilities _caps = getCaps(browser);
			currDriver = new RemoteWebDriver(new URL(serverUrl), _caps);
		} catch (MalformedURLException e) {
			throw new BotException(ITestErrorMessage.ERROR_remoteBrowserNULL);
		}
	}
	
	@Override
	public Capabilities getCaps(int browser) throws BotException {
		Capabilities caps;
		switch (browser) {
		case 0 :
			caps = capsFirefox();				
			break;
		case 1 :
			caps = capsChrome();
			break;
		case 2 :
			caps = capsIExplorer();
			break;
		case 3 :
			caps = capsPhantomJS();
			break;
		case 4 :
			caps = capsOpera();
			break;
		case 5 :
			caps = capsEdge();
			break;				
		default :
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}	
		return caps;
	}

	@Override
	public void close() {
		this.currDriver.close();
		this.currDriver.quit();		
		this.currDriver = null;
		
		//Refresh childs webdriver adapters		
		this.webNav.setDriver(this.currDriver);
	}
	//----------------------------------------------------------------------------------------
	@Override
	public WebDriver getDriverLocal() throws BotException {
		if(currDriver == null){
			throw new BotException("");
		}else{
			return currDriver;
		}
	}

	@Override
	public WebDriver getDriverRemote() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return currDriver;
		}
	}
	
	@Override
	public RemoteWebDriver getDriverAsRemote() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return (RemoteWebDriver)currDriver;
		}
	}

	@Override
	public WebDriver getDriverEvent() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return currDriver;
		}
	}
	
	@Override
	public EventFiringWebDriver getDriverAsEvent() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return (EventFiringWebDriver)currDriver;
		}
	}
	
	@Override
	public JavascriptExecutor getDriverJsExec() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return (JavascriptExecutor)currDriver;
		}
	}
	
	@Override
	public NavEventListener getDriverListener() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return this.driverListener;
		}
	}
	
	@Override
	public WebDriverWait getDriverWait() throws BotException {
		if(currDriver == null){
			throw new BotException(ITestErrorMessage.ERROR_botNull);
		}else{
			return this.driverWait;
		}
	}
	
	//----------------------------------------------------------------------------------------

	/**
	 *@see Open FIREFOX browser
	 * */	
	@Override
	public void openFirefox() {
		currDriver = new FirefoxDriver(capsFirefox());
	}

	/**
	 *@see Open CHROME browser
	 * */
	@Override
	public void openChrome() {				
		currDriver = new ChromeDriver(capsChrome());		
	}

	/**
	 *@see Open IEXPLORER browser
	 * */
	@Override
	public void openIExplorer() {		
		currDriver = new InternetExplorerDriver(capsIExplorer());
	}

	/**
	 *@see Open PHANTOMJS browser
	 * */
	@Override
	public void openPhantomJS() {	
		currDriver = new PhantomJSDriver(capsPhantomJS());
	}

	/**
	 * @see Don't work OPERA yet
	 * */
	@Override
	public void openOpera() {		
		currDriver = new OperaDriver(capsOpera());
	}

	/**
	 * @see Open EDGE browser
	 * */
	@Override
	public void openEdge() {		
		currDriver = new EdgeDriver(capsEdge());
	}
	
	//--------------------------------------------
	
	/**
	 * @see get settings(capabilities) for FIREFOX browser
	 * */
	@Override
	public Capabilities capsFirefox() {
		return DesiredCapabilities.firefox();
	}

	/**
	 * @see get settings(capabilities) for CHROME browser
	 * */
	@Override
	public Capabilities capsChrome() {
		System.setProperty("webdriver.chrome.driver", DRIVERSPATH+"chromeDriver.exe");
		return DesiredCapabilities.chrome();
	}

	/**
	 * @see get settings(capabilities) for IEXPLORER browser
	 * */
	@Override
	public Capabilities capsIExplorer() {
		System.setProperty("webdriver.ie.driver", DRIVERSPATH+"IEDriverServer.exe");
		return DesiredCapabilities.internetExplorer();
	}

	/**
	 * @see get settings(capabilities) for PHANTOMJS browser
	 * */
	@Override
	public Capabilities capsPhantomJS() {
		DesiredCapabilities caps = DesiredCapabilities.phantomjs();	
		((DesiredCapabilities )caps).setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,DRIVERSPATH+"phantomjs.exe");		
		return caps;
	}

	/**
	 * @see get settings(capabilities) for OPERA browser
	 * */
	@Override
	public Capabilities capsOpera() {
		System.setProperty("webdriver.opera.driver", DRIVERSPATH+"operadriver.exe");
		System.setProperty("opera.binary", DRIVERSPATH+"operadriver.exe");
		return DesiredCapabilities.operaBlink();
	}

	/**
	 * @see get settings(capabilities) for EDGE browser
	 * */
	@Override
	public Capabilities capsEdge() {
		System.setProperty("webdriver.edge.driver", DRIVERSPATH+"edgedriver.exe");		
		return DesiredCapabilities.edge();
	}
	
	//--------------------------------------------
	
	/**
	 * @see Check if all drivers ( except localDriver ) aren't NULL, and get count of ready drivers
	 * */
	@Override
	public int isDriversReady() {
		boolean[] isReadyDrivers = new boolean[]{false,false,false};//un true/false por cada propiedad para setear
		int readyDrivers = 0;//Cuenta cuantos drivers est�n listos
		//---		
		
		if(driverGridOne != null){ isReadyDrivers[0] = true; }
		if(driverRemote != null){ isReadyDrivers[1] = true; }
		if(driverJs != null){ isReadyDrivers[2] = true; }		
		
		//---
		for (boolean isReady : isReadyDrivers) {
			if(isReady){
				readyDrivers++;
			}
		}
		return readyDrivers;
	}


	/**
	 * @see Load modules for bot
	 * <br>WebNav
	 * <br>
	 * */
	@Override
	public void loadDrivers() throws BotException{
		try {
			this.driverWait = new WebDriverWait(currDriver,5000);
			this.driverJs = new EventFiringWebDriver(currDriver);
			this.driverListener = new NavEventListener(currDriver);
		} catch (Exception e) {
			throw new BotException(e.getMessage(),e);
		}
	}
	/**
	 * @see Load modules for bot
	 * <br>WebNav
	 * <br>
	 * */
	@Override
	public void loadModules() throws BotException{
		try {
			this.webNav = new Nav(currDriver);
		} catch (WebNavException e) {
			throw new BotException(e.getMessage(),e);
		}
	}
}
