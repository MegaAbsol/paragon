import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLParser {

	public static void genXMLFromTemplate(String template) {
		try {
			File inputFile = new File(template);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));



			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("test");
			doc.appendChild(rootElement);

			// add questions
			String text;
			Element question;
			text = reader.readLine();
			Integer number = 1;
			while ((text) != null) {
				if (text.equals("")) {
					// multiple choice question
					question = doc.createElement("problem");
					rootElement.appendChild(question);

					// question
					text = reader.readLine();
					System.out.println("question: "+text);
					Element qdata = doc.createElement("question");
					qdata.appendChild(doc.createTextNode(text));
					question.appendChild(qdata);

					// correct answer
					text = reader.readLine();
					System.out.println("correct answer: "+text);
					Element correctAnswer = doc.createElement("correctanswer");
					correctAnswer.appendChild(doc.createTextNode(text));
					question.appendChild(correctAnswer);

					// wrong answers
					while ((text = reader.readLine()) != null && !text.equals("")) {
						System.out.println(text);
						Element choice = doc.createElement("choice");
						choice.appendChild(doc.createTextNode(text));
						question.appendChild(choice);
					}
					Element qnum = doc.createElement("problemnumber");
					qnum.appendChild(doc.createTextNode(number.toString()));
					question.appendChild(qnum);
					number += 1;
				} else {
					text = reader.readLine();
				}

			}

			reader.close();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("test_template_2.txt"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);



		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void gradeTest(String key, String toGrade) {
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
				String keyNumber = keyText.split(" ")[0];
				String correctAnswer = keyText.split(" ")[1];
				numOfProbs += 1;
				if ("abcdefghijklmnopqrstuvwxyz".indexOf(text.toLowerCase()) == Integer.parseInt(correctAnswer)) {
					numCorrect += 1;
				} else {
					wrongAnswers.add(Integer.parseInt(keyNumber));
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
			
			File f = new File("grades.csv");
			if(!f.exists()) { 
				PrintWriter writer = new PrintWriter("grades.csv", "UTF-8");
				String generated = "File Name,# Correct (Out of "+((Integer)numOfProbs).toString()+"),Percentage,";
				for (Integer i=1; i <= numOfProbs; i++) {
					generated += i.toString() + ",";
				}
				writer.write(generated+"\n\n");
				writer.close();
			}
			
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(
					new File("grades.csv"),
					true));
			writer.append(csvString+"\n");
			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
				
				//System.out.println("question: " + question);
				//System.out.println("correct answer: " + correctAnswer);
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
				keyWriter.println(keyNumber + " " + key);
				for (int i = 0; i < out.size(); i++) {
					writer.println("abcdefghijklmnopqrstuvwxyz".charAt(i)+") "+out.get(i));
				}
				writer.println();
				problemNumber += 1;

			}

			writer.close();
			keyWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		/*
		genXMLFromTemplate("out.txt");
		for (int i = 0; i < 3; i++) {

			genTestFromXML("test_template_2.txt", i);
		}*/
		gradeTest("key2.txt","rohitisdagr8st.txt");
		gradeTest("key1.txt","hornpub.txt");
		gradeTest("key0.txt","answer0.txt");

	}

}
