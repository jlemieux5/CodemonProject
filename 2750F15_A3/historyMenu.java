import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class historyMenu extends JFrame{

	private JFrame historyFrame = new JFrame();
	private Statement stmt = null;
	private JComboBox<String> trainerDropDown = new JComboBox<>();
	private JComboBox<String> codemonDropDown = new JComboBox<>();
	private JTable gameTable;
	private JTable trainerTable;
	private JTable codemonTable;
	private String trainerSelected;
	private String codemonSelected;

	public historyMenu(){

		historyFrame.setSize(600,600);
		historyFrame.setTitle("History");
		historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		historyFrame.setLayout(new GridLayout(3,1));
		//Border thickBorder = new LineBorder(Color.WHITE, 3);
		JPanel gameHistoryPanel = new JPanel();
		gameHistoryPanel.setLayout(new FlowLayout());
		JButton gameHistoryButton = new JButton("Game History");
		gameHistoryButton.addActionListener(new gameButtonAction());
		gameHistoryButton.setPreferredSize(new Dimension(200, 100));
		gameHistoryPanel.add(gameHistoryButton);
		//gameHistoryButton.setBorder(thickBorder);
		JPanel trainerHistoryPanel = new JPanel();
		trainerHistoryPanel.setLayout(new FlowLayout());
		JButton trainerHistoryButton = new JButton("Trainer History");
		trainerHistoryButton.addActionListener(new trainerButtonAction());
		trainerHistoryButton.setPreferredSize(new Dimension(200, 100));
		trainerDropDown.setPreferredSize(new Dimension(300,50));
		trainerHistoryPanel.add(trainerHistoryButton);
		trainerHistoryPanel.add(trainerDropDown);
		//trainerHistoryButton.setBorder(thickBorder);
		JPanel codemonHistoryPanel = new JPanel();
		codemonHistoryPanel.setLayout(new FlowLayout());
		JButton codemonHistoryButton = new JButton("Codemon History");
		JButton codemonSourceButton = new JButton("Get Codemon Source");
		codemonSourceButton.addActionListener(new codemonSourceAction());
		codemonHistoryButton.addActionListener(new codemonButtonAction());
		codemonHistoryButton.setPreferredSize(new Dimension(200, 100));
		codemonDropDown.setPreferredSize(new Dimension(300,50));
		codemonHistoryPanel.add(codemonHistoryButton);
		codemonHistoryPanel.add(codemonDropDown);
		codemonHistoryPanel.add(codemonSourceButton);
		//codemonHistoryButton.setBorder(thickBorder);

		addTrainersToDropDown();
		addCodemonToDropDown();

		historyFrame.add(gameHistoryPanel);
		historyFrame.add(trainerHistoryPanel);
		historyFrame.add(codemonHistoryPanel);
		historyFrame.setVisible(true);
	}

	class codemonSourceAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			codemonSelected = codemonDropDown.getSelectedItem().toString();
			JFrame sourceFrame = new JFrame();
			sourceFrame.setSize(400,400);
			sourceFrame.setTitle(codemonSelected + " Source");
			sourceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			sourceFrame.setLayout(new BorderLayout());
			JTextArea sourceInfo = new JTextArea();
			
			try{
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT Source FROM Codemon WHERE Name = '" + codemonSelected + "'";
			rs = stmt.executeQuery(sqlq);
			rs.next();
			String sourceText = rs.getString("Source");
			sourceInfo.setText(sourceText);
			}catch(Exception ef){System.out.println(ef);}


			sourceFrame.add(sourceInfo, BorderLayout.CENTER);
			sourceFrame.setVisible(true);
			
		}
	}

	public void addTrainersToDropDown(){
		try{
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Trainers";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{
				String trainerName= rs.getString("Name");
				trainerDropDown.addItem(trainerName);
			}
			}catch(Exception ef){System.out.println(ef);}
	}

	public void addCodemonToDropDown(){
		try{
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Codemon";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{
				String codemonName= rs.getString("Name");
				codemonDropDown.addItem(codemonName);
			}
			}catch(Exception ef){System.out.println(ef);}
	}

	class gameButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JFrame gameHistoryFrame = new JFrame();
			gameHistoryFrame.setLayout(new BorderLayout());
			gameHistoryFrame.setSize(1200,600);
			gameHistoryFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			gameHistoryFrame.setTitle("History of Games");
			gameHistoryFrame.setVisible(true);


			String[] columnNames = {"Report ID", "Trainer 1", "Codemon 1", "Result", "Trainer 2", "Codemon 2", "Result", "Trainer 3", "Codemon 3", "Result", "Trainer 4", "Codemon 4", "Result"};
			DefaultTableModel model = new DefaultTableModel(columnNames, 100);
			gameTable = new JTable();
			gameTable.setGridColor(Color.BLACK);
			gameTable.setModel(model);
			

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			//centerRenderer.setPreferredSize(new Dimension(5,5));
			gameTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
			//gameTable.getColumnModel().getColumn(3).setPreferredWidth(15);
			gameTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
			JScrollPane gameTableScroll = new JScrollPane(gameTable);
			gameHistoryFrame.add(gameTableScroll, BorderLayout.CENTER);

			insertGameData();

		}
	}

	class trainerButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			trainerSelected = trainerDropDown.getSelectedItem().toString();
			JFrame gameHistoryFrame = new JFrame();
			gameHistoryFrame.setLayout(new BorderLayout());
			gameHistoryFrame.setSize(1200,600);
			gameHistoryFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			gameHistoryFrame.setTitle("History of " + trainerSelected);
			gameHistoryFrame.setVisible(true);


			String[] columnNames = {"Report ID", "Trainer 1", "Codemon 1", "Result", "Trainer 2", "Codemon 2", "Result", "Trainer 3", "Codemon 3", "Result", "Trainer 4", "Codemon 4", "Result"};
			DefaultTableModel model = new DefaultTableModel(columnNames, 100);
			gameTable = new JTable();
			gameTable.setGridColor(Color.BLACK);
			gameTable.setModel(model);
			

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			gameTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
			JScrollPane gameTableScroll = new JScrollPane(gameTable);
			gameHistoryFrame.add(gameTableScroll, BorderLayout.CENTER);

			insertGameTrainerData();
		}
	}
	public void insertGameTrainerData(){
		try{
			int y = 0;
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Games WHERE trainerOne = '" + trainerSelected + "' OR trainerTwo = '" + trainerSelected + "'";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{
				String tempString = rs.getString("trainerOne");
				gameTable.setValueAt(tempString, y, 1);
				tempString = rs.getString("codemonOne");
				gameTable.setValueAt(tempString, y, 2);
				tempString = rs.getString("resultOne");
				gameTable.setValueAt(tempString, y, 3);
				tempString = rs.getString("trainerTwo");
				gameTable.setValueAt(tempString, y, 4);
				tempString = rs.getString("codemonTwo");
				gameTable.setValueAt(tempString, y, 5);
				tempString = rs.getString("resultTwo");
				gameTable.setValueAt(tempString, y, 6);
				/*tempString = rs.getString("trainerThree");
				gameTable.setValueAt(tempString, y, 7);
				tempString = rs.getString("codemonThree");
				gameTable.setValueAt(tempString, y, 8);
				tempString = rs.getString("resultThree");
				gameTable.setValueAt(tempString, y, 9);
				tempString = rs.getString("trainerFour");
				gameTable.setValueAt(tempString, y, 10);
				tempString = rs.getString("codemonFour");
				gameTable.setValueAt(tempString, y, 11);
				tempString = rs.getString("resultFour");
				gameTable.setValueAt(tempString, y, 12);*/
				y++;
				
			}
			}catch(Exception ef){System.out.println(ef);}

	}

	class codemonButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			codemonSelected = codemonDropDown.getSelectedItem().toString();
			JFrame gameHistoryFrame = new JFrame();
			gameHistoryFrame.setLayout(new BorderLayout());
			gameHistoryFrame.setSize(1200,600);
			gameHistoryFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			gameHistoryFrame.setTitle("History of " + codemonSelected);
			gameHistoryFrame.setVisible(true);


			String[] columnNames = {"Report ID", "Trainer 1", "Codemon 1", "Result", "Trainer 2", "Codemon 2", "Result", "Trainer 3", "Codemon 3", "Result", "Trainer 4", "Codemon 4", "Result"};
			DefaultTableModel model = new DefaultTableModel(columnNames, 100);
			gameTable = new JTable();
			gameTable.setGridColor(Color.BLACK);
			gameTable.setModel(model);
			

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			gameTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
			gameTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
			JScrollPane gameTableScroll = new JScrollPane(gameTable);
			gameHistoryFrame.add(gameTableScroll, BorderLayout.CENTER);

			insertGameCodemonData();
		}
	}

	public void insertGameCodemonData(){
			try{
			int y = 0;
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Games WHERE codemonOne = '" + codemonSelected + "' OR codemonTwo = '" + codemonSelected + "'";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{
				String tempString = rs.getString("trainerOne");
				gameTable.setValueAt(tempString, y, 1);
				tempString = rs.getString("codemonOne");
				gameTable.setValueAt(tempString, y, 2);
				tempString = rs.getString("resultOne");
				gameTable.setValueAt(tempString, y, 3);
				tempString = rs.getString("trainerTwo");
				gameTable.setValueAt(tempString, y, 4);
				tempString = rs.getString("codemonTwo");
				gameTable.setValueAt(tempString, y, 5);
				tempString = rs.getString("resultTwo");
				gameTable.setValueAt(tempString, y, 6);
				/*tempString = rs.getString("trainerThree");
				gameTable.setValueAt(tempString, y, 7);
				tempString = rs.getString("codemonThree");
				gameTable.setValueAt(tempString, y, 8);
				tempString = rs.getString("resultThree");
				gameTable.setValueAt(tempString, y, 9);
				tempString = rs.getString("trainerFour");
				gameTable.setValueAt(tempString, y, 10);
				tempString = rs.getString("codemonFour");
				gameTable.setValueAt(tempString, y, 11);
				tempString = rs.getString("resultFour");
				gameTable.setValueAt(tempString, y, 12);*/
				y++;
				
			}
			}catch(Exception ef){System.out.println(ef);}
	}

	public void insertCodemonNames(){
		try{
			int y = 0;
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Codemon";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{				
				String tempString = rs.getString("Name");
				if(!tempString.equals(codemonSelected))
				{
					codemonTable.setValueAt(tempString, y, 0);
					y++;
				}
			}
			}catch(Exception ef){}
	}

	public void insertTrainerNames(){
		try{
			int y = 0;
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Trainers";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{				
				String tempString = rs.getString("Name");
				if(!tempString.equals(trainerSelected))
				{
					trainerTable.setValueAt(tempString, y, 0);
					y++;
				}
			}
			}catch(Exception ef){}
	}

	public void insertGameData(){
		try{
			int y = 0;
			stmt = Codemon.conn.createStatement();
			ResultSet rs;
			String sqlq;
			sqlq = "SELECT * FROM Games";

			rs = stmt.executeQuery(sqlq);

			while(rs.next())
			{
				String tempString = rs.getString("trainerOne");
				gameTable.setValueAt(tempString, y, 1);
				tempString = rs.getString("codemonOne");
				gameTable.setValueAt(tempString, y, 2);
				tempString = rs.getString("resultOne");
				gameTable.setValueAt(tempString, y, 3);
				tempString = rs.getString("trainerTwo");
				gameTable.setValueAt(tempString, y, 4);
				tempString = rs.getString("codemonTwo");
				gameTable.setValueAt(tempString, y, 5);
				tempString = rs.getString("resultTwo");
				gameTable.setValueAt(tempString, y, 6);
				/*tempString = rs.getString("trainerThree");
				gameTable.setValueAt(tempString, y, 7);
				tempString = rs.getString("codemonThree");
				gameTable.setValueAt(tempString, y, 8);
				tempString = rs.getString("resultThree");
				gameTable.setValueAt(tempString, y, 9);
				tempString = rs.getString("trainerFour");
				gameTable.setValueAt(tempString, y, 10);
				tempString = rs.getString("codemonFour");
				gameTable.setValueAt(tempString, y, 11);
				tempString = rs.getString("resultFour");
				gameTable.setValueAt(tempString, y, 12);*/
				y++;
				
			}
			}catch(Exception ef){System.out.println(ef);}

	}

	

	

}