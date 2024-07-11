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
package ch.heiafr.isc.datacockpit.general_libraries.utils;

import java.util.Iterator;
import java.util.PriorityQueue;

public class BoxedPriorityQueue<E> implements Iterable<E> {

	private PriorityQueue<Cell> queue;

	public BoxedPriorityQueue() {
		queue = new PriorityQueue<Cell>();
	}

	private class Cell implements Comparable<Cell> {
		E e;
		double score;

		@SuppressWarnings("unchecked")
		public int compareTo(Cell c) {
			int r = (int)Math.signum(this.score - c.score);
			if (r == 0) {
				if (e instanceof Comparable) {
					return ((Comparable)e).compareTo(c.e);
				} else {
					return r;
				}
			} else {
				return r;
			}
			
		}

		@Override
		public String toString() {
			return score+":{"+e+"}";
		}

	}

	public boolean offer(E e, double score) {
		Cell c = new Cell();
		c.e = e;
		c.score = score;
		return queue.offer(c);
	}

	public E pollElement() {
		return queue.poll().e;
	}

	public int size() {
		return queue.size();
	}
	
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			Iterator<Cell> it = queue.iterator();
				
			public E next() {
				return it.next().e;
			}
			
			public boolean hasNext() {
				return it.hasNext();
			}
			
			public void remove() {
			}
		};
	}

	@Override
	public String toString() {
		return queue.toString();
	}


}
