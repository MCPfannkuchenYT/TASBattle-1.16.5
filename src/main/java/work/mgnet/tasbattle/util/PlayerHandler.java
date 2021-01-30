package work.mgnet.tasbattle.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerHandler {

    public static HashMap<String, Identifier> capes = new HashMap<String, Identifier>();

    public static Identifier fromPlayer(PlayerEntity player) {
        return capes.get(player.getUuidAsString());
    }

    public static void onPlayerJoin(PlayerEntity player) {

        if (((AbstractClientPlayerEntity) player).getCapeTexture() == null) {

            Thread thread = new Thread() {
                public void run() {
                    setCapeFromURL(player.getUuidAsString(), "http://mgnet.work/capes/" + player.getUuidAsString() + ".png");
                }
            };
            thread.start();

        }
    }

    public static boolean setCapeFromURL(String uuid, String capeStringURL) {
        try {
            URL capeURL = new URL(capeStringURL);
            setCape(uuid, capeURL.openStream());
            return true;
        } catch (IOException e) {
            capes.put(uuid, null);
            return false;
        }
    }

    public static void setCape(String uuid, InputStream image) {
        NativeImage cape = null;
        try {
            cape = NativeImage.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NativeImageBackedTexture nIBT = new NativeImageBackedTexture(parseCape(cape));
        Identifier capeTexture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(uuid.replace("-", ""), nIBT);
        capes.put(uuid, capeTexture);
    }

    public static NativeImage parseCape(NativeImage image) {
        int imageWidth = 160;
        int imageHeight = 80;
        int imageSrcWidth = image.getWidth() / 2;
        int srcHeight = image.getHeight() / 2;

        for (int imageSrcHeight = image.getHeight(); imageWidth < imageSrcWidth
                || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setPixelColor(x, y, image.getPixelColor(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}