package Game;

import java.io.File;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Baran Cetin
 */
public class Game extends Application {

    Pane root;

    double x, y;
    double dx, dy;
    Pane gamePage;
    Rectangle paddle;
    Circle ball;

    VBox menuContainer;

    Button btnPlay;
    Button btnQuit;
    Button instructions;
    Button btnScores;
    Button continuePlaying;
    Button btnQuitCurrent;

    //rectangle objects for bricks
    Rectangle[] movingBricks;
    Rectangle[] bricks;
    int brickWidth;
    int brickHeight;
    int numberOfBricks;
    int offSetX;
    int offSetY;
    int offSetMovingX;
    int offSetMovingY;
    int numberOfMovingBricks;

    int numLives;

    //game states
    boolean isLaunched;
    boolean isPaused;
    //animation for ball
    Timeline animation;
   //explosion using rectangle2D and explosion class
    int nextPic;
    Rectangle2D[] explosionRec;
    ImageView explosion;
    Image explosionPath;
    Animation explosionM;
    Timeline brickAnimation;
    //music path
    File musicPath;
    String path;
    Media media;
    MediaPlayer mp;
    boolean isForward;
    
    boolean movingLeft;

    int score;
    Label scoreCounter;
    boolean movingRight;
    Rectangle left;
    Rectangle right;

    public Game() {

        left = new Rectangle(0, 0, 1, 50);
        right = new Rectangle(800, 0, 30, 50);
        right.setFill(Color.BLACK);

        scoreCounter = new Label("0");
        scoreCounter.setLayoutX(700);
        scoreCounter.setLayoutY(500);
        scoreCounter.setFont(Font.font(40));

        dx = -1;
        dy = 1;
        root = new StackPane();
        menuContainer = new VBox();
        gamePage = new Pane();

        btnPlay = new Button("Play");
        btnQuit = new Button("Quit");
        instructions = new Button("Instructions");

        btnScores = new Button("Scores");
        continuePlaying = new Button("Continue Game");
        btnQuitCurrent = new Button("Back To Main Menu");

        btnPlay.setId("playStyle");
        btnQuit.setId("quitStyle");
        btnScores.setId("scoreStyle");
        instructions.setId("instructionStyle");
        continuePlaying.setId("continueStyle");
        btnQuitCurrent.setId("currentStyle");

        isLaunched = false;
        isPaused = false;

        ball = new Circle(45, 560, 10);
        ball.setFill(Color.RED);

        paddle = new Rectangle(0, 570, 100, 30);
        paddle.setFill(Color.GOLD);

        numberOfBricks = 10;
        numberOfMovingBricks = 10;

        bricks = new Rectangle[numberOfBricks];
        movingBricks = new Rectangle[numberOfMovingBricks];

        isForward = true;
        brickHeight = 20;
        brickWidth = 50;

        btnQuitCurrent.setOnAction(e -> {
            removeBricks();
            makeBricks();

            root.getStyleClass().add("root");
            root.getStyleClass().remove("instructions");
            menuContainer.getChildren().removeAll(continuePlaying, btnQuitCurrent);
            gamePage.getChildren().remove(explosion);
            menuContainer.getChildren().addAll(btnPlay, instructions, btnScores, btnQuit);
            isLaunched = false;
            isPaused = false;
        });

        
        movingRight = true;
        movingLeft = true;
        
        explosionPath = new Image("pics/explosion.png");
        explosion = new ImageView(explosionPath);
        explosionRec = new Rectangle2D[20];

        nextPic = 0;
        for (int i = 0; i < 20; i++) {
            explosionRec[i] = new Rectangle2D(nextPic, 0, 50, 100);
            nextPic = nextPic + 50;
        }
        explosion.setViewport(explosionRec[0]);

        explosionM = new Explosion(explosion, explosionRec);

        paddle.setOnKeyPressed(e -> {
            movePaddle(e);
            startBallMovement(e);
            pauseBall(e);
        });
        ball.setOnKeyPressed(e -> {
            pauseBall(e);
        });

        btnPlay.setOnAction(e -> {
            resetGame();
            score = 0;
            scoreCounter.setText(Integer.toString(score));
            root.getChildren().remove(menuContainer);
            root.getChildren().add(gamePage);
            root.getStyleClass().removeAll("root", "instructions");
            gamePage.getChildren().removeAll(ball, paddle, scoreCounter, right, left);
            gamePage.getChildren().addAll(ball, paddle, scoreCounter, right, left);
            menuContainer.getChildren().removeAll(btnQuit, instructions, btnPlay, btnScores);
            menuContainer.getChildren().addAll(continuePlaying, btnQuitCurrent);
            paddle.requestFocus();
        });

        instructions.setOnAction(e -> {
            root.getStyleClass().add("instructions");
            menuContainer.getChildren().removeAll(btnQuit, instructions, btnPlay, btnScores);
            menuContainer.getChildren().addAll(btnQuitCurrent);
        });

        continuePlaying.setOnAction(e -> {
            animation.play();
            root.getChildren().remove(menuContainer);
            root.getChildren().add(gamePage);

            root.getStyleClass().remove("root");
            isPaused = false;
            paddle.requestFocus();
        });

        btnQuit.setOnAction(e -> {
            closeApp();
        });

        brickAnimation = new Timeline(
                new KeyFrame(Duration.millis(500), e -> moveBricks())
        );

        animation = new Timeline(
                new KeyFrame(Duration.millis(5), e -> moveBall())
        );
        animation.setCycleCount(Timeline.INDEFINITE);
        musicPath = new File("src/sounds/wiisong.mp3");
        path = musicPath.toURI().toASCIIString();
        media = new Media(path);
        mp = new MediaPlayer(media);
        mp.setCycleCount(MediaPlayer.INDEFINITE);
        mp.play();
        menuContainer.getChildren().addAll(btnPlay, instructions, btnScores, btnQuit);
        menuContainer.setSpacing(10);
        menuContainer.setAlignment(Pos.CENTER);
        root.getChildren().addAll(menuContainer);
    }

