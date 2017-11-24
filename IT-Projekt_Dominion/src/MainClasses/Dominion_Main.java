package MainClasses;

import Abstract_MVC.Controller;
import Abstract_MVC.Model;
import Abstract_MVC.View;
import Cards.CardName;
import Client_CreatePlayer_VC.CreatePlayer_Controller;
import Client_CreatePlayer_VC.CreatePlayer_View;
import Client_GameApp_MVC.GameApp_Model;
import Client_Login_VC.Login_Controller;
import Client_Login_VC.Login_View;
import Client_MainMenu_VC.MainMenu_Controller;
import Client_MainMenu_VC.MainMenu_View;
import Client_Services.ServiceLocator;
import Client_Services.Translator;
import Client_Splash_MVC.Splash_Controller;
import Client_Splash_MVC.Splash_Model;
import Client_Splash_MVC.Splash_View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Rene
 * @version 1.0
 * @created 31-Okt-2017 17:28:23
 */
public class Dominion_Main extends Application {
	Splash_View splashView;
	ServiceLocator sl;
	
	GameApp_Model model;

	public Dominion_Main(){

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception{
		Splash_Model splashModel = new Splash_Model();
		splashView = new Splash_View(stage, splashModel);
		Splash_Controller splashController = new Splash_Controller(this, splashModel, splashView);
		splashView.start();
		//splashModel.initialize();
		//this.startLogin();
		//startMainMenu();
		//this.startCreatePlayer(); // zur Kontrolle nicht in Reihenfolge gestartet 
		// noch anpassen, im Moment startet create new Player zeitgleich mit Splash
			
	}
	
	
	public void startLogin(){
		splashView.stop(); // Hides splashscreen
		sl = ServiceLocator.getServiceLocator();
		
//		ImageView testView = sl.getGallery().getImage("Gold_Card");
//		Stage testStage = new Stage();
//		Pane p = new Pane();
//		p.getChildren().add(testView);
//		Scene testScene = new Scene(p);
//		testStage.setScene(testScene);
//		testStage.show();
		
		if (this.model == null) {
			this.model = new GameApp_Model(this);
		}
		Login_View view = new Login_View(new Stage(), this.model);
		Login_Controller controller = new Login_Controller(this, this.model, view);
		view.start();
	}
	
	
	public void startCreatePlayer(){
		CreatePlayer_View view = new CreatePlayer_View(new Stage(), model);
		CreatePlayer_Controller controller = new CreatePlayer_Controller(this, model, view);
		view.start();
		
		//Thread.sleep(3000);

		//view.stop();
	}
	

	public void startGameApp(){
		// Create game mvc
		
		//sl = ServiceLocator.getServiceLocator();

		//--- Testcode
		// Button: Language DE
		sl.setTranslator(new Translator("de"));
		// Button: Language EN
		//sl.setTranslator(new Translator("en"));
		// Get label texts from translator
		Translator t = sl.getTranslator();
		System.out.println(t.getString("program.menu.file"));
		System.out.println(t.getString("program.menu.file.language"));
		System.out.println(t.getString("program.menu.help"));
		System.out.println(t.getString("login"));
		//---
		
		splashView.stop(); // Hides splashscreen
	}

//	public void startLogin(){  /// -> nicht sicher ob das so gemacht wird
//		GameApp_Model model = new GameApp_Model(null);
//		Login_View view = new Login_View(new Stage(), model);
//		Login_Controller controller = new Login_Controller(this, model, view);
//		view.start();
//
//	}

	public void startMainMenu(){
		MainMenu_View view = new MainMenu_View(new Stage(), model);
		MainMenu_Controller controller = new MainMenu_Controller(this, model, view);
		view.start();
		
		//Thread.sleep(3000);

		//view.stop();

	}
	
	@Override
	public void init(){

	}

	@Override
	public void stop(){

	}
}//end Dominion_Main