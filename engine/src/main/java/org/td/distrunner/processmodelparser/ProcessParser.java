package org.td.distrunner.processmodelparser;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Node;
import org.td.distrunner.helpers.JarHelper;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.NodeModel;

public class ProcessParser {
	public static NodeModel getProcessTree(String processName) {
		NodeModel result = null;
		try {
			RuleFlowProcess rawProcess = JarHelper.getProcessByName(processName);

			List<Node> starts = rawProcess.getStartNodes();

			if (!starts.isEmpty()) {
				StartNode start = (StartNode) starts.get(0);

				Node currNode = start.getTo().getTo();

				if (currNode != null) {
					result = new NodeModel(new ArrayList<NodeModel>(), false, processName);
					while (!(currNode instanceof EndNode)) {

						if (currNode instanceof ActionNode) {
							ActionNode action = (ActionNode) currNode;
							DroolsConsequenceAction p = (DroolsConsequenceAction) action.getAction();
							action.getName();
							result.SubItems.add(new NodeModel(p.getConsequence(), action.getName().replace(' ', '_')));
							currNode = action.getTo().getTo();
						}

						if (currNode instanceof ForEachNode) {
							ForEachNode feach = (ForEachNode) currNode;
							Node[] subs = feach.getNodes();
							ArrayList<NodeModel> subItems = new ArrayList<NodeModel>();
							ActionNode action = (ActionNode) subs[0];
							DroolsConsequenceAction p = (DroolsConsequenceAction) action.getAction();
							subItems.add(new NodeModel(p.getConsequence(), action.getName().replace(' ', '_')));
							result.SubItems.add(new NodeModel(subItems, true, feach.getName().replace(' ', '_')));
							currNode = feach.getTo().getTo();
						}

					}

				}
			}

		} catch (Exception e) {
			LogHelper.logError(e);
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		NodeModel res = ProcessParser.getProcessTree("org.td.samples.StringProcessor");
		res.toString();
	}

}
