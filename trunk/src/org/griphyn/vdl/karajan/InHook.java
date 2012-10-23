/*
 * Copyright 2012 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * Created on Jun 19, 2006
 */
package org.griphyn.vdl.karajan;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class InHook extends InputStream implements Runnable {

	public static final Logger logger = Logger.getLogger(InHook.class);

	public synchronized static void install(Monitor m) {
		if (!(System.in instanceof InHook)) {
			System.setIn(new InHook(System.in, m));
		}
	}

	private BufferedInputStream is;
	private Monitor m;

	private InHook(InputStream is, Monitor m) {
		if (is instanceof BufferedInputStream) {
			this.is = (BufferedInputStream) is;
			this.m = m;
			Thread t = new Thread(this, "Swift console debugger");
			t.setDaemon(true);
			t.start();
		} else {
			logger.error("Attempt to start console debugger with stdin not an instance of BufferedInputStream. InputStream class is "+is.getClass());
		}
	}

	public int read() throws IOException {
		return is.read();
	}

	public void run() {
		logger.debug("Starting console debugger thread");
		while (true) {
			logger.debug("Console debugger outer loop");
			try {
				int c = is.read();
				logger.debug("Command: "+c); // TODO display as char?
				if (c == 'd') {
					m.toggle();
				}
				else if (c == 'v') {
					m.dumpVariables();
				}
				else if (c == 't') {
					m.dumpThreads();
				} else if (c == 10) {
					logger.debug("Ignoring LF");
				} else if (c == -1) {
					logger.debug("End of stdin - exiting debugger");
					return;
				}
				else {
					logger.warn("Unknown console debugger command "+c);
				}
			}
			catch (IOException e) {
				logger.debug("Console debugger encountered IOException",e);
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
