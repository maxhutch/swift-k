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


package org.griphyn.vdl.karajan.lib;

import java.util.List;

import org.apache.log4j.Logger;
import org.globus.cog.karajan.arguments.Arg;
import org.globus.cog.karajan.stack.VariableStack;
import org.globus.cog.karajan.workflow.ExecutionException;
import org.globus.cog.karajan.workflow.futures.FutureFault;
import org.griphyn.vdl.mapping.AbstractDataNode;
import org.griphyn.vdl.mapping.DSHandle;
import org.griphyn.vdl.mapping.MappingParam;
import org.griphyn.vdl.mapping.MappingParamSet;
import org.griphyn.vdl.mapping.Path;
import org.griphyn.vdl.mapping.RootArrayDataNode;
import org.griphyn.vdl.type.Field;
import org.griphyn.vdl.type.Type;

public class CreateArray extends VDLFunction {

	public static final Logger logger = Logger.getLogger(CreateArray.class);

	public static final Arg PA_VALUE = new Arg.Positional("value");
	
	static {
		setArguments(CreateArray.class, new Arg[] { PA_VALUE });
	}

    public Object function(VariableStack stack) throws ExecutionException {
		Object value = PA_VALUE.getValue(stack);
		try {

			if (!(value instanceof List)) {
				throw new RuntimeException(
					"An array variable can only be initialized with a list of values");
			}

			Type type = checkTypes((List<?>) value);
			
			DSHandle handle = new RootArrayDataNode(type.arrayType());
			if (hasMappableFields(type)) {
			    setMapper(handle);
			}

			/*
			 *  The reason this is disabled without provenance is that the identifier
			 *  is essentially a random number plus a counter. It does not help
			 *  in debugging problems.  
			 */
			if (AbstractDataNode.provenance && logger.isInfoEnabled()) {
			    logger.info("CREATEARRAY START array=" + handle.getIdentifier());
			}

			int index = 0;
			for (Object o : (List<?>) value) {
				// TODO check type consistency of elements with
				// the type of the array
				DSHandle n = (DSHandle) o;
				// we know this DSHandle cast will work because we checked
				// it in the previous scan of the array contents
				Path p = Path.EMPTY_PATH.addLast(index, true);
				
				DSHandle dst = handle.getField(p);

				SetFieldValue.deepCopy(dst, n, stack, 1);
				
				if (AbstractDataNode.provenance && logger.isInfoEnabled()) {
				    logger.info("CREATEARRAY MEMBER array=" + handle.getIdentifier() 
				        + " index=" + index + " member=" + n.getIdentifier());
				}
				index++;
			}
			
			handle.closeShallow();
			
			if (AbstractDataNode.provenance && logger.isInfoEnabled()) {
			    logger.info("CREATEARRAY COMPLETED array=" + handle.getIdentifier());
			}

			return handle;
		}
		catch (FutureFault e) {
		    throw e;
		}
		catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

    private void setMapper(DSHandle handle) {
        // slap a concurrent mapper on this
        MappingParamSet params = new MappingParamSet();
        params.set(MappingParam.SWIFT_DESCRIPTOR, "concurrent_mapper");
        params.set(MappingParam.SWIFT_DBGNAME, "arrayexpr");
        handle.init(params);
    }

    private boolean hasMappableFields(Type type) {
        if (type.isPrimitive()) {
            return false;
        }
        else if (!type.isComposite()) {
            return true;
        }
        else if (type.isArray()) {
            return hasMappableFields(type.itemType());
        }
        else {
            // struct
            for (Field f : type.getFields()) {
                if (hasMappableFields(f.getType())) {
                    return true;
                }
            }
            return false;
        }
    }

    private Type checkTypes(List<?> value) {
        Type type = null;
        
        for (Object o : value) {            
            if (o instanceof DSHandle) {
                DSHandle d = (DSHandle)o;
                Type thisType = d.getType();
                if(type == null) {
                    // this first element
                    type = thisType;
                } else {
                    // other elements, when we have a type to expect
                    if(!(type.equals(thisType))) {
                        throw new RuntimeException(
                            "Expecting all array elements to have SwiftScript type " + 
                            type + " but found an element with type "+thisType);
                    }
                }
            }
            else {
                throw new RuntimeException("An array variable can only be initialized by a list of DSHandle values.");
            }
        }
        return type;
    }
}
