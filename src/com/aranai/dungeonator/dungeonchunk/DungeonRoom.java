package com.aranai.dungeonator.dungeonchunk;

import java.util.Vector;

import org.bukkit.Chunk;

import com.aranai.dungeonator.Direction;

/**
 * Represents a 16Xx16Zx8Y cuboid region within an overall DungeonChunk. Each DungeonChunk contains 16 DungeonRooms stacked vertically.
 */
public class DungeonRoom implements IDungeonRoom {
	
	/** The X coordinate of the room. This coordinate should match the DungeonChunk X coordinate. */
	private int x;
	
	/** The Y coordinate of the room. This ranges from 1-16. */
	private int y;
	
	/** The Z coordinate of the room. This coordinate should match the DungeonChunk Z coordinate. */
	private int z;
	
	/** Random seed used for procedural rooms. */
	private long seed = 0;
	
	/** Chunk type */
	private DungeonRoomType type;
	
	/** Neighboring chunks (NESW, above, below) */
	private DungeonRoom[] neighbors = new DungeonRoom[6];
	
	/** Doorways */
	private DungeonRoomDoorway[] doorways = new DungeonRoomDoorway[12];

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#getX()
	 */
	@Override
	public int getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#getZ()
	 */
	@Override
	public int getZ() {
		return z;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#getSeed()
	 */
	@Override
	public long getSeed() {
		return seed;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#getType()
	 */
	@Override
	public DungeonRoomType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#setType(com.aranai.Dungeonator.DungeonChunkType)
	 */
	@Override
	public void setType(DungeonRoomType type) {
		this.type = type;
		
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#hasDoorway(com.aranai.Dungeonator.Direction)
	 */
	@Override
	public boolean hasDoorway(byte d) {
		return (d <= doorways.length && doorways[d] != null);
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#getDoorway(byte)
	 */
	@Override
	public DungeonRoomDoorway getDoorway(byte direction) {
		if(this.hasDoorway(direction))
		{
			return doorways[direction];
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.IDungeonChunk#getDoorwaysOnSide(byte)
	 */
	@Override
	public Vector<DungeonRoomDoorway> getDoorwaysOnSide(byte[] side)
	{
		Vector<DungeonRoomDoorway> sideDoorways = new Vector<DungeonRoomDoorway>(3);
		
		// Loop through the directions available for this side
		for(byte i = 0; i < side.length; i++)
		{
			if(this.hasDoorway(side[i]))
			{
				// This DungeonChunk has a doorway at this side
				// Add the doorway to the list
				sideDoorways.add(this.getDoorway(side[i]));
			}
		}
		
		return sideDoorways;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#setDoorway(byte, com.aranai.Dungeonator.DungeonChunkDoorway)
	 */
	@Override
	public void setDoorway(DungeonRoomDoorway doorway) {
		doorways[doorway.getDirection()] = doorway;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#hasNeighbor(byte)
	 */
	@Override
	public boolean hasNeighbor(byte direction) {
		if(this.isValidChunkDirection(direction))
		{
			return (neighbors[direction] != null);
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#getNeighbor(byte)
	 */
	@Override
	public IDungeonRoom getNeighbor(byte direction) {
		if(this.hasNeighbor(direction))
		{
			return neighbors[direction];
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonChunk#setNeighbor(byte, com.aranai.Dungeonator.IDungeonChunk)
	 */
	@Override
	public void setNeighbor(byte direction, IDungeonRoom neighbor) {
		if(this.isValidChunkDirection(direction))
		{
			neighbors[direction] = (DungeonRoom)neighbor;
		}
	}
	
	/**
	 * Checks if the specified direction is a valid chunk direction (NESW)
	 *
	 * @param direction the direction to check
	 * @return true, if the direction is a valid chunk direction
	 */
	public boolean isValidChunkDirection(byte direction)
	{
		return (direction == Direction.N || direction == Direction.S || direction == Direction.E || direction == Direction.W);
	}
}
