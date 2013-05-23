package shukaro.enderore.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import shukaro.enderore.EnderOre;
import shukaro.enderore.util.BlockCoord;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGen implements IWorldGenerator
{
	private static List<Integer> blacklistedDimensions;
	private static List<String> blacklistedWorldtypes;
	private BlockCoord coord = new BlockCoord();
	private int x1;
	private int y1;
	private int z1;
	private List<BlockCoord> list = new ArrayList<BlockCoord>();
	private int genned;
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		generateWorld(random, chunkX, chunkZ, world, true);
	}
	
	public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen)
	{
		if (!EnderOre.genOre.getBoolean(true))
			return;
		
		if (blacklistedDimensions == null)
			blacklistedDimensions = EnderOre.dimBlacklist;
		
		if (blacklistedWorldtypes == null)
			blacklistedWorldtypes = EnderOre.worldTypeBlacklist;
		
		if (blacklistedDimensions.contains(world.provider.dimensionId))
			return;
		
		if (blacklistedWorldtypes.contains(world.getWorldInfo().getTerrainType().getWorldTypeName()))
			return;
		
		for (int i=0; i<EnderOre.enderOreFrequency.getInt(); i++)
		{
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = EnderOre.enderOreMinHeight.getInt() + (int)(Math.random() * ((EnderOre.enderOreMaxHeight.getInt() - EnderOre.enderOreMinHeight.getInt()) + 1));
            generateOre(world, random, x, y, z);
		}
		
		if (!newGen)
			world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
	}
	
	private boolean generateOre(World world, Random rand, int x, int y, int z)
	{
		int numberOfBlocks = rand.nextInt(EnderOre.enderOreSize.getInt()) + 3;
		
		genned = 0;

        coord.set(x, y, z);
        
        while (genned < numberOfBlocks)
        {
        	list.clear();
        	
	    	if (!(Block.blocksList[world.getBlockId(coord.x, coord.y, coord.z)] == null) && world.getBlockId(coord.x, coord.y, coord.z) == Block.stone.blockID)
	    	{
	    		world.setBlock(coord.x, coord.y, coord.z, EnderOre.blockEnderOre.blockID, 0, 2);
	    		genned++;
	    	}
	    	
	    	if (genned >= numberOfBlocks)
	    		return true;
	    	
    		for (BlockCoord c : coord.getAdjacent())
    		{
    			if (!(Block.blocksList[world.getBlockId(coord.x, coord.y, coord.z)] == null) && world.getBlockId(c.x, c.y, c.z) == Block.stone.blockID)
    				list.add(c);
    		}
    		
    		if (list.size() != 0)
    			coord.set(list.get(rand.nextInt(list.size())));
    		else
    			return true;
        }
        
        return true;
	}
}
