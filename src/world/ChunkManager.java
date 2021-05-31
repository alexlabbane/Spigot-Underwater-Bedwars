package world;

import java.util.HashSet;

import org.bukkit.Chunk;

public class ChunkManager {
	private static HashSet<Chunk> forceLoadedChunks;
	
	/**
	 * Mark a chunk to be force loaded by the server
	 * @param chunk	the chunk to force load
	 */
	public static void registerChunk(Chunk chunk) {
		chunk.setForceLoaded(true);
		forceLoadedChunks.add(chunk);
	}
	
	/**
	 * Unmark a chunk (set as not force loaded)
	 * @param chunk	the chunks to unmark
	 */
	public static void unregisterChunk(Chunk chunk) {
		chunk.setForceLoaded(false);
		forceLoadedChunks.remove(chunk);
	}
	
	/**
	 * Check if a chunk is registered as force loaded
	 * @param chunk	the chunk to check
	 * @return		true if the chunk is registered
	 */
	public static boolean isRegistered(Chunk chunk) {
		return forceLoadedChunks.contains(chunk);
	}
}
