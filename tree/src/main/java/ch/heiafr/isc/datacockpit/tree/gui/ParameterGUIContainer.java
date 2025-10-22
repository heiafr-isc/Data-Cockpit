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

import javax.swing.JButton;
import javax.swing.JOptionPane;
import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractParameterChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.TypableChooseNode;

public class ParameterGUIContainer extends AbstractGUIContainer {

	private static final String OPEN_FOLDER_ICON = "icons/openfolder.png";
	private static final String CLOSED_FOLDER_ICON = "icons/closedfolder.png";
	private static final String VOID_ICON = "icons/void.png";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Color lightBlue = new Color(0.92f, 0.92f, 0.97f);
	

	public ParameterGUIContainer(final AbstractParameterChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);

		refresh();
		this.setBackground(lightBlue);		
		if (node.isRoot()) {
			JButton save = new JButton("Save");
			ActionListener a = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					node.getContainingTreeModel().saveFile();
				}
			};
			save.setFocusable(false);
			save.addActionListener(a);
			
			JButton saveAs = new JButton("Save as...");
			a = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String s = JOptionPane.showInputDialog("Give a file name", "tree");
					node.getContainingTreeModel().saveFile(s);
				}
			};
			saveAs.setFocusable(false);
			saveAs.addActionListener(a);	
			
			JButton deploy = new JButton("Deploy tree");
			a = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					node.setAllExpanded();
				}
			};
			deploy.setFocusable(false);
			deploy.addActionListener(a);
			
			add(save,new Placement(200, 300, false));
			add(saveAs,new Placement(100, 200, false));
			add(deploy,new Placement(0, 100, false));
		}
		setMaximumSize(new Dimension(3000,lineHeight));
	}
	
	protected JButton getAddButton() {
		JButton add = new JButton("Add");
		add.setPreferredSize(new Dimension(60, 20));
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				absNode.actionPerformed(TypableChooseNode.ADD);
			}
		});
		add.setFocusable(false);
		return add;
	}

	@Override
	public void refreshImpl() {
		if (absNode.getChildCount() > 0) {
			if (absNode.isExpanded()) {
				super.setIcon(OPEN_FOLDER_ICON);
			} else {
				super.setIcon(CLOSED_FOLDER_ICON);
			}
		} else {
			super.setIcon(VOID_ICON);
		}
	}
}
