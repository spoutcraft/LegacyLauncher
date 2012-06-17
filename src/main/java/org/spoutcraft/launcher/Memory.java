package org.spoutcraft.launcher;

public final class Memory {
	public static final Memory[] memoryOptions = {
		(new Memory(512, "512 MB", 1)),
		(new Memory(768, "768 MB", 2)),
		(new Memory(1024, "1 GB", 0)),
		(new Memory(1536, "1.5 GB", 3)),
		(new Memory(2048, "2 GB", 4)),
		(new Memory(3072, "3 GB", 5)),
		(new Memory(4096, "4 GB", 6)),
	};
	public static final int MAX_32_BIT_MEMORY = 1024;

	int memory;
	String text;
	int option;
	private Memory(int memory, String text, int option) {
		this.memory = memory;
		this.text = text;
		this.option = option;
	}
	
	public int getMemoryMB() {
		return memory;
	}
	
	public String getDescription() {
		return text;
	}
	
	public int getSettingsId() {
		return option;
	}
	
	public static Memory getMemoryFromId(int id) {
		for (Memory m : memoryOptions) {
			if (m.getSettingsId() == id) {
				return m;
			}
		}
		return null;
	}
	
	public static int getMemoryIndexFromId(int id) {
		for (int i = 0; i < memoryOptions.length; i++) {
			if (memoryOptions[i].option == id) {
				return i;
			}
		}
		return id;
	}
}
