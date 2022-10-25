import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import static java.nio.file.StandardOpenOption.CREATE;

public class ExtractorFrame extends JFrame
{
    JButton quitButton, chooserButton, saveButton;
    JTextArea displayTextArea;
    JScrollPane scroller;
    JPanel mainPnl, titlePnl, displayPnl, buttonPnl;
    JLabel titleLbl;
    String name;

    ArrayList<String> noiseWords = new ArrayList<>();
    TreeMap<String, Integer> keywords = new TreeMap<String, Integer>();

    public ExtractorFrame()
    {
        setTitle("Keyword Extractor");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        add(mainPnl);

        createTitlePanel();
        createDisplayPanel();

        setVisible(true);
    }

    private void createTitlePanel()
    {
        displayPnl = new JPanel();

        displayTextArea = new JTextArea(30, 40);
        scroller = new JScrollPane(displayTextArea);
        displayTextArea.setFont(new Font("Comic Sans Ms", Font.PLAIN, 25));

        displayTextArea.setEditable(false);
        displayPnl.add(scroller);

        mainPnl.add(displayPnl, BorderLayout.CENTER);
    }

    private void createDisplayPanel()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1, 2));

        chooserButton = new JButton("Choose a File");
        chooserButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        saveButton = new JButton("Save to File");
        saveButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        chooserButton.addActionListener(e ->
        {
            readFile();
            for (Map.Entry map : keywords.entrySet())
            {
                displayTextArea.append(String.format("%-30s%d\n",map.getKey(), map.getValue()));
            }
        });

        quitButton.addActionListener(e ->
        {
            JOptionPane pane = new JOptionPane();

            int result = JOptionPane.showConfirmDialog(pane, "Want to exit the window?", "Exit", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
            else
            {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
        });

        saveButton.addActionListener(e ->
        {
            String saveFileName = JOptionPane.showInputDialog("Enter file name");

            File file = new File(System.getProperty("user.dir"));
            Path filePath = Paths.get(file.getPath() + "//src//" + saveFileName + ".txt");

            try
            {
                OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath, CREATE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

                for (Map.Entry map : keywords.entrySet())
                {
                    writer.write(String.format("%-30s%d\n", map.getKey(), map.getValue()));
                }
                writer.close();
                displayTextArea.append("\nFile Saved To: " + saveFileName + ".txt");

            }
            catch (IOException i)
            {
                i.printStackTrace();
            }
        });

        buttonPnl.add(chooserButton);
        buttonPnl.add(quitButton);
        buttonPnl.add(saveButton);

        mainPnl.add(buttonPnl, BorderLayout.SOUTH);
    }

    public void readStopWords()
    {
        try
        {
            File workingDirectory = new File("src/EnglishStopWords.txt");

            Scanner readFile = new Scanner(workingDirectory);

            while (readFile.hasNextLine())
            {
                noiseWords.add(readFile.nextLine());
            }
            readFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readFile()
    {
        JFileChooser chooser = new JFileChooser();
        String readLine = "";

        Path target = new File(System.getProperty("user.dir")).toPath();
        target = target.resolve("src");
        chooser.setCurrentDirectory(target.toFile());

        readStopWords();

        try
        {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                target = chooser.getSelectedFile().toPath();
                Scanner inFile = new Scanner(target);

                while (inFile.hasNextLine())
                {
                    readLine = inFile.nextLine().toLowerCase().replaceAll("[^A-Za-z]", " ");
                    name = chooser.getSelectedFile().getName();
                    displayTextArea.setText("File name: " + name + "\n\n\nKeyWords and their frequency:\n\n");
                    String word[] = readLine.split(" ");
                    for (int i = 0; i < word.length; i++)
                    {
                        String current = word[i];

                        if (keywordFrequency(current, keywords))
                        {

                        }
                        else if (!noiseCheck(current))
                        {
                            gatherKeyWords(current);

                        }
                    }
                }
                inFile.close();
            }
            else
            {
                displayTextArea.setText("No file chosen.  Choose a file.");
            }
        }
        catch (IOException e)
        {
            System.out.println("IOException Error");
            e.printStackTrace();
        }
    }

    public boolean noiseCheck(String word)
    {
        for (String bad : noiseWords) {
            if (bad.equals(word))
            {
                return true;
            }
        }
        return false;
    }

    public boolean keywordFrequency(String word, TreeMap<String, Integer> keyWords)
    {
        for (Map.Entry map : keyWords.entrySet())
        {
            if (map.getKey().equals(word))
            {
                int frequency = Integer.parseInt(map.getValue().toString()) + 1;
                map.setValue(frequency);

                return true;
            }
        }

        return false;
    }

    public void gatherKeyWords(String word)
    {
        if (word != null && !"".equals(word))
        {
            keywords.put(word, 1);
        }
    }
}
