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

	private static HashMap<String, Process> populateNodes(NodeList rawNodes, XPath xPath) throws Exception {
		HashMap<String, Process> unOrderedNodes = new HashMap<String, Process>();
		for (int i = 0; i < rawNodes.getLength(); i++) {
			Element node = (Element) rawNodes.item(i);
			String id = node.getAttribute("id");
			String executable = xPath.evaluate("script", node);
			unOrderedNodes.put(id, new Process(id, executable));
		}

		return unOrderedNodes;
	}

	private static Process populateProcess(Document doc, XPath xPath, Element node) throws Exception {
		Process result = new Process();

		// read main process id
		String mainId = node.getAttribute("id");
		result.Id = mainId;

		// read starting node id
		NodeList nodeList = node.getElementsByTagName("startEvent");
		result.StartNodeId = ((Element) nodeList.item(0)).getAttribute("id");

		// read ending node id
		nodeList = node.getElementsByTagName("endEvent");
		result.EndNodeId = ((Element) nodeList.item(0)).getAttribute("id");
		
		//set main node parallel
		result.IsParallel = false;

		// read nodes
		nodeList = node.getElementsByTagName("scriptTask");
		HashMap<String, Process> unOrderedNodes = populateNodes(nodeList, xPath);

		result.SubProcesses = new ArrayList<Process>();
		// read sub processes
		nodeList = node.getElementsByTagName("subProcess");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element pn = (Element) nodeList.item(i);
			Process innerProcess = populateProcess(doc, xPath, pn);
			innerProcess.IsParallel = pn.getElementsByTagName("multiInstanceLoopCharacteristics").getLength() > 0;
			unOrderedNodes.put(innerProcess.Id, innerProcess);
			result.SubProcesses.add(innerProcess);
		}

		// order items
		NodeList orderNodeList = node.getElementsByTagName("sequenceFlow");
		List<Process> orderedNodes = orderNodes(unOrderedNodes, result.StartNodeId, result.EndNodeId, orderNodeList);
		result.SubProcesses = orderedNodes;

		return result;
	}

	public static Process getFromXml(String xml) throws Exception {
		// Init xml document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		// Init the xpath factory
		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList nodeList = (NodeList) xPath.compile("/definitions/process").evaluate(doc, XPathConstants.NODESET);
		return populateProcess(doc, xPath, ((Element) nodeList.item(0)));
	}

	private static void dumpProcess(Process input) {
		if (input != null) {
			if (input.Executable == null)
				System.out.println(input.Id + ":" + input.IsParallel);
			else
				System.out.println(input.Id + ":" + input.Executable);
			for (Process process : input.SubProcesses) {
				dumpProcess(process);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String content;
		content = new String(Files.readAllBytes(Paths.get("D:\\StringProcessor.bpmn")));
		Process result = ProcessfromXML.getFromXml(content);
		dumpProcess(result);
	}

}
