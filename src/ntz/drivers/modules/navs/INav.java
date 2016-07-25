package ntz.drivers.modules.navs;

import org.openqa.selenium.WebDriver;

import ntz.drivers.navs.elements.IControl;
import ntz.exceptions.NavException;
/**
* @author netzulo.com
* @since 2016-07-25
* @version 0.5.4
* 
* <p></p>
* <p></p>
* <p></p>
*/

public interface INav {

	/***/
	boolean goToUrl(String url) throws NavException;
	/***/
	boolean goBack() throws NavException;
	/***/
	boolean goRefresh() throws NavException;	
	/***/
	boolean tabOpen() throws NavException;
	/***/
	boolean tabReOpenClosed() throws NavException;
	/***/
	boolean tabClose() throws NavException;
	/***/
	boolean tabChange(int numTab) throws NavException;	
	/***/
	boolean click(IControl control) throws NavException;
	/***/
	boolean clickJS(IControl control) throws NavException;
	/***/
	boolean text(IControl control) throws NavException;
	/***/
	boolean text(IControl control, String setText) throws NavException;
	/***/
	boolean textJS(IControl control) throws NavException;
	/***/
	boolean textJS(IControl control, String setText) throws NavException;
	/***/
	boolean attribute(IControl control, String getAttrName) throws NavException;
	/***/
	boolean attributeJS(IControl control, String getAttrName) throws NavException;
	/***/
	boolean attributeJS(IControl control, String getAttrName, String setAttrValue) throws NavException;
	/***/	
	boolean checkbox(IControl control,  boolean newState) throws NavException;
	/***/
	boolean checkboxJS(IControl control,  boolean newState) throws NavException;
	/***/
	WebDriver getDriver() throws NavException;
	/***/
	boolean setDriver(WebDriver driver) throws NavException;
}