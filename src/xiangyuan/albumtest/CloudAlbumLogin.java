package xiangyuan.albumtest;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * CloudAlbumLogin
 * 
 * Use Demo1.java as reference
 * 
 * Author:xiangyuan
 */

public class CloudAlbumLogin {
    private AndroidDriver<AndroidElement> driver;

    @BeforeClass
    public void setUp() throws Exception {
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "app");
        File app = new File(appDir, "Album_netease.apk");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
//        capabilities.setCapability(MobileCapabilityType.UDID, "e9de920d");//device ID, not use by android simulator
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "XYPhone"); // device name
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "4.4.2"); // device platform version
        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath()); //app under test 

//        capabilities.setCapability("unicodeKeyboard",true);
//        capabilities.setCapability("resetKeyboard", true);
        driver = new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities); // create session

    	swipeGuide();
        hideAutoBackupGuide();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     * Test Steps:
     * 1.Start App
     * 2.Login
     * 3.Logout
     * 4.Quit
     *
     * @throws InterruptedException
     */
    @Test
    public void cloudAlbumLogin() throws InterruptedException {

        login();

        // wait to login
        WebDriverWait wait = new WebDriverWait(driver, 30);
		// wait until find local album
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("local_album_wrap")));
        Assert.assertTrue(driver.findElement(By.name("本地相册")).isDisplayed(),"登录后顶部title与预期不符！");
        
        // check login user name
        driver.findElement(By.className("android.widget.ImageView")).click();
        String actualLoginUser = driver.findElement(By.id("g_slidemenu_loginfo_username")).getText();
        Assert.assertEquals(actualLoginUser, "xiangyuantest2","已登录用户名与预期不符！");

        logout();
        
        // check if logout
        WebDriverWait wait2 = new WebDriverWait(driver, 30);
        wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("登录")));
        Assert.assertTrue(driver.findElement(By.name("登录云相册可以使用更多功能")).isDisplayed(),"退出登录后提示信息与预期不符！");
    }
    
    /**
     * login
     */
    private void login() {
    	WebDriverWait wait = new WebDriverWait(driver, 30);
		//wait until find UserName input element
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("UserName")));
        driver.findElement(By.id("UserName")).sendKeys("xiangyuantest2@163.com");
        driver.findElement(By.id("PassWord")).click();
        driver.findElement(By.id("PassWord")).sendKeys("xiangyuan163");
        
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.id("login")).click();
    }

    /**
     * logout
     */
    private void logout() {
		
//        driver.findElement(By.className("android.widget.ImageView")).click();
        
        WebDriverWait wait1 = new WebDriverWait(driver, 5);
        wait1.until(ExpectedConditions.elementToBeClickable(By.id("g_slidemenu_set")));
        driver.findElement(By.id("g_slidemenu_set")).click();
        
        WebDriverWait wait2 = new WebDriverWait(driver, 10);
		// wait until find logout button
        wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("注 销")));
        driver.findElementByName("注 销").click();
        
        WebDriverWait wait3 = new WebDriverWait(driver, 10);
		// wait until find confirm logout button
        wait3.until(ExpectedConditions.presenceOfElementLocated(By.id("button1")));
        driver.findElementById("button1").click();
    }

    /**
     * skip user guide when use at first time
     */
    public void swipeGuide()throws InterruptedException {
        // GuideActivity.java
        Dimension dimension;
        dimension = driver.manage().window().getSize();
        int SCREEN_WIDTH = dimension.getWidth();
        int SCREEN_HEIGHT = dimension.getHeight();

        System.out.println("DeviceUnderTest Width:" + SCREEN_WIDTH + ",Height:" + SCREEN_HEIGHT);
        Thread.sleep(5000);
        driver.swipe(SCREEN_WIDTH - 100, SCREEN_HEIGHT / 2, 100,  SCREEN_HEIGHT / 2, 2000);
        Thread.sleep(2000);
        driver.swipe(SCREEN_WIDTH - 100, SCREEN_HEIGHT / 2, 100, SCREEN_HEIGHT / 2, 2000);
        Thread.sleep(2000);
        driver.findElement(By.id("guide_btn")).click(); 
    }

    /**
     * skip auto backup function when use at first time
     */
    public void hideAutoBackupGuide() {
        // AutoBackupGuideActivity.java

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("skipSet")));

        driver.findElement(By.id("skipSet")).click(); 
    }
}
