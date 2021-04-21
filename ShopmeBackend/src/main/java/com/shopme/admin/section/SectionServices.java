package com.shopme.admin.section;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Section;

@Service
@Transactional
public class SectionServices {

	@Autowired
	private SectionRepository sectionRepo;
	
	public void saveSection(Section section) {
		if (section.getId() == null) {
			setPositionForNewSection(section);
		}
		sectionRepo.save(section);
	}
	
	public List<Section> listSections() {
		return sectionRepo.findAllByOrderBySectionOrderAsc();
	}

	private void setPositionForNewSection(Section section) {
		Long newPosition = sectionRepo.count() + 1;
		section.setSectionOrder(newPosition.intValue());
	}
	
	public void enableSection(Integer id) throws SectionNotFoundException {
		updateSectionEnabledStatus(id, true);
	}
	
	public void disableSection(Integer id) throws SectionNotFoundException {
		updateSectionEnabledStatus(id, false);
	}
	
	private void updateSectionEnabledStatus(Integer id, boolean enabled) 
			throws SectionNotFoundException {
		Long countById = sectionRepo.countById(id);
		if (countById > 0) {
			sectionRepo.updateEnabledStatus(id, enabled);
		} else {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
	}
	
	public void moveSectionUp(Integer id) throws SectionNotFoundException, SectionUnmoveableException {
		Section currentSection = sectionRepo.findSimpleSectionById(id);
		if (currentSection == null) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		List<Section> allSections = sectionRepo.findSectionsForUpdatingPosition();
		
		int currentSectionIndex = allSections.indexOf(currentSection);
		if (currentSectionIndex == 0) {
			throw new SectionUnmoveableException("The section ID " + id + " is already in the first position");
		}
		
		int previousSectionIndex = currentSectionIndex - 1;
		Section previousSection = allSections.get(previousSectionIndex);
		
		currentSection.setSectionOrder(previousSectionIndex + 1);		
		previousSection.setSectionOrder(currentSectionIndex + 1);
		
		sectionRepo.updateSectionPosition(currentSection.getSectionOrder(), currentSection.getId());
		sectionRepo.updateSectionPosition(previousSection.getSectionOrder(), previousSection.getId());
	}
	
	public void moveSectionDown(Integer id) throws SectionNotFoundException, SectionUnmoveableException {
		Section currentSection = sectionRepo.findSimpleSectionById(id);
		if (currentSection == null) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		List<Section> allSections = sectionRepo.findSectionsForUpdatingPosition();
		
		int currentSectionIndex = allSections.indexOf(currentSection);
		if (currentSectionIndex == allSections.size() - 1) {
			throw new SectionUnmoveableException("The section ID " + id + " is already in the last position");
		}
		
		int nextSectionIndex = currentSectionIndex + 1;
		Section nextSection = allSections.get(nextSectionIndex);
		
		currentSection.setSectionOrder(nextSectionIndex + 1);		
		nextSection.setSectionOrder(currentSectionIndex + 1);
		
		sectionRepo.updateSectionPosition(currentSection.getSectionOrder(), currentSection.getId());
		sectionRepo.updateSectionPosition(nextSection.getSectionOrder(), nextSection.getId());
	}
	
	public void deleteSection(Integer id) throws SectionNotFoundException {
		Section currentSection = sectionRepo.findSimpleSectionById(id);
		if (currentSection == null) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		sectionRepo.deleteById(id);
	}
	
	public Section getSection(Integer id) throws SectionNotFoundException {
		try {
			return sectionRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
	}
}
