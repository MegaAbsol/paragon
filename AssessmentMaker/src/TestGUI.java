import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private ArrayList<String> questions = new ArrayList<String>();
    private ArrayList<String[]> answers = new ArrayList<String[]>();
    private int cindex = 0;
    private int maxcindex = 0;

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
                //cindex++;
                maxcindex++;
                System.out.println(maxcindex);
            }
        });
        problemlist.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String s = problemlist.getSelectedItem().toString();
                System.out.println(s);
                int p = Integer.parseInt(s.replace("Problem ",""));
                question.setText(questions.get(p));
                //cindex = p;
                problemdata.setText(join(answers.get(p),"\n"));
                System.out.println("is it working");
            }
        });
//        problemlist.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                JComboBox problemlist = (JComboBox) actionEvent.getSource();
//                String s = problemlist.getSelectedItem().toString();
//                System.out.println(s);
//                int p = Integer.parseInt(s.replace("Problem ",""));
//                question.setText(questions.get(p));
//                //cindex = p;
//                problemdata.setText(join(answers.get(p),"\n"));
//                System.out.println("is it working");
//
//            }
//        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestGUI");
        frame.setContentPane(new TestGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
