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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractParameterChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.ArrayChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.UntypableArrayInstanceNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.BooleanChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.ClassChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.ConstructorChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.ConstructorNodeChooserPointer;
import ch.heiafr.isc.datacockpit.tree.tree_model.LeafChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.ObjectConstuctionTreeModel;
import ch.heiafr.isc.datacockpit.tree.tree_model.TypableChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.ObjectConstuctionTreeModel.TreeModelUIManager;


public class HouseMadeTree<X> extends JScrollPane implements TreeModelUIManager, MouseWheelListener {
	
	private static final long serialVersionUID = 1L;

	class LocalPanel extends JPanel {
		private static final long serialVersionUID = 1L;

    }

	static int xOffset = 20;
	private static int yOffset = AbstractGUIContainer.lineHeight;
	int minWidth = 0;
	
	private Hashtable<AbstractChooseNode, AbstractGUIContainer> guiComponentMap = new Hashtable<AbstractChooseNode, AbstractGUIContainer>();
	
	ObjectConstuctionTreeModel<X> mod;
	
	LocalPanel mainPanel;
	
	public HouseMadeTree(ObjectConstuctionTreeModel<X> mod) {
		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setWheelScrollingEnabled(false);
		this.addMouseWheelListener(this);
		this.mod = mod;
		mod.setTreeModelUIManager(this);
		
		mainPanel = new LocalPanel();
		mainPanel.setBackground(Color.WHITE);
		
		this.setViewportView(mainPanel);
		
		mainPanel.setLayout(new LayoutManager() {

			@Override
			public void addLayoutComponent(String name, Component comp) {}

			@Override
			public void removeLayoutComponent(Component comp) {}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				int lines = parent.getComponentCount();
				return new Dimension(minWidth, lines*yOffset/2);
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return new Dimension(minWidth,parent.getComponentCount()*yOffset/2);
			}

			@Override
			public void layoutContainer(Container parent) {
				int panelWidth = HouseMadeTree.this.getWidth();
				if (HouseMadeTree.this.getVerticalScrollBar().isVisible()) {
					panelWidth -= HouseMadeTree.this.getVerticalScrollBar().getWidth();
				}
				parent.getComponentCount();
				if (containers != null) {
					int nComps = containers.size();
				    for (int i = 0 ; i < nComps ; i++) {			        	
			            Container c = containers.get(i);
			            Widget w = widgets.get(i);
			            int prefix = prefixes.get(i);
			            
			            int x = prefix*xOffset;
			            int y = i*yOffset;
			            int width = panelWidth - (prefix*xOffset);
			            int height = yOffset;
						if (c instanceof LeafNodeGUIContainer) {
							int wi = ((LineLayoutManager)c.getLayout()).getWidthOfContainer(c);
							c.setBounds(x, y, Math.max(wi, 70), height);					
						} else {
							c.setBounds(x, y, width, height);	
						}

						w.setBounds(0, i*yOffset, prefix*xOffset, yOffset);
			        }
				}
			}
		});
		rebuild();
	}
	
	private ArrayList<Container> containers;
	private ArrayList<Integer> prefixes;
	private ArrayList<Widget> widgets;
	
	
	private void rebuild() {
		ClassChooseNode root = (ClassChooseNode)mod.getRoot();
		mainPanel.removeAll();
		
		widgets = new ArrayList<Widget>();
		containers = new ArrayList<Container>();
		prefixes = new ArrayList<Integer>();
		exploreTree(root, 0, 0, new boolean[]{}, true);
		
		int nComps = containers.size();
		int max = 0;
        for (int i = 0 ; i < nComps ; i++) {
        	Component c = containers.get(i);
        	int prefix = prefixes.get(i);
            if (c instanceof AbstractGUIContainer) {		            	
            	AbstractGUIContainer cc = (AbstractGUIContainer)c;
            	int widthlab = ((LineLayoutManager)cc.getLayout()).getWidthOfContainer(cc);
            	int totNewWidth = widthlab + prefix * xOffset;
            	if (totNewWidth > max) {
            		max = totNewWidth;
            	}
            }
        }
        
        minWidth = max;

		this.revalidate();
		this.repaint();
	}
	
	private int exploreTree(AbstractChooseNode node, int lineIndex, int prefix, boolean[] cont, boolean isLast) {
		AbstractGUIContainer c = getContainer(node, prefix);
		containers.add(c);
		mainPanel.add(c);
		Widget w = new Widget(prefix, cont, isLast, node.isExpanded(), node.getChildCount() > 0, c);
		widgets.add(w);
		mainPanel.add(w);
		prefixes.add(prefix);
		
		lineIndex++;
		if (node.isExpanded()) {
			Enumeration e = node.children();
			int subIdx = 0;
			int subFamilySize = node.getChildCount();
			boolean[] subCont = new boolean[cont.length+1];
			System.arraycopy(cont, 0, subCont, 0, cont.length);
			for ( ; e.hasMoreElements() ; ) {
				Object obj = e.nextElement();
				if (obj instanceof AbstractChooseNode) {	
					AbstractChooseNode child = (AbstractChooseNode)obj;
					subCont[cont.length] = !(subIdx == subFamilySize-1);
					lineIndex = exploreTree(child, lineIndex, prefix+1, subCont, !(subIdx < subFamilySize-1));
					subIdx++;
				}
			}
		}		
		return lineIndex;
	}
	
	private AbstractGUIContainer getContainer(AbstractChooseNode node, int prefix) {
		AbstractGUIContainer c = null;
		c = guiComponentMap.get(node);
		if (c == null) {
			LayoutManager man = getNewLineLayout();
			if (node instanceof ClassChooseNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode)node, man, prefix);
			} else if (node instanceof BooleanChooseNode) {
				c = new BooleanParameterGUIContainer((BooleanChooseNode)node, man, prefix);
			} else if (node instanceof ArrayChooseNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode)node, man, prefix);
			} else if (node instanceof TypableChooseNode) {
				c = new TypableParameterGUIContainer((TypableChooseNode)node, man, prefix);
			} else if (node instanceof ConstructorChooseNode) {
				c = new ConstructorGUIContainer((ConstructorChooseNode)node, man, prefix);
			} else if (node instanceof LeafChooseNode) {
				c = new LeafNodeGUIContainer((LeafChooseNode)node, man, prefix);
			} else if (node instanceof ConstructorNodeChooserPointer) {
				c = new ConstructorPointerGUIContainer((ConstructorNodeChooserPointer)node, man, prefix);
			} else if (node instanceof UntypableArrayInstanceNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode)node, man, prefix);
			} else {
				throw new IllegalStateException("Should not be here, undefined choose node");
			}
			guiComponentMap.put(node, c);
		} else {
			c.refresh();
		}
		return c;
	}	
	
	private LayoutManager getNewLineLayout() {
		return new LineLayoutManager(this);
	}
	
	public Dimension getSize() {
		return super.getSize();
	}
	
	public void setSize(Dimension d) {
		super.setSize(d);
		rebuild();
	}


	@Override
	public void showErrorMessage(String string) {
		JOptionPane.showMessageDialog( null, string, "Error", JOptionPane.ERROR_MESSAGE);	
	}

	@Override
	public void removeNode() {
		// TODO Auto-generated method stub
	}

	public ObjectConstuctionTreeModel<X> getModel() {
		return mod;
	}

	public int getInstancesCount() {
		return ((AbstractChooseNode)mod.getRoot()).getInstancesCount();
	}

	@Override
	public void refresh() {
		rebuild();
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point p = this.getViewport().getViewPosition();
		
		int height = this.getViewport().getView().getHeight();
		
		p.y = Math.max(0, Math.min(p.y + e.getWheelRotation()*yOffset*(1+height/1000), height));
		
		this.getViewport().setViewPosition(p);	
	}
}

