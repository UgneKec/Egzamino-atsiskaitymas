package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static WebDriver browser;
    public static int counter;
    public static final String URL = "http://localhost:8080/skaiciuotuvas";
    public static String innerUrl;

    private static final String COUNTER_FILE = "counter.txt";
    public static void main(String[] args) {
        System.out.println("Selenium + Maven + TestNG -> Skaiciuotuvas");

        //        SetUp
        setUp(URL);
        try {

            // Paimti counter reikšmę iš failo ir sukurti unikalu username
            counter = readCounter();
            String username = "Testavimas" + counter;

//        •	Naujo vartotojo registracija (pozityvus ir negatyvus testas)
            //Paspaudžiame "Sukurti naują paskyrą"
            clickOnElement(By.xpath("/html/body/div/form/div/h4/a"));
            //Užpildome reikalingus laukus
            sendKeysToInputField(By.id("username"), username);
            sendKeysToInputField(By.id("password"), "testavimas");
            sendKeysToInputField(By.id("passwordConfirm"), "testavimas");
            //paspaudziame "Sukurti"
            clickOnElement(By.xpath("//*[@id=\"userForm\"]/button"));
            //Tikriname ar sėkmingai prisijungėme. Matomas pasirinkimas Logout ir atvaizduotas prisijungusio vartotojo userName
            boolean containsLogout = elementTextContainsString(By.xpath("/html/body/nav/div/ul[2]/a"), "Logout");
            boolean containsUsername = elementTextContainsString(By.xpath("/html/body/nav/div/ul[2]/a"), username);
            System.out.println(containsLogout);
            System.out.println(containsUsername);

//            •	Esamo vartotojo prisijungimas (pozityvus ir negatyvus testas)


//        •	Naujo įrašo sukūrimas (pozityvus ir negatyvus testas)
            sendKeysToInputField(By.id("sk1"), "999");
            sendKeysToInputField(By.id("sk2"), "998");
            selectValueFromDropdownList(By.xpath("//*[@id=\"number\"]/select"), "-");
            clickOnElement(By.xpath("//*[@id=\"number\"]/input[3]"));
            System.out.println(elementTextContainsString(By.xpath("/html/body/h4"), "999 - 998 = 1"));

//        •	Esamo įrašo paieška (pozityvus ir negatyvus testas)
            browser.get(URL);
            clickOnElement(By.xpath("/html/body/nav/div/ul[1]/li/a"));
            System.out.println(findRecordInTable(999, 998, "-", 1));

////        •	Tik autorizuoti vartotojai gali naudotis sistema (pozityvus ir negatyvus testas)
//            clickOnElement(By.xpath("/html/body/nav/div/ul[1]/li/a"));
//            System.out.println(browser.getCurrentUrl());
//            innerUrl = browser.getCurrentUrl();
//            clickOnElement(By.xpath("/html/body/nav/div/ul[2]/a"));
//            browser.get(innerUrl);
        }finally {
            // Padidinti vienu ir išsaugoti skaitiklį counter
            saveCounter(counter + 1);

//            closeBrowser();
        }


    }
    public static void setUp(String url){
        System.setProperty(
                "webdriver.chrome.driver",
                "src/drivers/chromedriver126.exe"
        );

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--start-maximized",
                "--ignore-certificate-errors"
        );

        browser = new ChromeDriver(chromeOptions);
        browser.get(url);
    }
    public static void closeBrowser(){
        browser.quit();
    }
    public static void clickOnElement(By locator){
        browser.findElement(locator).click();
    }
    public static void sendKeysToInputField(By locator, String value){
        WebElement element = browser.findElement(locator);
        element.clear();
        element.sendKeys(value);
    }
    public static int readCounter() {
        try (BufferedReader br = new BufferedReader(new FileReader(COUNTER_FILE))) {
            return Integer.parseInt(br.readLine().trim());
        } catch (IOException e) {
            System.out.println("Nepavyko išsaugoti skaitliuko. Plačiau:" + e.getMessage());
            return 1; // Return a default value in case of error
        }
    }
    public static void saveCounter(int counter) {
        try (FileWriter fw = new FileWriter(COUNTER_FILE)) {
            fw.write(Integer.toString(counter));
        } catch (IOException e) {
            System.out.println("Nepavyko išsaugoti skaitliuko. Plačiau:" + e.getMessage());
        }
    }
    public static boolean elementTextContainsString(By locator, String word){
        String logoutText = browser.findElement(locator).getText();
        return logoutText.contains(word);
    }
    public static void selectValueFromDropdownList(By locator, String value){
        WebElement selectField = browser.findElement(locator);
        Select select = new Select(selectField);
        select.selectByValue(value);
    }
    public static boolean findRecordInTable(int firstNumber, int secondNumber, String operator, int result) {
        List<WebElement> rows = browser.findElements(By.cssSelector("table tbody tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < 4) {
                // Skip this row if it does not have at least 4 cells
                continue;
            }

            int cellFirstNumber = Integer.parseInt(cells.get(0).getText());
            String cellOperator = cells.get(1).getText();
            int cellSecondNumber = Integer.parseInt(cells.get(2).getText());
            int cellResult = Integer.parseInt(cells.get(3).getText());

            if (cellFirstNumber == firstNumber && cellOperator.equals(operator) && cellSecondNumber == secondNumber && cellResult == result) {
                return true;
            }
        }
        return false;
    }
    public static void scrollIntoView(By locator){
        WebElement element = browser.findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) browser;
        // Slenkame paskutinis elementas būtų matomas
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
}