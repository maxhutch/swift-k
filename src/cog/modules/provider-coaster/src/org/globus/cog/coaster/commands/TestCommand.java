//----------------------------------------------------------------------
//This code is developed as part of the Java CoG Kit project
//The terms of the license can be found at http://www.cogkit.org/license
//This message may not be removed or altered.
//----------------------------------------------------------------------

/*
 * Created on Jul 20, 2005
 */
package org.globus.cog.coaster.commands;

import org.globus.cog.coaster.ProtocolException;



public class TestCommand extends Command {
	private String mode;
	
	public TestCommand() {
		this(true);
	}
	
	public TestCommand(boolean initial) {
		super("TEST");
		if (initial) {
			this.mode = "INITIAL";
		}
		else {
			this.mode = "DONE";
		}
	}

	public void send() throws ProtocolException {
		addOutData(mode);
		super.send();
	}
}