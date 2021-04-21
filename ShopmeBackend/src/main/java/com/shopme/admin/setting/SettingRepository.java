package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

public interface SettingRepository extends JpaRepository<Setting, String> {

	@Modifying
	@Query("UPDATE Setting s "
			+ "SET s.value = ?1 WHERE s.key='currency_id'")
	public void update(String currencyId);
	
	public List<Setting> findByCategory(SettingCategory category);	

	
}
