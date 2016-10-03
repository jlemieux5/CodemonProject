import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class helpMenu extends JFrame
{
	private JFrame helpFrame = new JFrame();

	// Creates a new help menu that has information about the program
	public helpMenu()
	{
		super();
		helpFrame.setSize(400,400);
		helpFrame.setTitle("Help");
		helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		helpFrame.setLayout(new BorderLayout());
		JLabel helpInfo = new JLabel();
		
		helpInfo.setText("<HTML><body>Welcome to the Codemon Help Menu<br><br>Editing: To Edit a Codemon use the Quick View Menu in the Training Center<br>You can Open an old Codemon file or start a new one<br>To assemble a codemon you must first save it and then hit the assemble button <br><br> When Switching Codemon Directories you will have to reload the window to see the change, reports will update automaticall on directory changes<body><HTML>");
		JButton OkButton = new JButton("OK");
		OkButton.addActionListener(new OkButtonAction());
		helpFrame.add(helpInfo, BorderLayout.CENTER);	
		helpFrame.add(OkButton, BorderLayout.SOUTH);
		helpFrame.setVisible(true);

	}
	// closes the frame on OK
	class OkButtonAction implements ActionListener{
  		public void actionPerformed(ActionEvent e) {
    		helpFrame.dispose();
  		}
	}
}