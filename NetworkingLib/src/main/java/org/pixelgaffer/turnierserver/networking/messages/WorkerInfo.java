package org.pixelgaffer.turnierserver.networking.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkerInfo
{
	@Getter
	@Setter
	private int sandboxes;
	
	@Getter
	@Setter
	private int port;
}
