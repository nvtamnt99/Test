package com.shopme.admin.brand;

import java.io.IOException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryServices;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {

	@Autowired
	private BrandServices brandService;
	
	@Autowired
	private CategoryServices categoryServices;
	
	@GetMapping("/brands")
	public String listAll(Model model) {
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		Page<Brand> page = brandService.listAll(pageNum, sortField, sortDir, keyword);
		List<Brand> listBrands = page.getContent();
		
		model.addAttribute("listBrands", listBrands);
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");		
		
		long startCount = (pageNum - 1) * BrandServices.BRANDS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + BrandServices.BRANDS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Brands (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Brands");
		}		
		return "brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryServices.listAll();
		
		model.addAttribute("listCategories", listCategories);
		
		model.addAttribute("pageTitle", "Create New Brand");
		model.addAttribute("brand", new Brand());
		
		return "brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(@ModelAttribute(name = "brand") Brand brand, HttpServletRequest request,
			@RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes ra) throws IOException {
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = brandService.save(brand);
			
			String uploadDir = "../brand-logos/" + savedBrand.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);	
		} else {
			brandService.save(brand);
		}		
		
		ra.addFlashAttribute("message", "The brand has been saved successfully.");
		
		return generateRedirectURLToNewlyCreatedBrand(brand, request);
	}
	
	private String generateRedirectURLToNewlyCreatedBrand(Brand brand, HttpServletRequest request) {
		return "redirect:/brands/page/1?sortField=id&sortDir=asc&keyword=" + brand.getName();
	}	
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			brandService.delete(id);
			
			ra.addFlashAttribute("message", "The brand ID " + id + " has been deleted.");
		} catch (BrandNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id, RedirectAttributes ra,
			Model model) {
		try {
			Brand brand = brandService.get(id);
			List<Category> listCategories = categoryServices.listAll();
			
			model.addAttribute("brand", brand);
			model.addAttribute("pageTitle", "Edit Brand (ID: " + brand.getId() + ")");
			model.addAttribute("listCategories", listCategories);
			
			return "brand_form";
		} catch (BrandNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/brands";
		}
		
	}
	
	@GetMapping("/brands/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=brands_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);
		
		List<Brand> listBrands = brandService.listAll();

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Brand ID", "Brand Name", "Categories"};
		String[] nameMapping = {"id", "name", "categoriesNames"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (Brand brand: listBrands) {
			csvWriter.write(brand, nameMapping);
		}
		
		csvWriter.close();
		
	}	
}
