package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.junit.Test;

import static org.junit.Assert.*;

public class GroundLayerTest {
    @Test
    public void repeatsGroundTilesToFitALargerLogicalLevel() {
        TiledMap map = new TiledMap();
        TiledMapTileLayer original = new TiledMapTileLayer(2, 1, 128, 128);
        original.setName("Ground");
        TiledMapTileLayer.Cell first = new TiledMapTileLayer.Cell();
        TiledMapTileLayer.Cell second = new TiledMapTileLayer.Cell();
        original.setCell(0, 0, first);
        original.setCell(1, 0, second);
        map.getLayers().add(original);
        TiledMapTileLayer ground = RenderUtils.resizeGroundLayer(map, 5, 3);
        assertSame(ground, map.getLayers().get(0));
        assertEquals(1, map.getLayers().size());
        assertEquals("Ground", ground.getName());
        assertEquals(5, ground.getWidth());
        assertEquals(3, ground.getHeight());
        assertEquals(128, ground.getTileWidth());
        assertSame(first, ground.getCell(4, 2));
        assertSame(second, ground.getCell(3, 1));
        assertEquals(5, map.getProperties().get("width"));
        assertEquals(3, map.getProperties().get("height"));
    }

    @Test
    public void canFitASmallerLogicalLevel() {
        TiledMap map = new TiledMap();
        map.getLayers().add(new TiledMapTileLayer(10, 8, 128, 128));
        TiledMapTileLayer ground = RenderUtils.resizeGroundLayer(map, 1, 1);
        assertEquals(1, ground.getWidth());
        assertEquals(1, ground.getHeight());
        assertNull(ground.getCell(1, 0));
    }
}
