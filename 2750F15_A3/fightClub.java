import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;	
import java.text.*;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;
import java.sql.*;

public class fightClub extends JFrame
{
	private JFrame fightClubFrame = new JFrame();
	private JFrame iterationFrame = new JFrame();
	private JTextField turnLimitText = new JTextField();
	private String codemonFileName = new String();
	private String reportFileName = new String();
	private String line = new String();
	private JComboBox<String> codemonOneDropDown = new JComboBox<>();
	private JComboBox<String> codemonTwoDropDown = new JComboBox<>();
	private int turnLimit = 0;
	private int pvpMode = 2;
	private DefaultListModel<String> reportsList = new DefaultListModel<>();;
	private DefaultListModel<String> codemonList = new DefaultListModel<>();
	private JList<String> reportsJList = new JList<>(reportsList);
	private JList<String> codemonJList = new JList<>(codemonList);
	private List<String> codemonFileList = new ArrayList<String>();
	private List<String> reportsFileList = new ArrayList<String>();
	private int index = 0;
	private int TIMER_SPEED = 100;
	private File currentReportsDirectory = new File(Codemon.currentDirectory, "/Reports");
	private List<JPanel> visualizeList = new ArrayList<JPanel>(8192);
	private List<codemonTrainer> codemonTrainerList = new ArrayList<codemonTrainer>(4);
	private Timer visualizeTimer = new Timer(0, null);
	private Statement stmt = null;


	// Constructor for building the GUi for fightclub
	public fightClub()
	{
		super();
		fightClubFrame.setSize(650,550);
		fightClubFrame.setTitle("Codemon Fight Club");
		fightClubFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		fightClubFrame.setLayout(new BorderLayout());
		
		JPanel fightClubWest = new JPanel();
		JPanel codemonChoicePanel = new JPanel();
		codemonChoicePanel.setLayout(new GridLayout(5,1));	
		fightClubWest.setLayout(new BorderLayout());
		
		JLabel iChooseYou = new JLabel();
		iChooseYou.setText(" I choose you...");
		codemonChoicePanel.add(iChooseYou);
		JLabel blank = new JLabel();
		codemonChoicePanel.add(blank);
		
		
		JPanel codemonOnePanel = new JPanel();
		codemonOnePanel.setLayout(new BorderLayout());
		JLabel codemonOneBlank = new JLabel();
		codemonOneBlank.setText("     ");
		JLabel codemonOne = new JLabel();
		codemonOne.setText(" Codemon 1: ");
		TextArea quickViewText = new TextArea(20,55);
		
		codemonOnePanel.add(codemonOne, BorderLayout.WEST);
		codemonOnePanel.add(codemonOneDropDown, BorderLayout.CENTER);
		codemonOnePanel.add(codemonOneBlank,BorderLayout.EAST);
		
		
		JPanel codemonTwoPanel = new JPanel();
		codemonTwoPanel.setLayout(new BorderLayout());
		JLabel codemonTwoBlank = new JLabel();
		codemonTwoBlank.setText("     ");
		JLabel codemonTwo = new JLabel();
		codemonTwo.setText(" Codemon 2: ");	
		codemonTwoPanel.add(codemonTwo, BorderLayout.WEST);
		codemonTwoPanel.add(codemonTwoDropDown, BorderLayout.CENTER);
		codemonTwoPanel.add(codemonTwoBlank, BorderLayout.EAST);
		
		JLabel quickViewLabel = new JLabel();
		quickViewLabel.setText(" Quick View");
		
		codemonChoicePanel.add(codemonOnePanel);
		codemonChoicePanel.add(codemonTwoPanel);
		codemonChoicePanel.add(quickViewLabel);
		

		JPanel reportsPanel = new JPanel();
		reportsPanel.setLayout(new BorderLayout());
		JLabel reportsLabel = new JLabel();
		reportsLabel.setText(" Reports");
		updateReportList();
		updateCodemonList();
		JScrollPane reportsListScrollPane = new JScrollPane(reportsJList);
		reportsListScrollPane.setPreferredSize(new Dimension(300, 100));
		//TextArea reportsText = new TextArea(20,30);
		JScrollPane codemonListScrollPane = new JScrollPane(codemonJList);
		codemonListScrollPane.setPreferredSize(new Dimension(300, 100));

		reportsPanel.add(reportsLabel, BorderLayout.NORTH);
		reportsPanel.add(reportsListScrollPane, BorderLayout.CENTER);
		
		fightClubWest.add(codemonListScrollPane, BorderLayout.CENTER);
		fightClubWest.add(codemonChoicePanel, BorderLayout.NORTH);
		
		fightClubFrame.add(reportsPanel, BorderLayout.EAST);
		fightClubFrame.add(fightClubWest, BorderLayout.WEST);

		JPanel fightClubMenuPanel = new JPanel();
		fightClubMenuPanel.setLayout(new GridLayout(2,1));
		JMenuBar fightClubButtonBar = new JMenuBar();
		JMenuBar fightClubBar = new JMenuBar();

		JMenu configMenu = new JMenu("Config");
		JMenu reportsMenu = new JMenu("Reports");
		JMenu helpMenu = new JMenu("Help");
		
		// Adding JMenu items for config and setting their action Listeners
		JMenuItem configSourceDir = new JMenuItem("Source Directory");
		configSourceDir.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		configSourceDir.addActionListener(new configSourceAction());
		JMenuItem configCodemonDir = new JMenuItem("Codemon Directory");
		configCodemonDir.addActionListener(new configCodemonAction());
		configCodemonDir.setAccelerator(KeyStroke.getKeyStroke('K', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem configReportsDir = new JMenuItem("Reports Directory");
		configReportsDir.addActionListener(new reportsDirAction());
		configReportsDir.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem configIterationLimit = new JMenuItem("Iteration Limit");
		configIterationLimit.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		configIterationLimit.addActionListener(new iterationButtonAction());
		JMenuItem configVS = new JMenu("VS Mode");

		reportsJList.addMouseListener(new reportListClicked());
		codemonJList.addMouseListener(new codemonListClicked());
		ButtonGroup versusGroup = new ButtonGroup();
		JRadioButton versusOne = new JRadioButton("vs.1", true);
		versusOne.addActionListener(new versusButtonActionOne());
		JRadioButton versusTwo = new JRadioButton("vs.2");
		versusTwo.addActionListener(new versusButtonActionTwo());
		JRadioButton versusThree = new JRadioButton("vs.3");
		versusThree.addActionListener(new versusButtonActionThree());

		Icon runTestIcon = new ImageIcon("Assets/runTest.jpg");
		JButton runTestButton = new JButton(runTestIcon);
		runTestButton.addActionListener(new testButtonAction());
		runTestButton.setToolTipText("Run Test with Codemon 1");
		fightClubButtonBar.add(runTestButton);

		Icon runSelfIcon = new ImageIcon("Assets/runSelf.jpg");
		JButton runSelfButton = new JButton(runSelfIcon);
		runSelfButton.addActionListener(new selfButtonAction());
		runSelfButton.setToolTipText("Run Self with Codemon 1 and 2");
		fightClubButtonBar.add(runSelfButton);

		Icon runPVPIcon = new ImageIcon("Assets/vs.jpg");
		JButton runPVPButton = new JButton(runPVPIcon);
		runPVPButton.addActionListener(new pvpButtonAction());
		runPVPButton.setToolTipText("Run PVP with Codemon 1");
		fightClubButtonBar.add(runPVPButton);

		Icon deleteIcon = new ImageIcon("Assets/delete.jpg");
		JButton deleteButton = new JButton(deleteIcon);
		deleteButton.addActionListener(new reportsDeleteAction());
		deleteButton.setToolTipText("Delete current report");
		fightClubButtonBar.add(deleteButton);

		Icon visualizeIcon = new ImageIcon("Assets/visualize.gif");
		JButton visualizeButton = new JButton(visualizeIcon);
		visualizeButton.addActionListener(new visualizeAction());
		fightClubButtonBar.add(visualizeButton);
		
		versusGroup.add(versusOne);
		versusGroup.add(versusTwo);
		versusGroup.add(versusThree);
		
		configVS.add(versusOne);
		configVS.add(versusTwo);
		configVS.add(versusThree);
		
		JMenuItem reportsView = new JMenuItem("View");
		reportsView.addActionListener(new reportViewAction());
		reportsView.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem reportsDelete = new JMenuItem("Delete");
		reportsDelete.addActionListener(new reportsDeleteAction());
		reportsDelete.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem reportsFetchAll = new JMenuItem("Fetch All");
		reportsFetchAll.addActionListener(new fetchAllAction());
		reportsFetchAll.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem reportsVisualize = new JMenuItem("Visualize");
		reportsVisualize.addActionListener(new visualizeAction());
		reportsVisualize.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		JMenuItem reportsHistory = new JMenuItem("History");
		reportsHistory.addActionListener(new historyAction());
		reportsHistory.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		
		JMenuItem helpHelp = new JMenuItem("Help");
		helpHelp.addActionListener(new helpButtonAction());
		helpHelp.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		
		configMenu.add(configSourceDir);
		configMenu.add(configCodemonDir);
		configMenu.add(configReportsDir);
		configMenu.add(configIterationLimit);
		configMenu.add(configVS);
		
		reportsMenu.add(reportsView);
		reportsMenu.add(reportsDelete);
		reportsMenu.add(reportsFetchAll);
		reportsMenu.add(reportsVisualize);
		reportsMenu.add(reportsHistory);
		
		helpMenu.add(helpHelp);
		
		fightClubBar.add(configMenu);
		fightClubBar.add(reportsMenu);
		fightClubBar.add(helpMenu);
		fightClubMenuPanel.add(fightClubBar);
		fightClubMenuPanel.add(fightClubButtonBar);
		fightClubFrame.add(fightClubMenuPanel, BorderLayout.NORTH);
		fightClubFrame.setVisible(true);


	}
	
	// Opens a new help menu
	class helpButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			helpMenu help = new helpMenu();
		}
	}

