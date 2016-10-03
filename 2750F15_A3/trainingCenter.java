import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;	
import javax.swing.text.Document.*;
import javax.swing.event.*;

// Creates a new training Center GUI that is used to edit and assemble .cm files 
public class trainingCenter extends JFrame
{
	private JFrame trainFrame = new JFrame();
	private JFrame saveDialog = new JFrame();
	private JFrame configSourceFrame = new JFrame();
	private JTextArea codemonEditArea = new JTextArea();
	private JLabel statusArea = new JLabel("Status",SwingConstants.CENTER);
	private JTextField configSourceText = new JTextField();
	private String textInFile = new String();
	private String modifiedText = "";
	private String fileName = "Untitled";
	private JLabel fileNameLabel = new JLabel();
	private boolean userSaveOption = false;
	private boolean isOpen = false;
	private boolean isQuit = false;

	public trainingCenter()
	{
		super();
		trainFrame.setSize(550,650);
		trainFrame.setTitle("Codemon Training Center");
		trainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		trainFrame.setLayout(new BorderLayout());

		// Creating all the panels and menus
		JPanel trainingCenterMenuPanel = new JPanel();
		trainingCenterMenuPanel.setLayout(new GridLayout(2,1));
		JPanel editAreaPanel = new JPanel();
		editAreaPanel.setLayout(new BorderLayout());
		JMenuBar trainingCenterBar = new JMenuBar();
		JMenuBar trainingCenterButtonBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem fileNew = new JMenuItem("New");
		fileNew.addActionListener(new newButtonAction());
		fileNew.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.addActionListener(new openButtonAction());
		fileOpen.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem fileSave = new JMenuItem("Save");
		fileSave.addActionListener(new saveButtonAction());
		fileSave.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem fileSaveAs = new JMenuItem("Save As");
		fileSaveAs.addActionListener(new saveAsButtonAction());
		fileSaveAs.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem fileQuit = new JMenuItem("Quit");
		fileQuit.addActionListener(new quitButtonAction());
		fileQuit.setAccelerator(KeyStroke.getKeyStroke('U', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenu buildMenu = new JMenu("Build");
		JMenuItem buildAssemble = new JMenuItem("Assemble");
		buildAssemble.addActionListener(new assembleButtonAction());
		buildAssemble.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem buildAssembleLaunch = new JMenuItem("Assemble and Launch");
		buildAssembleLaunch.addActionListener(new assembleAndRunButtonAction());
		buildAssembleLaunch.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		
		JMenu configMenu = new JMenu("Config");
		JMenuItem configSourceDir = new JMenuItem("Source Directory");
		configSourceDir.addActionListener(new configSourceAction());
		configSourceDir.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem configCodemonDir = new JMenuItem("Codemon Directory");
		configCodemonDir.setAccelerator(KeyStroke.getKeyStroke('K', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		configCodemonDir.addActionListener(new configCodemonAction());
		
		JMenu helpMenu = new JMenu("Help");
		JMenuItem helpHelp = new JMenuItem("Help");
		helpHelp.addActionListener(new helpButtonAction());
		helpHelp.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		Icon newFileIcon = new ImageIcon("Assets/newFile.jpg");
		JButton newFileButton = new JButton(newFileIcon);
		newFileButton.setToolTipText("Create a new file");
		newFileButton.addActionListener(new newButtonAction());
		trainingCenterButtonBar.add(newFileButton);

		Icon openIcon = new ImageIcon("Assets/openFile.jpg");
		JButton openButton = new JButton(openIcon);
		openButton.setToolTipText("Open a file");
		openButton.addActionListener(new openButtonAction());
		trainingCenterButtonBar.add(openButton);

		Icon saveIcon = new ImageIcon("Assets/save.jpg");
		JButton saveButton = new JButton(saveIcon);
		saveButton.setToolTipText("Save current file");
		saveButton.addActionListener(new saveButtonAction());
		trainingCenterButtonBar.add(saveButton);

		Icon saveAsIcon = new ImageIcon("Assets/saveAs.jpg");
		JButton saveAsButton = new JButton(saveAsIcon);
		saveAsButton.setToolTipText("Save current file to different name");
		saveAsButton.addActionListener(new saveAsButtonAction());
		trainingCenterButtonBar.add(saveAsButton);

		Icon assembleIcon = new ImageIcon("Assets/assemble.jpg");
		JButton assembleButton = new JButton(assembleIcon);
		assembleButton.setToolTipText("Assemble currently loaded file");
		assembleButton.addActionListener(new assembleButtonAction());
		trainingCenterButtonBar.add(assembleButton);

		// Adding all of the components to the GUI
		fileMenu.add(fileNew);
		fileMenu.add(fileOpen);
		fileMenu.add(fileSave);
		fileMenu.add(fileSaveAs);
		fileMenu.add(fileQuit);
		
		buildMenu.add(buildAssemble);
		buildMenu.add(buildAssembleLaunch);
		
		configMenu.add(configSourceDir);
		configMenu.add(configCodemonDir);

		helpMenu.add(helpHelp);
		
		trainingCenterBar.add(fileMenu);
		trainingCenterBar.add(buildMenu);
		trainingCenterBar.add(configMenu);
		trainingCenterBar.add(helpMenu);
		trainingCenterMenuPanel.add(trainingCenterBar);
		trainingCenterMenuPanel.add(trainingCenterButtonBar);
		trainFrame.add(trainingCenterMenuPanel, BorderLayout.NORTH);

		trainFrame.addWindowListener(new WindowAdapter()
        		{
           			@Override
            		public void windowClosing(WindowEvent e)
            		{
                		if(modifiedText.equals("Modified"))
						{
							makeSaveDialog();

						}
						e.getWindow().dispose();
            		}
        		});
		// Document listener that checks if anything is typed in edit area and adds modified if so
		codemonEditArea.getDocument().addDocumentListener(new DocumentListener()
        {

            public void changedUpdate(DocumentEvent e) 
            {
            	modifiedText = "Modified";
                statusArea.setText(modifiedText);

            }
            public void insertUpdate(DocumentEvent e) 
            {
                modifiedText = "Modified";
                statusArea.setText(modifiedText);
            }

            public void removeUpdate(DocumentEvent e) 
            {
                modifiedText = "Modified";
                statusArea.setText(modifiedText);
            }
        });

		JScrollPane quickViewScroll = new JScrollPane(codemonEditArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fileNameLabel.setText(fileName);
		editAreaPanel.add(quickViewScroll, BorderLayout.CENTER);
		editAreaPanel.add(fileNameLabel, BorderLayout.NORTH);
		statusArea.setText(modifiedText);
		trainFrame.add(statusArea, BorderLayout.SOUTH);
		trainFrame.add(editAreaPanel, BorderLayout.CENTER);
		trainFrame.setVisible(true);
		
	
	}

	// When quit button is clicked ask the user if they want to save then quit program
	class quitButtonAction implements ActionListener{
  		public void actionPerformed(ActionEvent e) {
  			if(modifiedText.equals("Modified"))	
  			{
  				isQuit = true;
  				makeSaveDialog();
  			}
  			else
    			System.exit(0);
  		}
	}

	// Open the help menu when help is selected
	class helpButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			helpMenu help = new helpMenu();
		}
	}

	// When new button is selected ask user if they want to save and then clear the text area 
	class newButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {

			if(modifiedText.equals("Modified"))
			{
				makeSaveDialog();
			}
			else
			{
				codemonEditArea.setText("");
				userSaveOption = false;
				fileName = "Untitled";
				fileNameLabel.setText(fileName);
				modifiedText = "";
				statusArea.setText(modifiedText);
			}

		}
	}

	// Open saveAs dialog
	class saveAsButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			saveAsFile();
		}
	}

