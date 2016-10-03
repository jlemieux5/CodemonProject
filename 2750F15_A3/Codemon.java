import java.io.PrintWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;	
import javax.swing.text.Document.*;
import javax.swing.event.*;
import java.sql.*;



public class Codemon
{	
	//Load the c library file 
	static {System.loadLibrary("codemon");}

	// Variables used to hold the current directories for source and codeomn files
	public static File currentDirectory = new File(new File(".").getAbsolutePath());
	public static File currentSaveDirectory = new File(currentDirectory, "/Source");
	public static File currentCodemonDirectory = new File(currentDirectory, "/Codemon");

	// SQL login information
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://eon.socs.uoguelph.ca/lemieuxj";
	public static final String USER = "lemieuxj";
	public static final String PASS = "A42750lemieuxj";
	public static Connection conn = null;
	// Start program by running the main Menu
	public static void main(String args[])
	{
		try{

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

		}catch(Exception e){
			System.out.println(e);}

		mainMenu firstMenu = new mainMenu();
		firstMenu.setVisible(true);

		/*try{
		conn.close();
		}catch(Exception e){System.out.println("Couldnt close");}*/
	}

	public File getCurrentDirectory(){
		return currentDirectory;
	}

	public File getSaveDirectory(){
		return currentSaveDirectory;
	}

	public File getCodemonDirectory(){
		return currentCodemonDirectory;
	}

	// JNI functions
	native static int parseCodemon(String fileName, String pathName);

	native static int runTestCodemon(String fileName, int turnLimit);

	native static int runSelfCodemon(String fileName, String fileNameTwo, int turnLimit);

	native static int runPVPCodemon(String fileName, int pvpMode);

	native static int getReport(int reportID, String filePath);

}

	