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

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;

public abstract class AbstractChooseNode extends DefaultMutableTreeNode implements Iterable<Pair<Object, ObjectRecipe>>, Serializable {

	private static final long serialVersionUID = 8955752123757035134L;

	protected transient static final HashMap<ConstructorNodeChooserPointer, ConstructorChooseNode> map = new HashMap<ConstructorNodeChooserPointer, ConstructorChooseNode>();

//	private transient boolean choosen;
	protected transient boolean configured;
	private transient List<AbstractChooseNode> childsRestore;
	protected transient boolean invalid = false;
	private boolean isExpanded = true;

	protected ObjectConstuctionTreeModel containingTreeModel;


	protected AbstractChooseNode(ObjectConstuctionTreeModel containingTreeModel) {
		this.configured = false;
		this.containingTreeModel = containingTreeModel;
	}
	
	public void removeChild(AbstractChooseNode n) {
		for (int i = 0 ; i < children.size() ; i++) {
			if (children.get(i) == n) {
				children.removeElementAt(i);
				n.setParent(null);
			}
		}
	}


	public abstract List<ActionItem> getActions();

	public abstract boolean checkConfiguredRecursive();
	public abstract boolean checkConfigured();
	public abstract Object getCurrentObject();
	public abstract int getInstancesCount();
	public abstract void actionPerformed(String key);
	public abstract String getText();
	public abstract String getColor();
	
	public abstract void cleanUp();

	public boolean isInvalid() {
		return invalid;
	}
	
	public void removeInvalidsRecursive() {
		removeInvalids();
		for (AbstractChooseNode child : this.getChilds()) {
			child.removeInvalidsRecursive();
		}		
	}	

	protected void removeInvalids() {
		ArrayList<AbstractChooseNode> f = new ArrayList<AbstractChooseNode>();
		for (AbstractChooseNode child : this.getChilds()) {
			if (child.isInvalid()) {
				f.add(child);
			}
		}
		for (AbstractChooseNode toRem : f) {
			this.remove(toRem);
		}
	}

	protected void resetIterators() {
		if (this.getChildCount() > 0) {
			for (Object child : this.children) {
				((AbstractChooseNode)child).resetIterators();
			}
		}
	}

	protected void removeConstructedConstructors(){
		if (this.getChildCount() > 0) {
			for (Object child : this.children) {
				((AbstractChooseNode)child).removeConstructedConstructors();
			}
		}
	}
	
/*	@Override
	public void add(MutableTreeNode newChild) {
		if (newChild != null) {
			super.add(newChild);
			((AbstractChooseNode) newChild).checkConfigured();
		}
		containingTreeModel.nodeStructureChanged(newChild);
	}*/


	public ObjectConstuctionTreeModel<?> getContainingTreeModel() {
		return this.containingTreeModel;
	}

	public String toLongString() {
		StringBuilder sb = new StringBuilder();
		ArrayList<TreeNode> al = new ArrayList<TreeNode>();
		TreeNode pointer = this;
		while (pointer != null) {
			al.add(pointer);
			pointer = pointer.getParent();
		}
		for (int i = al.size()-1 ; i >= 0 ; i--) {
			if (al.get(i) instanceof ConstructorChooseNode) {
				sb.append(((ConstructorChooseNode)al.get(i)).getConstructedClass().getSimpleName() + "<--");
			}
		}
		return sb.toString();
	}

	public boolean isConfigured() {
		return this.configured;
	}

/*	protected void restoreAll() {
		this.checkConfigured();
		this.restoreCreated();
		for (AbstractChooseNode child : this.getChildsRestore()) {
			this.add(child);
			child.restoreAll();
		}
	}*/
	
	protected void restoreChild(AbstractChooseNode child) {
		this.getChildsRestore().add(child);
	}

	private List<AbstractChooseNode> getChildsRestore() {
		if (this.childsRestore == null) {
			this.childsRestore = new ArrayList<AbstractChooseNode>();
		}
		return this.childsRestore;
	}	

	protected List<AbstractChooseNode> getChilds() {
		List<AbstractChooseNode> ret = new LinkedList<AbstractChooseNode>();
		if (this.getChildCount() > 0) {
			for (Object child : this.children) {
				ret.add((AbstractChooseNode) child);
			}
		}
		return ret;
	}

