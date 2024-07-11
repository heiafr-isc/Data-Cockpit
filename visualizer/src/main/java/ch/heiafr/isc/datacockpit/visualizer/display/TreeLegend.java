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
package ch.heiafr.isc.datacockpit.visualizer.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Texture;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.general_libraries.utils.PairList;

public class TreeLegend {

	public static JTree createTreeLegend(Map<PairList<String,String>, Paint> seriesPaint,
			Map<Pair<String,String>, Shape> seriesShape,
			Map<Pair<String,String>, Texture> seriesTextures,
			Set<PairList<String, String>> legends) {

		JTree tree;
		if (seriesPaint.size() > 0 && (seriesShape.size() > 0 || seriesTextures.size() > 0)) {
			tree = new JTree(createNodes(createLegendMap(seriesPaint),
					seriesPaint, createShapeOrTextureSet(seriesShape),
					createShapeOrTextureSet(seriesTextures), legends));
		} else {
			return null;
		}
		MyRenderer renderer = new MyRenderer(seriesShape, seriesTextures);
		tree.setCellRenderer(renderer);
		expandCompleteTree(tree);
		return tree;
	}

	private static void expandCompleteTree(JTree tree) {
		for (int i = 0; i < tree.getRowCount(); ++i) {
			tree.expandRow(i);
		}
	}


	private static Map<String, Set<StringDoubleOrInt>> createLegendMap(
			Map<PairList<String,String>, ?> serie) {

		HashMap<String, Set<StringDoubleOrInt>> ret = new HashMap<String, Set<StringDoubleOrInt>>();
		Set<PairList<String, String>> keys = serie.keySet();
		for (PairList<String, String> values : keys) {
			for (Pair<String, String> valuePair : values) {
				Set<StringDoubleOrInt> valueSet;
				String valueKey = valuePair.getFirst();
				if (ret.containsKey(valueKey)) {
					valueSet = ret.get(valueKey);
				} else {
					valueSet = new HashSet<StringDoubleOrInt>();
					ret.put(valueKey, valueSet);
				}
				StringDoubleOrInt toAdd;
				try {
					toAdd = new StringDoubleOrInt(Integer.parseInt(valuePair.getSecond()));
				} catch (NumberFormatException e) {
					try {
						toAdd = new StringDoubleOrInt(Double.parseDouble(valuePair.getSecond()));
					} catch (NumberFormatException ex) {
						toAdd = new StringDoubleOrInt(valuePair.getSecond());
					}
				}
				valueSet.add(toAdd);
			}
		}
		return ret;
	}

	private static DefaultMutableTreeNode createNodes(
			Map<String, Set<StringDoubleOrInt>> legends,
			Map<PairList<String,String>, Paint> seriesPaint,
			Pair<String, Map<StringDoubleOrInt, ?>> seriesShape,
			Pair<String, Map<StringDoubleOrInt, ?>> seriesTexture,
			Set<PairList<String, String>> existingLegends){
		boolean texture = seriesTexture.getFirst() != null;

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Legend");
		List<DefaultMutableTreeNode> parentNode = null;

		List<StringDoubleOrInt> ke;
		Map<StringDoubleOrInt, ?> active;
		String shapeOrTexture;
		if (texture) {
			ke = new ArrayList<StringDoubleOrInt>(seriesTexture.getSecond().keySet());
			active = seriesTexture.getSecond();
			shapeOrTexture = seriesTexture.getFirst();
		} else {
			ke = new ArrayList<StringDoubleOrInt>(seriesShape.getSecond().keySet());
			active = seriesShape.getSecond();
			shapeOrTexture = seriesShape.getFirst();
		}
		Collections.sort(ke);
		for (StringDoubleOrInt e : ke) {
			DefaultMutableTreeNode shapeNode = new DefaultMutableTreeNode(
					active.get(e));
			top.add(shapeNode);
			Pair<String, String> shapePair = new Pair<String, String>(shapeOrTexture, e.toString());
			parentNode = new ArrayList<DefaultMutableTreeNode>();
			parentNode.add(shapeNode);
			parentNode = doString(legends, parentNode);
			for (DefaultMutableTreeNode node : parentNode) {
				Paint p = null;
				p = getPaint(node, seriesPaint, shapePair, existingLegends);
				Color c;
				if (p instanceof Texture) {
					c = ((Texture) p).getColor();
				} else {
					c = (Color) p;
				}
				if (c != null) {
					String red = Integer.toHexString(c.getRed());
					while (red.length() != 2) {
						red = "0" + red;
					}
					String green = Integer.toHexString(c.getGreen());
					while (green.length() != 2) {
						green = "0" + green;
					}
					String blue = Integer.toHexString(c.getBlue());
					while (blue.length() < 2) {
						blue = "0" + blue;
					}
					String color = "" + red + green + blue;
					if (node.getUserObject() instanceof Pair<?, ?>) {
						Pair<?,?> nodeObject = (Pair<?, ?>)node.getUserObject();
						node.setUserObject("<HTML><font color=#" + color + ">"
								+ nodeObject.getFirst() + " = '" + nodeObject.getSecond() + "'" + "</font></HTML>\n");
					}
				} else {
					removeSecurely(node);
				}
			}
		}
		return top;
	}

