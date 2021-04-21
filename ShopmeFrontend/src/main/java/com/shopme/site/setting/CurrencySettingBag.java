package com.shopme.site.setting;

import java.util.List;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

public class CurrencySettingBag extends SettingBag {

	public CurrencySettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public String getSymbol() {
		return super.getValue("CURRENCY_SYMBOL");
	}
	
	public String getSymbolPosition() {
		return super.getValue("CURRENCY_SYMBOL_POSITION");
	}
	
	public String getDecimalPointType() {
		return super.getValue("DECIMAL_POINT_TYPE");
	}
	
	public String getThousandPointType() {
		return super.getValue("THOUSANDS_POINT_TYPE");
	}
	
	public String getDecimalDigits() {
		return super.getValue("DECIMAL_DIGITS");
	}	
}
