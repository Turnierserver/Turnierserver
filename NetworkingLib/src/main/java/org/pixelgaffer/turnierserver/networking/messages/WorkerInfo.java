/*
 * WorkerInfo.java
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
package org.pixelgaffer.turnierserver.networking.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkerInfo
{
	@RequiredArgsConstructor
	@ToString
	@EqualsAndHashCode(of = { "id" })
	public static class SandboxInfo
	{
		private static long nid = 0;
		private long id = nid++;
		
		@NonNull
		@Getter
		@Setter
		private Set<String> langs;
		
		@Getter
		@Setter
		private boolean busy = false;
		
		public SandboxInfo ()
		{
			this(Collections.emptySet());
		}
	}
	
	@NonNull
	@Getter
	@Setter
	private List<SandboxInfo> sandboxes = new ArrayList<>();
	
	@Getter
	@Setter
	private int port;
}
