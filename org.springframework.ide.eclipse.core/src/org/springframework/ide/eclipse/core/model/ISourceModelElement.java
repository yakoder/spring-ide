/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.springframework.ide.eclipse.core.model;

/**
 * Common protocol for all {@link IModelElement}s related to source code.
 * 
 * @author Torsten Juergeleit
 */
public interface ISourceModelElement extends IResourceModelElement {

	/**
	 * Returns the element's source code element.
	 */
	IResourceModelElement getElementSourceElement();

	/**
	 * Returns the element's source code information.
	 */
	IModelSourceLocation getElementSourceLocation();

	/**
	 * Returns the line number with the start of the element's source code.
	 */
	int getElementStartLine();
	
	/**
	 * Returns the line number with the logical end of the element's source
	 * code.
	 */
	int getElementEndLine();
}
