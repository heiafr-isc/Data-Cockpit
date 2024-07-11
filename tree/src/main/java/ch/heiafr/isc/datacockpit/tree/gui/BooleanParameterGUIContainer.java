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

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;

import ch.heiafr.isc.datacockpit.tree.tree_model.BooleanChooseNode;

public class BooleanParameterGUIContainer extends ParameterGUIContainer {

	private static final long serialVersionUID = 1L;

	public BooleanParameterGUIContainer(final BooleanChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		final JCheckBox choice = new JCheckBox();
		choice.setSelected(false);
		choice.setPreferredSize(new Dimension(60, lineHeight));
		choice.setBackground(this.getBackground());
		choice.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.setValue(choice.isSelected());
			//	node.actionPerformed(BooleanChooseNode.CHECKBOX);
			}
		});
		choice.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
		//		System.out.println("uiehr");
		//		node.setValue(choice.isSelected());
			}
		});
		add(choice, new Placement(100, 130, false));
		add(getAddButton(), new Placement(0, 100, false));		
	}

}
