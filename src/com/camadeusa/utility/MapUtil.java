package com.camadeusa.utility;

import com.camadeusa.utility.ImageUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapUtil {
    private static HashMap<Integer, Short> mapCache = new HashMap();

    public static ItemStack createMap(BufferedImage image, World world) {
        ItemStack map = new ItemStack(Material.MAP);
        if (mapCache.containsKey(image.hashCode())) {
            map.setDurability(mapCache.get(image.hashCode()).shortValue());
        }
        image = image.getSubimage(0, 0, 128, 128);
        MapView view = Bukkit.createMap((World)world);
        for (MapRenderer mr : view.getRenderers()) {
            view.removeRenderer(mr);
        }
        view.setCenterX(92253);
        view.setCenterZ(92253);
        BoobRenderer br = new BoobRenderer();
        br.setImage(image);
        view.addRenderer((MapRenderer)br);
        map.setDurability(view.getId());
        mapCache.put(image.hashCode(), view.getId());
        return map;
    }

    public static ArrayList<ItemStack> createMaps(BufferedImage image, World world, int width, int height) {
        ArrayList<ItemStack> maps = new ArrayList<ItemStack>();
        if (image.getWidth() > width * 128 || image.getHeight() > height * 128) {
            image = image.getWidth() > image.getHeight() ? ImageUtil.toBufferedImage(image.getScaledInstance(width * 128, (int)Math.floor((float)image.getHeight() * ((float)width * 128.0f / (float)image.getWidth())), 4)) : ImageUtil.toBufferedImage(image.getScaledInstance((int)Math.floor((float)image.getWidth() * ((float)height * 128.0f / (float)image.getHeight())), height * 128, 4));
        }
        BufferedImage border = new BufferedImage(width * 128, height * 128, 1);
        Graphics2D graphics = border.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, width * 128, height * 128);
        graphics.drawImage(image, (width * 128 - image.getWidth()) / 2, (height * 128 - image.getHeight()) / 2, null);
        graphics.dispose();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                maps.add(MapUtil.createMap(border.getSubimage(x * 128, y * 128, 128, 128), world));
            }
        }
        return maps;
    }

    public static class BoobRenderer
    extends MapRenderer {
        private Image image;
        private static HashMap<Integer, ArrayList<String>> hasDrawnFor = new HashMap();

        public void setImage(Image image) {
            if (image.getWidth(null) != 128 || image.getHeight(null) != 128) {
                throw new IllegalArgumentException("Image size must be 128x128");
            }
            this.image = image;
            hasDrawnFor.put(image.hashCode(), new ArrayList());
        }

        public void render(MapView map, MapCanvas canvas, Player player) {
            ArrayList<String> drawList;
            if (this.image != null && !(drawList = hasDrawnFor.get(this.image.hashCode())).contains(player.getName())) {
                for (int i = 0; i < canvas.getCursors().size(); ++i) {
                    canvas.getCursors().removeCursor(canvas.getCursors().getCursor(i));
                }
                canvas.drawImage(0, 0, this.image);
                drawList.add(player.getName());
                player.sendMap(map);
            }
        }

        public static void removePlayerFromRenders(Player player) {
            String playerName = player.getName();
            for (ArrayList<String> list : hasDrawnFor.values()) {
                list.remove(playerName);
            }
        }
    }

}

