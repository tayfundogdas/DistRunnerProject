package org.td.distrunner.processmodelparser;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ProcessModel;
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

	private static HashMap<String, ProcessModel> populateNodes(NodeList rawNodes, XPath xPath) throws Exception {
		HashMap<String, ProcessModel> unOrderedNodes = new HashMap<String, ProcessModel>();
		for (int i = 0; i < rawNodes.getLength(); i++) {
			Element node = (Element) rawNodes.item(i);
			String id = node.getAttribute("id");
			String executable = xPath.evaluate("script", node);
			unOrderedNodes.put(id, new ProcessModel(id, executable));
		}

		return unOrderedNodes;
	}

	private static ProcessModel populateProcess(Document doc, XPath xPath, String rootPath) throws Exception {
		ProcessModel result = new ProcessModel();
		// read process id
		NodeList nodeList = (NodeList) xPath.compile(rootPath).evaluate(doc, XPathConstants.NODESET);
		Element currentItem = ((Element) nodeList.item(0));
		result.Id = currentItem.getAttribute("id");

		// read starting node id
		nodeList = (NodeList) xPath.compile(rootPath + "/startEvent").evaluate(doc, XPathConstants.NODESET);
		currentItem = ((Element) nodeList.item(0));
		if (currentItem != null)
			result.StartNodeId = currentItem.getAttribute("id");

		// read ending node id
		nodeList = (NodeList) xPath.compile(rootPath + "/endEvent").evaluate(doc, XPathConstants.NODESET);
		currentItem = ((Element) nodeList.item(0));
		if (currentItem != null)
			result.EndNodeId = currentItem.getAttribute("id");

		// read nodes
		nodeList = (NodeList) xPath.compile(rootPath + "/scriptTask").evaluate(doc, XPathConstants.NODESET);
		HashMap<String, ProcessModel> unOrderedNodes = populateNodes(nodeList, xPath);
		result.SubProcesses.addAll(unOrderedNodes.values());

		// read sub processes
		nodeList = (NodeList) xPath.compile(rootPath + "/subProcess").evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element pn = (Element) nodeList.item(i);
			String id = pn.getAttribute("id");
			ProcessModel innerProcess = populateProcess(doc, xPath, rootPath + "/subProcess[@id='" + id + "']");
			unOrderedNodes.put(innerProcess.Id, innerProcess);
			result.SubProcesses.add(innerProcess);
		}

		// items order table
		NodeList orderNodeList = (NodeList) xPath.compile(rootPath + "/sequenceFlow").evaluate(doc,
				XPathConstants.NODESET);
		result.RelationTable = getRelationTable(orderNodeList);

		return result;
	}

	public static ProcessModel getFromXml(String xml) throws Exception {
		// Init xml document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		// Init the xpath factory
		XPath xPath = XPathFactory.newInstance().newXPath();

		return populateProcess(doc, xPath, "/definitions/process");
	}

	public static void main(String[] args) throws Exception {
		// for logging
		LogHelper.setupLog();

		String content;
		content = new String(Files.readAllBytes(Paths.get("D:\\StringProcessor.bpmn")));
		ProcessModel result = ProcessfromXML.getFromXml(content);
		LogHelper.logTrace(result);
	}

}
