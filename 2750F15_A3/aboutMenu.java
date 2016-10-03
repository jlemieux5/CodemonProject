import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class aboutMenu extends JFrame 
{
	private JFrame aboutFrame = new JFrame();

	// Creates a new about menu that his info about the author, student number and README
	public aboutMenu()
	{
		super();
		aboutFrame.setSize(400,400);
		aboutFrame.setTitle("About");
		aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		aboutFrame.setLayout(new BorderLayout());
		JLabel aboutInfo = new JLabel();
		aboutInfo.setText("<HTML><body>Author: Josh Lemieux <br><br> Student ID: 0859713 <br><br> Due Date: November 13th<br><br>This package contains different java and c source files, as well as an asset folder for all icons and pictures. It also has a reports folder, codemon folder and a source folder for the defaults for their respective files. To run this program type make when you are in the proper directory and then type “java Codemon” to run.<br><br> KNOWN LIMITATIONS: When changing your current codemon directory you must reload the window to get an updated list of available Codemon. <br> Brackets to not provided precedence. <br> If you wish to use the subtraction operator , you MUST separate the ‘-‘ with a space before your digit <body><HTML>");
		JButton dismissButton = new JButton("Dismiss");
		dismissButton.addActionListener(new dismissButtonAction());
		aboutFrame.add(aboutInfo, BorderLayout.CENTER);
		aboutFrame.add(dismissButton, BorderLayout.SOUTH);
		aboutFrame.setVisible(true);
	}

	//Closes the menu when dismiss is clicked
	class dismissButtonAction implements ActionListener{
  		public void actionPerformed(ActionEvent e) {
    		aboutFrame.dispose();
  		}
	}
}

