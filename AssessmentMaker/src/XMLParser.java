import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
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

			while ((text = reader.readLine()) != null) {
				//if (text.equals("")) {
					question = doc.createElement("problem");
					rootElement.appendChild(question);
					text = reader.readLine();
					System.out.println("question: "+text);
					Element qdata = doc.createElement("question");
					qdata.appendChild(doc.createTextNode(text));
					question.appendChild(qdata);


					text = reader.readLine();

					Element correctAnswer = doc.createElement("correctanswer");
					correctAnswer.appendChild(doc.createTextNode(text));
					question.appendChild(correctAnswer);
					while ((text = reader.readLine()) != null && !text.equals("")) {
						System.out.println(text);
						Element choice = doc.createElement("choice");
						choice.appendChild(doc.createTextNode(text));
						question.appendChild(choice);
					}
				//}

			}

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
				keyWriter.println(key);
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
		genXMLFromTemplate("inputtest.txt");
		for (int i = 0; i < 10; i++) {

			//genTestFromXML("test_template.txt", i);
		}

	}

}
