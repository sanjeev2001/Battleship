
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BattleshipGUIU {

    private static JFrame frame = new JFrame("Battleship GUI");
    private static JMenuItem open = new JMenuItem("Open...");
    private static JPanel mainPanel = new JPanel();
    private static JLabel[][] playerBoard = new JLabel[10][10], cpuBoard = new JLabel[10][10], dummyBoard = new JLabel[10][10];
    private static Font boardFont;
    private static Font font;
    private static JLabel playerMsg = new JLabel();
    private static JLabel cpuMsg = new JLabel();
    private static boolean playerMsgBool = true;
    private static boolean cpuMsgBool = true;
    private static int cpuHP = 17, playerHP = 17;
    private static boolean clicked = false;
    private static double cpuTurnCount = 0.0;
    private static String cpuGuesses = "";
    private static String playerGuesses = "";
    private static JLabel loadPlayer = new JLabel("<html><br>Please open the<br>player text file</html>");
    private static JLabel loadCPU = new JLabel("<html><br>Please open the<br>CPU text file</html>");
    private static JPanel loadFilePanel = new JPanel();
    private static JFrame loadFileFrame = new JFrame("LOAD FILES");

    public static Font fontSize(int size) {
        // Allows us to change the font size of our custom font for various labels
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("font.otf"))).deriveFont(Font.PLAIN, size);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    public static class OpenFile implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser selectFile = new JFileChooser();
            // Finds the directory with the source code
            File directory = new File(System.getProperty("user.dir"));
            // Sets the file explorer to open the directory with the source code
            selectFile.setCurrentDirectory(directory);
            selectFile.setDialogTitle("Open");
            // Filters out files to only include txt extensions
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
            selectFile.addChoosableFileFilter(filter);
            selectFile.setAcceptAllFileFilterUsed(true);

            if (selectFile.showOpenDialog(open) == JFileChooser.APPROVE_OPTION) {
                // Takes in the data from each of the text files
                File boardtxt = selectFile.getSelectedFile();
                String playerType = boardtxt.getName();

                if (boardtxt.getName().equals("PLAYER.txt")) {
                    loadPlayer.setVisible(false);
                    try {
                        // Loads the player.txt into playerBoard
                        loadFile(playerBoard, playerType);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    loadCPU.setVisible(false);
                    try {
                        // Loads the cpu.txt into cpuBoard
                        loadFile(cpuBoard, playerType);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public static void loadFile(JLabel[][] board, String fileName) throws IOException {
        BufferedReader inputStream = null;
        int row = 0, col = 0;
        String noSpace[] = new String[10];

        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            // Reads the first line of data in the file
            String lineRead = inputStream.readLine();

            while (lineRead != null) {
                noSpace = lineRead.split(" ");
                for (int i = 0; i < noSpace.length; i++) {
                    // Takes each line and takes out the spaces to manage the data easily
                    col = i;
                    board[row][col] = new JLabel();
                    board[row][col].setText(noSpace[i]);
                }
                row++;
                // Reads the next line of data in the file
                lineRead = inputStream.readLine();
            }
        } catch (FileNotFoundException exception) {
            System.out.println("File could not be found!");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        displayBoard(fileName, "");
    }

    public static void displayBoard(String playerType, String msg) {
        mainPanel.removeAll();
        displayMsg(playerType.replaceAll(".txt", ""), msg);
        // Depending on visibility of playerMsg and cpuMsg, one or both boards are displayed
        if (playerMsg.isVisible() == false && cpuMsg.isVisible() == false) {
            boardLoop(playerBoard, 77);
            boardLoop(dummyBoard, 551);
        } else if (playerType.replaceAll(".txt", "").equals("CPU")) {
            boardLoop(dummyBoard, 551);
        } else if (playerType.replaceAll(".txt", "").equals("PLAYER")) {
            boardLoop(playerBoard, 77);
        }
        JLabel bg = new JLabel(new ImageIcon("bg.png"));
        bg.setBounds(0, 0, 1000, 750);
        mainPanel.add(bg);
    }

    public static void boardLoop(JLabel[][] board, int offset) {
        boardFont = fontSize(36);
        // each JLabel is placed based off the offset given using an arithmetic sequence
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j].setFont(boardFont);
                board[i][j].setForeground(Color.WHITE);
                board[i][j].setBounds((40 * j) + offset, 168 + (40 * i), 48, 48);
                mainPanel.add(board[i][j]);
                frame.setContentPane(mainPanel);
                frame.pack();
            }
        }
    }

    public static void displayMsg(String playerType, String msg) {
        // When the user has loaded both files the second window will close
        if (loadPlayer.isVisible() == false && loadCPU.isVisible() == false && loadFileFrame.isVisible() == true) {
            loadFileFrame.dispose();
            clicked = false;
        }

        // This allows the user to select whichever file they want to first without getting rid of the other board
        if (playerType.equals("PLAYER") && playerMsgBool == true) {
            mainPanel.remove(playerMsg);
            mainPanel.add(cpuMsg);
            playerMsgBool = false;
        } else if (playerType.equals("CPU") && cpuMsgBool == true) {
            mainPanel.remove(cpuMsg);
            mainPanel.add(playerMsg);
            cpuMsgBool = false;
        }

        // Depending on who's turn it is, the program will display an attack message corresponding to who attacked
        if (playerMsgBool == false && cpuMsgBool == false) {
            boardLoop(playerBoard, 77);
            boardLoop(dummyBoard, 551);
            if (playerType.equals("PLAYER")) {
                playerMsg.setText(msg);
                mainPanel.add(playerMsg);
                mainPanel.add(cpuMsg);
            } else {
                cpuMsg.setText(msg);
                mainPanel.add(cpuMsg);
                mainPanel.add(playerMsg);
            }
        }
    }

    public static class MouseMovement implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            // gets mouse location
            java.awt.Point mouseLocation = e.getPoint();
            int row = 0, col = 0;
            // checks if mouse is within cpu board, if left mouse btn has been clicked and if the player has already clicked before
            if (mouseLocation.getX() > 543 && mouseLocation.getX() < 938 && mouseLocation.getY() > 220 && mouseLocation.getY() < 620 && SwingUtilities.isLeftMouseButton(e) == true && clicked == false) {
                // converts the x and y location of the mouse to a corresponding row and col based on pixel location
                if (((mouseLocation.getX() - 543) % 40) < 37) {
                    col = (int) ((mouseLocation.getX() - 543) / 40);
                }

                if (((mouseLocation.getY() - 220) % 40) < 37) {
                    row = (int) ((mouseLocation.getY() - 220) / 40);
                }
                String coordinate = Character.toString((char) (row + 65)) + Integer.toString(col);
                playerGuesses += coordinate;
                while (true) {
                    // Checks if the cpu has guessed the location already. If it has then it guesses again, else it continues
                    if (cpuTurnCount > 0 && ((playerGuesses.substring(0, playerGuesses.length() - 2)).contains(coordinate)) == true) {
                        JOptionPane.showMessageDialog(null, "You already clicked that coordinate. Try again...");
                        break;
                    } else {
                        cpuHP = hitOrMiss(row, col, cpuBoard, cpuHP, "CPU");
                        if (checkWin(cpuHP) == true) {
                            JOptionPane.showMessageDialog(null, "Player has won");
                            frame.dispose();

                        }
                        clicked = true;
                        cpuTurn(cpuTurnCount);
                        cpuTurnCount++;
                        break;
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    public static int hitOrMiss(int row, int col, JLabel[][] board, int hp, String playerType) {
        String coordinate = Character.toString((char) (row + 65)) + Integer.toString(col);
        // If the attacked coordinate contains an asterisk, the program will output that the player or cpu missed
        if (board[row][col].getText().equals("*")) {
            if (playerType.equals("CPU")) {
                dummyBoard[row][col].setText("M");
                displayBoard("PLAYER.txt", ("You attacked " + coordinate + " and missed!"));
            } else {
                playerBoard[row][col].setText("M");
                displayBoard("CPU.txt", ("The CPU attacked " + coordinate + " and missed!"));
            }
        } else {
            // If the attacked coordinate doesn't hold an * then the attacker hit
            hp--;
            if (playerType.equals("CPU")) {
                dummyBoard[row][col].setText("H");
                displayBoard("PLAYER.txt", ("You attacked " + coordinate + " and hit!"));
            } else {
                playerBoard[row][col].setText("H");
                displayBoard("CPU.txt", ("The CPU attacked " + coordinate + " and hit!"));
            }
        }
        return hp;
    }

    public static void cpuTurn(double cpuTurnCount) {
        Random random = new Random();
        char row = (char) (random.nextInt(75 - 65) + 65);
        int col = random.nextInt(10 - 0) + 0;
        String coordinate = Character.toString(row) + String.valueOf(col);
        while (true) {
            // Checks if the cpu has guessed the location already. If it has then it guesses again, else it continues
            if (cpuTurnCount > 0 && ((cpuGuesses.substring(0, cpuGuesses.length() - 2)).contains(coordinate)) == true) {
                row = (char) (random.nextInt(75 - 65) + 65);
                col = random.nextInt(10 - 0) + 0;
                coordinate = Character.toString(row) + String.valueOf(col);
            } else {
                // Adds the guessed coordinate the string of guessed coordinates
                cpuGuesses += coordinate;
                break;
            }
        }
        playerHP = hitOrMiss(((int) row - 65), col, playerBoard, playerHP, "PLAYER");
        if (checkWin(playerHP) == true) {
            JOptionPane.showMessageDialog(null, "CPU has won");
            System.exit(0);
        }
        clicked = false;
    }

    public static boolean checkWin(int hp) {
        // If the player or cpu's hp reaches 0 then the opposing player wins
        boolean win = false;
        if (hp == 0) {
            win = true;
        }
        return win;
    }

    public static class Exit implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // Closes the window when the exit button is clicked
            frame.dispose();
            System.exit(0);
        }
    }

    public static class Restart implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // Removes every item on the panel and resets all the variables that change throughout the game in order to have a new game
            mainPanel.removeAll();
            cpuHP = 17;
            playerHP = 17;
            clicked = true;
            playerGuesses = "";
            cpuGuesses = "";
            cpuTurnCount = 0.0;
            playerMsgBool = true;
            cpuMsgBool = true;
            loadPlayer.setVisible(true);
            loadCPU.setVisible(true);
            playerMsg.setText("");
            cpuMsg.setText("");
            Game();
        }
    }

    public static void Game() {
        // Gets the screen resolution in order to scale to all computer screens
        Dimension monitor = Toolkit.getDefaultToolkit().getScreenSize();
        // Initial font size set to 25 for the attack messages
        font = fontSize(25);

        playerMsg.setFont(font);
        playerMsg.setForeground(Color.WHITE);
        playerMsg.setBounds(25, 610, 1000, 48);
        mainPanel.add(playerMsg);

        cpuMsg.setFont(font);
        cpuMsg.setForeground(Color.WHITE);
        cpuMsg.setBounds(25, 685, 1000, 48);
        mainPanel.add(cpuMsg);

        // Fills the cpu board with asterisks so that the player cannot see the cpu's boats
        for (int i = 0; i < dummyBoard.length; i++) {
            for (int j = 0; j < dummyBoard[i].length; j++) {
                dummyBoard[i][j] = new JLabel();
                dummyBoard[i][j].setText("*");
            }
        }

        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(1000, 750));

        JLabel bg = new JLabel(new ImageIcon("bg.png"));
        bg.setBounds(0, 0, 1000, 750);
        mainPanel.add(bg);

        font = fontSize(40);
        // Adding all the elements to the load file window
        Color bgColour = new Color(6, 22, 45);
        loadPlayer.setFont(font);
        loadPlayer.setForeground(Color.WHITE);
        loadCPU.setFont(font);
        loadCPU.setForeground(Color.WHITE);
        loadFilePanel.setPreferredSize(new Dimension(300, 375));
        loadFilePanel.setBackground(bgColour);
        loadFilePanel.add(loadPlayer);
        loadFilePanel.add(loadCPU);
        loadFileFrame.setIconImage(new ImageIcon("icon.png").getImage());
        loadFileFrame.setLocation((int) ((monitor.getWidth() - 1000) / 2) + 693, (int) ((monitor.getHeight() - 750) / 2) - 40 + 391);
        loadFileFrame.setAlwaysOnTop(true);
        loadFileFrame.setContentPane(loadFilePanel);
        loadFileFrame.setVisible(true);
        loadFileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadFileFrame.pack();

        frame.setIconImage(new ImageIcon("icon.png").getImage());
        frame.setResizable(false);
        frame.setLocation((int) ((monitor.getWidth() - 1000) / 2), (int) ((monitor.getHeight() - 750) / 2) - 40);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    public static void main(String[] args) {
        JMenuBar menuBar = new JMenuBar();
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem restart = new JMenuItem("Restart");
        // Sets shortcuts to access the menubar
        open.setMnemonic('O');
        exit.setMnemonic('E');
        restart.setMnemonic('R');
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        file.add(open);
        file.add(restart);
        file.add(exit);
        menuBar.add(file);
        // Adding all the listeners to be able to click on locations, load files, restart the game, and close the game
        frame.setJMenuBar(menuBar);
        frame.addMouseListener(new MouseMovement());
        open.addActionListener(new OpenFile());
        exit.addActionListener(new Exit());
        restart.addActionListener(new Restart());

        // Runs the main game
        Game();
    }
}
