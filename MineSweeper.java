import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.input.KeyEvent;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.text.Font;


public class MineSweeper extends Application {

	Image bombPic;		ImageView bombIv;
	Image flagOnPic;	ImageView flagOnIv;
	Image flagOffPic;	ImageView flagOffIv;
	Image flagPic;		ImageView flagIv;
	
	Button flagButton;

	

	Square grid[][];
	Label leftIndicator;
	
	Alert gameover;
	Alert successInfo;
	
	int flag = 0;
	
	int column;

	HBox hbox;
	Label instruction;
	MenuBar menubar;

	int cnt_bomb;

	// state Scene
	Scene sceneBase;
	Scene scenes[] = { null, null, null};
	
	// state column
	int column_list[] = { 10, 15, 20 };


	@Override
	public void start(Stage stage) throws Exception{
		//column = 20;
		
		
		// configure stage
		stage.setTitle("MineSweeper");
		
		// configure images
		bombPic = new Image("bomb.png", 30, 30, false, false);
		bombIv = new ImageView(bombPic);
		flagOnPic = new Image("flagOn.png", 80, 80, false, false);
		flagOnIv = new ImageView(flagOnPic);
		flagOffPic = new Image("flagOff.png", 80, 80, false, false);
		flagOffIv = new ImageView(flagOffPic);
		flagPic = new Image("flagOn.png", 30, 30, false, false);
		flagIv = new ImageView(flagPic);
		
		// configure menubar
		menubar = new MenuBar();
		Menu gameMenu = new Menu("SELECT GAME");
		MenuItem easyMenu = new MenuItem("easy:10 * 10");
		MenuItem hardMenu = new MenuItem("hard:15 * 15");
		MenuItem hardestMenu = new MenuItem("hardest:20 * 20");
		MenuItem shortcutMenu = new MenuItem("shortcut-key");
		easyMenu.setOnAction(event -> setupScene(stage, 0));
		hardMenu.setOnAction(event -> setupScene(stage, 1));
		hardestMenu.setOnAction(event -> setupScene(stage, 2));
		gameMenu.getItems().addAll(easyMenu, hardMenu, hardestMenu, shortcutMenu);
		menubar.getMenus().addAll(gameMenu);
		
		// configure "GAMEOVER" information
		gameover = new Alert(AlertType.INFORMATION, "GAMEOVER");
		gameover.setContentText("GAMEOVER!!!!!!");

		// configure "SUCCESS" information
		successInfo = new Alert(AlertType.INFORMATION, "CLEAR");
		successInfo.setContentText("SUCCESS!!!!!!");
		
		
		
		// configure instruction label
		instruction = new Label();
		instruction.setText("ショートカットキー 一覧は Qキー で開きます。\n爆弾以外の全てのマスを開ければ成功！");
		instruction.setFont(new Font(15));

		//Enterキーでフラッグモードと切り替えできます。矢印キーでボタンの選択を移動し、aキーでひらけます。\n


		// configure flag button
		flagButton = new Button();
		flagButton.setMaxWidth(Double.MAX_VALUE);
		flagButton.setMaxHeight(Double.MAX_VALUE);
		flagButton.setPrefWidth(60);
		flagButton.setPrefHeight(60);
		flagButton.setOnAction(event -> flagUpdate());
		flagButton.setGraphic(flagOffIv);
		

		Label leftInfo = new Label("残り");
		leftInfo.setFont(new Font(30));
		leftIndicator = new Label(Integer.toString(cnt_bomb));
		leftIndicator.setFont(new Font(30));
		leftIndicator.setMinWidth(60);
		
		// layout
		hbox = new HBox(20);	
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.getChildren().addAll(flagButton, leftInfo, leftIndicator, instruction);
		
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(menubar, hbox);
		
		sceneBase = new Scene(root);


		stage.setScene(sceneBase);
		stage.show();
	}
	

	/* ---- change Scene ---- */
	void setupScene(Stage stage, int scene_number) {
		initScene(scene_number);
		stage.setScene(scenes[scene_number]);
		stage.show();
		initSquare();
	}

	/* ---- initialize Scene ---- */
	void initScene(int scene_number){

		column = column_list[scene_number];

		// configure array Square
		// Layout Square with gridPane
		GridPane gridLayout = new GridPane();
		grid = new Square[column][column];
		for(int i=0; i<column; i++) {
			for(int j=0; j<column; j++) {
				grid[i][j] = new Square();
				grid[i][j].setMinWidth(Double.MIN_VALUE);
				grid[i][j].setMinHeight(Double.MIN_VALUE);
				grid[i][j].setPrefWidth(60);
				grid[i][j].setPrefHeight(60);
				grid[i][j].setMaxWidth(Double.MAX_VALUE);
				grid[i][j].setMaxHeight(Double.MAX_VALUE);
				
				grid[i][j].setOnAction(event -> gridClicked(event));
				GridPane.setConstraints(grid[i][j], j, i);
				gridLayout.getChildren().add(grid[i][j]);
				GridPane.setHgrow(grid[i][j], Priority.ALWAYS);
				GridPane.setVgrow(grid[i][j], Priority.ALWAYS);
			}
		}

		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(menubar, hbox);
		root.getChildren().addAll(gridLayout);

		scenes[scene_number] = new Scene(root);
		scenes[scene_number].setOnKeyPressed(event -> doKeyAction(event));

		
	}
	

