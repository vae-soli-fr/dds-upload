package fr.melua.dds.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.melua.dds.util.DDSConverter;
import fr.melua.dds.util.MathUtils;
import fr.melua.dds.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageService {
	
	private static final String JPEG = "jpg";
	private static final String DIRECTDRAW = "dds";

    @Value("${image.folder}")
    private String imageFolder;
    
    public boolean check(MultipartFile imageFile) {
        try {    	
            Tika tika = new Tika();
            String mime = tika.detect(imageFile.getBytes());
            
            if ("image/jpeg".equals(mime) || "image/png".equals(mime) || "image/gif".equals(mime)) {
            	return true;
            }
            
            log.warn("Suspicious file {} with mime type {}", imageFile.getOriginalFilename(), mime);
            return false;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    public File upload(MultipartFile imageFile) {
        try {    	
            Path path = Paths.get(imageFolder, StringUtils.join(".", UUID.randomUUID().toString(), FilenameUtils.getExtension(imageFile.getOriginalFilename())));
            Files.write(path, imageFile.getBytes());
            log.info("Uploaded {}", path);

            return path.toFile();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public File resize(File sourceFile, String name, int width, int height) {
    	
    	if (!MathUtils.isPow2(width) || !MathUtils.isPow2(height)) {
    		return null;
    	}
    	
        try {
            BufferedImage sourceImage = ImageIO.read(sourceFile);
            
            // Remove alpha (PNG, GIF source)
            BufferedImage bufferedImage = new BufferedImage( sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics().drawImage(sourceImage, 0, 0, Color.WHITE, null);
            
            BufferedImage outputImage = Scalr.resize(bufferedImage, Mode.FIT_EXACT, width, height);
            
            File newImageFile = Paths.get(imageFolder, StringUtils.join(".", StringUtils.join("_", sanitizer(name), width, height), JPEG)).toFile();
            ImageIO.write(outputImage, JPEG, newImageFile);
            outputImage.flush();
            log.info("Resized {} => {}", sourceFile, newImageFile);

            return newImageFile;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public String convert(File newFile) {
        try {
        	byte[] data = DDSConverter.convertToDDS(newFile).array();
        	Path path = Paths.get(imageFolder, StringUtils.join(".", FilenameUtils.removeExtension(newFile.getName()), DIRECTDRAW));
        	Files.write(path, data);
        	log.info("Converted {} => {}", newFile, path);

        	return StringUtils.concat("[img]", path.getFileName(), "[/img]");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    
    public boolean delete(File file) {
        try {
        	FileUtils.delete(file);
        	log.info("Deleted {}", file);

        	return true;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean ddsExists(String name, int width, int height) {
    	return Files.exists(Paths.get(imageFolder, StringUtils.join(".", StringUtils.join("_", sanitizer(name), width, height), DIRECTDRAW)));
    }

    private static String sanitizer(String name) {
    	return name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
    
}
