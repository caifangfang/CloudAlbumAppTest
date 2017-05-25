package xiangyuan.albumtest;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * AlbumPhotoViewTest
 * include test cases:
 * local album photo view info
 * local album photo upload
 * cloud album photo download
 * cloud album photo delete
 * 
 * Author:xiangyuan
 */

public class AlbumPhotoViewTest {
    private AndroidDriver<AndroidElement> driver;
    private WebDriverWait wait;
    private int localpics;
    private int cloudpics;
    private int uploadpics=0;
    private int downloadpics=0;
    private String albumName = "Upload";

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
		
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     * Test Steps:
     * 1.Start App
     * 2.Login
     * 3.Open a local album
     * 4.click a picture and view info
     * 5.upload a picture to cloud album(default upload to first album),check result
     * 6.open a cloud album,delete a picture,check result
     * 7.download a picture to local,check result
     * 8.clear data,delete downloaded picture
     *
     * @throws InterruptedException
     */    
    @DataProvider
    public Object[][] localAlbumData(){
        return new Object[][]{
            {"img"}
        };
    }
    
    @DataProvider
    public Object[][] cloudAlbumData(){
        return new Object[][]{
            {"C"}
        };
    }
    
    @Test
    public void waitLaunch() throws InterruptedException{
    	wait = new WebDriverWait(driver, 30);
    	
    	swipeGuide();
        hideAutoBackupGuide();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("login_logo")));        
    }
    
    @Test(dependsOnMethods="waitLaunch")
    public void login() throws InterruptedException {
        driver.findElement(By.id("UserName")).sendKeys("xiangyuantest2@163.com");
        driver.findElement(By.id("PassWord")).click();
        driver.findElement(By.id("PassWord")).sendKeys("xiangyuan163");
        driver.hideKeyboard();
        
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login")));
        driver.findElement(By.id("login")).click();
        
        // wait until find local album
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("本地相册")));
        Assert.assertTrue(driver.findElement(By.name("本地相册")).isDisplayed(),"登录后顶部title与预期不符！");
        
        driver.findElement(By.name("163photo")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("image_photo")));
