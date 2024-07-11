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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import ch.heiafr.isc.datacockpit.tree.clazzes.ObjectRecipe;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;


public class ConstructorNodeChooserPointer extends AbstractChooseNode implements Serializable, ActionListener{

	private static final long serialVersionUID = -5767083112645867098L;
	public transient ConstructorChooseNode superInstance;
	public boolean isCartesianEnabled = false;
	
	protected ConstructorNodeChooserPointer(ObjectConstuctionTreeModel tree, ConstructorChooseNode node) {
		super(tree);
		this.superInstance = node;
		this.superInstance.pointed.add(this);
		checkConfigured();		
	}
	
	public boolean isCartesianEnabled() {
		return isCartesianEnabled;
	}
	
	public void setCartesianEnabled(boolean b) {
		isCartesianEnabled = b;
	}
	
	public String getName() {
		return "--> @ " + superInstance.getName();
	}
	
	public String getText() {
		return getName();
	}
	
	public String getColor() {
		return superInstance.getColor();
	}
	
	@Override
	public Iterator<Pair<Object, ObjectRecipe>> iterator() {
		Iterator<Pair<Object, ObjectRecipe>> ret;
		if (!isCartesianEnabled)
			ret = new Iterator<Pair<Object, ObjectRecipe>>() {

				boolean delivered = false;
				boolean first;

				{
					if (superInstance.firstAcessed == null) {
						first = true;
						superInstance.firstAcessed = false;
						superInstance.currentIterator = superInstance.new SpecialIterator();
					} else {
						first = false;
					}
				}

				@Override
				public boolean hasNext() {
					if (first) {
						return superInstance.currentIterator.hasNext;
					} else {
						return !this.delivered;
					}
				}

				@Override
				public Pair<Object, ObjectRecipe> next() {
					if (first){
						return superInstance.currentIterator.findNext();
					}
					else {
						this.delivered = true;
						return superInstance.currentIterator.pair;
					}
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		else 
			ret = new PointerIterator();
		return ret;
	}
	
	private class PointerIterator implements Iterator<Pair<Object, ObjectRecipe>> {

		private Object[] currentParams;
		private boolean first;
		private boolean hasNext;
		private int paramSize;
		private List<Iterator<Pair<Object, ObjectRecipe>>> iterators;
		private List<AbstractChooseNode> children;

		public PointerIterator() {
			
			this.first = true;
			this.hasNext = true;
			if (superInstance.getChildCount() > 0) children = superInstance.getChilds();
			else children = null;
			this.paramSize = ( children != null ? children.size() : 0) ;
			this.iterators =  new ArrayList<Iterator<Pair<Object, ObjectRecipe>>>();
			this.currentParams = new Object[this.paramSize];
			if (this.paramSize > 0) {
				int i = 0;
				for (AbstractChooseNode param : children) {
					if (param.configured) {
						iterators.add(param.iterator());
						if (this.iterators.get(i).hasNext()){
							this.currentParams[i] = this.iterators.get(i).next();
						}
						++i;
					}
				}
			}
		}

		@Override
		public boolean hasNext() {
			return this.hasNext;
		}

		@Override
		public Pair<Object, ObjectRecipe> next() {
			return findNext();
		}

		private Pair<Object, ObjectRecipe> findNext() {
			boolean changed = this.first;
			int i = 0;
			while (!changed) {
				Iterator<Pair<Object, ObjectRecipe>> it = this.iterators.get(i);
				if (it.hasNext()) {
					this.currentParams[i] = it.next();
					changed = true;
					for (i = 0; i<this.paramSize; ++i) {
						this.currentParams[i] = children.get(i).getCurrentObject();
					}
				} else {
					children.get(i).resetIterators();
					this.iterators.set(i, children.get(i).iterator());
					this.currentParams[i] = this.iterators.get(i).next();
					++i;
				}
			}
			this.checkNext();
			this.first = false;
			try {
				
				@SuppressWarnings("unchecked")
				ObjectRecipe recipe = new ObjectRecipe(superInstance.getConstructor(), this.currentParams);
				Object create = recipe.build();
				setUserObject(create);
				return new Pair<Object, ObjectRecipe>(create, recipe);
			} catch (Exception e) {
				System.err.println(superInstance.constructedClass.toString() + " : " + Arrays.toString(currentParams));
				System.err.println(superInstance.toLongString());
				throw new RuntimeException(e);
			}
		}

		private void checkNext() {
			this.hasNext = false;
			int i = 0;
			while (!this.hasNext && i < this.paramSize) {
				this.hasNext = this.hasNext || this.iterators.get(i).hasNext();
				++i;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public void actionPerformed(String key) 	{
		superInstance.actionPerformed(key);
	//	System.out.println("Action performed for constructor pointer " + key);
	}

	public List<ActionItem> getActions() {
		List<ActionItem> l = new ArrayList<ActionItem>(1);	
		l.add(new ActionItem("Suppress constructor", "suppress"));
		return l;
	}

	@Override
	public boolean checkConfiguredRecursive() {
		this.setConfigured(true);
		return true;
	}
	
	@Override
	public boolean checkConfigured() {
		this.setConfigured(true);
		return true;
	}	

	@Override
	public Object getCurrentObject() {
		if (!isCartesianEnabled) {
			return superInstance.currentIterator.currentIteratorObject;
		} else {
			return getUserObject();
		}
	}

	@Override
	protected void resetIterators() {
		if (superInstance.firstAcessed != null && !superInstance.firstAcessed) {
			superInstance.firstAcessed = null;
		}
	}

	@Override
	public int getInstancesCount() {
		int ret =  isCartesianEnabled ? superInstance.getInstancesCount() : 1;
		return ret;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (getContainingTreeModel() != null) {
			DefaultTreeModel model = (DefaultTreeModel) this.getContainingTreeModel();
			try {
				model.removeNodeFromParent(this);
			}
			catch (Exception ex) {
				System.out.println("TOFIX2");
			}
			this.removeFromParent();
			this.removeAllChildren();
			getContainingTreeModel().reloadTree();
		} else {
			// DO something ? (seb, january 2011)
		}


		if (!e.getActionCommand().equals("real_object")) {
			superInstance.pointed.remove(this);
		}
	}

	private void writeObject(java.io.ObjectOutputStream out)throws IOException {
		out.writeObject(this.containingTreeModel);
		out.writeObject(this.superInstance);
		out.writeBoolean(isCartesianEnabled);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.containingTreeModel = (ObjectConstuctionTreeModel<?>)in.readObject();
		this.superInstance = (ConstructorChooseNode)in.readObject();
		isCartesianEnabled = in.readBoolean();
		this.setConfigured(true);
		try {
			if (superInstance.firstAcessed != null){
				//Do nothing
			}
		} catch (NullPointerException e) {
			superInstance.firstAcessed = null;
		}
	}
	
	protected ConstructorChooseNode getSuperInstance() {
		return this.superInstance;
	}
	
	@Override
	public Object clone() {
		throw new RuntimeException("Not useable");
	}

	@Override
	public void cleanUp() {
		for (AbstractChooseNode child : this.getChilds()) {
			child.cleanUp();
		}
	}
}