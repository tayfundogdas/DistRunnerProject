package org.td.processmodel;

import java.util.List;

public interface CodeAction {
	
	public List<Byte> Execute(List<Byte> input) throws Exception;
	public Boolean ValidateInput(List<Byte> input) throws Exception;

}
