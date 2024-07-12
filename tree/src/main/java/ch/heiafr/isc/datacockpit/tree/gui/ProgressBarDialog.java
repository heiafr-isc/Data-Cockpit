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

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private int steps;
	private int current = 0;
	private JProgressBar progressBar;
	
	public ProgressBarDialog(int steps) {
		this.steps = steps;
		this.setSize(300, 100);
		this.setTitle("Progress");
		this.progressBar = new JProgressBar(0, steps);
		progressBar.setSize(220, 70);
		progressBar.setPreferredSize(new Dimension(220, 60));
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(progressBar);
		this.setContentPane(panel);
	}
	
	public void incrementProgression() {
		current++;
		progressBar.setValue(current);
		if (current == steps) {
			this.setVisible(false);
		}
	}
	
	public ProgressBarDialog setDialogVisible() {
		setLocationRelativeTo(null);
		super.setVisible(true);
		return this;
	}

}