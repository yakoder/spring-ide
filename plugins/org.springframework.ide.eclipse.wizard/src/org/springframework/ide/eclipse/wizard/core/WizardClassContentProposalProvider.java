/*******************************************************************************
 *  Copyright (c) 2012 VMware, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.wizard.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistContext;
import org.springframework.ide.eclipse.config.core.contentassist.providers.ClassContentProposalProvider;
import org.w3c.dom.Document;


/**
 * Wrapper class for ClassContentProposalProvider to work with bean wizard.
 * @author Terry Denney
 * @author Leo Dos Santos
 * @author Christian Dupuis
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class WizardClassContentProposalProvider extends ClassContentProposalProvider {

	private IFile file;

	private Document document;

	public WizardClassContentProposalProvider(IDOMElement node, String attrName, IFile file, Document document) {
		super(node, attrName);
		this.file = file;
		this.document = document;
	}

	public WizardClassContentProposalProvider(IDOMElement node, String attrName, IFile file, Document document,
			boolean isInterfaceRequired) {
		super(node, attrName, isInterfaceRequired);
		this.file = file;
		this.document = document;
	}

	@Override
	protected IContentAssistContext createContentAssistContext(String contents) {
		return new WizardContentAssistContext(getInput(), getAttributeName(), file, contents, document);
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

}
