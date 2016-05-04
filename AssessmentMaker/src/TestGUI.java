import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by kns10 on 5/2/16.
 */
public class TestGUI {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JComboBox problemlist;
    private JTextArea problemdata;
    private JTextField question;
    private JTextField testname;
    private JButton addProblemButton;
    private JButton finishButton;
    private JButton modifyProblemButton;
    private JButton testDirectoryButton;
    private JButton outputDirectoryButton;
    private JButton merFileButton;
    private JButton generateButton;
    private JTextField classname;
    private JButton keyOutputDirButton;
    private ArrayList<String> questions = new ArrayList<String>();
    private ArrayList<String[]> answers = new ArrayList<String[]>();
    private int cindex = 0;
    private int maxcindex = 0;
    private File testFile;
    private File merFile;
    private File outDir;
    private File keyOut;

    public String join(String[] s, String delimiter) {
        String out = "";
        for (String i: s) {
            out += i + delimiter;
        }
        return out.substring(0,out.length()-delimiter.length());
    }

    public TestGUI() {
        addProblemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String s = question.getText().trim();
                System.out.println(s);
                String choices = problemdata.getText();

                questions.add(s);
                answers.add(choices.trim().split("\n"));
                problemlist.addItem("Problem "+(maxcindex+1));

                maxcindex++;
                cindex = maxcindex;
                System.out.println(maxcindex);
            }
        });
        problemlist.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(maxcindex != 0) {
                    String s = problemlist.getSelectedItem().toString();
                    System.out.println(s);
                    int p = Integer.parseInt(s.replace("Problem ", ""))-1;
                    question.setText(questions.get(p));
                    cindex = p;
                    problemdata.setText(join(answers.get(p), "\n"));
                    System.out.println(cindex+" "+maxcindex);
                }
            }
        });
        modifyProblemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String s = question.getText().trim();
                System.out.println(s);
                String choices = problemdata.getText();

                questions.set(cindex,s);
                answers.set(cindex,choices.trim().split("\n"));

            }
        });
        finishButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String outstr = "";
                for (int i=0; i<questions.size();i++) {
                    if (answers.get(i).length > 1) {
                        outstr += "\n" + questions.get(i) + "\n";
                        outstr += join(answers.get(i), "\n") + "\n";
                    } else {
                        outstr += "_s\n" + questions.get(i) + "\n";
                        outstr += join(answers.get(i), "\n") + "\n";
                    }
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose a directory:");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    //System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    try {
                        PrintWriter writer = new PrintWriter(chooser.getSelectedFile()+"/"+testname.getText().trim()+".txt", "UTF-8");
                        writer.println(outstr);
                        writer.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("No Selection ");
                }

            }
        });
        testDirectoryButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose a directory:");
                //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    testFile = chooser.getSelectedFile();

                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        merFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose a directory:");
                //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    merFile = chooser.getSelectedFile();

                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        outputDirectoryButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose a directory:");
                //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    outDir = chooser.getSelectedFile();

                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String s;
                if (testFile != null && merFile != null && outDir != null && keyOut != null && (s = classname.getText()) != null) {
                    QuizUtils.genXMLFromTemplate(testFile,new File("temp/temp.xml"));
                    QuizUtils.generatePDFTests(new File("temp/temp.xml"),merFile,outDir,keyOut,s);
                }
            }
        });
        keyOutputDirButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose a directory:");
                //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    keyOut = chooser.getSelectedFile();

                } else {
                    System.out.println("No Selection ");
                }
            }
        });
    }

    public static void main(String[] args) {
        //try {
        //    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        JFrame frame = new JFrame("TestGUI");
        frame.setContentPane(new TestGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
