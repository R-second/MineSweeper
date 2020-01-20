import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.shape.Box;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.PerspectiveCamera;

//タイマー用のimport
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class jfx3d_anime extends Application {

    int i;
    Timeline timeline;
    
    void animation(Box box){
        
        //3D表示に関する記述
        Point3D p3 = new Point3D(1,1,0);
        p3.normalize();
        box.setRotationAxis( p3 );
        
        //タイマーの定義
        timeline = new Timeline(
            new KeyFrame(
                new Duration(25),//25ミリ秒
                new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        //ここに処理を記述
                        i+=1;
                        box.setRotate(i);
                    }
                }
            )
        );
        
        //タイマーの開始
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    
    
    @Override
    public void start(Stage stage) {
        Group root = new Group();
        
        // 辺の長さが20の立方体
        Box box = new Box(20, 20, 20);
        
        //回転軸を定義
        Point3D rotaxis = new Point3D(0.0, 1.0, 1.0);
        rotaxis.normalize();
        
        //立方体に回転軸と回転角を設定
        box.setRotationAxis( rotaxis );
        box.setRotate(45.0);
        
        root.getChildren().add(box);

        //ウィンドウのサイズを指定
        Scene scene = new Scene(root, 600, 600);
        
        //背景色を指定
        scene.setFill(Color.BLACK);
        
        // 透視投影カメラを設定
        PerspectiveCamera camera = new PerspectiveCamera(true);
        
        // カメラの位置を (0, 0, -100) にする
        camera.setTranslateZ(-100.0);
        
        //カメラが写す最近距離と最遠距離を指定
        camera.setFarClip(200);
        camera.setNearClip(50);
        
        scene.setCamera(camera);
        
        stage.setScene(scene);
        stage.setTitle("3Dサンプル");
        
        stage.show();
        
        
        animation(box);
    }

    public static void main(String... args) {
        launch(args);
    }
}