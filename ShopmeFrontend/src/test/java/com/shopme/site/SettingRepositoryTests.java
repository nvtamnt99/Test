package com.shopme.site;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.Setting;
import com.shopme.site.setting.SettingRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EntityScan("com.shopme.common.entity")
public class SettingRepositoryTests {
	
	@Autowired
	private SettingRepository repo;
	
	@Test
	public void testLoadAllSettings() {
		List<Setting> settings = repo.findAll();
		
		for (Setting setting : settings) {
			System.out.println(setting);
		}
		
		assertThat(settings).isNotEmpty();
	}
}