	/* ---- initialize Square ---- */
	void initSquare() {
		cnt_bomb = (int)(column * column * 0.15);
		leftIndicator.setText(Integer.toString(cnt_bomb));
		

		for(int i=0; i<column; i++) {
			for(int j=0; j<column; j++) {
				grid[i][j].STATE = 0; 
				grid[i][j].flagSTATE = 0;
				grid[i][j].setDisableSTATE(false);
				grid[i][j].setText(null);
				grid[i][j].setGraphic(null);
			}
		}
		int randA, randB;
		int c;

		// configure bomb with random number 
		int i=0;
		while(i<cnt_bomb) {
			randA = (int)(Math.random()*column);
			randB = (int)(Math.random()*column);
			if(grid[randA][randB].STATE == 0) {
				grid[randA][randB].STATE = -1;   // change STATE
				i++;
			}
		}
		
		// set counts of bomb around itself
		for(i=0; i<column; i++)
			for(int j=0; j<column; j++) 
				if(grid[i][j].STATE == 0) {
					c = countBomb(i, j);  // count bomb
					grid[i][j].STATE = c;  // change STATE
				}
	}


	/*---- set KeyEvent ----*/
	void doKeyAction(KeyEvent event){
		switch (event.getCode()){
		case A:
			flagUpdate();
			break;
		case R:
			initSquare();
			break;
		case Q:
			// short-cut key menu
			break;
		default:
			break;
		}
	}

	
	/*---- when clicked grid ----*/
	void gridClicked(ActionEvent event) {
		Square clickSquare = (Square) event.getSource();
		int clickFlagState = clickSquare.flagSTATE;
		
		int[] index = searchSquare(clickSquare);
		int y = index[0];
		int x = index[1];
		
		if(flag == 0) {
			if (grid[y][x].STATE == -1) { 
				grid[y][x].setGraphic(bombIv);
				gameover.showAndWait();
				square_open();
			} else { 
				squareProcess(y, x); 
			}	
		} else {
			flagProcess(y, x, clickFlagState);
		}
	}

	/*---- process flag event ----*/
	void flagProcess(int y, int x, int st){
		if(st == 1){
			grid[y][x].flagSTATE = 0;
			grid[y][x].setGraphic(null);
			grid[y][x].setText(null);
			cnt_bomb++;
		} else {
			grid[y][x].flagSTATE = 1;
			grid[y][x].setGraphic(new ImageView(flagPic));
			grid[y][x].setText(null);
			cnt_bomb--;
			judge_success();
		}
		leftIndicator.setText(Integer.toString(cnt_bomb));
	}
	
	/*---- process square event ----*/
	void squareProcess(int y, int x){
		if(grid[y][x].flagSTATE == 1){
			flagProcess(y, x, 1);
		}
			
		if (grid[y][x].STATE > 0) {
			grid[y][x].setText(Integer.toString(grid[y][x].STATE));
			grid[y][x].setDisableSTATE(true);
			judge_success();
		} else if (grid[y][x].STATE == 0) {
			int i = y-1;
			while(i<=y+1 & i<=column-1) {
				int j = x-1;
				while( j<=x+1 & j<=column-1 ) {
					if ( i == y & j == x ) grid[y][x].setDisableSTATE(true);
					else if (i >= 0 & j >= 0) if(!grid[i][j].getDisableSTATE()) {
						grid[i][j].setDisableSTATE(true);
						squareProcess(i, j);
					}
					j++;
				}
				i++;
			}
		}
	}

	/*---- judge success and show success infomation ----*/
	void judge_success(){
		int i=0, j=0;
		grid: for (i = 0; i < column; i++){
			for (j = 0; j < column; j++){
				if(grid[i][j].STATE >= 0 & !(grid[i][j].getDisableSTATE())){
					break grid;
				}
			}
		}
		if (i==column & j==column){
			successInfo.showAndWait();
			square_open();
		}
	}
	
	/*---- configure flag ----*/
	void flagUpdate() {
		if(flag == 0) {
			flag = 1;
			flagButton.setGraphic(flagOnIv);
		}
		else{
			flag = 0;
			flagButton.setGraphic(flagOffIv);
		}
	}
	
	/*---- make all squares open ----*/
	void square_open(){
		//String[] = {""}
		for (int i = 0; i < column; i++){
			for (int j = 0; j < column; j++){
				if (grid[i][j].STATE == -1 ) grid[i][j].setGraphic(new ImageView(bombPic));
				else if(grid[i][j].STATE > 0) grid[i][j].setText(Integer.toString(grid[i][j].STATE));
				grid[i][j].setDisableSTATE(true);
			}
		}
	}

	/*---- search Square and return index of it ----*/
	int[] searchSquare(Square search) {
		int[] index = new int[2];
		for(int i=0; i<column; i++) {
			for(int j=0; j<column; j++) {
				if(grid[i][j] == search) {
					index[0] = i;
					index[1] = j;
				}
			}
		}
		return index;
	}
	
	/*---- count Bomb around itself ----*/
	int countBomb(int y, int x) {
		int sum = 0;
		int i=y-1, j;
		while(i<=y+1 & i<=column-1) {
			j = x-1;
			while(j<=x+1 & j<=column-1) {
				if(i >= 0 & j >= 0)
					if(grid[i][j].STATE == -1) sum++;
				j++;
			}
			i++;
		}
		return sum;
	}
	
	public static void main(String[] args) {
	 	launch(args);
	}
}
