/*
 * Swift Parallel Scripting Language (http://swift-lang.org)
 * Code from Java CoG Kit Project (see notice below) with modifications.
 *
 * Copyright 2005-2014 University of Chicago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//----------------------------------------------------------------------
//This code is developed as part of the Java CoG Kit project
//The terms of the license can be found at http://www.cogkit.org/license
//This message may not be removed or altered.
//----------------------------------------------------------------------

/*
 * Created on Jan 28, 2013
 */
package org.griphyn.vdl.mapping;

import k.thr.Yield;

public class OOBYield extends HandleOpenException {
    private final Yield y;
    
    public OOBYield(Yield y, DSHandle h) {
        super(h);
        this.y = y;
    }
    
    public Yield wrapped() {
        return y;
    }
    
    public Yield wrapped(Object traceElement) {
        y.getState().addTraceElement(traceElement);
        return y;
    }
}
