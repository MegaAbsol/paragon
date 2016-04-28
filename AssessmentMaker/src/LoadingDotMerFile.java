import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;


public class LoadingDotMerFile {

	public static void main(String[] args) {
		Scanner file = null;
		try {
			file = new Scanner(new File("test3.mer"));
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
		System.out.println(field);
		
		//Load in student data
		ArrayList<ArrayList<String>> studentData = new ArrayList<ArrayList<String>>();
		while(file.hasNext()){
			ArrayList<String> aStudent = new ArrayList<String>();
			for(String studentField: file.nextLine().split("\",\"")){
				aStudent.add(studentField.trim());
			}
			studentData.add(aStudent);
		}
		System.out.println(studentData.size());
		for(ArrayList<String> student: studentData){
			System.out.println(student.get(field.get("ID")));
		}
		//eliminate duplicates
		System.out.println("**********************************************");
		for(i = 0; i < studentData.size();  i++){
			String id = studentData.get(i).get(field.get("ID"));
			for(int j = studentData.size() - 1; j > i; j--){
				String id2 = studentData.get(j).get(field.get("ID"));
				if(id.equals(id2)){
					studentData.remove(j);
				}
			}	
		}
		
		for(ArrayList<String> student: studentData){
			System.out.println(student.get(field.get("LAST")));
		}
		System.out.println(studentData.size());  
	}

}