	class historyAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			historyMenu history = new historyMenu();
		}
	}

	// Sets pvpMode to 2 when user selects vs 2
	class versusButtonActionOne implements ActionListener{
		public void actionPerformed(ActionEvent e){
			pvpMode = 2;
		}
	}
	// Sets pvpMode to 3 when user selects vs 3
	class versusButtonActionTwo implements ActionListener{
		public void actionPerformed(ActionEvent e){
			pvpMode = 3;
		}
	}
	// Sets pvpMode to 4 when user selects vs 4
	class versusButtonActionThree implements ActionListener{
		public void actionPerformed(ActionEvent e){
			pvpMode = 4;
		}
	}

	// Shows the selected report when user selects a reports
	class reportViewAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
				index = reportsJList.getSelectedIndex();
				showQuickViewReport();
				updateReportList();}
			catch(ArrayIndexOutOfBoundsException ex){
				JOptionPane.showMessageDialog(null, "No Report Selected", "Error", JOptionPane.INFORMATION_MESSAGE);}
			

		}
	}

	// Creates the initial frame for visualization after the visualize button has been pressed
	class visualizeAction implements ActionListener{
		public void actionPerformed(ActionEvent e){

			TIMER_SPEED = 100;
			try{
				index = reportsJList.getSelectedIndex();
				

				reportFileName = reportsFileList.get(index).concat(".log");
				reportFileName = currentReportsDirectory.toString().concat("/").concat(reportFileName);
				File tempFile = new File(reportFileName);

				// if report hasnt been grabbed yet, get it before trying to visualize
				if(isEmptyFile(reportFileName))
				{
					String fileSent = currentReportsDirectory.getName().concat("/").concat(tempFile.getName());
					int reportIDSent = Integer.parseInt(tempFile.getName().replaceFirst("[.][^.]+$", ""));
					int reportSuccess = Codemon.getReport(reportIDSent, fileSent);

					if(reportSuccess == 0)
					{
						JOptionPane.showMessageDialog(null, "Report could not be grabbed", "Error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				int counter = 0;
				JFrame visualizeFrame = new JFrame();
				visualizeTimer = null;
				visualizeTimer = new Timer(0, null);
				visualizeFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				visualizeFrame.addWindowListener(new WindowAdapter()
        		{
           			@Override
            		public void windowClosing(WindowEvent e)
            		{
                		visualizeTimer.stop();
                		e.getWindow().dispose();
            		}
        		});
			
				visualizeFrame.setTitle("Visualization: " + reportsJList.getSelectedValue());
				visualizeFrame.setSize(1000,700);
				visualizeFrame.setResizable(false);
				visualizeFrame.setLayout(new BorderLayout());
				JPanel centerPanel = new JPanel();
				JPanel southPanel = new JPanel();
				JLabel nothingLabel = new JLabel();
				JLabel nothingLabelTwo = new JLabel();
				JLabel nothingLabelThree = new JLabel();
				JLabel nothingLabelFour = new JLabel();
				JMenuBar southButtonBar = new JMenuBar();

				// creating all buttons for visualize frame
				Icon stopIcon = new ImageIcon("Assets/stop.gif");
				JButton stopButton = new JButton(stopIcon);
				stopButton.setToolTipText("Stops the display");
				stopButton.addActionListener(new stopButtonAction());
				southButtonBar.add(stopButton);

				Icon playIcon = new ImageIcon("Assets/play.gif");
				JButton playButton = new JButton(playIcon);
				playButton.setPreferredSize(new Dimension(40, 40));
				playButton.setToolTipText("Continue the display");
				playButton.addActionListener(new playButtonAction());
				southButtonBar.add(playButton);

				Icon forwardIcon = new ImageIcon("Assets/forward.gif");
				JButton forwardButton = new JButton(forwardIcon);
				forwardButton.setToolTipText("Speeds up display");
				forwardButton.addActionListener(new forwardButtonAction());
				southButtonBar.add(forwardButton);

				Icon slowIcon = new ImageIcon("Assets/slow.gif");
				JButton slowButton = new JButton(slowIcon);
				slowButton.setToolTipText("Speeds up display");
				slowButton.addActionListener(new slowButtonAction());
				southButtonBar.add(slowButton);

				southPanel.setLayout(new GridLayout(1,5));
				centerPanel.setLayout(new GridLayout(128,64));
				
				// creating the grid for displaying memory locations and added it to the visualize frame 
				while(counter < 8192)
				{
					visualizeList.add(counter, new JPanel());
					visualizeList.get(counter).setBackground(Color.BLACK);
					visualizeList.get(counter).setBorder(BorderFactory.createLineBorder(Color.black));
					centerPanel.add(visualizeList.get(counter));
					visualizeList.get(counter).setVisible(true);
					counter++;

				}

				// adding to visualize frame
				southButtonBar.setBackground(Color.GRAY);
				southPanel.add(nothingLabel);
				southPanel.add(nothingLabelThree);
				southPanel.add(southButtonBar);
				southPanel.add(nothingLabelTwo);
				southPanel.add(nothingLabelFour);
				visualizeFrame.add(centerPanel, BorderLayout.CENTER);
				visualizeFrame.add(southPanel, BorderLayout.SOUTH);
				visualizeFrame.setVisible(true);
				parseReport(visualizeFrame);


			}catch(ArrayIndexOutOfBoundsException ex){
				JOptionPane.showMessageDialog(null, "No Report Selected", "Error", JOptionPane.INFORMATION_MESSAGE);}
		}
	}

	// stops the timer if stop button pressed
	class stopButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			visualizeTimer.stop();
		}
	}

	// starts the timer if play button pressed
	class playButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			visualizeTimer.start();
		}
	}

	// Doubles the speed of the itmer if forward button pressed
	class forwardButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			visualizeTimer.stop();
			
			TIMER_SPEED = TIMER_SPEED / 2;
			visualizeTimer.setDelay(TIMER_SPEED);
			visualizeTimer.start();

		}
	}

	// halves the speed of the timer if the slow button is pressed
	class slowButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			visualizeTimer.stop();
			TIMER_SPEED = TIMER_SPEED * 2;
			visualizeTimer.setDelay(TIMER_SPEED);
			visualizeTimer.start();
		}
	}

	// Main function to parse the report
	public void parseReport(JFrame visualizeFrame1){

		int playerAmount = 0;
		int i  = 0;
		int j = 0;
		JPanel northPanel = new JPanel();
		JLabel trainerOne = new JLabel();
		JLabel trainerTwo = new JLabel();
		JLabel trainerThree = new JLabel();
		JLabel trainerFour = new JLabel();
		Font font = new Font("Ariel", Font.BOLD,18);
		trainerOne.setFont(font);
		trainerTwo.setFont(font);
		trainerThree.setFont(font);
		trainerFour.setFont(font);
		northPanel.setLayout(new GridLayout(1,4));
		northPanel.add(trainerOne);
		northPanel.add(trainerTwo);
		northPanel.add(trainerThree);
		northPanel.add(trainerFour);
		visualizeFrame1.add(northPanel, BorderLayout.NORTH);

		try{
		playerAmount = storeCodemonTrainers();

		// setting the name of the trainer and their color in the top of the frame
		while(j < playerAmount)
		{
			
			if(j == 0)
			{
				trainerOne.setForeground(codemonTrainerList.get(j).getColor());
				trainerOne.setText("           " + codemonTrainerList.get(j).getID());
			}
			else if(j == 1)
			{
				trainerTwo.setForeground(codemonTrainerList.get(j).getColor());
				trainerTwo.setText("           " + codemonTrainerList.get(j).getID());
			}
			else if(j == 2)
			{
				trainerThree.setForeground(codemonTrainerList.get(j).getColor());
				trainerThree.setText("           " + codemonTrainerList.get(j).getID());
			}
			else
			{
				trainerFour.setForeground(codemonTrainerList.get(j).getColor());
				trainerFour.setText("           "+ codemonTrainerList.get(j).getID());
			}
			j++;
		}
		}catch(FileNotFoundException e){}catch(IOException ex){}catch(NullPointerException exe){}

		
		try{
			while(i < playerAmount)
			{
				colorFirstLine(codemonTrainerList.get(i), i);
				i++;
			}
		}catch(FileNotFoundException e){} catch(IOException ex){}

		try{
			colorRemainingLines(visualizeFrame1);}catch(FileNotFoundException e){}catch(IOException ex){}catch(NullPointerException exe){}

	}

	public void addMatchToDatabase(int reportID)throws FileNotFoundException, IOException, NullPointerException{

		
		String reportString = reportID + "";
		reportString = currentReportsDirectory.toString().concat("/").concat(reportString.concat(".log"));
		BufferedReader buff = new BufferedReader(new FileReader(reportString));
		String line = " ";
		String[] split;
		String trainerID;
		String codemonName;
		int playerAmount = 1;
		char result; 
		String codemonSource = " ";
		String cName = "";

		createDatabaseRow(reportID);

		while(!(line.contains("battle"))){
			

			if(line.contains("said"))
			{
				split = line.split(" said, \"", 2);
				trainerID = split[0];
				codemonName = split[1];
				split = trainerID.split(" ", 2);
				trainerID = split[1];
				split = codemonName.split(",");
				codemonName = split[0];

				
				updateGameDatabase(reportID, playerAmount, trainerID, codemonName);

				playerAmount++;
			}
		
			if(line.contains("---"))
			{
				codemonSource = "";
				line = buff.readLine();
				while(!line.contains("---"))
				{
					codemonSource = codemonSource + "\n" + line;
					line = buff.readLine();
				}
				split = codemonSource.split("\n", 2);
				split = split[1].split("] ", 2);
				cName = split[0].substring(1);
				
				updateCodemonSource(codemonSource, cName);
			}

			if((line.contains("battle")))
				break;

			line = buff.readLine();

		}

		while(!line.contains(">>>"))
		{
			line = buff.readLine();
		}
		if(line.contains("TIE"))
		{
			result = 'T';
			updateGameDatabaseResult(result, playerAmount, reportID, 0);
		}

		else{
			split = line.split("P",2);
			split = split[1].split("]", 2);
			// Stores the player that won
			int playerWin = Integer.parseInt(split[0]);
			result = 'W';
			updateGameDatabaseResult(result, playerAmount, reportID, playerWin);
		}

		buff.close();
	}

	public void updateCodemonSource(String codemonSource, String cName)
	{
		try{
		stmt = Codemon.conn.createStatement();
		String sqlq;

		sqlq = "UPDATE Codemon SET Codemon.Source = '" + codemonSource + "' WHERE Codemon.Name = '" + cName + "'";
		stmt.executeUpdate(sqlq);

		}catch(Exception ef){}
	}

	public void updateGameDatabaseResult(char result, int playerNum, int reportID, int playerWon)
	{
		try{
		stmt = Codemon.conn.createStatement();
		String sqlq;

		if(result == 'T')
		{			
			sqlq = "UPDATE Games SET Games.ResultOne = 'T' WHERE Games.ReportID = " + reportID;
			stmt.executeUpdate(sqlq);
			sqlq = "UPDATE Games SET Games.ResultTwo = 'T' WHERE Games.ReportID = " + reportID;
			stmt.executeUpdate(sqlq);
			if(playerNum < 3)
				return;
			sqlq = "UPDATE Games SET Games.ResultThree = 'T' WHERE Games.ReportID = " + reportID;
			stmt.executeUpdate(sqlq);
			if(playerNum < 4)
				return;
			sqlq = "UPDATE Games SET Games.ResultFour = 'T' WHERE Games.ReportID = " + reportID;
			stmt.executeUpdate(sqlq);
		}
		else if(result == 'W')
		{
			if(playerWon == 1)
			{
				sqlq = "UPDATE Games SET Games.ResultOne = 'W' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultTwo = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				if(playerNum > 2)
				{
					sqlq = "UPDATE Games SET Games.ResultThree = 'L' WHERE Games.ReportID = " + reportID;
					stmt.executeUpdate(sqlq);
				}
				if(playerNum > 3)
				{
					sqlq = "UPDATE Games SET Games.ResultFour = 'L' WHERE Games.ReportID = " + reportID;
					stmt.executeUpdate(sqlq);
				}
			}
			else if(playerWon == 2)
			{
				sqlq = "UPDATE Games SET Games.ResultOne = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultTwo = 'W' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				if(playerNum > 2)
				{
					sqlq = "UPDATE Games SET Games.ResultThree = 'L' WHERE Games.ReportID = " + reportID;
					stmt.executeUpdate(sqlq);
				}
				if(playerNum > 3)
				{
					sqlq = "UPDATE Games SET Games.ResultFour = 'L' WHERE Games.ReportID = " + reportID;
					stmt.executeUpdate(sqlq);
				}
			}
			else if(playerWon == 3)
			{
				sqlq = "UPDATE Games SET Games.ResultOne = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultTwo = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultThree = 'W' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				if(playerNum > 3)
				{
					sqlq = "UPDATE Games SET Games.ResultFour = 'L' WHERE Games.ReportID = " + reportID;
					stmt.executeUpdate(sqlq);
				}
			}
			else if(playerWon == 4)
			{
				sqlq = "UPDATE Games SET Games.ResultOne = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultTwo = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultThree = 'L' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
				sqlq = "UPDATE Games SET Games.ResultFour = 'W' WHERE Games.ReportID = " + reportID;
				stmt.executeUpdate(sqlq);
			}


		}
		}catch(Exception ef){}
	}

	public void createDatabaseRow(int reportID)
	{
		try{
		stmt = Codemon.conn.createStatement();
		String sqlq;

		sqlq = "INSERT INTO Games (reportID) VALUES(" + reportID + ")";
		stmt.executeUpdate(sqlq);
		}catch(Exception ef){System.out.println(ef);}
	}

	public void updateGameDatabase(int reportID, int playerNum, String trainerName, String codemonNam){		
		try{
			stmt = Codemon.conn.createStatement();
			String sqlq;
			String player;
			String codemon;

			if(playerNum == 1){
				player = "trainerOne";
				codemon = "codemonOne";
			}
			else if(playerNum == 2){
				player = "trainerTwo";
				codemon = "codemonTwo";
			}
			else if(playerNum == 3){
				player = "trainerThree";
				codemon = "codemonThree";
			}
			else
			{
				player = "trainerFour";
				codemon = "codemonFour";
			}
			sqlq = "UPDATE Games SET Games." + player + " = '" + trainerName + "' WHERE Games.ReportID = " + reportID;
			//System.out.println(sqlq);
			stmt.executeUpdate(sqlq);
			sqlq = "UPDATE Games SET Games." + codemon + " = '" + codemonNam + "' WHERE Games.ReportID = " + reportID;
			//System.out.println(sqlq);
			stmt.executeUpdate(sqlq);

			sqlq = "INSERT INTO Codemon VALUES('" + trainerName + codemonNam + "' , '" + codemonNam + "' , NULL)";
			stmt.executeUpdate(sqlq);
			sqlq = "INSERT INTO Trainers VALUES('" + trainerName + "')";
			stmt.executeUpdate(sqlq);
			

			}catch(Exception ef){System.out.print(ef);}
	}

	// takes the initial information (player id, codemon name and lines) and stores it in a new class
	public int storeCodemonTrainers()throws FileNotFoundException, IOException, NullPointerException{
		
		BufferedReader buff = new BufferedReader(new FileReader(reportFileName));
		String line = " ";
		String[] split;
		String trainerID;
		String codemonName;
		int playerAmount = 0;

		while(!(line.contains("battle"))){
			while(!(line.contains(">")))
				line = buff.readLine();

			if(line.contains("said"))
			{
				split = line.split(" said, \"", 2);
				trainerID = split[0];
				codemonName = split[1];
				split = trainerID.split(" ", 2);
				trainerID = split[1];
				split = codemonName.split(",");
				codemonName = split[0];

				// setting the color of the player
				if(playerAmount == 0)
					codemonTrainerList.add(playerAmount, new codemonTrainer(trainerID, codemonName, Color.ORANGE));
				else if(playerAmount == 1)
					codemonTrainerList.add(playerAmount, new codemonTrainer(trainerID, codemonName, Color.RED));
				else if(playerAmount == 2)
					codemonTrainerList.add(playerAmount, new codemonTrainer(trainerID, codemonName, Color.BLUE));
				else
					codemonTrainerList.add(playerAmount, new codemonTrainer(trainerID, codemonName, Color.PINK));

				playerAmount++;
			}


			if((line.contains("battle")))
				break;

			line = buff.readLine();

		}

		buff.close();

		return playerAmount;
	}

	// color the initial lines of each codemon (first ones that are loaded into memory)
	public void colorFirstLine(codemonTrainer trainer, int i) throws FileNotFoundException, IOException{

		// Dosent work when trainer has the same name
		BufferedReader buff = new BufferedReader(new FileReader(reportFileName));
		String line = " ";
		String[] split;
		int counter = 0;
		int j = 0;
		i++;

		//while(!(line.contains(trainer.getID())))
		for(j = 0;j < i; j++)
		{
			line = buff.readLine();
			while(!(line.contains("you...")))
				line = buff.readLine();
		}

		while(line != null)
		{
			line = buff.readLine();
			if(line.contains("address"))
			{
				break;
			}
		}

		int memoryStart = Integer.parseInt(line.replaceAll("[\\D]", ""));
		line = buff.readLine();
		line = buff.readLine();
		split= line.split("begin");
		int numLines = Integer.parseInt(split[0].replaceAll("[\\D]", ""));

		while(counter < numLines)
		{
			visualizeList.get(memoryStart + counter).setBackground(trainer.getColor());
			counter++;
		}

		buff.close();
	}

	// color the remaining lines in the report file
	public void colorRemainingLines(JFrame visualizeFrame1)throws FileNotFoundException, IOException, NullPointerException{
		
		

        BufferedReader buff = new BufferedReader(new FileReader(reportFileName));
		line = " ";
		String[] split;
		String playerNumber;
		int playerNum;
		int lineNum = 0;

		while(!(line.contains("battle")))
			line = buff.readLine();

		line = buff.readLine();
		line = buff.readLine();

		if(line.contains("PC")){
			visualizeTimer.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){

					// Displays a message if a thread has died
					if(readBattleLine(line) == 1)
						JOptionPane.showMessageDialog(null, line, "Thread Death", JOptionPane.INFORMATION_MESSAGE);
					
					try{line = buff.readLine();}catch(IOException exe){}

					// If the battle is over stop the timer and display who has won the battle
					if(!(line.contains("PC")) && !(line.contains("THREAD")))
					{
						visualizeTimer.stop();
						while(!(line.contains(">>>")))
							try{line = buff.readLine();}catch(IOException exe){}
						JOptionPane.showMessageDialog(null, line, "Battle Completed", JOptionPane.INFORMATION_MESSAGE);
					}
	
				}
			});
			
			visualizeTimer.setDelay(TIMER_SPEED);
			visualizeTimer.start();
	}

			
	}

	// takes a turn from the report file and parses it to color that memory location in
	public int readBattleLine(String line)
	{

		String[] split;
		String[] splitTwo;
		String playerNumber;
		String threadString;
		int playerNum;
		int threadNum;
		int lineNum = 0;

		if(line.contains("THREAD")){
			
			return 1;
		}

		split = line.split("[|]", 2);
		playerNumber = split[1];
		split = playerNumber.split("[(]", 2);
		threadString = split[1];
		splitTwo = threadString.split("[)]", 2);
		threadNum = Integer.parseInt(splitTwo[0]);


		playerNum = Integer.parseInt(split[0]);
		
		if(split[1].contains("'"))
		{
			split = split[1].split("[']", 2);
			split = split[1].split("[ ]", 2);

			lineNum = Integer.parseInt(split[0].replaceAll("[\\D]", ""));

			// Color the line depending on which threads turn it is
			if(threadNum == 0)
				visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor());
			else if(threadNum == 1)
				visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor().darker());
			else if(threadNum == 2)
				visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor().brighter());
			else if(threadNum == 3)
				visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor().darker().darker().darker());

			while(split[1].matches(".*[0-9]+.*"))
			{	
				split = split[1].split("[']", 2);
				split = split[1].split("[ ]", 2);
				lineNum = Integer.parseInt(split[0].replaceAll("[\\D]", ""));
				if(threadNum == 0)
					visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor());
				else if(threadNum == 1)
					visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor().darker());
				else if(threadNum == 2)
					visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor().brighter());
				else if(threadNum == 3)
					visualizeList.get(lineNum).setBackground(codemonTrainerList.get((playerNum - 1)).getColor().darker().darker().darker());
			}
		}

		return 0;
		
		
	}

	// Deletes the selected report from the JList and deletes it in local storage
	class reportsDeleteAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String selectedFile = reportsJList.getSelectedValue();
			File fileToDelete = new File(currentReportsDirectory.toString().concat("/").concat(selectedFile).concat(".log"));
			fileToDelete.delete();
			updateReportList();

		}
	}

	// Gets all reports
	class fetchAllAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			fetchAllReports();
		}
	}

	// Takes the codemon in drop down 1 and attempts to submit it to the  server. Shows errors if anything goes wrong or their is no codemon selected. Also creates a new empty file with the reportID
	class testButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
			String newElement = String.valueOf(Codemon.runTestCodemon(Codemon.currentCodemonDirectory.getName().concat("/").concat(codemonOneDropDown.getSelectedItem().toString().concat(".codemon")), turnLimit));


			if(newElement.equals("0"))
				JOptionPane.showMessageDialog(null, "Codemon is invalid or there were networking problems", "Error", JOptionPane.INFORMATION_MESSAGE);

			else{
				reportsList.addElement("<HTML><font color=red>" +newElement+"</font></HTML>");

				try{PrintWriter reportWriter = new PrintWriter(currentReportsDirectory.toString().concat("/").concat(newElement.concat(".log")), "UTF-8");
					reportWriter.close();
				}catch(FileNotFoundException ex){} catch(UnsupportedEncodingException exe){}
			
				updateReportList();
				}
			}catch(Exception exo){JOptionPane.showMessageDialog(null, "No Codemon Selected", "Error", JOptionPane.INFORMATION_MESSAGE);}
		}
	}

	// Takes the codemon in drop down 1 and 2 and attempts to submit it to the  server. Shows errors if anything goes wrong or their is no codemon selected. Also creates a new empty file with the reportID
	class selfButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
			String newElement = String.valueOf(Codemon.runSelfCodemon(Codemon.currentCodemonDirectory.getName().concat("/").concat(codemonOneDropDown.getSelectedItem().toString().concat(".codemon")), Codemon.currentCodemonDirectory.getName().concat("/").concat(codemonTwoDropDown.getSelectedItem().toString().concat(".codemon")), turnLimit));

			if(newElement.equals("0"))
				JOptionPane.showMessageDialog(null, "Codemon is invalid or there were networking problems", "Error", JOptionPane.INFORMATION_MESSAGE);

			else{	
				reportsList.addElement("<HTML><font color=red>" +newElement+"</font></HTML>");

				try{PrintWriter reportWriter = new PrintWriter(currentReportsDirectory.toString().concat("/").concat(newElement.concat(".log")), "UTF-8");
					reportWriter.close();
				}catch(FileNotFoundException ex){} catch(UnsupportedEncodingException exe){}
			
				updateReportList();
			}
			}catch(Exception exo){JOptionPane.showMessageDialog(null, "No Codemon Selected", "Error", JOptionPane.INFORMATION_MESSAGE);}
		}
	}

	// Takes the codemon in drop down 1 and attempts to submit it to the  server for pvp mode. Shows errors if anything goes wrong or their is no codemon selected. Also creates a new empty file with the reportID
	class pvpButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
			String newElement = String.valueOf(Codemon.runPVPCodemon(Codemon.currentCodemonDirectory.getName().concat("/").concat(codemonOneDropDown.getSelectedItem().toString().concat(".codemon")), pvpMode));

			if(newElement.equals("0"))
				JOptionPane.showMessageDialog(null, "Codemon is invalid or there were networking problems", "Error", JOptionPane.INFORMATION_MESSAGE);

			else{
				reportsList.addElement("<HTML><font color=red>" +newElement+"</font></HTML>");
				try{PrintWriter reportWriter = new PrintWriter(currentReportsDirectory.toString().concat("/").concat(newElement.concat(".log")), "UTF-8");
					reportWriter.close();
				}catch(FileNotFoundException ex){} catch(UnsupportedEncodingException exe){}
			
				updateReportList();
			}
			}catch(Exception exo){JOptionPane.showMessageDialog(null, "No Codemon Selected", "Error", JOptionPane.INFORMATION_MESSAGE);}
		}
	}

	// Shows the codemon if it was double clicked in quick view
	class codemonListClicked implements MouseListener{
		public void mouseClicked(MouseEvent e){

			JList listNum = (JList)e.getSource();
    		if (e.getClickCount() == 2) {
     			index = listNum.locationToIndex(e.getPoint());
     			showQuickViewCodemon();
			}
		}
		 public void mousePressed(MouseEvent e) {}
     	 public void mouseReleased(MouseEvent e) {}
     	 public void mouseEntered(MouseEvent e) {}
     	 public void mouseExited(MouseEvent e) {}
	}

	// Updates what is shows in the report quick view area
	public void updateReportList()
	{

		File []reportFiles = new File(currentReportsDirectory.toString()).listFiles();
		reportsList = (DefaultListModel<String>) reportsJList.getModel();
		reportsList.removeAllElements();
		reportsFileList.clear();


		for (File file : reportFiles) {
   			 if (file.isFile() && file.getName().toLowerCase().endsWith(".log")) {
        		reportsFileList.add(file.getName().replaceFirst("[.][^.]+$", ""));
   			 }
		}

		for(String str : reportsFileList){
			if(isEmptyFile(currentReportsDirectory.toString().concat("/").concat(str).concat(".log")))
				reportsList.addElement("<HTML><font color=red>" +str+"</font></HTML>");
			else{
				reportsList.addElement(str);}
		}

		reportsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
	}

	public void updateCodemonList()
	{
		File []codemonFiles = new File(Codemon.currentCodemonDirectory.toString()).listFiles();
		codemonList = (DefaultListModel<String>) codemonJList.getModel();
		codemonList.removeAllElements();
		codemonFileList.clear();
		codemonOneDropDown.removeAllItems();
		codemonTwoDropDown.removeAllItems();

		for(File file : codemonFiles){
			if(file.isFile() && file.getName().toLowerCase().endsWith(".codemon")) {
				codemonFileList.add(file.getName().replaceFirst("[.][^.]+$", ""));
			}
		}

		for(String str : codemonFileList){
			codemonList.addElement(str);
			codemonOneDropDown.addItem(str);
			codemonTwoDropDown.addItem(str);
		}

		codemonJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}
	// Shows a report if it was double clicked
	class reportListClicked implements MouseListener{
		public void mouseClicked(MouseEvent e){
			JList listNum = (JList)e.getSource();
    		if (e.getClickCount() == 2) {
     			index = listNum.locationToIndex(e.getPoint());
     			showQuickViewReport();
     			updateReportList();
			}
		}
		 public void mousePressed(MouseEvent e) {}
     	 public void mouseReleased(MouseEvent e) {}
     	 public void mouseEntered(MouseEvent e) {}
     	 public void mouseExited(MouseEvent e) {}
	}
	
	// Attemps to fetch all reports that havent been received from the server yet, if the report cannot be grabbed a dialog box will appear
	public void fetchAllReports(){

		File []reportFiles = new File(currentReportsDirectory.toString()).listFiles();
		for(File file : reportFiles)
		{
			if(isEmptyFile(currentReportsDirectory.toString().concat("/").concat(file.getName())) && file.toString().endsWith(".log"))
			{
			
				String fileSent = currentReportsDirectory.getName().concat("/").concat(file.getName());
				int reportIDSent = Integer.parseInt(file.getName().replaceFirst("[.][^.]+$", ""));
				int reportSuccess = Codemon.getReport(reportIDSent, fileSent);

				if(reportSuccess == 0){
					JOptionPane.showMessageDialog(null, "Report " + reportIDSent + "  could not be grabbed", "Error", JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					try{addMatchToDatabase(reportIDSent);}catch(Exception efx){}
				}

			}
		}
		updateReportList();
	}

	// Opens a new window that shows the currently selected report
	public void showQuickViewReport(){
		try{

			reportFileName = reportsFileList.get(index).concat(".log");
			reportFileName = currentReportsDirectory.toString().concat("/").concat(reportFileName);
			File tempFile = new File(reportFileName);

			if(isEmptyFile(reportFileName))
			{
				String fileSent = currentReportsDirectory.getName().concat("/").concat(tempFile.getName());
				int reportIDSent = Integer.parseInt(tempFile.getName().replaceFirst("[.][^.]+$", ""));
				int reportSuccess = Codemon.getReport(reportIDSent, fileSent);

				if(reportSuccess == 0)
				{
					JOptionPane.showMessageDialog(null, "Report could not be grabbed", "Error", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else
				{
					try{addMatchToDatabase(reportIDSent);}catch(Exception efx){}
				}
			}

			try{
				readReportFile();}
				catch(FileNotFoundException ex){
					//JOptionPane.showMessageDialog(null, ".log File could not be Opened", "Error", JOptionPane.INFORMATION_MESSAGE);
					try{PrintWriter reportWriter = new PrintWriter(reportFileName, "UTF-8"); 
					reportWriter.close();
				} catch(FileNotFoundException e){} catch(UnsupportedEncodingException e){}

				} catch(IOException e){}
			}
			catch(NullPointerException e){
				JOptionPane.showMessageDialog(null, "Improper index Value", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
	}

	// Opens a new window that shows the currently selected .cm file
	public void showQuickViewCodemon(){
		try{
			codemonFileName = codemonFileList.get(index).concat(".cm");
			codemonFileName = Codemon.currentSaveDirectory.toString().concat("/").concat(codemonFileName);
		
			try{
				readCodemonFile();}
			catch(FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null, ".cm File could not be Opened", "Error", JOptionPane.INFORMATION_MESSAGE);
				} catch(IOException exe) {}
		}
		catch(NullPointerException e){
			JOptionPane.showMessageDialog(null, "Improper index Value", "Error", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	// Puts the contents of the report file into a popup window
	public void readReportFile() throws FileNotFoundException, IOException{

		BufferedReader buff = new BufferedReader(new FileReader(reportFileName));

		try {
	   	StringBuilder sb = new StringBuilder();
	    	String line = buff.readLine();

	    	while (line != null) 
	    	{
	        	sb.append(line);
	        	sb.append(System.lineSeparator());
	        	line = buff.readLine();
	    	}

	    	JFrame quickReportFrame = new JFrame();
	    	quickReportFrame.setSize(400,500);
	    	int indexx = reportFileName.lastIndexOf("/");
			String fileName = reportFileName.substring(indexx + 1);
	    	quickReportFrame.setTitle(fileName);
	    	quickReportFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    	quickReportFrame.setLayout(new BorderLayout());
	    	JTextArea quickViewText = new JTextArea();
	    	//try{quickViewText.read(new InputStreamReader(getClass().getResourceAsStream(reportFileName)), null);}
    		//catch(IOException e){}
	    	quickViewText.setText(sb.toString());
	    	quickViewText.setEditable(false);
	    	JScrollPane quickViewScroll = new JScrollPane(quickViewText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    	quickReportFrame.add(quickViewScroll, BorderLayout.CENTER);
	    	quickReportFrame.setVisible(true);

		} finally {
    		buff.close();
		}
	}

	public void readCodemonFile() throws FileNotFoundException, IOException{
	BufferedReader buff = new BufferedReader(new FileReader(codemonFileName));
		try {
	    	StringBuilder sb = new StringBuilder();
	    	String line = buff.readLine();

	    	while (line != null) 
	    	{
	       	sb.append(line);
	       	sb.append(System.lineSeparator());
	     	line = buff.readLine();
	    	}

	    	JFrame quickCodemonFrame = new JFrame();
	    	quickCodemonFrame.setSize(600,600);
	    	int indexx = codemonFileName.lastIndexOf("/");
			String fileName = codemonFileName.substring(indexx + 1);
	    	quickCodemonFrame.setTitle(fileName);
	    	quickCodemonFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    	quickCodemonFrame.setLayout(new BorderLayout());
	    	JTextArea quickViewText = new JTextArea();
	    //	try{quickViewText.read(new InputStreamReader(getClass().getResourceAsStream(codemonFileName)), null);}
	    //	catch(IOException e){}
	    	quickViewText.setText(sb.toString());
	    	quickViewText.setEditable(false);
	    	JScrollPane quickViewScroll = new JScrollPane(quickViewText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    	quickViewScroll.setPreferredSize(new Dimension(480,700));
	    	quickCodemonFrame.add(quickViewScroll, BorderLayout.CENTER);
	    	quickCodemonFrame.setVisible(true);

			} finally {
	    		buff.close();
			}

	}

	// Checks to see if a file is empty
	public boolean isEmptyFile(String filePath){
		File fName = new File(filePath);
		boolean empty;
		if(fName.length() == 0)
			empty = true;
		else
			empty = false;
		return empty;
	}
	
	// Changes source (codemon) directory
	class configSourceAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser configSource = new JFileChooser(Codemon.currentSaveDirectory);
			configSource.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			configSource.setDialogTitle("Source Directory for Input Files");
			int isValidFile = configSource.showDialog(fightClubFrame, "Change Directory");

			if(isValidFile == JFileChooser.APPROVE_OPTION && configSource.getSelectedFile().isDirectory())
				Codemon.currentSaveDirectory = configSource.getSelectedFile();

			
		}
	}
	
	// Changes codemon directory
	class configCodemonAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser configSource = new JFileChooser(Codemon.currentCodemonDirectory);
			configSource.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			configSource.setDialogTitle("Source Directory for Input Files");
			int isValidFile = configSource.showDialog(fightClubFrame, "Change Directory");

			if(isValidFile == JFileChooser.APPROVE_OPTION && configSource.getSelectedFile().isDirectory())
			{
				Codemon.currentCodemonDirectory = configSource.getSelectedFile();
				updateCodemonList();
			}

			

		}
	}

	// Changes reports directory
	class reportsDirAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser reportsDir = new JFileChooser(currentReportsDirectory);
			reportsDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			reportsDir.setDialogTitle("Directory for Report Files");
			int isValidFile = reportsDir.showDialog(fightClubFrame, "Change Directory");

			if(isValidFile == JFileChooser.APPROVE_OPTION && reportsDir.getSelectedFile().isDirectory())
			{
				currentReportsDirectory = reportsDir.getSelectedFile();
				updateReportList();
			}
			
		}
	}

	// Opens a new dialog window where the user can type in a turn limit
	class iterationButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			iterationFrame.setSize(400,75);
			iterationFrame.setTitle("Iteration Limit");
			iterationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			iterationFrame.setLayout(new BorderLayout());

			JLabel iterationLabel = new JLabel("iteration", SwingConstants.CENTER);
			iterationLabel.setText("Please provide the turn limit for TEST and SELF games:");
			iterationFrame.add(iterationLabel, BorderLayout.NORTH);
			iterationFrame.add(turnLimitText, BorderLayout.CENTER);

			JButton turnOKButton = new JButton("OK");
			turnOKButton.addActionListener(new turnOKButtonAction());
			iterationFrame.add(turnOKButton, BorderLayout.EAST);
			iterationFrame.setVisible(true);

		}
	}

	class turnOKButtonAction implements ActionListener{
  		public void actionPerformed(ActionEvent e) {
    		String turnLimitString = turnLimitText.getText();

    		try{
    		turnLimit = Integer.parseInt(turnLimitString);

    			if(turnLimit > 10000 || turnLimit < 0){
    				JOptionPane.showMessageDialog(null, "Turn Limit must be less than 10001 and equal to or greater than 0", "Error", JOptionPane.INFORMATION_MESSAGE);
    				turnLimit = 0;
    			}

    			else
    				iterationFrame.dispose();
    		}
    		catch (NumberFormatException f){
    			JOptionPane.showMessageDialog(null, "Turn Limit must be an integer", "Error", JOptionPane.INFORMATION_MESSAGE);
    		}
    		
  
  		}
	}

}