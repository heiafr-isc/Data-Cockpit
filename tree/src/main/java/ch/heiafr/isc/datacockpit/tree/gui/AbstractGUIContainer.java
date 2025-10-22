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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EtchedBorder;

import ch.heiafr.isc.datacockpit.general_libraries.utils.TypeParser;
import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractChooseNode;
import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractChooseNode.ActionItem;
import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractChooseNode.ActionStructure;
import ch.heiafr.isc.datacockpit.tree.tree_model.AbstractChooseNode.SeparatorItem;
import ch.heiafr.isc.datacockpit.tree.tree_model.LeafChooseNode;


public abstract class AbstractGUIContainer extends JPanel {
	
	protected static int lineHeight = 21;
	
	private final JLabel textLabel;	
	
	private static final Font menuFont = new Font("Arial", java.awt.Font.BOLD, 10);	
	
	private static final long serialVersionUID = 1L;
	
	protected transient static java.awt.Font defaultFont = new java.awt.Font("Arial", Font.BOLD, 11);
	protected transient FontMetrics defaultFontMetrics;
	
	protected final AbstractChooseNode absNode;
	protected transient int prefix;
	
	public abstract void refreshImpl();
	
	public AbstractGUIContainer(AbstractChooseNode absNode, LayoutManager man, int prefix) {
		super(man);
		this.absNode = absNode;
		this.prefix = prefix;
		setBackground(Color.WHITE);
		textLabel = new JLabel();
		textLabel.setBackground(Color.RED);
		textLabel.setFont(defaultFont);
		defaultFontMetrics = textLabel.getFontMetrics(defaultFont);
		this.add(textLabel);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));		
		
		
		final AbstractChooseNode local = absNode;
				
		MouseListener mouseL = new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == 3) {
					JPopupMenu menu = buildMenu(local);
					//JPopupMenu menu = node.getOptions();
					menu.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
					menu.show((JComponent) e.getSource(), e.getX(),e.getY());
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					local.setExpanded(!local.isExpanded());
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
		//		System.out.println(local.getText());
			}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {
		//		System.out.println("ff" + local.getText());				
			}
		};
		
		this.addMouseListener(mouseL);
	//	this.textLabel.addMouseListener(mouseL);
		
	}
	
	private JPopupMenu buildMenu(AbstractChooseNode node) {
		JPopupMenu root = new JPopupMenu();
		for (ActionItem item : node.getActions()) {
			if (item instanceof ActionStructure) {
				JMenu menu = buildSubMenu((ActionStructure)item, node, root);
				menu.setFont(menuFont);
				root.add(menu);
			} else if (item instanceof SeparatorItem) {
				root.addSeparator();
			} else {
				root.add(buildItem(item, node));
			}
		}
		return root;
	}
	
	private JMenu buildSubMenu(final ActionStructure s, final AbstractChooseNode node, final JPopupMenu root) {
		JMenu menu = new JMenu(adaptMenuText(s.text));
		menu.setFont(menuFont);
		if (s.actionName != null) {
			menu.addMouseListener(new MouseListener() {
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
					node.actionPerformed(s.actionName);
					root.setVisible(false);
				}
			});
		}
		for (ActionItem item : s.childs) {
			if (item instanceof ActionStructure) {
				menu.add(buildSubMenu((ActionStructure)item, node, root));
			} else if (item instanceof SeparatorItem) {
				menu.addSeparator();
			} else {
				menu.add(buildItem(item, node));
			}
		}
		return menu;
	}
	
	private JMenuItem buildItem(final ActionItem item, final AbstractChooseNode node) {
		JMenuItem menuItem = new JMenuItem(item.text);
		menuItem.setFont(menuFont);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.actionPerformed(item.actionName);
			}
		});
		return menuItem;
	}	
	
	private final static int maxLength = 150;
	
	String adaptMenuText(String s) {
		if (s.length() <= maxLength) return s;
		return "<html><table border='1'>" + tablizeLinear(s) + "</table></html>";
	}
	
	String tablizeLinear(String s) {	
		ArrayList<String> ret = tablize(s, maxLength);
		StringBuilder sb = new StringBuilder();
		for (String str : ret) {
			sb.append( str + "<br>");
		}
		return sb.toString();		
	}
	
	ArrayList<String> tablize(String s, int maxLength) {
		char[] breakers = new char[]{' ', '.'};
		ArrayList<String> ret = new ArrayList<String>();
		if (s.length() <= maxLength) {
			ret.add(s);
			return ret;
		}
 		int currentIndex = maxLength;
		int previousIndex = 0;
		int breakerIndex = 0;
		while(currentIndex < s.length()) {
			int currentLines = ret.size();
			for (int i = currentIndex ; i > previousIndex ; i--) {
				if (s.charAt(i) == breakers[breakerIndex]) {
					String sub = s.substring(previousIndex, i);
					ret.add(sub);
				//	s = s.substring(i+1, s.length()-1);
					previousIndex = i;
					currentIndex = i+maxLength;
					break;
				}
			}
			if (currentLines == ret.size()) {
				breakerIndex++;
			}
		}
		ret.add(s.substring(previousIndex, s.length()));
		return ret;
	}

	public void setIcon(String path) {
		ImageIcon image = new ImageIcon(ClassLoader.getSystemResource(path));
		textLabel.setIcon(image);
	}
	
	public void refresh() {
		textLabel.setForeground(TypeParser.parseColor(absNode.getColor()));
		String text = absNode.getText();
		String display;
		if (text.length() > 130 - (2*prefix)) {
			display = text.substring(0, 120 - (2*prefix)) + " ... ";
	//		textLabel.setToolTipText(text);
		} else {
			display = text;
			textLabel.setToolTipText(null);
		}
		if (absNode.isConfigured() && !(absNode instanceof LeafChooseNode)) {
			display += " " + absNode.getInstancesCount() + " instances ";
		}
		
		textLabel.setText(display);	
		int width = defaultFontMetrics.stringWidth(display);
		textLabel.setMinimumSize(new Dimension(width, lineHeight-2));
	//	textLabel.setSize(new Dimension(width, lineHeight-2));
		refreshImpl();
	}

	public int getPrefix() {
		return prefix;
	}

	public boolean isExpanded() {
		return absNode.isExpanded();
	}

	public void setExpanded(boolean b) {
		absNode.setExpanded(b);
	}

}
