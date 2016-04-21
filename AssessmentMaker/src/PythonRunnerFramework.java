import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class PythonRunnerFramework {
	
	public static boolean checkForOpen(String filename) {
		try {
			File inputFile = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String text;
			
			while ((text = reader.readLine()) != null) {
				if (text.toLowerCase().contains("open(")) {
					return true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	public static String runFile(String filename, String input, String expectedOutput) {
		return runFile(filename, "C:\\Python34\\python.exe", input, expectedOutput);
	}
	
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
			boolean timeout = p.waitFor(10, TimeUnit.SECONDS);
			
			if (timeout == false) {
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
				return "wrong answer: "+s;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public static ArrayList<String> test(String filename, ArrayList<String> inputs, ArrayList<String> outputs) {
		ArrayList<String> results = new ArrayList<String>();
		String warnings = "";
		
		if (checkForOpen(filename)) {
			warnings += "WARNING: open() function detected";
		}
		
		if (inputs.size() != outputs.size()) {
			System.out.println("input length and ouput length aren't the same!");
			return null;
		}
		for (int i = 0; i < inputs.size(); i++) {
			results.add(runFile(filename, inputs.get(i), outputs.get(i)));
		}
		
		if (!warnings.equals("")) {
			results.add(0,warnings);
		}
		return results;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		runFile("test.py","4","5");
	}

}