    @Override
    public void start(Stage stage) {
        makeBricks();
        brickAnimation.play();
        root.getStylesheets().add("css/styles.css");
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JavaFX EXTREME BRICKBREAKER Game");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void closeApp() {
        System.exit(0);
    }

    private void movePaddle(KeyEvent e) {
        switch (e.getCode()) {
            case LEFT:
                if (paddle.getTranslateX() > 0) {
                    for (int i = 0; i < 10; i++) {
                        paddle.setTranslateX(paddle.getTranslateX() - 1);
                        if (!isLaunched) {
                            ball.setCenterX(ball.getCenterX() - 1);
                        }
                    }
                }
                break;
            case RIGHT:
                if (paddle.getTranslateX() < 700) {
                    for (int i = 0; i < 10; i++) {
                        paddle.setTranslateX(paddle.getTranslateX() + 1);
                        if (!isLaunched) {
                            ball.setCenterX(ball.getCenterX() + 1);
                        }
                    }
                }
                break;
        }
    }

    public void moveBall() {
        if (x < 0 || x > 800) {
            dx *= -1;
        }
        if (y < 0) {
            dy *= -1;
        }
        if (y > 575) {
            collisionDetectionBallPaddle();
            dy *= -1;
        }
        if (y < 550) {
            collisionDetectionBricks();
        }
        x -= dx;
        y -= dy;
        ball.setCenterX(x);
        ball.setCenterY(y);
    }

    private void startBallMovement(KeyEvent e) {
        if (e.getCode() == KeyCode.SPACE && !isLaunched) {
            isLaunched = true;
            x = ball.getCenterX();
            y = ball.getCenterY();
            animation.play();
        }
    }

    public void pauseBall(KeyEvent e) {
        if (e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) {
            if (!isPaused) {
                animation.stop();
                isPaused = true;
                ball.requestFocus();

                root.getChildren().remove(gamePage);
                root.getChildren().addAll(menuContainer);
                root.getStyleClass().add("root");
            }
        }
    }

    private void collisionDetectionBricks() {
        Bounds ballBounds = ball.getBoundsInParent();

        for (Rectangle obj : bricks) {
            Bounds brickBounds = obj.getBoundsInParent();
            if (ballBounds.intersects(brickBounds)) {
                gamePage.getChildren().remove(obj);
                obj.setX(0);
                obj.setY(0);
                obj.setWidth(0);
                obj.setHeight(0);
                dy *= -1;
                score++;
                scoreCounter.setText(Integer.toString(score));
                break;
            }
        }
        for (Rectangle obj : movingBricks) {
            Bounds brickBounds = obj.getBoundsInParent();
            if (ballBounds.intersects(brickBounds)) {
                gamePage.getChildren().remove(obj);
                obj.setX(0);
                obj.setY(0);
                obj.setWidth(0);
                obj.setHeight(0);
                dy *= -1;
                score++;
                scoreCounter.setText(Integer.toString(score));
                break;
            }
        }

    }

    private void collisionDetectionBallPaddle() {
        Bounds ballBounds = ball.getBoundsInParent();
        Bounds paddleBounds = paddle.getBoundsInParent();
        if (!ballBounds.intersects(paddleBounds)) {
            gamePage.getChildren().remove(ball);
            gamePage.getChildren().add(explosion);
            animation.stop();
            explosion.setX(x - 15);
            explosion.setY(y - 100);
            explosionM.play();
        }
    }

