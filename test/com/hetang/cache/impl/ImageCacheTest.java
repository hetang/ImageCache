package com.hetang.cache.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hetashah on 6/26/16.
 */
public class ImageCacheTest {
    ImageCache imageCache;

    @Before
    public void setUp() throws Exception {
        imageCache = ImageCache.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        imageCache = null;
    }

    @Test
    public void setCache() throws Exception {
        imageCache.setCacheLimit(-1);
        assertEquals(113, imageCache.getCacheLimit());

        imageCache.setCacheLimit(10);
        assertEquals(10, imageCache.getCacheLimit());
    }

    @Test
    public void load() throws Exception {
        imageCache.setCacheLimit(2);

        //Using dummy images to test caching.
        byte[] image1 = imageCache.load("http://r.ddmcdn.com/s_f/o_1/cx_633/cy_0/cw_1725/ch_1725/w_720/APL/uploads/2014/11/too-cute-doggone-it-video-playlist.jpg");
        assertNotNull(image1);
        byte[] image2 = imageCache.load("http://e2ua.com/data/wallpapers/18/WDF_667595.jpg");
        assertNotNull(image2);

        assertEquals(2, imageCache.getCacheSize());

        byte[] image3 = imageCache.load("http://r.ddmcdn.com/s_f/o_1/cx_633/cy_0/cw_1725/ch_1725/w_720/APL/uploads/2014/11/too-cute-doggone-it-video-playlist.jpg");
        assertNotNull(image3);

        assertEquals(image3, image1);

        byte[] image4 = imageCache.load("http://www.am1150.ca/pics/Feeds/Articles/201617/111328/26084bf6-4235-4f8f-9c2f-b7294ea62c15.jpg");
        assertNotNull(image4);

        assertEquals(2, imageCache.getCacheSize());

        byte[] image5 = imageCache.load("http://animal-dream.com/data_images/dog/dog6.jpg");
        assertNotNull(image5);

        assertEquals(2, imageCache.getCacheSize());
    }
}