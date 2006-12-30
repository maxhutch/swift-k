/*
 * Created on Dec 28, 2006
 */
package org.griphyn.vdl.karajan.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.globus.cog.karajan.arguments.Arg;
import org.globus.cog.karajan.stack.VariableStack;
import org.globus.cog.karajan.util.TypeUtil;
import org.globus.cog.karajan.workflow.ExecutionException;
import org.griphyn.vdl.karajan.lib.cache.CacheReturn;
import org.griphyn.vdl.karajan.lib.cache.File;
import org.griphyn.vdl.karajan.lib.cache.VDLFileCache;

public class CacheUnlockFiles extends CacheFunction {
	public static final Arg FILE = new Arg.Positional("files");
	public static final Arg DIR = new Arg.Positional("dir");
	public static final Arg HOST = new Arg.Positional("host");

	static {
		setArguments(CacheUnlockFiles.class, new Arg[] { FILE, DIR, HOST });
	}

	public void partialArgumentsEvaluated(VariableStack stack) throws ExecutionException {
		List pairs = TypeUtil.toList(FILE.getValue(stack));
		String dir = TypeUtil.toString(DIR.getValue(stack));
		Object host = HOST.getValue(stack);
		VDLFileCache cache = getCache(stack);
		List rem = new ArrayList();
		
		Iterator i = pairs.iterator();
		while (i.hasNext()) {
			String file = (String) i.next();
			File f = new File(file, dir, host, 0);
			CacheReturn cr = cache.unlockEntry(f);
			rem.addAll(cr.remove);
		}
		super.partialArgumentsEvaluated(stack);
		stack.setVar(CACHE_FILES_TO_REMOVE, rem);
		startRest(stack);
	}
}
