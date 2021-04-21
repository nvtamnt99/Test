package com.shopme.site.section;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Section;

public interface SectionRepository extends JpaRepository<Section, Integer> {
	public List<Section> findAllByEnabledOrderBySectionOrderAsc(boolean enabled);
}
