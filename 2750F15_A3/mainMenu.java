import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainMenu extends JFrame
{

  // Creates a new main menu that has buttons for train, fight, about and exit
	public mainMenu()
	{
		super();
		setSize(600,450);
		setTitle("Codemon - gotta kill em all!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Icon codemonIcon = new ImageIcon("Assets/codemonIMG.jpg");
		JLabel codemonImage = new JLabel(codemonIcon);
		
		setLayout(new BorderLayout());
		JPanel bottomLabel = new JPanel();
		JPanel trainAndFight = new JPanel();
		JPanel aboutAndExit = new JPanel();
		
		bottomLabel.setLayout(new GridLayout(3,1));
		JLabel bottomText = new JLabel("Label", SwingConstants.CENTER);
        bottomText.setText("CIS*2750F15 - Author: Josh Lemieux");
       	
       	JButton trainButton = new JButton("          Train          ");
        trainButton.addActionListener(new trainButtonAction());
       	JButton fightButton = new JButton("          Fight          ");
        fightButton.addActionListener(new fightButtonAction());
       	
       	JButton aboutButton = new JButton("         About         ");
        aboutButton.addActionListener(new aboutButtonAction());
       	JButton exitButton = new JButton( "           Exit           ");
        exitButton.addActionListener(new exitButtonAction());
       	
       	aboutAndExit.add(aboutButton);
       	aboutAndExit.add(exitButton);
       	
       	trainAndFight.add(trainButton);
       	trainAndFight.add(fightButton);
       	
       	bottomLabel.add(trainAndFight);
       	bottomLabel.add(aboutAndExit);
       	bottomLabel.add(bottomText);
       	add(codemonImage, BorderLayout.CENTER);
        add(bottomLabel, BorderLayout.SOUTH);
	}

}

// Creates a new training center menu when clicked
class trainButtonAction implements ActionListener{
  public void actionPerformed(ActionEvent e) {
    trainingCenter trainMenu = new trainingCenter();
  }
}

// Creates a new fight club menu when clicked
class fightButtonAction implements ActionListener{
  public void actionPerformed(ActionEvent e) {
    fightClub fightMenu = new fightClub();
  }
}

// Creates a new about menu when clicked
class aboutButtonAction implements ActionListener{
  public void actionPerformed(ActionEvent e) {
    aboutMenu aboutMen = new aboutMenu();
  }
}

// Closes the program
class exitButtonAction implements ActionListener{
  public void actionPerformed(ActionEvent e){
    System.exit(0);
  }
}

