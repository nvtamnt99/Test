package com.shopme.admin.user;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	private UserServices service;
	
	@GetMapping("/account")
	public String viewUserAccountForm(@AuthenticationPrincipal ShopmeUserDetails userDetails,
			Model model) {
		String userEmail = userDetails.getUsername();
		User user = service.getByEmail(userEmail);
		
		model.addAttribute("user", user);
		model.addAttribute("pageTitle", "Account Details");
		
		return "users/account_form";
	}
	
	@PostMapping("/account/update")
	public String updateUserAccountDetails(User user, 
			RedirectAttributes ra, @AuthenticationPrincipal ShopmeUserDetails userDetails,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			user.setPhotos(fileName);
			
			User savedUser = service.updateAccount(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.updateAccount(user);
		}
		
		userDetails.setFirstName(user.getFirstName());
		userDetails.setLastName(user.getLastName());
		
		ra.addFlashAttribute("message", "Your account details have been updated.");
		
		return "redirect:/account";
	}
}
