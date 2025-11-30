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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ArrayChooseNode extends AbstractParameterChooseNode {
	
	private static final long serialVersionUID = 1L;
	private Class<?> t;

	public ArrayChooseNode(
			Class<?> c,
			Map<String, String> annotationMap,
			ObjectConstructionTreeModel<?> containingTree,
			boolean checkDef) {
		super(c, containingTree, annotationMap);
		try {
			this.annotationMap = annotationMap;
			parameterType = parameterType.substring(1);
			if (parameterType.length() == 1) {
				switch (parameterType.charAt(0)) {
					case 'I':
						parameterType = "int";
						t = Integer.TYPE;
						break;
					case 'J':
						parameterType = "long";
						t = Long.TYPE;
						break;
					case 'F':
						parameterType = "float";
						t = Float.TYPE;
						break;
					case 'D':
						parameterType = "double";
						t = Double.TYPE;
						break;
					case 'S':
						parameterType = "short";
						t = Short.TYPE;
						break;
					case 'C':
						parameterType = "char";
						t = Character.TYPE;
						break;
					case 'Z':
						parameterType = "boolean";
						t = Boolean.TYPE;
						break;
				}
			} else {
				parameterType = parameterType.substring(1, parameterType.length() - 1);
				t = Class.forName(parameterType);
			}
			addLeaf(checkDef);
			checkConfigured();
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void addLeaf(boolean checkDef) {
		if (isTypableType()) {
			this.add(
					new TypableArrayInstanceNode(
							t,
							annotationMap,
							containingTreeModel)
			);
		} else {
			try {
				this.add(
						new UntypableArrayInstanceNode(
								Class.forName(parameterType), containingTreeModel,
								annotationMap,
								checkDef)
				);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	public String getText() {
		return "Array of " + super.getText();
	}

	@Override
	public List<ActionItem> getActions() {
		List<ActionItem> toReturn = new ArrayList<>();
		ActionItem addNew = new ActionItem("Create new array", "new");
		ActionItem addNull = new ActionItem("Use null", "null");		
		ActionItem remove = new ActionItem("Remove all", ArrayChooseNode.REMOVE_ALL);
		toReturn.add(addNew);
		toReturn.add(addNull);		
		toReturn.add(new SeparatorItem());
		toReturn.add(remove);
		return toReturn;
	}

	@Override
	public void actionPerformed(String key) {
		if (key.equals("new")) {
			try {
				addLeaf(false);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else if (key.equals("null")) {
			add(new LeafChooseNode(null, getContainingTreeModel()));
		} else {
			removeAllChildren();
		}
		getContainingTreeModel().reloadTree();
	}

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject,
			Map<String, String> annotationMap2,
			ObjectConstructionTreeModel<?> containingTreeModel,
			boolean b) {
		return new ArrayChooseNode(userObject, annotationMap2, containingTreeModel, b);
	}
}
