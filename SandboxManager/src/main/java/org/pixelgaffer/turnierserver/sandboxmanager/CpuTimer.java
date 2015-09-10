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
