
import javafx.scene.control.Button;

class Square extends Button {

	int STATE;
	int flagSTATE;
	boolean disableSTATE;

	/*---- constructor with no argument ----*/ 
	Square(){
		super();
		this.disableSTATE = false;
	}
	
	/*---- constructor with String argument ----*/
	Square(String txt){
		super(txt);
		this.disableSTATE = false;
	}

	/*---- set disableSTATE and disable Button ----*/
	void setDisableSTATE(boolean p){
		this.setDisable(p);
		this.disableSTATE = p;
	}

	/*---- get disableSTATE ----*/
	boolean getDisableSTATE(){
		return this.disableSTATE;
	}


	
}
