package game;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import score.Score;
import view.models.GameMenuButton;
import view.models.GameSubScene;
import view.models.GameText;

public class MainGame {

	// gui
	private static final int WIDTH = 500;
	private static final int HEIGHT = 700;
	private AnchorPane gamePane;
	private Stage menuStage;
	private Stage gameStage;
	private Scene gameScene;
	// controls
	private boolean leftPressed;
	private boolean rightPressed;
	private AnimationTimer gameTimer;
	// moving background
	private GridPane gridPane1;
	private GridPane gridPane2;
	private final static String BACKGROUND = "assets/background_game.png";
	// obstacle cars and stars
	private final static String POWERUP = "assets/powerup.png";
	private ImageView powerup;
	private final static String OBSTACLE = "assets/obstacle_car";
	private ImageView[] obstacleCars = new ImageView[3];
	Random randomPosition;
	//player and stats
	private int lives;
	private int points;
	private ImageView player;
	private final static String PLAYER = "assets/player_car.png";
	private GameText pointsLabel;
	private GameText livesLabel;
	private ImageView panel;
	private final static String PANEL = "assets/panel.png";
	// velocity
	private int velocity;
	
	GameSubScene endingSubScene;
	
	public MainGame(Stage menuStage){
		// initializing
		gamePane = new AnchorPane();
		gameScene = new Scene(gamePane, WIDTH, HEIGHT);
		gameStage = new Stage();
		gameStage.setScene(gameScene);
		
		// hiding menu
		this.menuStage = menuStage;
		menuStage.hide();
		// calling method to get the moving background
		setGameBackground();
		// adding icon, title
		gameStage.getIcons().add(new Image("/assets/logo.png"));
		gameStage.setResizable(false);
		gameStage.setTitle("Trails - A 2D Car Game");
        // showing stage
		gameStage.show();
		
		// adding player car
		player = new ImageView(new Image(PLAYER, 40, 90, false, true));
		player.setLayoutX(WIDTH/2);
		player.setLayoutY(HEIGHT-100);
		gamePane.getChildren().add(player);
		//ading key listeners to control playercar
		addListeners();
		// stats
		lives = 3;
		points = 0;
		velocity=1;
		
		//showing stats
		panel = new ImageView(new Image(PANEL, 100, 60, false, true));
		panel.setLayoutX(WIDTH-101);
		panel.setLayoutY(10);
		gamePane.getChildren().add(panel);
		pointsLabel = new GameText();
		pointsLabel.setText("Points: " + points);
		pointsLabel.setLayoutX(WIDTH-90);
		pointsLabel.setLayoutY(35);
		pointsLabel.applyParagraphFont(18);
		gamePane.getChildren().add(pointsLabel);
		livesLabel = new GameText();
		livesLabel.setText("Lives:   " + lives);
		livesLabel.setLayoutX(WIDTH-90);
		livesLabel.setLayoutY(60);
		livesLabel.applyParagraphFont(18);
		gamePane.getChildren().add(livesLabel);
		
		
		//adding points and obstacles
		randomPosition = new Random();
		powerup = new ImageView(new Image(POWERUP));
		placeObject(powerup);
		gamePane.getChildren().add(powerup);
		for (int i = 1; i<=obstacleCars.length; i++) {
			obstacleCars[i-1] = new ImageView(new Image((OBSTACLE + i + ".png"), 40, 90, false, true));
			placeObject(obstacleCars[i-1]);
			gamePane.getChildren().add(obstacleCars[i-1]);
		}
	}
	
	public void initialize() {
		// initializing game loop
		gameTimer = new AnimationTimer() {

			@Override
			public void handle(long arg0) {
				moveGameBackground();
				moveObjects();
				checkCollison();
				updateStats();
				updateObjects();
				controlPlayer();
			}
		};
		gameTimer.start();
	}
	
	private void setGameBackground() {
		// initializing background
		gridPane1 = new GridPane();
		gridPane2 = new GridPane();
		
		for(int i = 0; i < 12; i++) {
			ImageView backgroundImage1 = new ImageView(BACKGROUND);
			ImageView backgroundImage2 = new ImageView(BACKGROUND);
			GridPane.setConstraints(backgroundImage1, i%3, i/3);
			GridPane.setConstraints(backgroundImage2, i%3, i/3);
			gridPane1.getChildren().add(backgroundImage1);
			gridPane2.getChildren().add(backgroundImage2);
		}
		gridPane2.setLayoutY(-700);
		gamePane.getChildren().addAll(gridPane1, gridPane2);
	}
	
