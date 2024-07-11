/*
 * This file is part of one of the Data-Cockpit libraries.
 * 
 * Copyright (C) 2024 ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE (EPFL)
 * 
 * Author - Sébastien Rumley (sebastien.rumley@hefr.ch)
 * 
 * This open source release is made with the authorization of the EPFL,
 * the institution where the author was originally employed.
 * The author is currently affiliated with the HEIA-FR, which is the actual publisher.
 * 
 * The Data-Cockpit program is free software, you can redistribute it and/or modify
 * it under the terms of GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Data-Cockpit program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * Contributor list -
 */
package ch.heiafr.isc.datacockpit.tree.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ch.heiafr.isc.datacockpit.tree.tree_model.TypableChooseNode;

public class TypableParameterGUIContainer extends ParameterGUIContainer {

	private static final long serialVersionUID = 1L;
	JTextField text = null;	
	
	public TypableParameterGUIContainer(final TypableChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		if (text == null) {
			text = new JTextField("Put your value here", 30);
		}
		text.setFont(defaultFont);
		text.setSize(150, 30);
		text.setPreferredSize(new Dimension(150, lineHeight));
		text.setMaximumSize(new Dimension(150, lineHeight));
		text.setBackground(Color.WHITE);
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {}
		});
		text.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.actionPerformed(TypableChooseNode.ADD);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						text.requestFocusInWindow();
					}
					
				});
			}
		});	
		text.getDocument().addDocumentListener(new DocumentListener() {			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				node.setTextValue(text.getText());
			}			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				node.setTextValue(text.getText());			
			}			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				node.setTextValue(text.getText());
			}
		});
		JButton addButton = getAddButton();
		this.add(text, new Placement(100, 250, false));
		this.add(addButton, new Placement(0, 100, false));	
	}
	
}
