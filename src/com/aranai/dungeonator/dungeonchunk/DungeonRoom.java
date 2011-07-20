package com.aranai.dungeonator.dungeonchunk;

import java.util.Vector;

import com.aranai.dungeonator.Direction;
import com.aranai.dungeonator.generator.DungeonMath;

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
	
	/** The room name. */
	private String name = "";
	
	/** The room filename. */
	private String filename = "";
	
	/** The library id. */
	private long libraryId = 0;
	
	/** Temporary raw block array */
	private byte[] tempRawBlocks;
	
	/** Temporary raw block data array */
	private byte[] tempRawBlockData;
	
	/** The DungeonChunk for this room */
	private DungeonChunk chunk;
	
	/** Room type */
	private DungeonRoomType type;
	
	/** Neighboring rooms (NESW, above, below) */
	private DungeonRoom[] neighbors = new DungeonRoom[6];
	
	/** Doorways */
	private DungeonRoomDoorway[] doorways = new DungeonRoomDoorway[14];
	
	public DungeonRoom()
	{
		this.chunk = null;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public DungeonRoom(DungeonChunk chunk, int y)
	{
		this.chunk = chunk;
		this.x = chunk.getX();
		this.z = chunk.getZ();
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#getDungeonChunk()
	 */
	@Override
	public DungeonChunk getDungeonChunk() {
		return chunk;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#getX()
	 */
	@Override
	public int getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#getZ()
	 */
	@Override
	public int getZ() {
		return z;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#getSeed()
	 */
	@Override
	public long getSeed() {
		return seed;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#getType()
	 */
	@Override
	public DungeonRoomType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#setType(com.aranai.Dungeonator.DungeonChunkType)
	 */
	@Override
	public void setType(DungeonRoomType type) {
		this.type = type;
		
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#hasDoorway(com.aranai.Dungeonator.Direction)
	 */
	@Override
	public boolean hasDoorway(byte d) {
		return (d <= doorways.length && doorways[d] != null);
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#getDoorway(byte)
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
	 * @see com.aranai.dungeonator.IDungeonRoom#getDoorwaysOnSide(byte)
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
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#getDoorways()
	 */
	@Override
	public Vector<DungeonRoomDoorway> getDoorways() {
		Vector<DungeonRoomDoorway> doorways = new Vector<DungeonRoomDoorway>();
		
		// Loop through the directions available for this side
		for(byte i = 0; i < this.doorways.length; i++)
		{
			if(this.hasDoorway(i))
			{
				// This DungeonChunk has a doorway at this side
				// Add the doorway to the list
				doorways.add(this.getDoorway(i));
			}
		}
		
		return doorways;
	}
	
	public byte[] getDoorwaysRaw()
	{
		byte[] doorways = new byte[this.doorways.length];
		
		// Loop through the directions available for this side
		for(byte i = 0; i < this.doorways.length; i++)
		{
			if(this.hasDoorway(i))
			{
				// This DungeonChunk has a doorway at this side
				// Add the doorway to the list
				doorways[i] = this.getDoorway(i).getDirection();
			}
		}
		
		return doorways;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#setDoorway(byte, com.aranai.Dungeonator.DungeonChunkDoorway)
	 */
	@Override
	public void setDoorway(DungeonRoomDoorway doorway) {
		doorways[doorway.getDirection()] = doorway;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#setDoorway(byte, boolean)
	 */
	@Override
	public void setDoorway(byte direction, boolean status) {
		if(Direction.isValidDirection(direction))
		{
			if(status)
			{
				// Set the doorway
				doorways[direction] = new DungeonRoomDoorway(direction);
			}
			else
			{
				// Unset the doorway
				doorways[direction] = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#resetDoorways()
	 */
	@Override
	public void resetDoorways() {
		for(int i = 0; i < doorways.length; i++)
		{
			if(doorways[i] != null)
			{
				doorways[i] = null;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#hasNeighbor(byte)
	 */
	@Override
	public boolean hasNeighbor(byte direction) {
		if(this.isValidRoomDirection(direction))
		{
			return (neighbors[direction] != null);
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aranai.Dungeonator.IDungeonRoom#getNeighbor(byte)
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
	 * @see com.aranai.Dungeonator.IDungeonRoom#setNeighbor(byte, com.aranai.Dungeonator.IDungeonRoom)
	 */
	@Override
	public void setNeighbor(byte direction, IDungeonRoom neighbor) {
		if(this.isValidRoomDirection(direction))
		{
			neighbors[direction] = (DungeonRoom)neighbor;
		}
	}
	
	/**
	 * Checks if the specified direction is a valid chunk direction (NESW,Up,Down)
	 *
	 * @param direction the direction to check
	 * @return true, if the direction is a valid chunk direction
	 */
	public boolean isValidRoomDirection(byte direction)
	{
		return (direction == Direction.N || direction == Direction.S || direction == Direction.E || direction == Direction.W
				|| direction == Direction.UP || direction == Direction.DOWN);
	}
	
	/**
	 * Gets the raw blocks for the room.
	 *
	 * @return the block byte array
	 */
	public byte[] getRawBlocks()
	{
		if(chunk != null && chunk.isReady())
		{
			/*
			 * If the chunk is in ready state, retrieve the real-world blocks
			 */
			
			byte[] blocks = new byte[16*16*8];
			int pos = 0;
			
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					for(int y = 0; y < 8; y++)
					{
						pos = DungeonMath.getRoomPosFromCoords(x, y, z);
						blocks[pos] = (byte)chunk.getHandle().getBlock(x, (this.y*8)+y, z).getTypeId();
					}
				}
			}
			
			return blocks;
		}
		else if(tempRawBlocks != null)
		{
			/*
			 * If the chunk is NOT ready, retrieve the local blocks
			 */
			
			return tempRawBlocks;
		}
		
		return null;
	}
	
	/**
	 * Sets the raw blocks for the room.
	 *
	 * @param blocks the raw blocks
	 */
	public void setRawBlocks(byte[] blocks)
	{
		this.tempRawBlocks = blocks;
	}
	
	/**
	 * Gets the raw block data for the room.
	 *
	 * @return the raw block data byte array
	 */
	public byte[] getRawBlockData()
	{
		if(chunk != null && chunk.isReady())
		{
			/*
			 * If the chunk is in ready state, retrieve the real-world block data
			 */

			byte[] data = new byte[16*16*8];
			int pos = 0;
			
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					for(int y = 0; y < 8; y++)
					{
						pos = DungeonMath.getRoomPosFromCoords(x, y, z);
						data[pos] = chunk.getHandle().getBlock(x, (this.y*8)+y, z).getData();
					}
				}
			}
			
			return data;
		}
		else if(tempRawBlockData != null)
		{
			/*
			 * If the chunk is NOT ready, retrieve the local block data
			 */
			
			return tempRawBlockData;
		}
		
		return null;
	}
	
	/**
	 * Sets the raw block data for the room.
	 *
	 * @param blocks the raw block data
	 */
	public void setRawBlockData(byte[] blockData)
	{
		this.tempRawBlockData = blockData;
	}

	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#setLibraryId(long)
	 */
	@Override
	public void setLibraryId(long id) {
		this.libraryId = id;
	}

	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#getLibraryId()
	 */
	@Override
	public long getLibraryId() {
		return this.libraryId;
	}

	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#setFilename(java.lang.String)
	 */
	@Override
	public void setFilename(String name) {
		this.filename = name;
	}

	/* (non-Javadoc)
	 * @see com.aranai.dungeonator.dungeonchunk.IDungeonRoom#getFilename()
	 */
	@Override
	public String getFilename() {
		return this.filename;
	}
}