class LineLayoutManager implements LayoutManager2 {
	
	HouseMadeTree tree;
	
	LineLayoutManager(HouseMadeTree tree) {
		this.tree = tree;
	}
	
	HashMap<Component, Placement> map = new HashMap<Component, Placement>();

	@Override
	public void addLayoutComponent(String name, Component comp) {}

	@Override
	public void removeLayoutComponent(Component comp) {}

	@Override
	public Dimension preferredLayoutSize(Container parent) { 
		return new Dimension(100, AbstractGUIContainer.lineHeight);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(100, AbstractGUIContainer.lineHeight);
	}
	
	public int getWidthOfContainer(Container target) {
		int xLeftFree = 0;
		int xRightOffset = 0;
		int floatWidth = 0;
		for (Component comp : target.getComponents()) {
			Placement p = map.get(comp);
			if (p == null) {	
				floatWidth += comp.getMinimumSize().width;
			} else {
				if (p.left) {
					xLeftFree = Math.max(xLeftFree, p.to);
				} else {
					xRightOffset = Math.max(xRightOffset, p.to);
				}						
			}
		}
		return xLeftFree + floatWidth + xRightOffset + 24;
	}

	@Override
	public void layoutContainer(Container parent) {
		int xLeftFree = 0;
		int xRightOffset = 0;
		for (Placement p : map.values()) {
			if (p.left) {
				xLeftFree = Math.max(xLeftFree, p.to);
			} else {
				xRightOffset = Math.max(xRightOffset, p.to);
			}
		}

		int currentWidth = tree.mainPanel.getWidth();
		int prefix = ((AbstractGUIContainer)parent).getPrefix();
		
		for (Component comp : parent.getComponents()) {
			Placement p = map.get(comp);
			Rectangle r;
			if (p == null) {
				r = new Rectangle(xLeftFree+2, 1, currentWidth - xLeftFree - xRightOffset, parent.getHeight()-3); 
			} else {
				if (p.left) {
					r = new Rectangle(p.from, 1, p.to, parent.getHeight()-3);
				} else {
					r = new Rectangle(tree.mainPanel.getWidth() - p.to - (prefix * HouseMadeTree.xOffset), 1, p.to - p.from, parent.getHeight()-3);
				}
			}
			comp.setBounds(r);
			comp.setVisible(true);
			comp.validate();
		}
	//	System.out.println("iheur" + parent.isValid() + parent.getBounds());
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints instanceof Placement) {
			map.put(comp, (Placement)constraints);
		}
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {}
}

