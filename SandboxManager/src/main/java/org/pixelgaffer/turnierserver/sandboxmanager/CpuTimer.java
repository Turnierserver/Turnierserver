/*
 * CpuTimer.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver.sandboxmanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CpuTimer {
	
	public static long getCpuTime(int boxId) {
		File cpuFile = new File("/sys/fs/cgroup/cpuacct/box-" + boxId + "/cpuacct.usage");
		try {
			return Long.parseLong(Files.readAllLines(cpuFile.toPath()).get(0));
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
}
