package ua.dp.eremeev;


import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class LongMapImplTest {

    @Test
    public void testPut() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");

        Assert.assertEquals("{1=Test}", map.toString());
    }

    @Test
    public void testGet() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");

        Assert.assertEquals("Test", map.get(1));
    }

    @Test
    public void removeTest() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals("Test2", map.remove(2));

    }

    @Test
    public void isEmptyTest() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals(false, map.isEmpty());
    }

    @Test
    public void isEmptyTest2() {
        LongMap map = new LongMapImpl();

        Assert.assertEquals(true, map.isEmpty());
    }

    @Test
    public void containsKey() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals(true, map.containsKey(1));

    }

    @Test
    public void notContainsKey() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals(false, map.containsKey(3));

    }

    @Test
    public void containsValue() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals(true , map.containsValue("Test"));
    }

    @Test
    public void notContainsValue() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals(false, map.containsValue("Tes"));
    }

    @Test
    public void keys() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals("[1, 2]", Arrays.toString(map.keys()));
    }

    @Test
    public void values() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals("[Test, Test2]", Arrays.toString(map.values()));
    }

    @Test
    public void size() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");

        Assert.assertEquals(2, map.size());
    }

    @Test
    public void clear() {
        LongMap map = new LongMapImpl();
        map.put(1L, "Test");
        map.put(2L, "Test2");
        map.clear();
        Assert.assertEquals("{}", map.toString());

    }
}