//        localpics = driver.findElements(By.id("image_photo")).size();
        localpics = getLocalPhotoNumber(driver.findElements(By.className("android.widget.TextView")).get(0).getText());
        
        driver.pressKeyCode(4);
        try {
             Thread.sleep(2000);
        } catch (InterruptedException e) {
        	// TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    /*
     * get first album's pictures number of cloud album
     */
    @Test(dependsOnMethods="login")
    public void getCloudAlbumPicNumber() throws InterruptedException {
    	cloudAlbum();
    	driver.findElements(By.id("cloud_album_cover")).get(0).click();
    	cloudpics = getCloudPhotoNumber(driver.findElement(By.id("cloud_photo_last")).getText());
    	driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
        driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("本地相册")));
    }
    
    @Test(dependsOnMethods="getCloudAlbumPicNumber",dataProvider = "localAlbumData")
    public void localAlbumPicInfoAndUpload(String localAlbumName) throws InterruptedException {
    	driver.findElement(By.name(localAlbumName)).click();
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@text,'"+localAlbumName+"')]")));
    	driver.findElementsById("image_photo").get(0).click();
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_info_btn")));
    	driver.findElement(By.id("photo_info_btn")).click();
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.name("相片信息")));
        Assert.assertTrue(driver.findElement(By.name("相片信息")).isDisplayed(),"查看相片信息失败！");
        Assert.assertTrue(driver.findElement(By.id("photo_info_create_time")).isDisplayed(),"相片信息未显示创建时间！");
        Assert.assertTrue(driver.findElement(By.id("photo_info_photo_size")).isDisplayed(),"相片信息未显示相片大小！");
        Assert.assertTrue(driver.findElement(By.id("photo_info_photo_dimensions")).isDisplayed(),"相片信息未显示相片尺寸！");
        Assert.assertTrue(driver.findElement(By.id("photo_info_photo_type")).isDisplayed(),"相片信息未显示相片格式！");
        Assert.assertTrue(driver.findElement(By.id("photo_info_photo_path")).isDisplayed(),"相片信息未显示相片路径！");
        driver.findElement(By.name("确定")).click();
        
        // click screen
        if(!isElementExist(driver,By.id("photo_back_or_not"))){
            driver.swipe(10,10,10,10,1000);
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_back_or_not")));
        driver.findElement(By.id("photo_back_or_not")).click();
        driver.findElementsById("cloud_album_name").get(0).click();
        driver.findElement(By.id("photo_confirm_upload_btn")).click();
        // wait until upload finish
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        uploadpics += 1;
        
        driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@text,'"+localAlbumName+"')]")));
        driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("本地相册")));
    }
    
    /*
     * get first album's pictures number of cloud album after upload
     */
    @Test(dependsOnMethods="localAlbumPicInfoAndUpload")
    public void getCloudAlbumPicNumberAfterUpload() throws InterruptedException {
    	cloudAlbum();
    	driver.findElements(By.id("cloud_album_cover")).get(0).click();
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cloud_photo_last")));
    	Assert.assertEquals(getCloudPhotoNumber(driver.findElement(By.id("cloud_photo_last")).getText()), cloudpics+uploadpics,"本地相册图片上传失败！");
    	
    	driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
    }
    
    /*
     * delete a picture of cloud album
     */
    @Test(dependsOnMethods="getCloudAlbumPicNumberAfterUpload")
    public void cloudAlbumDeletePic() throws InterruptedException {
    	
    	driver.findElements(By.id("cloud_album_cover")).get(0).click();
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cloud_image_photo")));
    	
    	for(int i=0;i<uploadpics;i++){
	    	driver.findElements(By.id("cloud_image_photo")).get(0).click();
	    	wait.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_delete_btn")));
	    	driver.findElement(By.id("photo_delete_btn")).click();
	    	wait.until(ExpectedConditions.presenceOfElementLocated(By.name("删除相片")));
	    	driver.findElement(By.name("确定")).click();
	    	// wait until delete finish
	        try {
	            Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        driver.pressKeyCode(4);
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_view_title")));
    	}
        
        Assert.assertEquals(getCloudPhotoNumber(driver.findElement(By.id("cloud_photo_last")).getText()), cloudpics,"云相册图片删除失败！");
        
    	driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
    }
    
    @Test(dependsOnMethods = "cloudAlbumDeletePic",dataProvider = "cloudAlbumData")
    public void cloudAlbumDownloadPic(String cloudAlbumName){
        driver.findElement(By.name(cloudAlbumName)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name(cloudAlbumName)));
        driver.findElements(By.id("cloud_image_photo")).get(0).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_save_btn")));
        driver.findElement(By.id("photo_save_btn")).click();
        try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        downloadpics += 1;
        driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name(cloudAlbumName)));
        driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
    }
     
    @Test(dependsOnMethods = "cloudAlbumDownloadPic")
    public void getLocalAlbumPicNumberAfterDownload(){
        driver.swipe(100, 200, 600, 200, 200);
        driver.findElement(By.id("g_slidemenu_local_txt")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("本地相册")));
        driver.findElement(By.name("163photo")).click();
	    swipeToDown(driver,1000);//swipe down to reload album
        Assert.assertEquals(getLocalPhotoNumber(driver.findElements(By.className("android.widget.TextView")).get(0).getText()), localpics+downloadpics,"云相册图片下载失败！");
    }
    
    @AfterClass(alwaysRun=true)
    public void clearDownloadedPics() throws Exception {
    	try{
		    for(int i=0;i<downloadpics;i++){
			   	driver.findElements(By.id("image_photo")).get(0).click();
			   	wait.until(ExpectedConditions.presenceOfElementLocated(By.id("photo_delete_btn")));
			   	driver.findElement(By.id("photo_delete_btn")).click();
			   	wait.until(ExpectedConditions.presenceOfElementLocated(By.name("删除相片")));
			   	driver.findElement(By.name("确定")).click();
			   	// wait until delete finish
			    try {
			        Thread.sleep(5000);
			    } catch (InterruptedException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    }
			    driver.pressKeyCode(4);
			    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@text,'163photo')]")));
		    }
		    swipeToDown(driver,1000);//swipe down to reload album
		    Assert.assertEquals(getLocalPhotoNumber(driver.findElements(By.className("android.widget.TextView")).get(0).getText()), localpics,"本地相册图片删除失败！");
    
    	}catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
    }
    
    /**
     * get the number from a string, for local album
     */
    public int getLocalPhotoNumber(String stringInput){
    	int startIndex = stringInput.indexOf("(");
    	int endIndex = stringInput.indexOf(")");
    	int finalNumber = Integer.valueOf(stringInput.substring(startIndex+1, endIndex));
    	return finalNumber;
    }
    
    /**
     * get the number from a string, for cloud album
     */
    public int getCloudPhotoNumber(String stringInput){
    	int startIndex = stringInput.indexOf("：");
    	int endIndex = stringInput.indexOf("张");
    	int finalNumber = Integer.valueOf(stringInput.substring(startIndex+1, endIndex));
    	return finalNumber;
    }
    
    /**
     * swipe to down
     */
    public void swipeToDown(AndroidDriver<AndroidElement> driver, int during) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        driver.swipe(width / 2, height / 4, width / 2, height * 3 / 4, during);
        // wait for page loading
    }
    
    /**
     * swipe to up
     */
    public void swipeToUp(AndroidDriver<AndroidElement> driver, int during) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        driver.swipe(width / 2, height * 3/ 4, width / 2, height / 4, during);
        // wait for page loading
    }
    
    /**
     * return true if element is found,else return false
     */
	public static boolean isElementExist(AndroidDriver<AndroidElement> driver, By selector)
	{
	    try 
	    { 
	       driver.findElement(selector); 
	       return true; 
	    } 
	    catch (NoSuchElementException e) 
	    { 
	       return false;
	    } 
	}

    
    /**
     * go into cloud album page
     */
    public void cloudAlbum() throws InterruptedException {
        driver.swipe(100, 200, 600, 200, 200);
        driver.findElement(By.id("g_slidemenu_cloud_txt")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
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
    public void hideAutoBackupGuide() throws InterruptedException {
        // AutoBackupGuideActivity.java
        wait.until(ExpectedConditions.elementToBeClickable(By.id("skipSet")));

        driver.findElement(By.id("skipSet")).click(); 
    }
}
