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
 * CloudAlbumUpload
 * 
 * Use Demo1.java as reference
 * 
 * Author:xiangyuan
 */

public class CloudAlbumUpload {
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

        login();

        // wait to login
        WebDriverWait wait1 = new WebDriverWait(driver, 30);
		// wait until find local album
        wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("local_album_wrap")));
        Assert.assertTrue(driver.findElement(By.name("本地相册")).isDisplayed(),"登录后顶部title与预期不符！");

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     * Test Steps:
     * 1.Start App
     * 2.Login
     * 3.Choose local picture
     * 4.Upload to cloud album
     * 5.Quit after upload finish
     *
     * @throws InterruptedException
     */
    @Test
    public void cloudAlbumUpload() throws InterruptedException {

        upload();
        
        // wait until upload finish
        Thread.sleep(5000);
        
        // click screen
        driver.swipe(10,10,10,10,1000);
        // check photo backup status
        WebDriverWait wait2 = new WebDriverWait(driver, 30);
        wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_view_bk_status")));
        Assert.assertEquals(driver.findElement(By.id("photo_view_bk_status")).getText(),"已备份","照片没有备份成功！");
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
     * upload picture
     */
    private void upload() {
		
        driver.findElement(By.name("img")).click();// change name according to local album name
        driver.findElementsById("image_photo").get(0).click();
        driver.findElement(By.id("photo_back_or_not")).click();
        driver.findElementById("cloud_album_name").click();
        driver.findElement(By.id("photo_confirm_upload_btn")).click();

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
