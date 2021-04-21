package com.shopme.admin.section;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Section;

public interface SectionRepository extends JpaRepository<Section, Integer> {
	public List<Section> findAllByOrderBySectionOrderAsc();
	
	@Query("SELECT NEW Section(s.id) FROM Section s ORDER BY s.sectionOrder ASC")
	public List<Section> findSectionsForUpdatingPosition();
	
	@Query("UPDATE Section s SET s.enabled = ?2 WHERE s.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public Long countById(Integer id);	
	
	@Query("SELECT NEW Section(s.id) FROM Section s WHERE s.id = ?1")
	public Section findSimpleSectionById(Integer id);
	
	@Query("UPDATE Section s SET s.sectionOrder = ?1 WHERE s.id = ?2")
	@Modifying
	public void updateSectionPosition(int sectionOrder, Integer id);
}
