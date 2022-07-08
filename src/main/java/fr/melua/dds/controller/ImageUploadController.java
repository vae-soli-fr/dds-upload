package fr.melua.dds.controller;

import java.io.File;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.melua.dds.api.UploadApi;
import fr.melua.dds.service.ImageService;
import fr.melua.dds.service.ModifyMode;
import fr.melua.dds.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ImageUploadController implements UploadApi {

    private final ImageService imageService;

    public String uploadImage(MultipartFile imageFile, String name, String mode, Integer width, Integer height, RedirectAttributes redirectAttributes) {

        if(imageFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose file to upload.");
            return "redirect:/";
        }
        
        if(StringUtils.isBlank(name) || !StringUtils.hasLength(name, 3)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid name");
            return "redirect:/";
        }

        if(imageService.ddsExists(name, width, height)) {
            redirectAttributes.addFlashAttribute("errorMessage", "DDS already exists, choose another name.");
            return "redirect:/";
        }

        if(!imageService.check(imageFile)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid file type.");
            return "redirect:/";
        }
        
        ModifyMode modifyMode = ModifyMode.fromString(mode);
        
        if (modifyMode == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid mode.");
            return "redirect:/";
        }
        
        File original = imageService.upload(imageFile);
        
        if(original == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Upload failed.");
            return "redirect:/";

        }
        
        File modified = imageService.modify(original, name, modifyMode, width, height);

        imageService.delete(original);

        if(modified == null) {
            redirectAttributes.addFlashAttribute("errorMessage", modifyMode + " failed.");
            return "redirect:/";
        }
       
        String code = imageService.convert(modified);

        imageService.delete(modified);

        if(code == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Conversion failed.");
            return "redirect:/";
        }
       
        redirectAttributes.addFlashAttribute("successMessage", "File upload successfully.");
        redirectAttributes.addFlashAttribute("linkMessage", code);
        return "redirect:/";
    }

}