class Widget extends Container {
	
	private static final long serialVersionUID = 1L;
	private static int xOffset = HouseMadeTree.xOffset;
	private static int yOffset = AbstractGUIContainer.lineHeight;	
	
	int prefix;
	boolean[] cont;
	boolean isLast;
	boolean isExpanded;
	boolean hasChild;
	
	Widget(int prefix, boolean[] cont, boolean isLast, boolean isExpanded, boolean hasChild, final AbstractGUIContainer assos) {
		this.prefix = prefix;
		this.cont = cont;
		this.isLast = isLast;
		this.isExpanded = isExpanded;
		this.hasChild = hasChild;
		this.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				assos.setExpanded(!assos.isExpanded());
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
			//	if (arg0.getClickCount() == 1) {

			//	}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
		});		
	}
	
	@Override
	public void paint(Graphics g) {
		int xCenterOfLast = (int)((prefix-0.5)*xOffset);
		
		for (int i = 0 ; i < prefix-1 ; i++) {
			int x = (i*xOffset) + xOffset/2;
			if (cont[i])
				g.drawLine(x, 0, x, yOffset);
		}
		int x = ((prefix-1)*xOffset) + xOffset/2;	
		if (!isLast) {
			g.drawLine(x,0,x,yOffset);
		} else {
			g.drawLine(x,0,x,yOffset/2);
		}
		// dummy line for debug
		//g.drawLine(0,0,pre_, cc.length);
		
		// horizontal line
		g.drawLine(xCenterOfLast, yOffset/2, prefix*xOffset, yOffset/2);
		if (hasChild) {
			if (isExpanded) {
				g.drawOval(xCenterOfLast-3, (yOffset/2)-3, 6, 6);
			} else {
				g.fillOval(xCenterOfLast-3, (yOffset/2)-3, 7, 7);
			}
		}
	}
}
