package com.shopme.admin.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingServices {

	@Autowired
	private SettingRepository repo;
	
	public List<Setting> getAllSettings() {
		return repo.findAll();
	}
	
	public SettingBag getGeneralSettings() {
		List<Setting> generalSettings = repo.findByCategory(SettingCategory.GENERAL);
		return new SettingBag(generalSettings);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		repo.saveAll(settings);
	}
			
	public List<Setting> getCurrencySettings() {
		return repo.findByCategory(SettingCategory.CURRENCY);
	}
	
	public List<Setting> getMailServerSettings() {
		return repo.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplatesSettings() {
		return repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	public List<Setting> getPaymentSettings() {
		return repo.findByCategory(SettingCategory.PAYMENT);
	}	
}
