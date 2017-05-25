package xiangyuan.albumtest;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
 * AlbumBatchUpload
 * 
 * 
 * Author:xiangyuan
 */

public class AlbumBatchUpload {
    private AndroidDriver<AndroidElement> driver;
    private WebDriverWait wait;
    private int pics = 0;
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
     * 3.Open cloud album
     * 4.Create new album for upload
     * 5.Upload all pictures of local album to cloud album
     * 6.Check uploaded pictures in cloud album
     * 7.Clear data,delete uploaded album
     *
     * @throws InterruptedException
     */
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
    }
    
    @Test(dependsOnMethods= "login")
    public void cloudAlbumFirst() throws InterruptedException {
        cloudAlbum();
    }
    
    @Test(dependsOnMethods = "cloudAlbumFirst")
    public void createAlbum() throws InterruptedException {
    	driver.findElements(By.className("android.widget.ImageView")).get(2).click();
    	
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.name("创建相册")));
    	driver.findElement(By.id("createalbum_edit_name")).click();
    	driver.findElement(By.id("createalbum_edit_name")).sendKeys(albumName);
    	driver.findElement(By.id("createalbum_confirm_btn")).click();
    	
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.id("upload_into_thisalbum")));
    	Assert.assertEquals(driver.findElements(By.className("android.widget.TextView")).get(0).getText(), albumName,"新创建相册名称有误!");
    	
    	driver.pressKeyCode(4);
        try {
            Thread.sleep(2000);
       } catch (InterruptedException e) {
       	// TODO Auto-generated catch block
           e.printStackTrace();
       }
    }
    
    @Test(dependsOnMethods = "createAlbum")
    public void cloudAlbumBatchUpload() throws InterruptedException {

//        driver.findElement(By.id("upload_into_thisalbum")).click();
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("选择备份相片")));
    	

    	driver.pressKeyCode(4);
        try {
             Thread.sleep(2000);
        } catch (InterruptedException e) {
        	// TODO Auto-generated catch block
            e.printStackTrace();
        }
        // wait until find local album
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("local_album_wrap")));
        Assert.assertTrue(driver.findElement(By.name("本地相册")).isDisplayed(),"返回本地相册失败！");
        
//        int albumCount = driver.findElements(By.id("local_album_cover")).size();
    	List<AndroidElement> albumElements = driver.findElements(By.id("local_album_cover"));
    	for(AndroidElement albumItem:albumElements)
    	{
//	        driver.findElement(By.name("img")).click();// change name according to local album name
    		albumItem.click();
    		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("image_photo")));
    		int imageCount = getLocalPhotoNumber(driver.findElements(By.className("android.widget.TextView")).get(0).getText());
//    		pics += driver.findElements(By.id("image_photo")).size();
    		pics += imageCount;
    		
    		driver.findElements(By.className("android.widget.ImageView")).get(0).click();    		
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("勾 选")));
//	        pics = driver.findElements(By.id("local_image_check_layout")).size();
	        
	        driver.findElement(By.name("勾 选")).click();
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("全选")));
	        
	        driver.findElement(By.name("全选")).click();//id text1
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("全不选")));
	        
	        driver.findElement(By.id("open_cloud_album_bt")).click();
	        driver.findElement(By.xpath("//*[contains(@text,'"+albumName+"')]")).click();
	        
	        driver.findElement(By.id("photo_list_backup_btn")).click();
	        Thread.sleep(15000);
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("备份报告")));
	        
	    	driver.pressKeyCode(4);
	        try {
	             Thread.sleep(2000);
	        } catch (InterruptedException e) {
	        	// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
    	}
    }

    @Test(dependsOnMethods= "cloudAlbumBatchUpload")
    public void viewUploadPictures() throws InterruptedException {
    	// check upload pictures number 
    	cloudAlbum();
    	
    	if(isElementExist(driver,By.name(albumName))){
        	driver.findElementByName(albumName).click();
        }else{
        	swipeToDown(driver,1000);//swipe down to reload cloud albums
        	wait.until(ExpectedConditions.presenceOfElementLocated(By.name(albumName)));
        	driver.findElementByName(albumName).click();
        }
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("Upload")));
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.name("操 作")));
//        int uploadPics = driver.findElements(By.xpath("//android.widget.GridView/android.widget.RelativeLayout")).size();
    	int uploadPics = getCloudPhotoNumber(driver.findElement(By.id("cloud_photo_last")).getText());
    	Assert.assertEquals(uploadPics, pics,"已上传相片数与预期不符！");
    	
    	driver.pressKeyCode(4);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
    }
    
    @AfterClass(alwaysRun=true)
    public void clearUp() throws InterruptedException {
    	swipeToDown(driver,1000);
    	if(isElementExist(driver,By.name(albumName))){
        	driver.findElementByName(albumName).click();
        	wait.until(ExpectedConditions.presenceOfElementLocated(By.name("操 作")));
	        driver.findElement(By.name("操 作")).click();
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("删除相册")));
	        driver.findElement(By.name("删除相册")).click();
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
	        driver.findElement(By.name("确定")).click();
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("我的云相册")));
	        Assert.assertFalse(isElementExist(driver,By.name(albumName)),"删除相册失败");
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
