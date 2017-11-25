package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import pojos.Track;

public class CustomImageUtil {
	
	public static final String DEFAULT_COVER_PATH = "/images/img_default_cover.png";

	public static Image setCoverImage(Track track) {
		Image cover;
        if (track.getCoverImage().isPresent()) {
            byte[] coverBytes = track.getCoverImage().get();
            cover = new Image(new ByteArrayInputStream(coverBytes));
        }
        else {
            cover = new Image(DEFAULT_COVER_PATH);
        }
        return cover;
    }
	
	public static byte[] extractBytes (String ImageName) throws IOException {
		 File imgPath = new File(ImageName);
		 BufferedImage bufferedImage = ImageIO.read(imgPath);

		 WritableRaster raster = bufferedImage .getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return data.getData();
		}
}
