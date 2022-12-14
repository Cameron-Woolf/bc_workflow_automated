import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.BugUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class GenerateHubBugForm {

    public WindowsDriver rootDriver;
    public BugUtil bugUtil;

    private String bugType = "bc_hub_bug";

    private String date, month, day, dailyBugCount;
    private String fileName;

    @BeforeClass
    public void setUpDriver() throws IOException {

            rootDriver = setUpRootDriver();

    }

    @Test
    private void generateBugForm() throws IOException {

            bugUtil = new BugUtil();
            getDateAndBugCount();
            setFileName();
            generateBugDirectory();
            generateBugFormFile();
            openBugForm();

    }

    private void openBugForm() {

        WebElement bugDirectory = rootDriver.findElementByName(fileName);
        Actions action = new Actions(rootDriver);
        action.moveToElement(bugDirectory);
        action.doubleClick();
//        action.build();
        action.perform();

        WebElement bugForm = rootDriver.findElementByName(fileName+"_form");
        action.moveToElement(bugForm);
        action.doubleClick();
        action.perform();

    }

    public void generateBugDirectory() throws IOException {
        String bugDirectory = "C:\\Users\\camer\\OneDrive\\Desktop\\" + fileName;
        Files.createDirectories(Paths.get(bugDirectory));
    }

    public void generateBugFormFile() throws IOException {

        String bugForm = "C:\\Users\\camer\\OneDrive\\Desktop\\" + fileName +"\\"+ fileName+ "_form.txt";
        File file = new File(bugForm); //initialize File object and passing path as argument
        boolean result = file.createNewFile();
        if (result) {
            formatBugForm(bugForm);
            increaseBugCount();
        }
        else {
            System.out.println("File name not unqiue.");
            Assert.assertTrue(result);
        }

    }

    private void formatBugForm(String fileName) {

        String hub_bug_form =
                        "**Version/Platform Info** \n"
                        + "ID: bc_hub_bug_"+month+"_"+day+"_"+0+dailyBugCount +"\n"
                        + "Date: "+ date + "\n"
                        + "OS/Browser: Windows, Scorpion \n"
                        + "Hub/Sequencer Version: Copyright  0.2.0 / BeatConnect DAW 4.0.4/ BeatConnectLib 4.0.19 \n\n"
                        + "**Describe the bug** \n\n\n "
                        + "**To Reproduce** \n\n\n"
                        +"**Expected behavior** \n\n\n"
                        +"**Screenshots + Video** \n\n\n"
                        +"**Additional context** \n\n\n";

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);  // true for append mode
            byte[] bugFormData = hub_bug_form.getBytes();
            fileOutputStream.write(bugFormData);
            fileOutputStream.close();
            System.out.println("file saved.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDateAndBugCount() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");;
        LocalDateTime now = LocalDateTime.now();
        date = dateTimeFormatter.format(now);
        month = date.substring(5,7);
        day = date.substring(8);
        dailyBugCount = getBugCount();
    }

    private void setFileName() {
        fileName =
        bugType +"_" +
        month + "_" +
        day + "_" +
        0+dailyBugCount;

        System.out.println(fileName);

    }


    // Maybe move those out to the Util class..
    private void increaseBugCount() {
        BugUtil.BugCounter bugCounter = bugUtil.readBugCounterFile(1);
        bugCounter.setDate(date);
        bugCounter.increaseBugCount();
        bugUtil.writeBugCounterFile(1,bugCounter);
    }


    private String getBugCount() {

        BugUtil.BugCounter bugCounter = bugUtil.readBugCounterFile(1);
        System.out.println(bugCounter.getDate());
        System.out.println(bugCounter.getBugCount());

        return String.valueOf(bugCounter.getBugCount());
    }


    private WindowsDriver setUpRootDriver() throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("app", "Root");
        WindowsDriver driver =  new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        return driver;

    }

}
