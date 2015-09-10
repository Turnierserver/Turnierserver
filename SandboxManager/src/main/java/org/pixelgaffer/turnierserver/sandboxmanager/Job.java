package org.pixelgaffer.turnierserver.sandboxmanager;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "uuid")
public class Job
{
	@Getter
	private int id, version;
	@Getter
	private String lang;
	@NonNull
	@Getter
	private UUID uuid;
}
