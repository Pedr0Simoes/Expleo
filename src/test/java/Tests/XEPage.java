package Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.text.DecimalFormat;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class XEPage {
	private final WebDriver driver;
	final WebDriverWait wait;
	private final String pageTitle = "Xe Currency Converter - Live Exchange Rates Today";

	public XEPage(WebDriver driver, String baseUrl) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		driver.get(baseUrl);
		driver.manage().window().maximize();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
		// Check that we're on the right page.
		if (!pageTitle.equals(driver.getTitle())) {
			// Alternatively, we could navigate to the login page, perhaps logging out first
			throw new IllegalStateException("This is not the XE page");
		}
	}

	// The login page contains several HTML elements that will be represented as
	// WebElements.
	// The locators for these elements should only be defined once.
	By amountField = By.id("amount");

	By fromCurrencyField = By.id("midmarketFromCurrency");
	By fromCurrencyUSDoption = By.id("midmarketFromCurrency-option-0");
	By fromCurrencyFieldText = By.id("midmarketFromCurrency-descriptiveText");

	By toCurrencyField = By.id("midmarketToCurrency");
	By toCurrencyGBPoption = By.id("midmarketFromCurrency-option-2");
	By toCurrencyFieldText = By.id("midmarketToCurrency-descriptiveText");

	By convertButton = By.xpath("/html/body/div[1]/div[2]/div[2]/section/div[2]/div/main/div/div[2]/button");
	By convertionMetric = By
			.xpath("/html/body/div[1]/div[2]/div[2]/section/div[2]/div/main/div/div[2]/div[1]/div/p[1]");
	By sentValue = By.cssSelector(".result__ConvertedText-sc-1bsijpp-0.gwvOOF");
	By receivedValue = By.cssSelector(".result__BigRate-sc-1bsijpp-1.iGrAod");

	public XEPage insertAmountToConvert(String sentAmount) {
		driver.findElement(amountField).click();
		driver.findElement(amountField).clear();
		driver.findElement(amountField).sendKeys(sentAmount);

		return this;
	}

	public XEPage selectFromCurrency(By selectedFromCurrency) throws InterruptedException {
		driver.findElement(fromCurrencyField).click();
		wait.until(ExpectedConditions.elementToBeClickable(selectedFromCurrency));
		driver.findElement(selectedFromCurrency).click();

		return this;
	}

	public XEPage selectToCurrency(By selectedToCurrency) throws InterruptedException {
		driver.findElement(toCurrencyField).click();
		wait.until(ExpectedConditions.elementToBeClickable(selectedToCurrency));
		driver.findElement(selectedToCurrency).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(selectedToCurrency));

		return this;
	}

	public XEPage clickOnConvertButton() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/section/div[2]/div/main/div/div[2]/button"))
				.click();

		return this;
	}

	public XEPage validateConvertionResult() {
		DecimalFormat df = new DecimalFormat("0.0000");
		wait.until(ExpectedConditions.visibilityOfElementLocated(sentValue));
		wait.until(ExpectedConditions.visibilityOfElementLocated(receivedValue));
		wait.until(ExpectedConditions.visibilityOfElementLocated(convertionMetric));

		String sentValueSplit[] = driver.findElement(sentValue).getText().split(" ");
		String sentAmountToConvertion = sentValueSplit[0];
		String usedCurrencyConvertion = String.join(" ", sentValueSplit).replace(sentAmountToConvertion, "")
				.replace("=", "").trim();

		String receivedValueSplit[] = driver.findElement(receivedValue).getText().split(" ");
		String receivedAmountFromConvertion = receivedValueSplit[0];
		String receivedCurrencyConvertion = String.join(" ", receivedValueSplit)
				.replace(receivedAmountFromConvertion, "").replace("=", "").trim();

		String selectedCurrencyOnFromCurrencyField = driver.findElement(fromCurrencyFieldText).getText();
		String selectedCurrencyOnToCurrencyField = driver.findElement(toCurrencyFieldText).getText();

		String usedAmount = driver.findElement(amountField).getAttribute("value");

		assertTrue(selectedCurrencyOnFromCurrencyField
				.contains(usedCurrencyConvertion.substring(0, usedCurrencyConvertion.length() - 1)));
		assertTrue(selectedCurrencyOnToCurrencyField
				.contains(receivedCurrencyConvertion.substring(0, receivedCurrencyConvertion.length() - 1)));
		assertEquals(usedAmount, sentAmountToConvertion);

		String convertionMetricData[] = driver.findElement(convertionMetric).getText().split(" ");
		String productFromCurrencyValue = convertionMetricData[1];
		String multiplierValue = convertionMetricData[3];
		String multiplierToCurrencyValue = convertionMetricData[4];

		assertTrue(selectedCurrencyOnFromCurrencyField.contains(productFromCurrencyValue));
		assertTrue(selectedCurrencyOnToCurrencyField.contains(multiplierToCurrencyValue));
		float result = Float.parseFloat(sentAmountToConvertion) * Float.parseFloat(multiplierValue);
		assertEquals(df.format(Float.parseFloat(receivedAmountFromConvertion)), df.format(result));

		return this;
	}
}
