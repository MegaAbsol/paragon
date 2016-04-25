import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;

import com.sun.deploy.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class QuizUtils {


	/**
	 * Makes a directory if it does not exist.
	 *
	 * @param dirname name of the directory to be created.
     */
	public static void makeDirectory(String dirname) {
		new File(dirname).mkdirs();
	}
	/**
	 * Generates a personalized PDF test and an answer key from an XML file.
	 *
	 * @param template name of xml template to generate test from
	 * @param testNumber student ID
     */
	public static void genPDFTestFromXML(String template, int testNumber) {
		genPDFTestFromXML(template, testNumber, "", "", "", "");
	}

	/**
	 * Generates a personalized PDF test and an answer key from an XML file in the right directories. XML must be in
	 * test>question format.
	 *
	 * @param template name of xml template to generate test from
	 * @param testNumber student ID
	 * @param templateDirectory directory where XML template is located
	 * @param outDirectory directory to put output
	 * @param formOutDirectory directory to put student entering forms
	 * @param keyOutDirectory directory to put answer keys
     */
	public static void genPDFTestFromXML(String template, int testNumber, String templateDirectory, String outDirectory, String formOutDirectory, String keyOutDirectory) {

		makeDirectory(templateDirectory);
		makeDirectory(outDirectory);
		makeDirectory(formOutDirectory);
		makeDirectory(keyOutDirectory);

		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, Font.NORMAL, new CMYKColor(0, 0, 0, 255));
		Font defaultFont = FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL, new CMYKColor(0, 0, 0, 255));
		try {
			PdfWriter docwriter = PdfWriter.getInstance(document, new FileOutputStream(outDirectory+testNumber+".pdf"));
			document.open();

			File inputFile = new File(templateDirectory+template);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			String title = doc.getDocumentElement().getAttribute("title");
			Paragraph titlepg = new Paragraph(title,titleFont);
			titlepg.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			titlepg.setSpacingAfter(12);
			document.add(titlepg);
			//document.add(new Paragraph("\n"));

			PrintWriter keyWriter = new PrintWriter(keyOutDirectory+"key"+testNumber+".txt", "UTF-8");



			//System.out.println("Root element :"
			//		+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("problem");
			ArrayList<Integer> newOrder = new ArrayList<Integer>();
			for (int i =0; i < nList.getLength(); i++) {
				newOrder.add(i);
			}
			Collections.shuffle(newOrder, new Random());

			int problemNumber = 1;
			// in case want newline at front
			// writer.println();
			for (int temp : newOrder) {
				// each problem
				ArrayList<String> out = new ArrayList<String>();

				Node nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());

				Element eElement = (Element) nNode;
				String question = eElement.getElementsByTagName("question")
						.item(0).getTextContent();
				String correctAnswer = eElement
						.getElementsByTagName("correctanswer").item(0)
						.getTextContent();

				String keyNumber = eElement
						.getElementsByTagName("problemnumber").item(0)
						.getTextContent();

				String type = eElement.getAttribute("type");

				// this part is for multiple choice questions
				if (type.equals("multiplechoice")) {
					out.add(correctAnswer);
					NodeList answers = eElement.getElementsByTagName("choice");
					for (int temp2 = 0; temp2 < answers.getLength(); temp2++) {
						String choice = answers.item(temp2).getTextContent();
						out.add(choice);
						//System.out.println("other answer: " + choice);
					}

					Collections.shuffle(out, new Random());
					//System.out.println(out);
					int key = out.indexOf(correctAnswer);
					//System.out.println(key);
					Paragraph currentQuestion = new Paragraph(problemNumber + ". " + question + "\n",defaultFont);
					currentQuestion.setKeepTogether(true);
					currentQuestion.setSpacingAfter(12);
					//currentQuestion.setIndentationLeft(20);
					//currentQuestion.setFirstLineIndent(0);

					keyWriter.println("mc`"+keyNumber + "`" + key);
					for (int i = 0; i < out.size(); i++) {
						currentQuestion.add("   "+"abcdefghijklmnopqrstuvwxyz".charAt(i)+") "+out.get(i)+"\n");
					}
					document.add(currentQuestion);

					// end mc questions
				} else if (type.equals("shortanswer")) {
					Paragraph currentQuestion = new Paragraph(problemNumber + ". " + question + "\n",defaultFont);
					currentQuestion.setKeepTogether(true);
					//currentQuestion.setIndentationLeft(20);
					//currentQuestion.setFirstLineIndent(0);
					currentQuestion.add("   Ans: _____________");
					currentQuestion.setSpacingAfter(10);
					document.add(currentQuestion);

					keyWriter.println("sa`"+keyNumber + "`" + correctAnswer);
				}

				//document.add(new Paragraph("\n"));

				/* text
				else if (type.equals("text")) {
					writer.println(question);
					problemNumber -= 1;
				} */
				problemNumber += 1;

			}
			Paragraph footer = new Paragraph("Made with <3 just for Student ID #"+testNumber,defaultFont);
			footer.setSpacingBefore(12);
			footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			document.add(footer);
			generateStudentForm(testNumber,title,problemNumber-1,formOutDirectory);

			keyWriter.close();

			document.close();
			docwriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a personalized PDF test and an answer key from an XML file in the right directories. This PDF has
	 * sections which group relevant questions together, such that they are not split apart. Header and footer text
	 * is also supported, so instructions and word boxes can be used. However, XML must be in test>section>question
	 * format.
	 *
	 * @param template name of xml template to generate test from
	 * @param testNumber student ID
	 * @param templateDirectory directory where XML template is located
	 * @param outDirectory directory to put output
	 * @param formOutDirectory directory to put student entering forms
	 * @param keyOutDirectory directory to put answer keys
	 */
	public static void genSectionsPDFTestFromXML(String template, int testNumber, String templateDirectory, String outDirectory, String formOutDirectory, String keyOutDirectory) {

		makeDirectory(templateDirectory);
		makeDirectory(outDirectory);
		makeDirectory(formOutDirectory);
		makeDirectory(keyOutDirectory);

		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, Font.NORMAL, new CMYKColor(0, 0, 0, 255));
		Font sectionFont = FontFactory.getFont(FontFactory.COURIER, 18, Font.NORMAL, new CMYKColor(0, 0, 0, 255));
		Font defaultFont = FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL, new CMYKColor(0, 0, 0, 255));
		try {
			PdfWriter docwriter = PdfWriter.getInstance(document, new FileOutputStream(outDirectory+testNumber+".pdf"));
			document.open();

			File inputFile = new File(templateDirectory+template);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			String title = doc.getDocumentElement().getAttribute("title");
			Paragraph titlepg = new Paragraph(title,titleFont);
			titlepg.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			titlepg.setSpacingAfter(12);
			document.add(titlepg);


			Node docNode = doc.getDocumentElement();
			//System.out.println("\nCurrent Element :" + nNode.getNodeName());

			Element docElement = (Element) docNode;
			try {
				String header = docElement.getElementsByTagName("head")
						.item(0).getTextContent();


				Paragraph headdata = new Paragraph(header.trim(), sectionFont);
				headdata.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
				titlepg.setSpacingAfter(12);
				document.add(headdata);
			} catch (Exception e) {

			}

			//document.add(new Paragraph("\n"));

			PrintWriter keyWriter = new PrintWriter(keyOutDirectory+"key"+testNumber+".txt", "UTF-8");



			//System.out.println("Root element :"
			//		+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("section");
			ArrayList<Integer> newOrder = new ArrayList<Integer>();
			for (int i =0; i < nList.getLength(); i++) {
				newOrder.add(i);
			}
			// shuffling the sections
			Collections.shuffle(newOrder, new Random());

			int problemNumber = 1;
			// in case want newline at front
			// writer.println();
			int sectionNumber = 1;
			for (int temp2 : newOrder) {
				// each section

				Node sNode = nList.item(temp2);
				Element sElement = (Element) sNode;

				try {
					String header = sElement.getElementsByTagName("head")
							.item(0).getTextContent();

					Paragraph headdata = new Paragraph(header.trim(), sectionFont);
					headdata.setSpacingAfter(12);
					headdata.setSpacingBefore(30);
					headdata.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					document.add(headdata);
				} catch (Exception e) {

				}

				NodeList qList = sElement.getElementsByTagName("problem");
				ArrayList<Integer> qOrder = new ArrayList<Integer>();
				for (int i =0; i < qList.getLength(); i++) {
					qOrder.add(i);
				}
				// shuffling the sections
				Collections.shuffle(qOrder, new Random());


				for (int temp : qOrder) {
					// each problem
					ArrayList<String> out = new ArrayList<String>();

					Node nNode = qList.item(temp);
					//System.out.println("\nCurrent Element :" + nNode.getNodeName());

					Element eElement = (Element) nNode;
					String question = eElement.getElementsByTagName("question")
							.item(0).getTextContent();
					String correctAnswer = eElement
							.getElementsByTagName("correctanswer").item(0)
							.getTextContent();

					String keyNumber = eElement
							.getElementsByTagName("problemnumber").item(0)
							.getTextContent();

					String type = eElement.getAttribute("type");

					// this part is for multiple choice questions
					if (type.equals("multiplechoice")) {
						out.add(correctAnswer);
						NodeList answers = eElement.getElementsByTagName("choice");
						for (int temp3 = 0; temp3 < answers.getLength(); temp3++) {
							String choice = answers.item(temp3).getTextContent();
							out.add(choice);
							//System.out.println("other answer: " + choice);
						}

						Collections.shuffle(out, new Random());
						//System.out.println(out);
						int key = out.indexOf(correctAnswer);
						//System.out.println(key);
						Paragraph currentQuestion = new Paragraph(problemNumber + ". " + question + "\n", defaultFont);
						currentQuestion.setKeepTogether(true);
						currentQuestion.setSpacingAfter(12);
						//currentQuestion.setIndentationLeft(20);
						//currentQuestion.setFirstLineIndent(0);

						keyWriter.println("mc`" + keyNumber + "`" + key);
						for (int i = 0; i < out.size(); i++) {
							currentQuestion.add("   " + "abcdefghijklmnopqrstuvwxyz".charAt(i) + ") " + out.get(i) + "\n");
						}
						document.add(currentQuestion);

						// end mc questions
					} else if (type.equals("shortanswer")) {
						Paragraph currentQuestion = new Paragraph(problemNumber + ". " + question + "\n", defaultFont);
						currentQuestion.setKeepTogether(true);
						//currentQuestion.setIndentationLeft(20);
						//currentQuestion.setFirstLineIndent(0);
						currentQuestion.add("   Ans: _____________");
						currentQuestion.setSpacingAfter(10);
						document.add(currentQuestion);

						keyWriter.println("sa`" + keyNumber + "`" + correctAnswer);
					}

					//document.add(new Paragraph("\n"));

				/* text
				else if (type.equals("text")) {
					writer.println(question);
					problemNumber -= 1;
				} */
					problemNumber += 1;
				}

				try {
					String foot = sElement.getElementsByTagName("foot")
							.item(0).getTextContent();

					//Paragraph footer = new Paragraph("Made with <3 just for Student ID #"+testNumber,defaultFont);
					Paragraph footer = new Paragraph(foot.trim(), sectionFont);
					footer.setSpacingBefore(12);
					footer.setSpacingAfter(30);
					footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
					document.add(footer);
				} catch (Exception e) {

				}

			}

			try {
				String foot = docElement.getElementsByTagName("foot")
						.item(0).getTextContent();

				//Paragraph footer = new Paragraph("Made with <3 just for Student ID #"+testNumber,defaultFont);
				Paragraph footer = new Paragraph(foot.trim(), sectionFont);
				footer.setSpacingBefore(12);
				footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
				document.add(footer);
			} catch (Exception e) {

			}

			generateStudentForm(testNumber,title,problemNumber-1,formOutDirectory);

			keyWriter.close();

			document.close();
			docwriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//TODO make new generator that makes test>section>question format xmls instead of test>question

	/**
	 * Generates an XML file from a plaintext test template
	 *
	 * @param template the test template file containing test questions
	 * @param XMLName the output xml filename
     */
	public static void genXMLFromTemplate(String template, String XMLName) {
		genXMLFromTemplate(template,XMLName,"","");
	}

	/**
	 * Generates an XML file from a plaintext test template
	 *
	 * @param template name of test file
	 * @param XMLName name of xml file
	 * @param templateDir directory where test is
     * @param XMLDir directory to put xml
     */
	public static void genXMLFromTemplate(String template, String XMLName, String templateDir, String XMLDir) {

		makeDirectory(templateDir);
		makeDirectory(XMLDir);

		try {
			File inputFile = new File(templateDir+template);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("test");

			// make title string based on filename (have to parse it)
			Attr attr = doc.createAttribute("title");
			String regex = "([a-z])([A-Z]+)";
			String replacement = "$1_$2";

			String titlestr = template.replaceAll(regex, replacement);
			titlestr = titlestr.replace("_"," ");
			if (titlestr.endsWith(".txt")) {
				titlestr = titlestr.substring(0, titlestr.length() - 4);
			}
			titlestr = titlestr.substring(0,1).toUpperCase()+titlestr.substring(1);
			attr.setValue(titlestr.trim());
			rootElement.setAttributeNode(attr);

			doc.appendChild(rootElement);

			// add questions
			String text;
			Element question;
			text = reader.readLine();
			Integer number = 1;
			while ((text) != null) {
				if (text != null && text.equals("")) {
					// multiple choice question
					question = doc.createElement("problem");
					rootElement.appendChild(question);

					attr = doc.createAttribute("type");
					attr.setValue("multiplechoice");
					question.setAttributeNode(attr);

					// question
					text = reader.readLine();
					//System.out.println("question: "+text);
					Element qdata = doc.createElement("question");
					qdata.appendChild(doc.createTextNode(text));
					question.appendChild(qdata);

					// correct answer
					text = reader.readLine();
					//System.out.println("correct answer: "+text);
					Element correctAnswer = doc.createElement("correctanswer");
					correctAnswer.appendChild(doc.createTextNode(text));
					question.appendChild(correctAnswer);

					// wrong answers
					while ((text = reader.readLine()) != null && !text.equals("") && !text.equals("_s")) {
						//System.out.println(text);
						Element choice = doc.createElement("choice");
						choice.appendChild(doc.createTextNode(text));
						question.appendChild(choice);
					}
					Element qnum = doc.createElement("problemnumber");
					qnum.appendChild(doc.createTextNode(number.toString()));
					question.appendChild(qnum);
					number += 1;
				} else if (text != null && text.equals("_s")) {
					// short answer
					question = doc.createElement("problem");
					rootElement.appendChild(question);

					attr = doc.createAttribute("type");
					attr.setValue("shortanswer");
					question.setAttributeNode(attr);

					// question
					text = reader.readLine();
					//System.out.println("question: "+text);
					Element qdata = doc.createElement("question");
					qdata.appendChild(doc.createTextNode(text));
					question.appendChild(qdata);

					// correct answer
					text = reader.readLine();
					//System.out.println("correct answer: "+text);
					Element correctAnswer = doc.createElement("correctanswer");
					correctAnswer.appendChild(doc.createTextNode(text));
					question.appendChild(correctAnswer);

					Element qnum = doc.createElement("problemnumber");
					qnum.appendChild(doc.createTextNode(number.toString()));
					question.appendChild(qnum);
					number += 1;
				}
				/* text
				else if (text != null && text.equals("_t")) {
					//text block
					question = doc.createElement("problem");
					rootElement.appendChild(question);

					Attr attr = doc.createAttribute("type");
					attr.setValue("text");
					question.setAttributeNode(attr);



					// question
					String s = "";

					while (!(text = reader.readLine()).equals("end_t")) {
						s += text + "\n";
					}
					text = reader.readLine();
					System.out.println("text: "+text);
					Element qdata = doc.createElement("question");
					qdata.appendChild(doc.createTextNode(text));
					question.appendChild(qdata);

					Element correctAnswer = doc.createElement("correctanswer");
					correctAnswer.appendChild(doc.createTextNode("none"));
					question.appendChild(correctAnswer);

					Element qnum = doc.createElement("problemnumber");
					qnum.appendChild(doc.createTextNode("0"));
					question.appendChild(qnum);


				}
				*/
				else {
					text = reader.readLine();
				}

			}

			reader.close();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(XMLDir+XMLName));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);



		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Grades a plaintext test, given a key. Outputs to "grades.csv".
	 *
	 * @param key the answer key file.
	 * @param toGrade the student's answer file.
	 *
	 * @deprecated use {@link #gradeForm(String, String)} instead.
	 */
	@Deprecated
	public static void gradeTest(String key, String toGrade) {
		gradeTest(key, toGrade, "grades.csv");
	}

	/**
	 * Grades a plaintext test, given a key. Outputs to the given CSV file.
	 *
	 * @param key the answer key file.
	 * @param toGrade the student's answer file.
	 * @param outFile the csv to output files to
	 *
	 * @deprecated use {@link #gradeForm(String, String)} instead.
	 */
	@Deprecated
	public static void gradeTest(String key, String toGrade, String outFile) {
		/**
		 * grades test
		 */
		try {
			File inputFile = new File(toGrade);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			File keyFile = new File(key);
			BufferedReader keyReader = new BufferedReader(new FileReader(keyFile));

			String text, keyText;
			ArrayList<Integer> wrongAnswers = new ArrayList<Integer>();
			int numOfProbs = 0;
			int numCorrect = 0;
			while ((text = reader.readLine()) != null && (keyText = keyReader.readLine()) != null) {
				String problemType = keyText.split("`")[0];
				String keyNumber = keyText.split("`")[1];
				String correctAnswer = keyText.split("`")[2];
				numOfProbs += 1;
				if (problemType.equals("mc")) {
					if ("abcdefghijklmnopqrstuvwxyz".indexOf(text.toLowerCase()) == Integer.parseInt(correctAnswer)) {
						numCorrect += 1;
					} else {
						wrongAnswers.add(Integer.parseInt(keyNumber));
					}
				} else if (problemType.equals("sa")) {
					if (text.toLowerCase().trim().equals(correctAnswer.toLowerCase().trim())) {
						numCorrect += 1;
					} else {
						wrongAnswers.add(Integer.parseInt(keyNumber));
					}
				}
			}

			while ((keyText = keyReader.readLine()) != null) {
				String keyNumber = keyText.split(" ")[0];
				numOfProbs += 1;
				wrongAnswers.add(Integer.parseInt(keyNumber));
			}
			
			reader.close();
			keyReader.close();

			//System.out.println("they got "+numCorrect+" correct out of "+numOfProbs);
			//System.out.println("they got "+wrongAnswers+" wrong.");
			String csvString = "";
			csvString += toGrade+","+numCorrect+","+Math.round(10000.0*numCorrect/numOfProbs)/100.0+",";
			for (int i=1;i<numOfProbs+1; i++) {
				csvString += (wrongAnswers.contains(i)?"X":"-")+",";
			}
			
			File f = new File(outFile);
			if(!f.exists()) { 
				PrintWriter writer = new PrintWriter(outFile, "UTF-8");
				String generated = "File Name,# Correct (Out of "+((Integer)numOfProbs).toString()+"),Percentage,";
				for (Integer i=1; i <= numOfProbs; i++) {
					generated += i.toString() + ",";
				}
				writer.write(generated+"\n\n");
				writer.close();
			}
			
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(
					new File(outFile),
					true));
			writer.append(csvString+"\n");
			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Grades a generated form, where key is given as either filename or in StudentID field. Outputs to the given CSV file.
	 *
	 * @param toGrade the student's test to grade. Assumes the key file is of format "key"+studentID+".txt".
	 * @param outFile the output CSV to display grades.
     */
	public static void gradeForm(String toGrade, String outFile) {
		gradeForm(toGrade,outFile,"","","");
	}

	/**
	 * grades a student's test.
	 *
	 * @param toGrade student test filename
	 * @param outFile output csv for grade data
	 * @param studentDir directory where student test is located
	 * @param keyDir directory where answer key is
     * @param CSVDir directory where to put csv
     */
	public static void gradeForm(String toGrade, String outFile, String studentDir, String keyDir, String CSVDir) {

		makeDirectory(studentDir);
		makeDirectory(keyDir);
		makeDirectory(CSVDir);

		try {
			File inputFile = new File(studentDir+toGrade);
			String id = toGrade.replaceAll("[^\\d]","");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			System.out.println(inputFile);
			// parse file
			String title = reader.readLine().replace("Test Title:","").trim();
			String studentID = reader.readLine().replace("Student ID:","").trim();
			if (!studentID.equals(id)) {
				System.out.println("Warning: student ID ("+studentID+") does not match filename ("+id+")!");
			}
			String studentName = reader.readLine().replace("Name (Last, First):","").trim().replace(", ","_").replace(",","_");
			String period = reader.readLine().replace("Period:","").trim();
			// skip insignificant lines
			while (!reader.readLine().contains("-----------------")) {
				reader.readLine();
			}
			File keyFile = new File(keyDir+"key"+id+".txt");
			BufferedReader keyReader = new BufferedReader(new FileReader(keyFile));

			String text, keyText;
			ArrayList<Integer> wrongAnswers = new ArrayList<Integer>();
			int numOfProbs = 0;
			int numCorrect = 0;
			while ((text = reader.readLine()) != null && (keyText = keyReader.readLine()) != null) {
				// take out the leading question number
				text = text.replaceFirst("[\\d]+\\.","").trim();

				// parse the answer key
				String problemType = keyText.split("`")[0];
				String keyNumber = keyText.split("`")[1];
				String correctAnswer = keyText.split("`")[2];
				numOfProbs += 1;
				if (problemType.equals("mc")) {
					if ("abcdefghijklmnopqrstuvwxyz".indexOf(text.toLowerCase()) == Integer.parseInt(correctAnswer)) {
						numCorrect += 1;
					} else {
						wrongAnswers.add(Integer.parseInt(keyNumber));
					}
				} else if (problemType.equals("sa")) {
					if (text.toLowerCase().trim().equals(correctAnswer.toLowerCase().trim())) {
						numCorrect += 1;
					} else {
						wrongAnswers.add(Integer.parseInt(keyNumber));
					}
				}
			}

			while ((keyText = keyReader.readLine()) != null) {
				String keyNumber = keyText.split(" ")[0];
				numOfProbs += 1;
				wrongAnswers.add(Integer.parseInt(keyNumber));
			}

			reader.close();
			keyReader.close();

			//System.out.println("they got "+numCorrect+" correct out of "+numOfProbs);
			//System.out.println("they got "+wrongAnswers+" wrong.");
			String csvString = "";
			csvString += title+","+id+","+studentName+","+period+","+numCorrect+","+Math.round(10000.0*numCorrect/numOfProbs)/100.0+",";
			for (int i=1;i<numOfProbs+1; i++) {
				csvString += (wrongAnswers.contains(i)?"X":"-")+",";
			}

			File f = new File(CSVDir+outFile);
			if(!f.exists()) {
				PrintWriter writer = new PrintWriter(CSVDir+outFile, "UTF-8");
				String generated = "Test Title,File Name,Student Name,Period,# Correct (Out of "+((Integer)numOfProbs).toString()+"),Percentage,";
				for (Integer i=1; i <= numOfProbs; i++) {
					generated += i.toString() + ",";
				}
				writer.write(generated+"\n\n");
				writer.close();
			}


			PrintWriter writer = new PrintWriter(new FileOutputStream(
					new File(CSVDir+outFile),
					true));
			writer.append(csvString+"\n");
			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * grades a student's test. shows what wrong answer they put.
	 *
	 * @param toGrade student test filename
	 * @param outFile output csv for grade data
	 * @param studentDir directory where student test is located
	 * @param keyDir directory where answer key is
	 * @param CSVDir directory where to put csv
	 */
	public static void gradeForm2(String toGrade, String outFile, String studentDir, String keyDir, String CSVDir) {

		makeDirectory(studentDir);
		makeDirectory(keyDir);
		makeDirectory(CSVDir);

		try {
			File inputFile = new File(studentDir+toGrade);
			String id = toGrade.replaceAll("[^\\d]","");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			// parse file
			String title = reader.readLine().replace("Test Title:","").trim();
			String studentID = reader.readLine().replace("Student ID:","").trim();
			if (!studentID.equals(id)) {
				System.out.println("Warning: student ID ("+studentID+") does not match filename ("+id+")!");
			}
			String studentName = reader.readLine().replace("Name (Last, First):","").trim();
			String period = reader.readLine().replace("Period:","").trim();
			// skip insignificant lines
			while (!reader.readLine().contains("-----------------")) {
				reader.readLine();
			}
			File keyFile = new File(keyDir+"key"+id+".txt");
			BufferedReader keyReader = new BufferedReader(new FileReader(keyFile));

			String text, keyText;
			ArrayList<Integer> wrongAnswers = new ArrayList<Integer>();
			HashMap<Integer,String> theirAnswers = new HashMap<Integer, String>();
			int numOfProbs = 0;
			int numCorrect = 0;
			while ((text = reader.readLine()) != null && (keyText = keyReader.readLine()) != null) {
				// take out the leading question number
				text = text.replaceFirst("[\\d]+\\.","").trim();

				// parse the answer key
				String problemType = keyText.split("`")[0];
				String keyNumber = keyText.split("`")[1];
				String correctAnswer = keyText.split("`")[2];
				numOfProbs += 1;
				if (problemType.equals("mc")) {
					if ("abcdefghijklmnopqrstuvwxyz".indexOf(text.toLowerCase()) == Integer.parseInt(correctAnswer)) {
						numCorrect += 1;
					} else {
						wrongAnswers.add(Integer.parseInt(keyNumber));
						theirAnswers.put(Integer.parseInt(keyNumber),"X");
					}
				} else if (problemType.equals("sa")) {
					if (text.toLowerCase().trim().equals(correctAnswer.toLowerCase().trim())) {
						numCorrect += 1;
					} else {
						wrongAnswers.add(Integer.parseInt(keyNumber));
						theirAnswers.put(Integer.parseInt(keyNumber),text.toLowerCase().trim());
					}
				}
			}

			while ((keyText = keyReader.readLine()) != null) {
				String keyNumber = keyText.split(" ")[0];
				numOfProbs += 1;
				wrongAnswers.add(Integer.parseInt(keyNumber));
			}

			reader.close();
			keyReader.close();

			//System.out.println("they got "+numCorrect+" correct out of "+numOfProbs);
			//System.out.println("they got "+wrongAnswers+" wrong.");
			String csvString = "";
			csvString += title+","+id+","+studentName+","+period+","+numCorrect+","+Math.round(10000.0*numCorrect/numOfProbs)/100.0+",";
			for (int i=1;i<numOfProbs+1; i++) {
				csvString += (wrongAnswers.contains(i)?theirAnswers.get(i):"-")+",";
			}

			File f = new File(CSVDir+outFile);
			if(!f.exists()) {
				PrintWriter writer = new PrintWriter(CSVDir+outFile, "UTF-8");
				String generated = "Test Title,File Name,Student Name,Period,# Correct (Out of "+((Integer)numOfProbs).toString()+"),Percentage,";
				for (Integer i=1; i <= numOfProbs; i++) {
					generated += i.toString() + ",";
				}
				writer.write(generated+"\n\n");
				writer.close();
			}


			PrintWriter writer = new PrintWriter(new FileOutputStream(
					new File(CSVDir+outFile),
					true));
			writer.append(csvString+"\n");
			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate a plaintext test from xml. Use when space is an issue, but otherwise the PDF looks nicer.
	 *
	 * @param template xml template for test
	 * @param testNumber student id
     */
	public static void genTestFromXML(String template, int testNumber) {
		try {
			File inputFile = new File(template);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();



			PrintWriter writer = new PrintWriter("test"+testNumber+".txt", "UTF-8");
			PrintWriter keyWriter = new PrintWriter("key"+testNumber+".txt", "UTF-8");



			//System.out.println("Root element :"
			//		+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("problem");
			//System.out.println("----------------------------");
			ArrayList<Integer> newOrder = new ArrayList<Integer>();
			for (int i =0; i < nList.getLength(); i++) {
				newOrder.add(i);
			}
			Collections.shuffle(newOrder, new Random());

			int problemNumber = 1;
			// in case want newline at front
			// writer.println();
			for (int temp : newOrder) {
				// each problem
				ArrayList<String> out = new ArrayList<String>();

				Node nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());

				Element eElement = (Element) nNode;
				String question = eElement.getElementsByTagName("question")
						.item(0).getTextContent();
				String correctAnswer = eElement
						.getElementsByTagName("correctanswer").item(0)
						.getTextContent();
				
				String keyNumber = eElement
						.getElementsByTagName("problemnumber").item(0)
						.getTextContent();
				
				String type = eElement.getAttribute("type");

				// this part is for multiple choice questions
				if (type.equals("multiplechoice")) {
					out.add(correctAnswer);
					NodeList answers = eElement.getElementsByTagName("choice");
					for (int temp2 = 0; temp2 < answers.getLength(); temp2++) {
						String choice = answers.item(temp2).getTextContent();
						out.add(choice);
						//System.out.println("other answer: " + choice);
					}
	
					Collections.shuffle(out, new Random());
					//System.out.println(out);
					int key = out.indexOf(correctAnswer);
					//System.out.println(key);
	
	
					writer.println(problemNumber + ". " + question);
					keyWriter.println("mc`"+keyNumber + "`" + key);
					for (int i = 0; i < out.size(); i++) {
						writer.println("abcdefghijklmnopqrstuvwxyz".charAt(i)+") "+out.get(i));
					}
	
					// end mc questions
				} else if (type.equals("shortanswer")) {
					writer.println(problemNumber + ". " + question);
					writer.println("Ans: _____________");
					keyWriter.println("sa`"+keyNumber + "`" + correctAnswer);
				}

				/* text
				else if (type.equals("text")) {
					writer.println(question);
					problemNumber -= 1;
				} */
				writer.println();
				problemNumber += 1;

			}

			writer.close();
			keyWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates n tests given a template file, and a temporary xml file.
	 *
	 * @param template template test file
	 * @param howMany generate this many tests
	 *
	 * @deprecated don't use this
     */
	@Deprecated
	public static void generateNTests(String template, int howMany) {
		generateNTests(template,howMany,"temp.xml");
	}

	/**
	 * Generates n tests given a template file, and makes a xml file with given name.
	 *
	 * @param template template test file
	 * @param howMany generate this many tests
	 * @param templateName xml file to generate to
	 *
	 *  @deprecated don't use this
     */
	public static void generateNTests(String template, int howMany, String templateName) {
		genXMLFromTemplate(template, templateName);
		for (int i=0; i < howMany; i++) {
			genPDFTestFromXML(templateName,i);
		}
	}

	/**
	 * Generates a form for students.
	 *
	 * @param studentID the student id
	 * @param testName the name of the test
	 * @param numOfProbs the number of problems in the test
	 * @param outDir directory to put student forms
     */
	public static void generateStudentForm(int studentID, String testName, int numOfProbs, String outDir) {

		makeDirectory(outDir);

		try {
			PrintWriter writer = new PrintWriter(outDir+studentID+".txt", "UTF-8");
			writer.println("Test Title: "+testName);
			writer.println("Student ID: "+studentID);
			writer.println("Name (Last, First): ");
			writer.println("Period: ");
			writer.println();
			writer.println("Instructions: For multiple choice questions, put the letter of the choice (i.e. \"a\" or \"c\").");
			writer.println("For short answer questions, put the string answer (i.e. \"answer\" or \"hello\").");
			writer.println("Don't forget to fill out your name and period!");
			writer.println("-----------------");
			for (int i=1; i <= numOfProbs; i++) {
				writer.println(i+". ");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * generates tests for all student ids
	 *
	 * @param template name of plaintext test template
	 * @param templateDir directory where test template is
	 * @param ids arraylist of student ids
     */
	public static void generatePDFTests(String template, String templateDir, ArrayList<Integer> ids) {
		genXMLFromTemplate(template,"temp.xml",templateDir,"temp/");
		for (int id: ids) {
			genPDFTestFromXML("temp.xml", id, "temp/", "tests/"+id+"/", "tests/"+id+"/", "keys/");
		}
	}

	/**
	 * generate tests for student ids with more customizability
	 *
	 * @param template name of plaintext test template
	 * @param templateDir directory where test template is
	 * @param outKeyDir directory where to put answer key
	 * @param outTestDir directory where to put test + student form
     * @param ids arraylist of student ids
     */
	public static void generatePDFTests(String template, String templateDir, String outKeyDir, String outTestDir, ArrayList<Integer> ids) {
		genXMLFromTemplate(template,"temp.xml",templateDir,"temp/");
		for (int id: ids) {
			genPDFTestFromXML("temp.xml", id, "temp/", outTestDir, outTestDir, outKeyDir);
		}
	}

	/**
	 * extracts student ids from file
	 *
	 * @param filename file with student ids
	 * @return an arraylist of student ids
     */
	public static ArrayList<Integer> getIDs(String filename) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		try {
			File inputFile = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String text;
			while ((text = reader.readLine()) != null) {
				out.add(Integer.parseInt(text));
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * easy generation of student tests given test template
	 */
	public static void easyGenerate() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Where is your test file (directory)? Empty string for current dir. ");
		String dir = sc.nextLine();
		System.out.println("What is your test file called? Should be like 'Sample_Test.txt'. ");
		String fn = sc.nextLine();
		System.out.println("Where are the student ids located? ");
		String idFile = sc.nextLine();
		generatePDFTests(fn, dir, getIDs(idFile));
	}

	/**
	 * easy grading of tests
	 */
	public static void easyGrader() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Where are the student handin tests? (directory)");
		String directory = sc.nextLine();

		try {
			File dir = new File(directory);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					System.out.println(child.getName());
					gradeForm(child.getName(), "grades.csv", directory, "keys/", "grades/");
				}
				sortCSV("grades.csv","grades/");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Sorts a csv based on test title first, then by student name.
	 *
	 * @param csvFile filename
	 * @param csvPath path to csv
     */
	public static void sortCSV(String csvFile, String csvPath) {
		try {
			File inputFile = new File(csvPath+csvFile);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			List<ArrayList<String>> csvLines = new ArrayList<ArrayList<String>>();
			String text;
			// ignore first 2 lines
			String line1 = reader.readLine();
			String line2 = reader.readLine();
			// student data
			while ((text = reader.readLine()) != null) {
				ArrayList<String> line = new ArrayList<String>(Arrays.asList(text.split(",")));
				csvLines.add(line);
			}
			Comparator<ArrayList<String>> comp = new Comparator<ArrayList<String>>() {
				public int compare(ArrayList<String> csvLine1, ArrayList<String> csvLine2) {
					// example is for numeric field 2
					return (csvLine1.get(0).toLowerCase()+csvLine1.get(2).toLowerCase()).compareTo(
							(csvLine2.get(0).toLowerCase()+csvLine2.get(2).toLowerCase()));
				}
			};
			Collections.sort(csvLines, comp);
			reader.close();
			PrintWriter writer = new PrintWriter(csvPath+csvFile, "UTF-8");
			writer.println(line1);
			writer.println(line2);
			for (ArrayList<String> s : csvLines) {
				String joined = StringUtils.join(s,",");
				writer.println(joined);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Interactive test generator.
	 */
	public static void interactiveTestGen() {
		try {
			Scanner sc = new Scanner(System.in);
			/*
			System.out.println("------------------------------");
			System.out.println("| Interactive Test Generator |");
			System.out.println("------------------------------");
			System.out.println("Warning: if you make a typo, you cannot undo.");
			System.out.println("However, you can edit the generated file manually.");
			System.out.println();
			*/
			System.out.println("Title: ");
			String title = sc.nextLine();
			PrintWriter writer = new PrintWriter(title.replace(" ","_")+".txt", "UTF-8");
			String problemType;
			do {
				System.out.println("Problem type? m for multiple choice, s for short answer, or q to quit: ");
				problemType = sc.nextLine();

				if (problemType.equals("m")) {
					// multiple choice
					// header for multiple choice questions
					System.out.println("Question: ");
					String question = sc.nextLine();
					writer.println();
					writer.println(question);
					System.out.println("Correct Answer: ");
					String ca = sc.nextLine();
					writer.println(ca);
					String answerChoice;
					do {
						System.out.println("Other choice (empty string to continue): ");
						answerChoice = sc.nextLine();
						if (!answerChoice.equals(""))
							writer.println(answerChoice);
					} while (!answerChoice.trim().equals(""));
				}
				else if (problemType.equals("s")) {
					System.out.println("Question: ");
					String question = sc.nextLine();
					writer.println("_s");
					writer.println(question);
					System.out.println("Correct Answer: ");
					String ca = sc.nextLine();
					writer.println(ca);
				}
				else if (problemType.equals("q")) {
					break;
				}

			} while (!problemType.equals("q"));
			writer.close();
			System.out.println("Your generated test is located at: " + title.replace(" ","_")+".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param args: arguments.
	 *            make: interactively generate a test.
	 *            generate: generate tests for students, given the template and student id list. Will make an intermediate XML file.
	 *            grade: grades the tests, given a student answers directory and a key directory.
     */
	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("make")) {
				interactiveTestGen();
			}
			else if (args[0].equals("generate")) {
				// generate test
				// needs list of student ids
				easyGenerate();
			}
			else if (args[0].equals("grade")) {
				// grade tests
				easyGrader();
			}
		}
		//easyGenerate();
		easyGrader();
		//sortCSV("goodformat.csv","");
		//genSectionsPDFTestFromXML("sections.xml",697089,"","","","");
		//gradeForm("697089.txt","outform.csv");
		//easyGenerate();
		//interactiveTestGen();
		//genXMLFromTemplate("out.txt","temp2.xml");
		//genPDFTestFromXML("temp2.xml",123987);
		//gradeForm2("123987.txt","goodformat.csv","","","");
		//gradeForm2("327672.txt","goodformat.csv","","","");
		//gradeTest("key5.txt", "answerpdf5.txt");
		//genXMLFromTemplate("Super_Test.txt","temp.xml");
		//genPDFTestFromXML("temp.xml",123456);
		//genPDFTestFromXML("temp.xml",234568,"","out2/","out2/","keys2/");
		//gradeForm("234567.txt","out2.csv","out1/","keys/","newcsv/");
		//gradeForm("327672.txt","out.csv","","","newcsv/");
		//generateNTests("out.txt", 3);
		//gradeTest("key0.txt", "studentanswers.txt","krust.csv");
		/*
		genXMLFromTemplate("out.txt", "test_template_2.txt");
		for (int i = 0; i < 3; i++) {

			genTestFromXML("test_template_2.txt", i);
		}*/
		/*

		gradeTest("key2.txt","rohitisdagr8st.txt", "test.csv");
		gradeTest("key1.txt","andyisbad.txt", "test.csv");
		gradeTest("key0.txt","answer0.txt", "test.csv");
		*/

	}

}
