/*
 * Copyright (C) 2021 MaxSav Team
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.maxsavteam.calculator.tree;

public class OperatorPosition implements Comparable<OperatorPosition> {
	public final int level;
	public final int priority;
	public final int position;

	public OperatorPosition(int level, int priority, int position) {
		this.level = level;
		this.priority = priority;
		this.position = position;
	}

	@Override
	public int compareTo(OperatorPosition o) {
		if (level != o.level)
			return Integer.compare(level, o.level);
		if (priority != o.priority)
			return Integer.compare(priority, o.priority);
		return Integer.compare(position, o.position);
	}
}
