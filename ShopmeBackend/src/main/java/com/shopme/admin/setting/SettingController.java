package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.currency.CurrencyRepository;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

@Controller
public class SettingController {

	@Autowired
	private SettingServices service;
	
	@Autowired
	private CurrencyRepository currencyRepo;
	
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> allSettings = service.getAllSettings();
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
		
		model.addAttribute("pageTitle", "Settings");
		model.addAttribute("currencies", listCurrencies);
		
		for (Setting setting : allSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		return "settings/settings";
	}
	
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(HttpServletRequest request, @RequestParam("fileImage") MultipartFile multipartFile, 
			RedirectAttributes ra) throws IOException {
		SettingBag settingBag = service.getGeneralSettings();
		
		saveCurrencySymbol(request, settingBag);
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);			
		}
		
		updateSettings(settingBag.list(), request);
		
		ra.addFlashAttribute("message", "General settings have been saved.");
		
		return "redirect:/settings";
	}


	private void saveCurrencySymbol(HttpServletRequest request, SettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> currencyById = currencyRepo.findById(currencyId);
		
		if (currencyById.isPresent()) {
			Currency currency = currencyById.get();
			String symbol = currency.getSymbol();
			settingBag.updateCurrencySymbol(symbol);
		}
	}
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra) throws IOException {
		List<Setting> mailServerSettings = service.getMailServerSettings();
		
		updateSettings(mailServerSettings, request);
		
		ra.addFlashAttribute("message", "Mail server settings have been saved.");
		
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplates(HttpServletRequest request, RedirectAttributes ra) throws IOException {
		List<Setting> mailTemplateSettings = service.getMailTemplatesSettings();
		
		updateSettings(mailTemplateSettings, request);
		
		ra.addFlashAttribute("message", "Mail templates settings have been saved.");
		
		return "redirect:/settings";
	}	
	
	@PostMapping("/settings/save_payment")
	public String savePayment(HttpServletRequest request, RedirectAttributes ra) throws IOException {
		List<Setting> paymentSettings = service.getPaymentSettings();
		
		updateSettings(paymentSettings, request);
		
		ra.addFlashAttribute("message", "Payment settings have been saved.");
		
		return "redirect:/settings";
	}	
	
	private void updateSettings(List<Setting> listSettings, HttpServletRequest request) {
		for (Setting setting : listSettings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}			
		}	
		
		service.saveAll(listSettings);		
	}
}
