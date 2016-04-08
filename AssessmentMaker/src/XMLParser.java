import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLParser {

	public static void main(String[] args) {

		try {
			File inputFile = new File(
					"H:/Downloads/PortableGit/paragon/AssessmentMaker/test1.txt");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			
			
			
			PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
			
			
			
			
			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("problem");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				// each problem
				ArrayList<String> out = new ArrayList<String>();

				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				Element eElement = (Element) nNode;
				String question = eElement.getElementsByTagName("question")
						.item(0).getTextContent();
				String correctAnswer = eElement
						.getElementsByTagName("correctanswer").item(0)
						.getTextContent();
				System.out.println("question: " + question);
				System.out.println("correct answer: " + correctAnswer);
				out.add(correctAnswer);
				NodeList answers = eElement.getElementsByTagName("choice");
				for (int temp2 = 0; temp2 < answers.getLength(); temp2++) {
					String choice = answers.item(temp2).getTextContent();
					out.add(choice);
					System.out.println("other answer: " + choice);
				}

				Collections.shuffle(out, new Random());
				System.out.println(out);
				int key = out.indexOf(correctAnswer);
				System.out.println(key);
				
				
				writer.println(question);
				for (String i : out)
					writer.println(i);
				writer.println();

			}
			
			writer.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
