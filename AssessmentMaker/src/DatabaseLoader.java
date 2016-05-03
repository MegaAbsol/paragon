import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

public class DatabaseLoader {
    public static ArrayList<ArrayList<String>> retrieve(String filename, String filter) {
        Scanner file = null;
        try {
            file = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //create Map of Fields (line 1)
        Map<String, Integer> field = new HashMap<String,Integer>();
        int i = 0;
        for(String fieldName: file.nextLine().split(",")){
            field.put(fieldName.trim(), i++);
        }
        //System.out.println(field);

        //Load in student data
        ArrayList<ArrayList<String>> studentData = new ArrayList<ArrayList<String>>();
        while(file.hasNext()){
            ArrayList<String> aStudent = new ArrayList<String>();
            for(String studentField: file.nextLine().split("\",\"")){
                aStudent.add(studentField.trim());
            }
            studentData.add(aStudent);
        }
        ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
        //eliminate duplicates
        // filter will look something like S2-- 02-- Algorithm/Data B-- 293-- Estep Mark, field ClassLine
        //System.out.println("**********************************************");
        for(i = 0; i < studentData.size();  i++){
            String c = studentData.get(i).get(field.get("ClassLine"));
            if (c.toLowerCase().equals(filter.toLowerCase())) {
                out.add(studentData.get(i));
            }
        }
        ArrayList<ArrayList<String>> realout = new ArrayList<ArrayList<String>>();
        for(ArrayList<String> student: out){
            ArrayList<String> curr = new ArrayList<>();
            curr.add(student.get(field.get("ID")));
            curr.add(student.get(field.get("Alpha_name")));
            curr.add(student.get(field.get("PD"))); // should all be the same number
            realout.add(curr);
        }
        //System.out.println(studentData.size());
        return realout;
    }

    public static ArrayList<ArrayList<String>> retrieve(File filename, String filter) {
        Scanner file = null;
        try {
            file = new Scanner(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //create Map of Fields (line 1)
        Map<String, Integer> field = new HashMap<String,Integer>();
        int i = 0;
        for(String fieldName: file.nextLine().split(",")){
            field.put(fieldName.trim(), i++);
        }
        //System.out.println(field);

        //Load in student data
        ArrayList<ArrayList<String>> studentData = new ArrayList<ArrayList<String>>();
        while(file.hasNext()){
            ArrayList<String> aStudent = new ArrayList<String>();
            for(String studentField: file.nextLine().split("\",\"")){
                aStudent.add(studentField.trim());
            }
            studentData.add(aStudent);
        }
        ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
        //eliminate duplicates
        // filter will look something like S2-- 02-- Algorithm/Data B-- 293-- Estep Mark, field ClassLine
        //System.out.println("**********************************************");
        for(i = 0; i < studentData.size();  i++){
            String c = studentData.get(i).get(field.get("ClassLine"));
            if (c.toLowerCase().equals(filter.toLowerCase())) {
                out.add(studentData.get(i));
            }
        }
        ArrayList<ArrayList<String>> realout = new ArrayList<ArrayList<String>>();
        for(ArrayList<String> student: out){
            ArrayList<String> curr = new ArrayList<>();
            curr.add(student.get(field.get("ID")));
            curr.add(student.get(field.get("Alpha_name")));
            curr.add(student.get(field.get("PD"))); // should all be the same number
            realout.add(curr);
        }
        //System.out.println(studentData.size());
        return realout;
    }

    public static void main(String[] args) {
        retrieve("SMCS10_noGrades.mer", "S2-- 02-- Algorithm/Data B-- 293-- Estep Mark");
    }
}
