import java.awt.Color;	

public class codemonTrainer{
	
	private String playerID;
	private String codemonName;
	private int lineAmount;
	private Color playerColor;

	public codemonTrainer(){
		this.playerID = null;
		this.lineAmount = 0;
		this.playerColor = null;
	}

	public codemonTrainer(String playerID, String codemonName, int lineAmount, Color playerColor){
		this.playerID = playerID;
		this.lineAmount = lineAmount;
		this.playerColor = playerColor;
		this.codemonName = codemonName;
	}

	public codemonTrainer(String playerID, String codemonName, Color playerColor)
	{
		this.playerID = playerID;
		this.codemonName = codemonName;
		this.playerColor = playerColor;
	}

	public String getID(){
		return this.playerID;
	}

	public Color getColor(){
		return this.playerColor;
	}

	public int getLines(){
		return this.lineAmount;
	}

	public String getCodemonName(){
		return this.codemonName;
	}
}