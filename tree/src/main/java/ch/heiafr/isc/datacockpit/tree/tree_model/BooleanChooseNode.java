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
package ch.heiafr.isc.datacockpit.tree.tree_model;

import java.util.Map;


public class BooleanChooseNode extends AbstractParameterChooseNode {
	
	private static final long serialVersionUID = 1L;

	public static final String CHECKBOX = "checkbox";
	
	private boolean current;

	public BooleanChooseNode(Class<?> c, Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree)
					throws Exception {
		super(c, containingTree, annotationMap);
		String def = annotationMap.get("ParamName.default_");
		if (def != null) {
			addBoolean(Boolean.parseBoolean(def));		
		}	
		checkConfigured();
	}
		
	private void addBoolean(boolean bol) {
		LeafChooseNode newNode = new LeafChooseNode(bol, this.getContainingTreeModel());
		if (this.getChildCount() == 0 || !this.children.contains(newNode)) {
			this.add(newNode);
		}
	}
	
	public void setValue(boolean b) {
		current = b;
	}

	@Override
	public void actionPerformed(String key) {
		if (key.equals(REMOVE_ALL)) {
			this.removeAllChildren();
		} else if (key == CHECKBOX) {
			current = !current;
		} else {
			addBoolean(current);

		//	this.getContainingTreeModel().getTreeModelUIManager().expandPath(new TreePath(this.getPath()));
		//	if (this.getChildCount() > 0) {
		//		for (Object c : this.children) {
		//			this.getContainingTreeModel().getTreeModelUIManager().expandPath(new TreePath(((AbstractChooseNode) c).getPath()));
		//		}
		//	}
		}
		getContainingTreeModel().reloadTree();
	}

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b)
			throws Exception {
		return new BooleanChooseNode(userObject, annotationMap2, containingTreeModel);
	}



}
