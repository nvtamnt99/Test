package com.shopme.site.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;
import com.shopme.site.currency.CurrencyRepository;

@Service
public class SettingServices {

	@Autowired
	private SettingRepository settingRepo;
	
	@Autowired
	private CurrencyRepository currencyRepo;
	
	public List<Setting> getCurrencySettings() {
		return getSettings(SettingCategory.CURRENCY);
	}
	
	public CurrencySettingBag getCurrencySettingBag() {
		return new CurrencySettingBag(getSettings(SettingCategory.CURRENCY));
	}
	
	public List<Setting> getGeneralSettings() {
		return getSettings(SettingCategory.GENERAL);
	}

	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = getSettings(SettingCategory.MAIL_SERVER);
		settings.addAll(getSettings(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(settings);
	}
	
	public PaymentSettingBag getPaymentSettings() {
		return new PaymentSettingBag(getSettings(SettingCategory.PAYMENT));
	}
	
	private List<Setting> getSettings(SettingCategory category) {
		return settingRepo.findByCategory(category);
	}
	
	public String getCurrencyCode() {
		Setting setting = settingRepo.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		Currency currency = currencyRepo.findById(currencyId).get();
		
		return currency.getCode();
	}	
}
