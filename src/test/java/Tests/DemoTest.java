package Tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DemoTest {
	static WebDriverWait wait;
	static WebDriver driver;
	static String baseUrl;
	static XEPage XEPageObject;

	@BeforeAll
	static public void setUp() throws Exception {
		System.setProperty("webdriver.firefox.driver", "./drivers/geckodriver.exe");
		baseUrl = "https://www.xe.com/currencyconverter/";
		driver = new FirefoxDriver();
		XEPageObject = new XEPage(driver, baseUrl);
	}

	@ParameterizedTest
	@CsvSource({ "12.0, midmarketFromCurrency-option-0, midmarketToCurrency-option-2",
			"15.0, midmarketFromCurrency-option-0, midmarketToCurrency-option-2",
			"20.0, midmarketFromCurrency-option-0, midmarketToCurrency-option-2",
			"10.0, midmarketFromCurrency-option-1, midmarketToCurrency-option-2",
			"5.0, midmarketFromCurrency-option-0, midmarketToCurrency-option-1" })
	public void sampleTest(String sentAmount, String fromCurrency, String toCurrency) throws InterruptedException {

		driver.get(baseUrl);
		XEPageObject.insertAmountToConvert(sentAmount);
		XEPageObject.selectFromCurrency(By.id(fromCurrency));
		XEPageObject.selectToCurrency(By.id(toCurrency));
		XEPageObject.clickOnConvertButton();
		XEPageObject.validateConvertionResult();
	}

	@AfterAll
	static public void tearDown() throws Exception {
		driver.quit();
	}
}
