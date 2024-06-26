import org.example.Main;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

public class CalculatorTestCase {

    @BeforeClass
    public static  void setUp(){
        Main.setUp(Main.URL);
    }
    @Test (priority = 1)
    static void registruotiNaujaVartotojaNegatyvusTestas(){
        //Paspaudžiame "Sukurti naują paskyrą"
        Main.clickOnElement(By.xpath("/html/body/div/form/div/h4/a"));
        //Užpildome reikalingus laukus
        Main.sendKeysToInputField(By.id("username"), "?");
        Main.sendKeysToInputField(By.id("password"), "a");
        //Paspaudziame "Sukurti"
        Main.clickOnElement(By.xpath("//*[@id=\"userForm\"]/button"));
        //Tikriname klaidų pranešimus.
        boolean usernameErrors = Main.elementTextContainsString(By.id("username.errors"), "Privaloma įvesti nuo 3 iki 32 simbolių");
        boolean passwordErrors = Main.elementTextContainsString(By.id("password.errors"), "Privaloma įvesti bent 3 simbolius");
        boolean passwordConfirmErrors = Main.elementTextContainsString(By.id("passwordConfirm.errors"), "Įvesti slaptažodžiai nesutampa");
        Assert.assertTrue(usernameErrors);
        Assert.assertTrue(passwordErrors);
        Assert.assertTrue(passwordConfirmErrors);
    }
    @Test(priority = 2)
    static void registruotiNaujaVartotoja(){
        // Paimti counter reikšmę iš failo ir sukurti unikalu username
        Main.counter = Main.readCounter();
        String username = "Testavimas" + Main.counter;

        //Užpildome reikalingus laukus teisingai
        Main.sendKeysToInputField(By.id("username"), username);
        Main.sendKeysToInputField(By.id("password"), "testavimas");
        Main.sendKeysToInputField(By.id("passwordConfirm"), "testavimas");
        //paspaudziame "Sukurti"
        Main.clickOnElement(By.xpath("//*[@id=\"userForm\"]/button"));
        //Tikriname ar sėkmingai prisijungėme. Matomas pasirinkimas Logout ir atvaizduotas prisijungusio vartotojo userName
        boolean containsLogout = Main.elementTextContainsString(By.xpath("/html/body/nav/div/ul[2]/a"), "Logout");
        boolean containsUsername = Main.elementTextContainsString(By.xpath("/html/body/nav/div/ul[2]/a"), username);
        Assert.assertTrue(containsLogout);
        Assert.assertTrue(containsUsername);
    }
    @Test (priority = 3)
    static void prisijungtiSuEsamuVartotojuNegatyvusTestas(){
        //Atsijungiame
        Main.clickOnElement(By.xpath("/html/body/nav/div/ul[2]/a"));

        //Užpildome reikalingus laukus su esamo vartotojo duomenimis
        Main.sendKeysToInputField(By.xpath("/html/body/div/form/div/input[1]"), "");
        Main.sendKeysToInputField(By.xpath("/html/body/div/form/div/input[2]"), "testavimas");
        //Paspaudziama "Prisijungti"
        Main.clickOnElement(By.xpath("/html/body/div/form/div/button"));
        //Tikriname ar sėkmingai prisijungėme. Matomas pasirinkimas Logout ir atvaizduotas prisijungusio vartotojo userName
        boolean loginErrors = Main.elementTextContainsString(By.xpath("/html/body/div/form/div/span[2]"), "Įvestas prisijungimo vardas ir/ arba slaptažodis yra neteisingi");
        Assert.assertTrue(loginErrors);
    }
    @Test (priority = 4)
    static void prisijungtiSuEsamuVartotoju(){
        //Užpildome reikalingus laukus su esamo vartotojo duomenimis
        Main.sendKeysToInputField(By.xpath("/html/body/div/form/div/input[1]"), "Testavimas" + Main.counter);
        Main.sendKeysToInputField(By.xpath("/html/body/div/form/div/input[2]"), "testavimas");
        //Paspaudziama "Prisijungti"
        Main.clickOnElement(By.xpath("/html/body/div/form/div/button"));
        //Tikriname ar sėkmingai prisijungėme. Matomas pasirinkimas Logout ir atvaizduotas prisijungusio vartotojo userName
        boolean containsLogout = Main.elementTextContainsString(By.xpath("/html/body/nav/div/ul[2]/a"), "Logout");
        boolean containsUsername = Main.elementTextContainsString(By.xpath("/html/body/nav/div/ul[2]/a"), "Testavimas" + Main.counter);
        Assert.assertTrue(containsLogout);
        Assert.assertTrue(containsUsername);
    }
    @Test (priority = 5)
    static void kurtiNaujaIrasaNegatyvusTestas() {
        //Suvedame skaicius
        Main.sendKeysToInputField(By.id("sk1"), "-2");
        Main.sendKeysToInputField(By.id("sk2"), "-3");
        //Pasirenkame veiksma
        Main.selectValueFromDropdownList(By.xpath("//*[@id=\"number\"]/select"), "+");
        Main.clickOnElement(By.xpath("//*[@id=\"number\"]/input[3]"));
        //Tikriname pranesima
        boolean sk1Error = Main.elementTextContainsString(By.id("sk1.errors"), "Validacijos klaida: skaičius negali būti neigiamas");
        boolean sk2Error = Main.elementTextContainsString(By.id("sk2.errors"), "Validacijos klaida: skaičius negali būti neigiamas");
        Assert.assertTrue(sk1Error);
        Assert.assertTrue(sk2Error);
    }
    @Test (priority = 6)
    static void kurtiNaujaIrasa(){
        //Suvedame skaicius
        Main.sendKeysToInputField(By.id("sk1"), "999");
        Main.sendKeysToInputField(By.id("sk2"), "998");
        //Pasirenkame veiksma
        Main.selectValueFromDropdownList(By.xpath("//*[@id=\"number\"]/select"), "-");
        Main.clickOnElement(By.xpath("//*[@id=\"number\"]/input[3]"));
        //Tikriname pranesima
        boolean resultActual = Main.elementTextContainsString(By.xpath("/html/body/h4"), "999 - 998 = 1");
        Assert.assertTrue(resultActual);
    }
    @Test (priority = 7)
    static void ieskotiIrasoRegistre(){
        Main.browser.get(Main.URL);
        //Nueiname i irasu registra
        Main.clickOnElement(By.xpath("/html/body/nav/div/ul[1]/li/a"));
        //Ieskome musu sukurto iraso
        boolean resultActual = Main.findRecordInTable(999, 998, "-", 1);
        Assert.assertTrue(resultActual);
    }
    @Test (priority = 8)
    static void ieskotiIrasoRegistreNegatyvusTestas(){
        //Ieskome iraso, kuris negali buti sukurtas
        boolean resultActual = Main.findRecordInTable(-999, 999, "+", 0);
        Assert.assertFalse(resultActual);
    }

