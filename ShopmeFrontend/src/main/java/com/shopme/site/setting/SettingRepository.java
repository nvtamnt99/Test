package com.shopme.site.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

public interface SettingRepository extends JpaRepository<Setting, String> {
	public List<Setting> findByCategory(SettingCategory category);
	
	@Query("SELECT s FROM Setting s WHERE s.key = ?1")
	public Setting findByKey(String key);	
}
