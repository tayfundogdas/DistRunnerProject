package org.td.distrunner.processmodelparser;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ProcessfromXML {

	private static HashMap<String, Process> convertNodeListtoListofCodeActionNode(NodeList nodeList, XPath xPath)
			throws Exception {
		HashMap<String, Process> unOrderedNodes = new HashMap<String, Process>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element) nodeList.item(i);
			String id = node.getAttribute("id");
			String executable = xPath.evaluate("script", node);
			unOrderedNodes.put(id, new Process(id, executable));
		}

		return unOrderedNodes;
	}

	private static HashMap<String, String> getRelationTable(NodeList orderNodeList) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < orderNodeList.getLength(); i++) {
			Element node = (Element) orderNodeList.item(i);
			result.put(node.getAttribute("sourceRef"), node.getAttribute("targetRef"));
		}
		return result;
	}

	private static List<Process> orderNodes(HashMap<String, Process> unOrderedNodes, String startId, String endId,
			NodeList orderNodeList) throws Exception {
		List<Process> orderedNodes = new ArrayList<Process>();
		HashMap<String, String> orderTable = getRelationTable(orderNodeList);

		String currSource = orderTable.get(startId);

		for (int i = 0; i < unOrderedNodes.size(); ++i) {
			Process currNode = unOrderedNodes.get(currSource);
			orderedNodes.add(currNode);
			currSource = orderTable.get(currSource);
		}

		return orderedNodes;
	}

	private static HashMap<String, Process> populateNodes(Document doc, XPath xPath, String query) throws Exception {
		NodeList nodeList = (NodeList) xPath.compile(query).evaluate(doc, XPathConstants.NODESET);
		HashMap<String, Process> unOrderedNodes = convertNodeListtoListofCodeActionNode(nodeList, xPath);
		return unOrderedNodes;
	}

	private static List<Process> populateSubProcesses(Document doc, XPath xPath, String query) throws Exception {
		List<Process> unOrderedSubProcesses = new ArrayList<Process>();

		NodeList nodeList = (NodeList) xPath.compile(query).evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element) nodeList.item(i);

			String id = node.getAttribute("id");
			Process process = new Process(id,
					node.getElementsByTagName("multiInstanceLoopCharacteristics").getLength() > 0);

			NodeList innerNodeList = node.getElementsByTagName("scriptTask");
			HashMap<String, Process> unOrderedNodes = convertNodeListtoListofCodeActionNode(innerNodeList, xPath);

			// NodeList orderNodeList =
			// node.getElementsByTagName("sequenceFlow");
			// process.SubProcesses = orderNodes(unOrderedNodes, "", "",
			// orderNodeList);
			process.SubProcesses = new ArrayList<Process>(unOrderedNodes.values());

			unOrderedSubProcesses.add(process);

		}

		return unOrderedSubProcesses;
	}

	public static Process getFromXml(String xml) throws Exception {
		// Init xml document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		// Init the xpath factory
		XPath xPath = XPathFactory.newInstance().newXPath();

		// read mainprocess id
		NodeList nodeList = (NodeList) xPath.compile("/definitions/process").evaluate(doc, XPathConstants.NODESET);
		String mainId = ((Element) nodeList.item(0)).getAttribute("id");

		Process result = new Process(mainId, false);

		// read starting node id
		nodeList = (NodeList) xPath.compile("/definitions/process/startEvent").evaluate(doc, XPathConstants.NODESET);
		result.StartNodeId = ((Element) nodeList.item(0)).getAttribute("id");

		// read ending node id
		nodeList = (NodeList) xPath.compile("/definitions/process/endEvent").evaluate(doc, XPathConstants.NODESET);
		result.EndNodeId = ((Element) nodeList.item(0)).getAttribute("id");

		// read nodes
		HashMap<String, Process> unOrderedNodes = populateNodes(doc, xPath, "/definitions/process/scriptTask");

		// read sub processes
		List<Process> unOrderedSubProcesses = populateSubProcesses(doc, xPath, "/definitions/process/subProcess");
		for (Process process : unOrderedSubProcesses) {
			unOrderedNodes.put(process.Id, process);
		}

		// order items
		NodeList orderNodeList = (NodeList) xPath.compile("/definitions/process/sequenceFlow").evaluate(doc,
				XPathConstants.NODESET);
		List<Process> orderedNodes = orderNodes(unOrderedNodes, result.StartNodeId, result.EndNodeId, orderNodeList);
		result.SubProcesses = orderedNodes;

		return result;
	}

	public static void main(String[] args) throws Exception {
		String content;
		content = new String(Files.readAllBytes(Paths.get("D:\\StringProcessor.bpmn")));
		Process result = ProcessfromXML.getFromXml(content);
		System.out.println("Process of:" + result.Id + ":IsParallel:" + result.IsParallel);
		for (Process sp : result.SubProcesses) {
			if (sp.isCodeActionNode()) {
				System.out.println("Node:" + sp.Id + ":" + sp.Executable);
			} else {
				System.out.println("Process of:" + sp.Id + ":IsParallel:" + sp.IsParallel);
				for (Process node : sp.SubProcesses) {
					System.out.println(node.Id + ":" + node.Executable);
				}
			}
		}
	}

}
