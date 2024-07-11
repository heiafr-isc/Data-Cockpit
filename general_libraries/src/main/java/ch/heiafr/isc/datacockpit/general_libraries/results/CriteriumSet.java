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
package ch.heiafr.isc.datacockpit.general_libraries.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.general_libraries.utils.PairList;

public class CriteriumSet extends ArrayList<List<Criterium>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean valuesSet = false;


	public CriteriumSet(int size) {
		super(size);
	}

	public void add(String s) {
		ArrayList<Criterium> al = new ArrayList<Criterium>(1);
		Criterium c = new Criterium(s);
		al.add(c);
		this.add(al);
	}

	@Override
	public boolean add(List<Criterium> a) {

		return super.add(a);
	}

	public CriteriaIterator criteriaIterator() {
		return new CriteriaIterator(this);
	}

	public void setPossibleValues(AbstractDataRetriever ret) {
		if (valuesSet == true) {
			return;
		}
		for (int i = 0 ; i < size() ; i++) {
			List<Criterium> l = get(i);
			for (int j = 0 ; j < l.size() ; j++) {
				Criterium c = l.get(j);
				if (!c.getName().equals(AbstractDataRetriever.CONSTANT)) {
					c.setPossibleValues(ret.getPossibleValuesOfGivenProperty(c.getName()));
				} else {
					ArrayList<String> s = new ArrayList<String>(1);
					s.add(AbstractDataRetriever.CONSTANT);
					c.setPossibleValues(s);
				}
			}
		}
		valuesSet = true;
	}

	public class CriteriaIterator implements Iterable<List<PairList<String, String>>>,
	Iterator<List<PairList<String, String>>>{
		List<List<Criterium>> list;
		int[] indexes;
		Integer[] maxs;
		boolean hasNext = true;
		int total = 1;

		private CriteriaIterator(CriteriumSet list) {
			this.list = list;
			int critN = 0;
			ArrayList<Integer> maxs = new ArrayList<Integer>(3);
			for (List<Criterium> l : list) {
				for (Criterium c : l) {
					critN++;
					int coeff = c.getPossibleValues().size();
					maxs.add(coeff);
					total *= coeff;
				}
			}
			indexes = new int[critN];
			this.maxs = maxs.toArray(new Integer[maxs.size()]);
			if (critN == 0) {
				hasNext = false;
			}
		}

		public int getNbCombinations() {
			return total;
		}

		public boolean hasNext() {
			return hasNext;
		}

		public Iterator<List<PairList<String, String>>> iterator() {
			return this;
		}

		public void remove() {
		}

		public List<PairList<String, String>> next() {
			List<PairList<String, String>> ret = new ArrayList<PairList<String, String>>(list.size());
			int index = 0;
			for (List<Criterium> l : list) {
				PairList<String, String> lp = new PairList<String, String>(l.size());
				for (Criterium c : l) {
					if (c.getPossibleValues().size() > 0) {
						Pair<String, String> p = new Pair<String, String>(c.getName(), c.getPossibleValue(indexes[index]));
						index++;
						lp.add(p);
					}
				}
				ret.add(lp);
			}
			increaseIndex(0);
			return ret;
		}

		private void increaseIndex(int i) {
			indexes[i]++;
			if (indexes[i] >= maxs[i]) {
				indexes[i] = 0;
				if (i < indexes.length-1) {
					increaseIndex(i+1);
				} else {
					hasNext = false;
				}
			}
		}

	}


}
