package fr.melua.dds.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface UploadApi {

	@GetMapping
	default String uploadImage() {
		return "uploadImage";
	}

	@PostMapping("/uploadImage")
	String uploadImage(@RequestParam("image") MultipartFile imageFile, @RequestParam("name") @Size(max = 30) String name,
			@RequestParam(value = "mode") String mode,
			@RequestParam(value = "width", defaultValue = "256") @Min(32) @Max(256) Integer width,
			@RequestParam(value = "height", defaultValue = "256") @Min(32) @Max(256) Integer height,
			RedirectAttributes redirectAttributes);
}