	// When save button is clicked save the file , if it is a new document open the save as dialog
	class saveButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){

			if(fileNameLabel.getText().equals("Untitled"))
			{
				saveAsFile();
				return;
			}
			else
			{
				save();
			}
			try{
				PrintWriter writer = new PrintWriter(fileNameLabel.getText(), "UTF-8"); 
				writer.println(codemonEditArea.getText());
				writer.close();
				modifiedText = "";
				statusArea.setText(modifiedText);
			} catch(FileNotFoundException ex) {
				JOptionPane.showMessageDialog(null, "File could not be saved", "Error", JOptionPane.INFORMATION_MESSAGE);
			} catch(UnsupportedEncodingException exe) {}
			
		}
	}

	// Assemble the codemon in the quick view text and open a new fightclub window, displays a error message if there was a problem with the .cm file
	class assembleAndRunButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){

			File fName = new File(fileName);
			String fSend = Codemon.currentSaveDirectory.toString().concat("/").concat(fName.getName().toString());

			if(fSend.equals(Codemon.currentSaveDirectory.getName().concat("/Untitled"))){
				JOptionPane.showMessageDialog(null, "File cannot be Untitled", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				String fPath =Codemon.currentCodemonDirectory.toString().concat("/") + fName.getName().toString().replaceFirst("[.][^.]+$", "").concat(".codemon");
				int parseWorked = Codemon.parseCodemon(fSend, fPath);
				if(parseWorked == 1)
					JOptionPane.showMessageDialog(null, "Error in .cm file", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
			fightClub newWindow = new fightClub();
		}
	}

	// Assemble the codemon in the quick view text, displays a error message if there was a problem with the .cm file
	class assembleButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			File fName = new File(fileName);
			//System.out.println(Codemon.parseCodemon(Codemon.currentSourceDirectory.getName().concat("/").concat(fileName.getName().toString()));
			String fSend = Codemon.currentSaveDirectory.toString().concat("/").concat(fName.getName().toString());
	
			if(fSend.equals(Codemon.currentSaveDirectory.getName().concat("/Untitled")) || fName.getName().toString().equals("Untitled")){
				JOptionPane.showMessageDialog(null, "File cannot be Untitled", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				String fPath =Codemon.currentCodemonDirectory.toString().concat("/") + fName.getName().toString().replaceFirst("[.][^.]+$", "").concat(".codemon");
				int parseWorked = Codemon.parseCodemon(fSend, fPath);

				if(parseWorked == 1)
					JOptionPane.showMessageDialog(null, "Error in .cm file", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// Saves the file
	public void save()
	{
		try{
			PrintWriter writer = new PrintWriter(fileNameLabel.getText(), "UTF-8"); 
			writer.println(codemonEditArea.getText());
			writer.close();
			modifiedText = "";
			statusArea.setText(modifiedText);
			} catch(FileNotFoundException ex) {
				JOptionPane.showMessageDialog(null, "File could not be saved", "Error", JOptionPane.INFORMATION_MESSAGE);
			} catch(UnsupportedEncodingException exe) {}

	}
	// Opens a file when open button pressed
	class openButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {

			isOpen = true;
			if(modifiedText.equals(""))
				openFile();
			else
				makeSaveDialog();
		}
	}

	// Opens a directory dialog so the user can change their source .cm directory
	class configSourceAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser configSource = new JFileChooser(Codemon.currentSaveDirectory);
			configSource.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			configSource.setDialogTitle("Source Directory for Input Files");
			int isValidFile = configSource.showDialog(trainFrame, "Change Directory");

			if(isValidFile == JFileChooser.APPROVE_OPTION && configSource.getSelectedFile().isDirectory())
				Codemon.currentSaveDirectory = configSource.getSelectedFile();
			
		}
	}
	// Opens a directory dialog so the user can change their codemon .cm directory
	class configCodemonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){

			JFileChooser configCodemon = new JFileChooser(Codemon.currentCodemonDirectory);
			configCodemon.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			configCodemon.setDialogTitle("Codemon Directory for Output Files");
			int isValidFile = configCodemon.showDialog(trainFrame, "Change Directory");

			if(isValidFile == JFileChooser.APPROVE_OPTION && configCodemon.getSelectedFile().isDirectory())
				Codemon.currentCodemonDirectory = configCodemon.getSelectedFile();

		}
	}

	// Asks user if they would like to save thier file
	public void makeSaveDialog()
	{
		saveDialog.setSize(300,200);
		saveDialog.setTitle("Save");
		saveDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		saveDialog.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,4));
		JLabel saveLabel = new JLabel("save", SwingConstants.CENTER);
		saveLabel.setText("Would you like to Save your changes?");

		JButton yesButton = new JButton("Yes");
		JButton noButton = new JButton("No");
		noButton.setFocusPainted(false);
		yesButton.setFocusPainted(false);
		noButton.addActionListener(new noButtonAction());
		yesButton.addActionListener(new yesButtonAction());
		buttonPanel.add(noButton);
		buttonPanel.add(yesButton);
		saveDialog.add(saveLabel, BorderLayout.CENTER);
		saveDialog.add(buttonPanel, BorderLayout.SOUTH);

		saveDialog.setVisible(true);

	}


	class noButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{
			if(isQuit)
			{
				System.exit(0);
			}
			else if(isOpen)
			{
				openFile();
			}

			else if(!isOpen)
			{
				codemonEditArea.setText("");
				userSaveOption = false;
				fileName = "Untitled";
				fileNameLabel.setText(fileName);
				modifiedText = "";
				statusArea.setText(modifiedText);
			}
			userSaveOption = false;
			isOpen = false;
			saveDialog.dispose();
		}
	}

	class yesButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){

			userSaveOption = true;

			if(isQuit)
			{
				if(!(fileName.equals("Untitled")))
					save();
				else
					saveAsFile();
			}
			if(!(fileName.equals("Untitled")) && !(isOpen))
			{

				if(userSaveOption == true)
				{
					save();
					codemonEditArea.setText("");
					userSaveOption = false;
					fileName = "Untitled";
					fileNameLabel.setText(fileName);
					modifiedText = "";
					statusArea.setText(modifiedText);
				}
			}
			else if(fileName.equals("Untitled") && !(isOpen))
			{

				if(userSaveOption == true)
				{
					saveAsFile();
					codemonEditArea.setText("");
					userSaveOption = false;
					fileName = "Untitled";
					fileNameLabel.setText(fileName);
					modifiedText = "";
					statusArea.setText(modifiedText);
				}
			}

			if(!(fileName.equals("Untitled")) && isOpen)
			{
				save();
				openFile();
			}

			else if(fileName.equals("Untitled") && isOpen)
			{
				saveAsFile();
				openFile();
			}

			saveDialog.dispose();
		}
	}

	// Opens a the selected file and loads it into the quick view area
	public void openFile()
	{
		JFileChooser openCodemon = new JFileChooser();
		openCodemon.setAcceptAllFileFilterUsed(false);
		openCodemon.setCurrentDirectory(Codemon.currentSaveDirectory);
		FileNameExtensionFilter cmFilter = new FileNameExtensionFilter("Codemon Doc", "cm");
		openCodemon.setFileFilter(cmFilter);
		int isValidFile = openCodemon.showOpenDialog(trainFrame);

		if(isValidFile == JFileChooser.APPROVE_OPTION)
		{
			File codemonFile = new File(openCodemon.getCurrentDirectory(), openCodemon.getSelectedFile().getName());
			fileName = codemonFile.getAbsolutePath();

			try {
				readCodemonFile(); }
				catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null, "File could not be opened", "Error", JOptionPane.INFORMATION_MESSAGE);
				}
				catch (IOException ex2) {
					JOptionPane.showMessageDialog(null, "File could not be read", "Error", JOptionPane.INFORMATION_MESSAGE);
				}
				modifiedText = "";
				statusArea.setText(modifiedText);	
				isOpen = false;	
		}
	}

	// Opens a saveAs dialog box and saves the file to the new name
	public void saveAsFile()
	{
		File currentFile = new File(fileName);
		JFileChooser saveCodemon = new JFileChooser();
		saveCodemon.setAcceptAllFileFilterUsed(false);
		saveCodemon.setCurrentDirectory(Codemon.currentSaveDirectory);

		if(!(fileName.equals("Untitled")))
			saveCodemon.setSelectedFile(currentFile);

		FileNameExtensionFilter cmFilter = new FileNameExtensionFilter("Codemon Doc", "cm");
		saveCodemon.setFileFilter(cmFilter);
		int isValidFile = saveCodemon.showSaveDialog(trainFrame);
		// Check isValidFile to see if cancelled

		if(isValidFile == JFileChooser.APPROVE_OPTION)
		{

			try{
					PrintWriter writer = new PrintWriter(new File(saveCodemon.getCurrentDirectory(), saveCodemon.getSelectedFile().getName()), "UTF-8"); 
					writer.println(codemonEditArea.getText());
					writer.close();
					fileName = saveCodemon.getSelectedFile().getAbsolutePath();
					fileNameLabel.setText(fileName);
					modifiedText = "";
					statusArea.setText(modifiedText);
					
				} catch(FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null, "File could not be saved", "Error", JOptionPane.INFORMATION_MESSAGE);
				} catch(UnsupportedEncodingException exe) {}
		}
	}

	// Puts the text in in a cm file in the edit area
	public void readCodemonFile() throws FileNotFoundException, IOException{
	BufferedReader buff = new BufferedReader(new FileReader(fileName));
	try {
    	StringBuilder sb = new StringBuilder();
    	String line = buff.readLine();

    	while (line != null) 
    	{
        	sb.append(line);
        	sb.append(System.lineSeparator());
        	line = buff.readLine();
    	}

    	textInFile = sb.toString();
    	codemonEditArea.setText("");
    	codemonEditArea.append(textInFile);
    	codemonEditArea.setCaretPosition(0);
    	fileNameLabel.setText(fileName);
		} finally {
    		buff.close();
		}

	}

	
}