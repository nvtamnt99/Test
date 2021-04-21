package com.shopme.admin.category;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {

	@Autowired
	private CategoryServices service;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword
			) {
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listAll(pageNum, pageInfo, sortField, sortDir, keyword);
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());		
		model.addAttribute("currentPage", pageNum);
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("keyword", keyword);
		
		long startCount = (pageNum - 1) * CategoryServices.CATS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + CategoryServices.CATS_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);		
		
		model.addAttribute("listCategories", listCategories);
		
		if (pageInfo.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Categories (Page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Categories");
		}
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/search")
	public String search(Model model,
			@Param("keyword") String keyword
			) {
		
		List<Category> listCategories = service.search(keyword);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Categories Search Result");
		model.addAttribute("keyword", keyword);
		
		return "categories/categories";
	}
	
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		model.addAttribute("category", new Category(true));
		
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public RedirectView saveCategory(@ModelAttribute(name = "category") Category category,
			HttpServletRequest request,
			@RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes ra) 
					throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = service.save(category);
			
			String uploadDir = "../category-images/" + savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);	
		} else {
			service.save(category);
		}		
		
		ra.addFlashAttribute("message", "The category has been saved successfully.");
		String redirectURL = generateRedirectURLToNewlyCreatedCategory(category, request);
		RedirectView rv = new RedirectView(redirectURL, true);
		
		return rv;
	}
	
	private String generateRedirectURLToNewlyCreatedCategory(Category category, HttpServletRequest request) {
		return "/categories/search?sortField=id&sortDir=asc&keyword=" + category.getAlias();
	}	
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listAll();
			model.addAttribute("listCategories", listCategories);
			
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category (ID: " + category.getId() + ")");		
			
			return "categories/category_form";
		} catch (CategoryNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws IOException {
		try {
			service.delete(id);
			String categoryImageDir = "category-images/" + id;
			FileSystemUtils.deleteRecursively(Paths.get(categoryImageDir));
			
			ra.addFlashAttribute("message", "The category ID " + id + " has been deleted.");
		} catch (CategoryNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/categories";		
	}

	@GetMapping("/categories/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=categories_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);
		
		List<Category> listCategories = service.listAll();

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Category ID", "Category Name", "Enabled"};
		String[] nameMapping = {"id", "name", "enabled"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (Category cat : listCategories) {
			cat.setName(cat.getName().replace("-", "   "));
			csvWriter.write(cat, nameMapping);
		}
		
		csvWriter.close();
		
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public RedirectView updateUserEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes ra) {
		service.updateCategoryEnabledStatus(id, enabled);
		RedirectView rv = new RedirectView("/categories", true);
		String status = enabled ? "enabled" : "disabled";
		ra.addFlashAttribute("message", String.format("The category ID %d has been %s.", id, status));
		
		return rv;
	}	
}
