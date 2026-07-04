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
import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import ch.heiafr.isc.datacockpit.tree.tree_model.ConstructorNodeChooserPointer;

public class ConstructorPointerGUIContainer extends ConstructorGUIContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConstructorPointerGUIContainer(final ConstructorNodeChooserPointer node, LayoutManager man, int prefix) {
		super(node.superInstance, man, prefix);
		JLabel lab = null;
		for (int i=0; i< getComponentCount(); ++i) {
			Component current = getComponent(i);
			if (current instanceof JLabel) {
				lab = (JLabel)current;
			}
		}
		if (lab != null) {
			String text = lab.getText();
			String newText = "";
			int index = text.indexOf('>', text.indexOf("font")) +1;
			newText += text.substring(0, index);
			newText += "@ ->" + text.substring(index);
			JLabel newLabel = new JLabel(newText);
			add(newLabel);
			final JCheckBox completeCartesian = new JCheckBox("Complete Cartesian Product");
			completeCartesian.setBackground(Color.WHITE);
			completeCartesian.addActionListener(e -> {
                node.isCartesianEnabled = completeCartesian.isSelected();
                node.getContainingTreeModel().reloadTree();
            });
			add(completeCartesian);
		}
	}

}