	private static List<DefaultMutableTreeNode> doString(
			Map<String, Set<StringDoubleOrInt>> legends, List<DefaultMutableTreeNode> parentNode) {


		for (Entry<String, Set<StringDoubleOrInt>> legendEntry : legends.entrySet()) {
			List<DefaultMutableTreeNode> newParent = new LinkedList<DefaultMutableTreeNode>();
			String leg = legendEntry.getKey();
			List<StringDoubleOrInt> list = new ArrayList<StringDoubleOrInt>(legendEntry.getValue());
			Collections.sort(list);
			for (StringDoubleOrInt i : list) {
				for (DefaultMutableTreeNode p : parentNode) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new Pair<String, StringDoubleOrInt>(leg, i));
					p.add(childNode);
					newParent.add(childNode);
				}
			}
			parentNode = newParent;
		}
		return parentNode;
	}

	private static Paint getPaint(
			DefaultMutableTreeNode node, Map<PairList<String, String>, ?> seriesPaintOrTexture,
			Pair<String, String> shapePair, Set<PairList<String, String>> existingLegends) {


		for (Entry<PairList<String, String>, ?> entry : seriesPaintOrTexture.entrySet()) {
			PairList<String, String> key = new PairList<String, String>();
			for (Pair<String, String> pair : entry.getKey()) {
				if(pair.equals(getPair(pair.getFirst(), node))) {
					key.add(pair);
				}
			}
			Paint ret = (Paint)seriesPaintOrTexture.get(key);
			if (ret != null) {
				PairList<String, String> newList = new PairList<String, String>();
				newList.addAll(key);
				if (!shapePair.equals(new Pair<String, String>("", ""))) {
					newList.add(shapePair);
				}
				if (existingLegends.contains(newList)) {
					return ret;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private static Pair<String, String> getPair(String valueName, DefaultMutableTreeNode node) {
		Pair<String, String> ret = new Pair<String, String>();
		ret.setFirst(valueName);
		DefaultMutableTreeNode current = node;
		while (ret.getSecond() == null && current.getUserObject() instanceof Pair<?, ?>){
			Pair<?, ?> userObject = (Pair<?, ?>)current.getUserObject();
			if (userObject.getFirst().equals(valueName)) {
				ret.setSecond(userObject.getSecond().toString());
			}
			current = (DefaultMutableTreeNode)current.getParent();
		}
		return ret;
	}

	private static void removeSecurely(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node
		.getParent();
		node.removeFromParent();
		if (parent != null && parent.getChildCount() == 0) {
			removeSecurely(parent);
		}

	}

	@SuppressWarnings("unchecked")
	private static Pair<String, Map<StringDoubleOrInt, ?>> createShapeOrTextureSet(
			Map<Pair<String,String>, ?> toChange) {

		Pair<String, Map<StringDoubleOrInt, ?>> ret = new Pair<String, Map<StringDoubleOrInt, ?>>();
		for (Entry<Pair<String,String>, ?> e : toChange.entrySet()) {
			Pair<String, String> key = e.getKey();
			Map<StringDoubleOrInt, ?> valueMap;
			Object shapeOrTexture = e.getValue();
			boolean shape = shapeOrTexture instanceof Shape;
			if (ret.getFirst() == null) {
				if (shape) {
					valueMap = new HashMap<StringDoubleOrInt, Shape>();
				} else {
					valueMap = new HashMap<StringDoubleOrInt, Texture>();
				}
				ret.setFirst(key.getFirst());
				ret.setSecond(valueMap);
			} else {
				valueMap = ret.getSecond();
			}
			StringDoubleOrInt toAdd;
			try {
				toAdd = new StringDoubleOrInt(Integer.parseInt(key.getSecond()));
			} catch (NumberFormatException ex) {
				try {
					toAdd = new StringDoubleOrInt(Double.parseDouble(key.getSecond()));
				} catch (NumberFormatException exc) {
					toAdd = new StringDoubleOrInt(key.getSecond());
				}
			}
			if (shape) {
				((Map<StringDoubleOrInt, Shape>)valueMap).put(toAdd, (Shape)shapeOrTexture);
			} else {
				((Map<StringDoubleOrInt, Texture>)valueMap).put(toAdd, (Texture)shapeOrTexture);
			}
		}
		return ret;
	}

	private static class MyRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 4813474475512960086L;
		private Map<Pair<String,String>, Shape> seriesShape;
		private Map<Pair<String,String>, Texture> seriesTextures;

		public MyRenderer(Map<Pair<String,String>, Shape> seriesShape,
				Map<Pair<String,String>, Texture> seriesTexture) {
			this.seriesShape = seriesShape;
			this.seriesTextures = seriesTexture;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof Shape) {
				Shape s = (Shape) ((DefaultMutableTreeNode) value)
				.getUserObject();
				Image im = null;

				if (s instanceof Ellipse2D) {
					im = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
					Graphics2D gr = (Graphics2D) im.getGraphics();
					gr.setColor(Color.WHITE);
					gr.fillRect(0, 0, 12, 12);
					gr.setColor(Color.BLACK);
					gr.fillOval((int) ((Ellipse2D) s).getX() + 3,
							(int) ((Ellipse2D) s).getX() + 3,
							2 * (int) ((Ellipse2D) s).getWidth(),
							2 * (int) ((Ellipse2D) s).getHeight());
				} else {
					im = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
					Graphics2D gr = (Graphics2D) im.getGraphics();
					gr.setColor(Color.WHITE);
					gr.fillRect(0, 0, 6, 6);
					gr.setColor(Color.BLACK);
					gr.translate(-s.getBounds().x, -s.getBounds().y);
					gr.fill(s);
					im = scale(im, 12, 12);
				}

				setIcon(new ImageIcon(im));
				Pair<String, String> key = null;
				Iterator<Pair<String, String>> it = this.seriesShape.keySet().iterator();
				while (it.hasNext() && key == null) {
					Pair<String, String> k = it.next();
					if (s == this.seriesShape.get(k)) {
						key = k;
					}
				}
				if (key != null) {
					setText(key.getFirst() + "= '" + key.getSecond() + "'");
				} else {
					setText("");
				}
			} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof Texture) {
				Texture t = (Texture) ((DefaultMutableTreeNode) value)
				.getUserObject();
				Image im = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
				Graphics2D gr = (Graphics2D) im.getGraphics();

				gr.setColor(Color.BLACK);
				gr.fillRect(0, 0, 12, 12);

				t.setColor(Color.WHITE);
				gr.setPaint(t);
				gr.fillRect(1, 1, 10, 10);

				setIcon(new ImageIcon(im));
				Pair<String, String> key = null;
				Iterator<Pair<String, String>> it = this.seriesTextures.keySet().iterator();
				while (it.hasNext() && key == null) {
					Pair<String, String> k = it.next();
					if (t == this.seriesTextures.get(k)) {
						key = k;
					}
				}
				if (key != null) {
					setText(key.getFirst() + "= '" + key.getSecond() + "'");
				} else {
					setText("");
				}
			}  else {
				setToolTipText(null); // no tool tip
			}

			return this;
		}

		public Image scale(Image source, int width, int height) {
			BufferedImage buf = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = buf.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(source, 0, 0, width, height, null);
			g.dispose();
			return buf;
		}
	}

	private static class StringDoubleOrInt implements Comparable<StringDoubleOrInt>{

		private final Double d;
		private final Integer i;
		private final String s;
		//private final boolean isDouble;

		public StringDoubleOrInt(Double d) {
			this.d = d;
			this.i = null;
			this.s = null;
		}

		public StringDoubleOrInt(String s) {
			this.s = s;
			this.d = null;
			this.i = null;
		}

		public StringDoubleOrInt(Integer i) {
			this.i = i;
			this.s = null;
			this.d = null;
		}

		public Comparable<?> getValue() {
			if (this.d != null) {
				return this.d;
			} else if (this.i != null) {
				return i;
			} else {
				return this.s;
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof StringDoubleOrInt) {
				return this.getValue().equals(((StringDoubleOrInt) o).getValue());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.getValue().hashCode();
		}

		@Override
		public int compareTo(StringDoubleOrInt o) {
			if (o.getValue() instanceof Double) {
				if (this.getValue() instanceof Double) {
					return this.d.compareTo(o.d);
				} else if (this.getValue() instanceof Integer) {
					return o.d.compareTo((double)this.i);
				} else if (this.getValue() instanceof String) {
					return -1;
				} else {
					throw new UnsupportedOperationException();
				}
			} else if (o.getValue() instanceof Integer) {
				if (this.getValue() instanceof Double) {
					return this.d.compareTo((double)o.i);
				} else if (this.getValue() instanceof Integer) {
					return this.i.compareTo(o.i);
				} else if (this.getValue() instanceof String) {
					return -1;
				} else {
					throw new UnsupportedOperationException();
				}
			} else if (o.getValue() instanceof String) {
				if (this.getValue() instanceof Double) {
					return 1;
				} else if (this.getValue() instanceof Integer) {
					return 1;
				} else if (this.getValue() instanceof String) {
					return this.s.compareTo(o.s);
				} else {
					throw new UnsupportedOperationException();
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public String toString() {
			return this.getValue().toString();
		}

	}
}