    private void collisionDetectionBrickWallRight() {       
        Bounds rightWall = right.getBoundsInParent();
        for (Rectangle obj : bricks) {
            Bounds brickBounds = obj.getBoundsInParent();
            if (brickBounds.intersects(rightWall)) {
               obj.setTranslateX(obj.getTranslateX() - 1);
               movingRight = false;
            }
            
        }   
        for (Rectangle obj : movingBricks) {
            Bounds brickBounds = obj.getBoundsInParent();
            if (brickBounds.intersects(rightWall)) {
                obj.setTranslateX(obj.getTranslateX() - 1);
                movingRight = false;
            }
        }
        
    }
    
    private void collisionDetectionBrickWallLeft() {        
        Bounds leftWall = left.getBoundsInParent();     
        for (Rectangle obj : bricks) {
            Bounds brickBounds = obj.getBoundsInParent();
            if (brickBounds.intersects(leftWall)) {
                obj.setTranslateX(obj.getTranslateX() + 1);
                movingLeft = false;
            }
        }
        for (Rectangle obj : movingBricks) {
            Bounds brickBounds = obj.getBoundsInParent();
            if (brickBounds.intersects(leftWall)) {
                obj.setTranslateX(obj.getTranslateX() + 1);
                movingLeft = false;
            }
        }
    }

    private void resetGame() {
        animation.stop();
        score = 0;
        ball.setCenterX(45);
        ball.setCenterY(560);
        paddle.setTranslateX(0);
        paddle.setY(570);
        paddle.requestFocus();
    }

    private Color getColor() {
        int r = (int) ((Math.random() * 10000) % 256);
        int g = (int) ((Math.random() * 10000) % 256);
        int b = (int) ((Math.random() * 10000) % 256);
        return Color.rgb(r, g, b);
    }

    public void makeBricks() {
        offSetX = -50;
        offSetY = -20;
        for (int i = 0; i < numberOfBricks; i++) {
            bricks[i] = new Rectangle();
            bricks[i].setX((i % (800 / brickWidth)) * brickWidth);
            bricks[i].setY((i / (800 / brickWidth)) * brickHeight);
            bricks[i].setWidth(brickWidth);
            bricks[i].setHeight(brickHeight);
            bricks[i].setFill(getColor());
            bricks[i].setLayoutX(brickWidth + offSetX);
            bricks[i].setLayoutY(brickHeight + offSetY);
            gamePage.getChildren().add(bricks[i]);
            offSetX += 20;
        }
        offSetX = -50;
        offSetY = 20;
        for (int i = 0; i < numberOfBricks; i++) {
            movingBricks[i] = new Rectangle();
            movingBricks[i].setX((i % (800 / brickWidth)) * brickWidth);
            movingBricks[i].setY((i / (800 / brickWidth)) * brickHeight);
            movingBricks[i].setWidth(brickWidth);
            movingBricks[i].setHeight(brickHeight);
            movingBricks[i].setFill(getColor());
            movingBricks[i].setLayoutX(brickWidth + offSetX);
            movingBricks[i].setLayoutY(brickHeight + offSetY);
            gamePage.getChildren().add(movingBricks[i]);
            offSetX += 20;
        }
    }

    public void removeBricks() {
        for (int i = 0; i < numberOfBricks; i++) {
            gamePage.getChildren().remove(bricks[i]);
        }
        for (int i = 0; i < numberOfBricks; i++) {
            gamePage.getChildren().remove(movingBricks[i]);
        }
    }

    public void moveBricks() {
        new AnimationTimer() {

            @Override
            public void handle(long now) {
                for (int i = 0; i < numberOfBricks; i++) {
                    bricks[i].setTranslateX(bricks[i].getTranslateX() + 1);
                    movingBricks[i].setTranslateX(movingBricks[i].getTranslateX() + 1);
                    if (movingRight) { 
                        collisionDetectionBrickWallRight();
                        
                        //movingBricks[i].setTranslateX(movingBricks[i].getTranslateX() + 1);                      
                    } 
                    else{
                        bricks[i].setTranslateX(bricks[i].getTranslateX() - 1);
                        movingBricks[i].setTranslateX(movingBricks[i].getTranslateX() - 1);
                    }                  
                    //System.out.println(bricks[i].getTranslateX());
                    if(movingLeft){
                        collisionDetectionBrickWallLeft();
                    }
                   
                }
            }

        }.start();
    }

}
