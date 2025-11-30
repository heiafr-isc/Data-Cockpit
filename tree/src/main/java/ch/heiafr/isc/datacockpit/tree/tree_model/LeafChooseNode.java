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
import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import ch.heiafr.isc.datacockpit.tree.clazzes.ObjectRecipe;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;


public class LeafChooseNode extends AbstractChooseNode {

	private static final long serialVersionUID = -5276221026077034218L;
	private transient boolean isNull = false;

	public LeafChooseNode(
			Object obj,
			ObjectConstructionTreeModel<?> tree) {
		super(tree);
		this.setUserObject(obj);
		if (obj == null) {
			this.isNull = true;
		}
		checkConfigured();		
	}
	
	public String getColor() {
		return "#00AA00";
	}

	public String getText() {
		String value;
		if (getUserObject() instanceof Class) {
			value = ((Class<?>)getUserObject()).getSimpleName();
		} else {
			value = isNull() ? "null" : getUserObject().toString();
		}	
		return value;
	}
	
	public boolean isNull() {
		return isNull;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof LeafChooseNode) {
			LeafChooseNode alt = (LeafChooseNode)anObject;
			return (this.getUserObject() == alt.getUserObject()) || this.getUserObject().equals(alt.getUserObject());
		} else {
			return false;
		}
	}
	
	@Override
	public void actionPerformed(String key) 	{
		LeafChooseNode instance = LeafChooseNode.this;
		DefaultTreeModel model = instance.getContainingTreeModel();
		try {
			model.removeNodeFromParent(instance);
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			// TOFIX
		}
		instance.removeFromParent();
		instance.removeAllChildren();
		getContainingTreeModel().reloadTree();
	}
	
	public List<ActionItem> getActions() {
		List<ActionItem> l = new ArrayList<>(1);
		l.add(new ActionItem("Suppress value", "suppress"));
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
	public Object getCurrentObject(){
		return getUserObject();
	}

	@Override
	public int getInstancesCount() {
		return 1;
	}

	@Override
	public Iterator<Pair<Object, ObjectRecipe<?>>> iterator() {
		return new Iterator<Pair<Object, ObjectRecipe<?>>>() {

			private boolean delivered = false;

			@Override
			public boolean hasNext() {
				return !delivered;
			}

			@Override
			public Pair<Object, ObjectRecipe<?>> next() {
				this.delivered = true;
				return new Pair<>(getUserObject(), null);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public DefinitionIterator getDefinitionIterator() {
		return new DefinitionIterator() {
			
			private boolean delivered;

			@Override
			public boolean hasNext() {
				return !delivered;
			}

			@Override
			public AbstractDefinition next() {
				delivered = false;
				return new StringDefinition(getUserObject().toString());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();		
			}

			@Override
			void reset() {	
				delivered = true;
			}

			@Override
			AbstractDefinition current() {
				if (getUserObject() != null) {
					return new StringDefinition(getUserObject().toString());
				} else {
					return null;
				}
			}

			@Override
			boolean hasFutureNext() {
				return false;
			}
		};
	}		
	
	@Override
	// Issue github #81
	public Object clone() {
		return new LeafChooseNode(this.getUserObject(), this.getContainingTreeModel());
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(this.containingTreeModel);
		if (this.getUserObject() instanceof Serializable || getUserObject() == null) {
			out.writeBoolean(getUserObject() == null);
			out.writeBoolean(this.configured);
			if (getUserObject() != null) {
				out.writeObject(getUserObject());
			}
		} else {
			System.err.println(this.getUserObject().getClass().getName() + " Not serializable.");
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.containingTreeModel = (ObjectConstructionTreeModel<?>)in.readObject();
		boolean isNull = in.readBoolean();
		this.isNull = isNull;
		this.setConfigured(in.readBoolean());
		if (!isNull) {
			this.setUserObject(in.readObject());
		}
	}

	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidClassException("Uncompatible class");
	}

	@Override
	public void cleanUp() {

	}
}