	private void moveGameBackground() {
		gridPane1.setLayoutY(gridPane1.getLayoutY() +(1.2*velocity));
		gridPane2.setLayoutY(gridPane2.getLayoutY() +(1.2*velocity));
		
		if(gridPane1.getLayoutY() >= 700) {
			gridPane1.setLayoutY(-700);
		}
		
		if(gridPane2.getLayoutY() >= 700) {
			gridPane2.setLayoutY(-700);
		}
	}
	
	private void addListeners() {
		gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
					leftPressed = true;
				}
				else if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.W) {
					rightPressed = true;
				}
			}
			
		});
		
		gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
					leftPressed = false;
				}
				else if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
					rightPressed = false;
				}
			}
			
		});
	}
	
	private void controlPlayer() {
		//left pressed and right not pressed
				if(leftPressed) {
					if(player.getLayoutX() > 80) {
						player.setLayoutX(player.getLayoutX() - 3);
					}
				}
				//right pressed and left not pressed
				if(rightPressed) {
					if(player.getLayoutX() < 380) {
						player.setLayoutX(player.getLayoutX() + 3);
					}
				}
	}
	
	private void placeObject(ImageView image) {
		image.setLayoutY(-(randomPosition.nextInt(1500)+700));
		image.setLayoutX(randomPosition.nextInt(330)+70);
	}
	
	private void moveObjects() {
		powerup.setLayoutY(powerup.getLayoutY() + (5*velocity));
		
		for(int i = 0; i < obstacleCars.length; i++) {
			obstacleCars[i].setLayoutY(obstacleCars[i].getLayoutY() + (6*velocity));
		}
	}
	
	private void updateObjects() {
		if(powerup.getLayoutY() > HEIGHT) {
			placeObject(powerup);
		}
		for(int i=0;i<obstacleCars.length; i++) {
			if(obstacleCars[i].getLayoutY()>800) {
				placeObject(obstacleCars[i]);
			}
		}
	}
	
	private void checkCollison() {
		// checking powerup
		if(player.getBoundsInParent().intersects(powerup.getBoundsInParent())) {
			placeObject(powerup);
			points++;
		}
		for(int i=0;i<obstacleCars.length; i++) {
			if(player.getBoundsInParent().intersects(obstacleCars[i].getBoundsInParent())) {
				placeObject(obstacleCars[i]);
				lives--;
				playHitAnimation();
			}
		}
	}
	
	private void playHitAnimation() {
		FadeTransition animation = new FadeTransition(Duration.millis(200), player);
		animation.setFromValue(1.0);
		animation.setToValue(0.2);
		animation.setAutoReverse(true);
		animation.setCycleCount(4);
		animation.play();
		
	}

	private void updateStats() {
		if(lives==0) {
			gameTimer.stop();
			saveScore();
			showEndingMessage();
		}
		// to increase general speed of the game with points reaching multiples of 5
		if(points%5==0 && points!=0) {
			velocity += 0.5;
		}
		pointsLabel.setText("Points: " + points);
		livesLabel.setText("Lives:   " + lives);
	}
	
	private void showEndingMessage() {
		ImageView imageView = new ImageView(new Image("assets/panel.png", 300,300,false, true));
		imageView.setLayoutY(200);
		imageView.setLayoutX(101);
		gamePane.getChildren().add(imageView);
		GameText label1 = new GameText();
		label1.setText("You Earned:");
		label1.setLayoutX(180);
		label1.setLayoutY(270);
		label1.applyTitleFont(30);
		GameText label2 = new GameText();
		label2.setText(""+points);
		label2.setLayoutX(250);
		label2.setLayoutY(320);
		label2.applyTitleFont(30);
		GameText label3 = new GameText();
		label3.setText("Points!");
		label3.setLayoutX(210);
		label3.setLayoutY(380);
		label3.applyTitleFont(30);
		GameMenuButton confirm = new GameMenuButton("okay");
		confirm.setLayoutX(155);
		confirm.setLayoutY(400);
		confirm.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				end();
			}
			
		});
		gamePane.getChildren().add(label1);
		gamePane.getChildren().add(label2);
		gamePane.getChildren().add(label3);
		gamePane.getChildren().add(confirm);
	}

	private void end() {
		gameStage.close();
		menuStage.show();
	}

	private void saveScore() {
		Score score = new Score();
		score.save(points);
	}
	
}