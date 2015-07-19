package org.pixelgaffer.turnierserver.networking.messages;

import java.util.ArrayList;
import java.util.List;

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
		private String langs[];
		
		@Getter
		@Setter
		private boolean busy = false;
		
		public SandboxInfo ()
		{
			this(new String[] {});
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