    @Test (priority = 9)
    static void patikrintiAutorizuotoVartotojoGalimybesMatytiRegistra(){
        //Nueiname i irasu registra
        Main.clickOnElement(By.xpath("/html/body/nav/div/ul[1]/li/a"));
        //Nusikopijuojame jo URL adresa
        Main.innerUrl = Main.browser.getCurrentUrl();
        //Atsijungiame
        Main.clickOnElement(By.xpath("/html/body/nav/div/ul[2]/a"));

        //Prisijungti nauju vartotoju
        // Padidinti vienu ir išsaugoti skaitiklį counter
        Main.saveCounter(Main.counter + 1);
        // Paimti counter reikšmę iš failo ir sukurti unikalu username
        Main.counter = Main.readCounter();
        String username = "Testavimas" + Main.counter;
        //Paspaudžiame "Sukurti naują paskyrą"
        Main.clickOnElement(By.xpath("/html/body/div/form/div/h4/a"));
        //Užpildome reikalingus laukus teisingai
        Main.sendKeysToInputField(By.id("username"), username);
        Main.sendKeysToInputField(By.id("password"), "testavimas");
        Main.sendKeysToInputField(By.id("passwordConfirm"), "testavimas");
        //paspaudziame "Sukurti"
        Main.clickOnElement(By.xpath("//*[@id=\"userForm\"]/button"));
        Main.browser.get(Main.innerUrl);
        String resultActual = Main.browser.getCurrentUrl();
        Assert.assertEquals(resultActual, Main.innerUrl);
    }
    @Test (priority = 10)
    static void patikrintiAutorizuotoVartotojoGalimybesMatytiRegistraNegatyvusTestas(){
        //Nueiname i irasu registra
        Main.clickOnElement(By.xpath("/html/body/nav/div/ul[1]/li/a"));
        //Nusikopijuojame jo URL adresa
        Main.innerUrl = Main.browser.getCurrentUrl();
        Main.clickOnElement(By.xpath("/html/body/nav/div/ul[2]/a"));

        //Su neautorizuotu vetotoju bandeome pasieki vidini psl
        Main.browser.get(Main.innerUrl);
        String resultActual = Main.browser.getCurrentUrl();
        Assert.assertNotEquals(resultActual, Main.innerUrl);
    }
    @AfterClass
    public static void closeBrowserAndIncrementCounter(){
        // Padidinti vienu ir išsaugoti skaitiklį counter
        Main.saveCounter(Main.counter + 1);
        Main.closeBrowser();
    }
}
