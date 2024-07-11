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
package ch.heiafr.isc.datacockpit.visualizer.charts;

import java.awt.Paint;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Grid;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.HorizontalScratches;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.LeftScratches;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.RightScratches;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Texture;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Uniform;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.VerticalScratches;

public class TextureSupplier {
	private List<Class<? extends Texture>> list;
	private Iterator<Class<? extends Texture>> it;


	public TextureSupplier() {
		this.list = new LinkedList<Class<? extends Texture>>();
		this.fillList();
		this.it = list.iterator();
	}

	private void fillList() {
		this.list.add(Uniform.class);
		this.list.add(HorizontalScratches.class);
		this.list.add(VerticalScratches.class);
		this.list.add(LeftScratches.class);
		this.list.add(RightScratches.class);
		this.list.add(Grid.class);
	}

	public Paint getNext () {
		if (this.it.hasNext())  {
			try {
				Class<? extends Paint> cl = this.it.next();
				return cl.newInstance();
			} catch (Exception e) {
				return null;
			}
		} else {
			this.it = this.list.iterator();
			return this.getNext();
		}
	}
}
