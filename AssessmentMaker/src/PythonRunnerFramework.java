import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class PythonRunnerFramework {
	/**
	 * generate a csv containing grades of a program
	 *
	 * @param filename filename of python program to test
	 * @param inputs list of inputs to enter to program
	 * @param outputs list of outputs that are expected
     */
	public static void generateCSV(String filename, ArrayList<String> inputs, ArrayList<String> outputs) {
		try {
			ArrayList<String> data = test(filename, inputs, outputs);
			String csvString = "";
			csvString += filename+",";
			Integer occurrences = Collections.frequency(data, "correct");
			csvString += occurrences.toString() + ",";
			
			csvString += data.get(0) +",";
			for (int i=1;i<data.size(); i++) {
				csvString += data.get(i) + ",";
			}
			
			File f = new File("programscore.csv");
			if(!f.exists()) { 
				PrintWriter writer = new PrintWriter("programscore.csv", "UTF-8");
				String generated = "File Name,Score,Warnings,";
				for (Integer i=1; i <= inputs.size(); i++) {
					generated += "Test "+i.toString() + ",";
				}
				writer.write(generated+"\n\n");
				writer.close();
			}
			
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(
					new File("programscore.csv"),
					true));
			writer.append(csvString+"\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if program attempts to open another file
	 *
	 * @param filename file to check
	 * @return true if it attempts to open a file, false otherwise
     */
	public static boolean checkForOpen(String filename) {
		try {
			File inputFile = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String text;
			
			while ((text = reader.readLine()) != null) {
				if (text.toLowerCase().contains("open(")) {
					return true;
				}
				if (text.toLowerCase().contains("__open__")) {
					return true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}

	/**
	 * Check if program imports a module
	 *
	 * @param filename file to check
	 * @return true if it imports something, false otherwise
     */
	public static boolean checkForImport(String filename) {
		try {
			File inputFile = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			String text;

			while ((text = reader.readLine()) != null) {
				if (text.toLowerCase().contains("import")) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * runs a python file with input and expected output
	 *
	 * @param filename name of program
	 * @param input input to program
	 * @param expectedOutput expected output
     * @return status of program
     */
	public static String runFile(String filename, String input, String expectedOutput) {
		return runFile(filename, "C:\\Python34\\python.exe", input, expectedOutput);
	}

	/**
	 * runs a python file with input and expected output and python path
	 *
	 * @param filename name of program
	 * @param pythonPath path to python
	 * @param input input to program
	 * @param expectedOutput expected output
     * @return status of program
     */
	public static String runFile(String filename, String pythonPath, String input, String expectedOutput) {
		
		try {
			
			Process p = Runtime.getRuntime().exec(pythonPath+" "+filename);
			OutputStream in = p.getOutputStream();
			InputStream out = p.getInputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(in));
			//System.out.println(input);
			writer.write(input+"\n");
			writer.flush();
			writer.close();
			boolean timeout = p.waitFor(400, TimeUnit.MILLISECONDS);
			
			if (!timeout) {
				return "timeout";
			}
			
			String s = "";
			int n;
			while ((n = out.read()) != -1)
			    s += (char)n;
			s = s.trim();
			
			if (s.equals(expectedOutput)) {
				System.out.println("correct!");
				return "correct";
			} else if (s.equals("")) {
				System.out.println("empty output");
				return "no output, probable error";
			} else {
				System.out.println("wrong, you said: "+s+" and I wanted "+expectedOutput);
				return "wrong: "+s;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * tests program multiple times
	 *
	 * @param filename program to run
	 * @param inputs inputs to program
	 * @param outputs expected outputs
     * @return arraylist of program statuses
     */
	public static ArrayList<String> test(String filename, ArrayList<String> inputs, ArrayList<String> outputs) {
		ArrayList<String> results = new ArrayList<String>();
		String warnings = "";
		
		if (checkForOpen(filename)) {
			warnings += "WARNING: open() function detected! ";
		}

		if (checkForImport(filename)) {
			warnings += "WARNING: import detected! ";
		}

		if (inputs.size() != outputs.size()) {
			System.out.println("input length and output length aren't the same!");
			return null;
		}
		for (int i = 0; i < inputs.size(); i++) {
			results.add(runFile(filename, inputs.get(i), outputs.get(i)));
		}
		
		if (!warnings.equals("")) {
			results.add(0,warnings);
		} else {
			results.add(0, "none");
		}
		return results;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		for (Integer i = 0; i < 10; i++) {
			inputs.add(i.toString());
		}
		for (Integer i = 0; i < 10; i++) {
			outputs.add("1");
		}
		
		generateCSV("test.py",inputs,outputs);
	}

}
