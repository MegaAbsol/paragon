import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private JLabel fileNameField;
    private JLabel merFileField;
    private JLabel OutputField;
    private JLabel keyOutField;
    private JButton studentFormDirectoryButton;
    private JButton answerKeyDirectoryButton;
    private JButton outputDirectoryButton1;
    private JButton gradeTestsButton;
    private JLabel SFLabel;
    private JLabel AKLabel;
    private JLabel OutCSVLabel;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JTextArea pinput;
    private JTextArea poutput;
    private JCheckBox strictWarningsCheckBox;
    private JButton pythonFileDirectoryButton;
    private JButton outputCSVDirectoryButton;
    private JButton runStudentProgramsButton;
    private JCheckBox promptOnWarningCheckBox;
    private JProgressBar progressBar3;
    private JLabel pfd;
    private JLabel pcd;
    private JProgressBar progressBar4;
    private ArrayList<String> questions = new ArrayList<String>();
    private ArrayList<String[]> answers = new ArrayList<String[]>();
    private int cindex = 0;
    private int maxcindex = 0;
    private File testFile;
    private File merFile;
    private File outDir;
    private File keyOut;
    private File gradeSFDir;
    private File AnswerDir;
    private File outCSVDir;
    private File pythonDir;
    private File pythonCSVDir;
    private int strictness = 0;
    private ArrayList<String> inputs;
    private ArrayList<String> outputs;


    private int pause = 0;

    public String join(String[] s, String delimiter) {
        String out = "";
        for (String i: s) {
            out += i + delimiter;
        }
        return out.substring(0,out.length()-delimiter.length());
    }

    public void gradeSingleProgram(String filename) {
        //ArrayList<String> inputs, ArrayList<String> outputs, int strictness
        String warnings = "";
        if (PythonRunnerFramework.checkForOpen(filename)) {
            warnings += "WARNING: open() function detected! ";
        }

        if (PythonRunnerFramework.checkForImport(filename)) {
            warnings += "WARNING: import detected! ";
        }
        if (warnings.length()>0) {
            if (strictness == 1) {
                // don't run, just give them a 0
                PythonRunnerFramework.giveZero(filename,inputs,pythonCSVDir);
                return;
            }
            else if (strictness == 2) {
                // give a warning
                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", filename);
                try {
                    pb.start();
                } catch (Exception e) {

                }
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to run this program?",
                        "Are you sure you want to run this program?",
                        JOptionPane.YES_NO_OPTION);

                if (n!=0) {
                    PythonRunnerFramework.generateCSV(filename,inputs,outputs,pythonCSVDir);
                } else {
                    PythonRunnerFramework.giveZero(filename,inputs,pythonCSVDir);
                }

            }
            else {
                PythonRunnerFramework.generateCSV(filename,inputs,outputs,pythonCSVDir);
            }
        }
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
                problemlist.addItem("Problem " + (maxcindex + 1));

                maxcindex++;
                cindex = maxcindex;
                System.out.println(maxcindex);
                problemdata.setText("");
            }
        });
        problemlist.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (maxcindex != 0) {
                    String s = problemlist.getSelectedItem().toString();
                    System.out.println(s);
                    int p = Integer.parseInt(s.replace("Problem ", "")) - 1;
                    question.setText(questions.get(p));
                    cindex = p;
                    problemdata.setText(join(answers.get(p), "\n"));
                    System.out.println(cindex + " " + maxcindex);
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

                questions.set(cindex, s);
                answers.set(cindex, choices.trim().split("\n"));

            }
        });
        finishButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                progressBar3.setMaximum(100);
                progressBar3.setValue(0);
                String outstr = "";
                for (int i = 0; i < questions.size(); i++) {
                    if (answers.get(i).length > 1) {
                        outstr += "\n" + questions.get(i) + "\n";
                        outstr += join(answers.get(i), "\n") + "\n";
                    } else {
                        outstr += "_s\n" + questions.get(i) + "\n";
                        outstr += join(answers.get(i), "\n") + "\n";
                    }
                }
                outstr = outstr.substring(0, outstr.length() - 1);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose a directory:");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    //System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    try {

                        progressBar3.setValue(10);
                        PrintWriter writer = new PrintWriter(chooser.getSelectedFile() + "/" + testname.getText().trim() + ".txt", "UTF-8");
                        writer.print(outstr);
                        writer.close();
                        progressBar3.setValue(100);
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
                chooser.setDialogTitle("Choose a file:");
                //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    testFile = chooser.getSelectedFile();
                    fileNameField.setText((testFile.getAbsolutePath().length() > 75) ? testFile.getAbsolutePath().substring(0, 72) + "..." : testFile.getAbsolutePath());

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
                chooser.setDialogTitle("Choose a file:");
                //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    merFile = chooser.getSelectedFile();

                    merFileField.setText((merFile.getAbsolutePath().length() > 75) ? merFile.getAbsolutePath().substring(0, 72) + "..." : merFile.getAbsolutePath());


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

                    OutputField.setText((outDir.getAbsolutePath().length() > 75) ? outDir.getAbsolutePath().substring(0, 72) + "..." : outDir.getAbsolutePath());


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
                progressBar1.setMaximum(100);
                progressBar1.setValue(0);
                //progressBar1.setStringPainted(true);
                progressBar1.setString("Starting Generation...");
                if (testFile != null && merFile != null && outDir != null && keyOut != null && (s = classname.getText()) != null) {
                    progressBar1.setValue(10);
                    progressBar1.setString("Generating...");
                    QuizUtils.genXMLFromTemplate(testFile, new File(keyOut.getAbsolutePath() + "/" + "temp.xml"));
                    progressBar1.setValue(30);
                    QuizUtils.generatePDFTests(new File(keyOut.getAbsolutePath() + "/" + "temp.xml"), merFile, outDir, keyOut, s);
                    progressBar1.setValue(100);
                    progressBar1.setString("Forms Generated!");
                } else {
                    progressBar2.setString("Error: please fill all fields in");
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
                    keyOutField.setText((keyOut.getAbsolutePath().length() > 75) ? keyOut.getAbsolutePath().substring(0, 72) + "..." : keyOut.getAbsolutePath());


                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        studentFormDirectoryButton.addMouseListener(new MouseAdapter() {
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
                    gradeSFDir = chooser.getSelectedFile();
                    SFLabel.setText((gradeSFDir.getAbsolutePath().length() > 75) ? gradeSFDir.getAbsolutePath().substring(0, 72) + "..." : gradeSFDir.getAbsolutePath());


                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        answerKeyDirectoryButton.addMouseListener(new MouseAdapter() {
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
                    AnswerDir = chooser.getSelectedFile();
                    AKLabel.setText((AnswerDir.getAbsolutePath().length() > 75) ? AnswerDir.getAbsolutePath().substring(0, 72) + "..." : AnswerDir.getAbsolutePath());


                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        outputDirectoryButton1.addMouseListener(new MouseAdapter() {
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
                    outCSVDir = chooser.getSelectedFile();
                    OutCSVLabel.setText((outCSVDir.getAbsolutePath().length() > 75) ? outCSVDir.getAbsolutePath().substring(0, 72) + "..." : outCSVDir.getAbsolutePath());


                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        gradeTestsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                progressBar2.setMaximum(100);
                progressBar2.setValue(0);
                //progressBar2.setStringPainted(true);
                progressBar2.setString("Starting job...");
                if (outCSVDir != null && gradeSFDir != null && AnswerDir != null) {
                    progressBar2.setValue(10);
                    progressBar2.setString("Grading...");
                    QuizUtils.gradePDFDir(gradeSFDir, AnswerDir, new File(outCSVDir.getAbsolutePath() + "/grades.csv"));
                    progressBar2.setValue(100);
                    progressBar2.setString("Done Grading!");
                } else {
                    progressBar2.setString("Error: please fill all fields in");
                }
            }
        });
        strictWarningsCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (strictWarningsCheckBox.isSelected()) {
                    promptOnWarningCheckBox.setEnabled(false);
                    promptOnWarningCheckBox.setSelected(false);
                } else {
                    promptOnWarningCheckBox.setEnabled(true);
                }
            }
        });
//        strictWarningsCheckBox.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                if (strictWarningsCheckBox.isSelected()) {
//                    promptOnWarningCheckBox.setEnabled(false);
//                    promptOnWarningCheckBox.setSelected(false);
//                } else {
//                    promptOnWarningCheckBox.setEnabled(true);
//                }
//            }
//        });
        pythonFileDirectoryButton.addMouseListener(new MouseAdapter() {
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
                    pythonDir = chooser.getSelectedFile();
                    pfd.setText((AnswerDir.getAbsolutePath().length() > 75) ? AnswerDir.getAbsolutePath().substring(0, 72) + "..." : AnswerDir.getAbsolutePath());


                } else {
                    System.out.println("No Selection ");
                }
            }
        });

        outputCSVDirectoryButton.addMouseListener(new MouseAdapter() {
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
                    pythonCSVDir = chooser.getSelectedFile();
                    pcd.setText((AnswerDir.getAbsolutePath().length() > 75) ? AnswerDir.getAbsolutePath().substring(0, 72) + "..." : AnswerDir.getAbsolutePath());


                } else {
                    System.out.println("No Selection ");
                }
            }
        });
        runStudentProgramsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String inp, expout;
                progressBar4.setMaximum(100);
                if (pythonDir != null && pythonCSVDir != null && (inp = pinput.getText()) != null && (expout = poutput.getText()) != null) {
                    // TODO
                    // for file in directory:
                    // gradesinglefile
                    if (strictWarningsCheckBox.isSelected()) {
                        strictness = 2;
                    } else if (promptOnWarningCheckBox.isSelected()) {
                        strictness = 1;
                    } else {
                        strictness = 0;
                    }

                    File[] directoryListing = pythonDir.listFiles();
                    if (directoryListing != null) {
                        for (File child : directoryListing) {
                            // Do something with child
                            gradeSingleProgram(child.getAbsolutePath());
                        }
                    } else {
                        // Handle the case where dir is not really a directory.
                        // Checking dir.isDirectory() above would not be sufficient
                        // to avoid race conditions with another process that deletes
                        // directories.
                    }
                } else {
                    progressBar2.setString("Error: please fill all fields in");
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ImageIcon img = new ImageIcon("pathtoiconhere");
        JFrame frame = new JFrame("Assessment Maker");
        //frame.setIconImage(img.getImage());
        frame.setContentPane(new TestGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
