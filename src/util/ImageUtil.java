package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;

public class ImageUtil {

	public static final String DEFAULT_COVER_PATH = "/images/img_default_cover.png";

	public static Image setCoverImage(byte[] coverByte) {
		Image cover;
		if (coverByte != null) {
			cover = new Image(new ByteArrayInputStream(coverByte));
		} else {
			cover = new Image(DEFAULT_COVER_PATH);
		}
		return cover;
	}

	public static byte[] extractBytes(String name) throws IOException {
		File imgPath = new File(name);
		BufferedImage bufferedImage = ImageIO.read(imgPath);

		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

		return data.getData();
	}
}
