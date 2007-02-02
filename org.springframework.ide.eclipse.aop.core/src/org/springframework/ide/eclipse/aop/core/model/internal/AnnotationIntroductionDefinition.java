/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.ide.eclipse.aop.core.model.internal;

import org.springframework.ide.eclipse.aop.core.model.IAnnotationAopDefinition;
import org.springframework.ide.eclipse.aop.core.model.IIntroductionDefinition;

public class AnnotationIntroductionDefinition
        extends BeanIntroductionDefinition implements IIntroductionDefinition,
        IAnnotationAopDefinition {
    
	private String definingField;
	
    public AnnotationIntroductionDefinition(String interfaceType,
            String typePattern,String defaultImpl, String definingField) {
        super(interfaceType, typePattern, defaultImpl);
        this.definingField = definingField;
    }

	public String getDefiningField() {
		return definingField;
	}
}