	protected void setConfigured(boolean b) {
		this.configured = b;
		if (getContainingTreeModel() != null) {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)getContainingTreeModel().getRoot();
			if (!(root == this)) {
			/*	if (getParent() != null) {
					((AbstractChooseNode) this.getParent()).checkConfigured();
				}*/
			} else {
				this.getContainingTreeModel().readyStateChanged();
			}
		}
	}

	public String parseName(String name) {
		String[] split = name.split("\\.");
		return split[split.length - 1];
	}

	public static Map<String, String> parseAnnotations(Annotation[] annotations) {
		Map<String, String> ret = new HashMap<String, String>();
		for (Annotation a : annotations) {
			if (a instanceof ConstructorDef) {
				ret.put("Constructor_def.def", ((ConstructorDef)a).def());
			}
			else if (a instanceof ParamName) {
				if (!((ParamName)a).name().equals("")) ret.put("ParamName.name", ((ParamName)a).name());
				if (!((ParamName)a).default_().equals("")) ret.put("ParamName.default_", ((ParamName)a).default_());
				if (!((ParamName)a).defaultClass_().equals(Object.class)) ret.put("ParamName.defaultClass_", ((ParamName)a).defaultClass_().getCanonicalName());
				if (!((ParamName)a).abstractClass().equals(Object.class)) ret.put("ParamName.abstractClass", ((ParamName)a).abstractClass().getCanonicalName());
				if (!((ParamName)a).requireInterface().equals(Object.class)) ret.put("ParamName.requireInterface", ((ParamName)a).requireInterface().getCanonicalName());

			} else { //default_case
				ret.put("default", a.toString());
			}
		}
		return ret;
	}

	protected boolean containsNode(AbstractChooseNode node) {
		if (node == this) {
			return true;
		} else {
			if (this.getChildCount() > 0) {
				boolean ret = false;
				for (Object c : this.children) {
					AbstractChooseNode child = (AbstractChooseNode)c;
					ret = ret || child.containsNode(node);
				}
				return ret;
			} else {
				return false;
			}
		}
	}
	
	public static class ActionStructure extends ActionItem {
		public List<ActionItem> childs;
		
		public ActionStructure(String text, String actionName) {
			super(text, actionName);
		}
		
		public void addItem(ActionItem item) {
			if (childs == null) childs = new ArrayList<ActionItem>(1);
			childs.add(item);
		}
	}
	
	public static class SeparatorItem extends ActionItem {
		public SeparatorItem() {
			super(null, null);
		}
	}
	
	public static class NullItem extends ActionItem {
		public NullItem() {
			super("null", "NULL");
		}
	}
	
	public static class ActionItem {
		public String text;
		public String actionName;
		
		public ActionItem(String text, String actionName) {
			this.text = text;
			this.actionName = actionName;
		}
	}
	
	 private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		 Object[]             tValues;
		 stream.defaultWriteObject();
		 // Save the userObject, if its Serializable.
		 if(userObject != null && userObject instanceof Serializable) {
			 tValues = new Object[2];
			 tValues[0] = "userObject";
			 tValues[1] = userObject;
		 }
		 else {
		 	tValues = new Object[0];
		 }
		 stream.writeObject(tValues);
	 }
	 
	 private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		 Object[]      tValues;
		 stream.defaultReadObject();
		 tValues = (Object[])stream.readObject();
		 if(tValues.length > 0 && tValues[0].equals("userObject"))
			 userObject = tValues[1];
	 }


	public ArrayList<AbstractChooseNode> getAllChildren() {
		ArrayList<AbstractChooseNode> list = new ArrayList<AbstractChooseNode>();
		this.getAllChildren(list);
		return list;
	}
	
	private void getAllChildren(ArrayList<AbstractChooseNode> list) {
		if (this.children != null) {
			for (Object n : this.children) {
				AbstractChooseNode node = (AbstractChooseNode)n;
				list.add(node);
				node.getAllChildren(list);	
			}
		}
	}
	
	public void setAllExpanded() {
		if (this.children != null) {
			for (Object n : this.children) {
				AbstractChooseNode node = (AbstractChooseNode)n;
				node.setAllExpanded();	
			}
		}
		this.setExpanded(true);
	}


	public Collection<AbstractChooseNode> getAllExpandedChildren() {
		ArrayList<AbstractChooseNode> list = getAllChildren();
		for (Iterator<AbstractChooseNode> ite = list.iterator() ; ite.hasNext() ; ) {
			AbstractChooseNode n = ite.next();
			if (!n.isExpanded()) {
				ite.remove();
			}
		}
		return list;
	}
	
	public void setExpanded(boolean b) {
		this.setExpanded(b,true);
	}


	public boolean isExpanded() {
		return isExpanded;
	}


	public void setExpanded(boolean b, boolean genEvent) {
		this.isExpanded = b;
		if (genEvent) {
			getContainingTreeModel().reloadTree();			
		}
	}


}
