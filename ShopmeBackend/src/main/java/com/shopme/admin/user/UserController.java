package com.shopme.admin.user;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.lowagie.text.DocumentException;
import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserServices service;
	
	@Autowired 
	private RoleRepository roleRepository;
	
	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "id", "asc", null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(Model model, 
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * UserServices.USERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + UserServices.USERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Users (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Users");
		}
		
		return "users/users";
	}
	
	@GetMapping("/users/new")
	public ModelAndView newUser() {
		User user = new User();
		ModelAndView mav = new ModelAndView("users/user_form");
		mav.addObject("user", user);
		
		List<Role> roles = (List<Role>) roleRepository.findAll();
		
		mav.addObject("allRoles", roles);
		mav.addObject("pageTitle", "Create New User");
		
		return mav;
		
	}
	
	@PostMapping("/users/save")
	public RedirectView saveUser(User user, RedirectAttributes ra, HttpServletRequest request, 
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		String redirectURL = generateRedirectURLToNewlyCreatedUser(user, request);
		RedirectView rv = new RedirectView(redirectURL, true);
		ra.addFlashAttribute("message", "The user has been saved successfully.");
		
		return rv;
	}
	
	private String generateRedirectURLToNewlyCreatedUser(User user, HttpServletRequest request) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public ModelAndView editUser(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			User user = service.get(id);
			ModelAndView mav = new ModelAndView("users/user_form");
			mav.addObject("user", user);
			
			List<Role> roles = (List<Role>) roleRepository.findAll();
			
			mav.addObject("allRoles", roles);
			mav.addObject("pageTitle", String.format("Edit User (ID: %d)", user.getId()));
			
			return mav;
		} catch (UserNotFoundException ex) {
			ModelAndView mav = new ModelAndView("redirect:/users");
			ra.addFlashAttribute("message", ex.getMessage());
			
			return mav;	
		}
		
	}
	
	@GetMapping("/users/delete/{id}")
	public RedirectView deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws IOException {
		RedirectView rv = new RedirectView("/users", true);
		
		try {
			service.delete(id);
			String userPhotoDir = "user-photos/" + id;
			FileSystemUtils.deleteRecursively(Paths.get(userPhotoDir));
			
			ra.addFlashAttribute("message", String.format("The user ID %d has been deleted successfully.", id));
		} catch (UserNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
					
		return rv;
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public RedirectView updateUserEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes ra) {
		service.updateUserEnabledStatus(id, enabled);
		RedirectView rv = new RedirectView("/users", true);
		String status = enabled ? "enabled" : "disabled";
		ra.addFlashAttribute("message", String.format("The user ID %d has been %s.", id, status));
		
		return rv;
	}
	
	@GetMapping("/users/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=users_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);
		
		List<User> listUsers = service.listAll();

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
		String[] nameMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (User user : listUsers) {
			csvWriter.write(user, nameMapping);
		}
		
		csvWriter.close();
		
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);
		
		List<User> listUsers = service.listAll();
		
		UserExcelExporter excelExporter = new UserExcelExporter(listUsers);
		
		excelExporter.export(response);
		
	}
	
	@GetMapping("/users/export/pdf") 
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
		response.setHeader(headerKey, headerValue);
		
		List<User> listUsers = service.listAll();
		
		UserPDFExporter exporter = new UserPDFExporter(listUsers);
		exporter.export(response);
		
	}
}