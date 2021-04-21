package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
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
import org.springframework.web.servlet.view.RedirectView;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import com.shopme.admin.brand.BrandServices;
import com.shopme.admin.category.CategoryServices;
import com.shopme.admin.security.ShopmeUserDetails;

@Controller
public class ProductController {

	@Autowired
	private ProductServices productService;
	
	@Autowired
	private CategoryServices categoryService;
	
	@Autowired
	private BrandServices brandService;
	
	@GetMapping("/products")
	public String listAll(Model model) {
		return listByPage(1, model, "name", "asc", null, 0);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			@Param("category") Integer category) {
		List<Category> listCategories = categoryService.listAll();
		
		Page<Product> page = productService.listAll(pageNum, sortField, sortDir, keyword, category);
		List<Product> listProducts = page.getContent();
		
		model.addAttribute("listCategories", listCategories);
		
		if (category != null) model.addAttribute("category", category);
				
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listProducts", listProducts);
		
		long startCount = (pageNum - 1) * ProductServices.PRODUCTS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ProductServices.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Products (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Products");
		}		
		
		return "products/products";
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			model.addAttribute("product", product);
			
			return "products/product_detail_modal";
		} catch (ProductNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any products with ID " + id);
			return "redirect:/products";
		}
		
	}	
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {		
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setInStock(true);
		product.setEnabled(true);
		
		model.addAttribute("product", product);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("listBrands", listBrands);
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(@ModelAttribute(name = "product") Product product, RedirectAttributes ra,
			HttpServletRequest request, @AuthenticationPrincipal ShopmeUserDetails loggedUser,
			@RequestParam(value = "image", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipart,
			@RequestParam(value = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(value = "imageNames", required = false) String[] imageNames,
			@RequestParam(value = "detailIDs", required = false) Integer[] detailIDs,
			@RequestParam(value = "detailNames", required = false) String[] detailNames,
			@RequestParam(value = "detailValues", required = false) String[] detailValues) throws IOException {
		
		if (loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			
			ra.addFlashAttribute("message", "The product has been saved successfully.");
			return "redirect:/products";
		}
		
		setProductDetails(product, detailIDs, detailNames, detailValues);
		
		getImagesInfo(product, imageIDs, imageNames);
			
		setExtraImageNames(product, extraImageMultipart);

		setMainImageName(product, mainImageMultipart);
		
		Product savedProduct = productService.save(product);
		
		saveUploadedImages(mainImageMultipart, extraImageMultipart, savedProduct);		
		
		deleteExtraImagesWereRemoved(savedProduct);
		
		ra.addFlashAttribute("message", "The product has been saved successfully.");
		
		return generateRedirectURLToNewlyCreatedProduct(savedProduct, request);
	}
	
	private String generateRedirectURLToNewlyCreatedProduct(Product product, HttpServletRequest request) {
		String keyword = product.getName().length() > 30 ? product.getName().substring(0, 30) : product.getName();
		return "redirect:/products/page/1?sortField=id&sortDir=asc&keyword=" + keyword;
	}	

	private void setProductDetails(Product product, Integer[] detailIDs, String[] detailNames, String[] detailValues) {
		if (detailNames == null || detailNames.length == 0) return;
		
		int count = 0;
		for (Integer id: detailIDs) {
			String name = detailNames[count];
			String value = detailValues[count];
			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
			count++;
		}
	}
	
	private void deleteExtraImagesWereRemoved(Product product) {
		String extraImageDir = "../product-images/" + product.getId() + "/extras/";
		
		Path dirPath = Paths.get(extraImageDir);
		try {
			Files.list(dirPath).forEach(file -> { 
				String fileName = file.toFile().getName();
				if (!product.containsImageName(fileName))
					try {
						Files.delete(file);
						System.out.println("Deleted file: " + fileName);
					} catch (IOException e) {
						System.out.println("Could not delete file: " + fileName);
					}
			});	
		} catch (IOException ex) {
			System.out.println("Could not list directory: " + dirPath);
		}		
	}
	
	private void setExtraImageNames(Product product, MultipartFile[] extraImageMultipart) {
		if (extraImageMultipart.length > 0) {
			for (MultipartFile extraMultipartFile : extraImageMultipart) {
				if (extraMultipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(extraMultipartFile.getOriginalFilename());
				
				if (!product.containsImageName(fileName)) {
					product.addExtraImage(fileName);
				}
			}
		}
	}

	private void getImagesInfo(Product product, String[] imageIDs, String[] imageNames) {
		Set<ProductImage> images = new HashSet<>();
		
		if (imageIDs != null && imageIDs.length > 0) { 
			for (int i = 0; i < imageIDs.length; i++) {
				Integer id = Integer.valueOf(imageIDs[i]);
				String name = imageNames[i];
				images.add(new ProductImage(id, name, product));
			}
		}
		
		product.setImages(images);
	}
	
	private void setMainImageName(Product product, MultipartFile mainImageMultipart) {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}

	private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
			Product savedProduct) throws IOException {
		String uploadDir = "../product-images/" + savedProduct.getId();
		
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);		
		}
		
		if (extraImageMultipart.length > 0) {
			uploadDir = "../product-images/" + savedProduct.getId() + "/extras/";
			
			for (MultipartFile extraMultipartFile : extraImageMultipart) {
				if (extraMultipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(extraMultipartFile.getOriginalFilename());
				
				FileUploadUtil.saveFile(uploadDir, fileName, extraMultipartFile);	
			}
		}
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			model.addAttribute("product", product);

			List<Brand> listBrands = brandService.listAll();
			
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("listBrands", listBrands);
			
			return "products/product_form";
			
		} catch (ProductNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable Integer id, RedirectAttributes ra) {
		try {
			productService.delete(id);
			
			ra.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully.");
			
		} catch (ProductNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public RedirectView updateProductEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes ra) {
		productService.updateProductEnabledStatus(id, enabled);
		RedirectView rv = new RedirectView("/products", true);
		String status = enabled ? "enabled" : "disabled";
		ra.addFlashAttribute("message", String.format("The product ID %d has been %s.", id, status));
		
		return rv;
	}	
}

