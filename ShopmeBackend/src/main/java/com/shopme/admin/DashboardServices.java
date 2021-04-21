package com.shopme.admin;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServices {
	@Autowired
	private EntityManager entityManager;
	
	public DashboardInfo loadSummary() {
		DashboardInfo summary = new DashboardInfo();
		Query query = entityManager.createQuery("SELECT "
					+ "(SELECT COUNT(DISTINCT u.id) AS totalUsers FROM User u),"
					+ "(SELECT COUNT(DISTINCT c.id) AS totalCategories FROM Category c), "
					+ "(SELECT COUNT(DISTINCT b.id) AS totalBrands FROM Brand b), "
					+ "(SELECT COUNT(DISTINCT p.id) AS totalProducts FROM Product p), "
					+ "(SELECT COUNT(DISTINCT q.id) AS totalQuestions FROM Question q), "
					+ "(SELECT COUNT(DISTINCT r.id) AS totalReviews FROM Review r), "
					+ "(SELECT COUNT(DISTINCT cu.id) AS totalCustomers FROM Customer cu), "
					+ "(SELECT COUNT(DISTINCT o.id) AS totalOrders FROM Order o), "
					+ "(SELECT COUNT(DISTINCT sr.id) AS totalShippingRates FROM ShippingRate sr), "
					+ "(SELECT COUNT(DISTINCT a.id) AS totalArticles FROM Article a), "
					+ "(SELECT COUNT(DISTINCT m.id) AS totalMenuItems FROM Menu m), "
					+ "(SELECT COUNT(DISTINCT se.id) AS totalSections FROM Section se), "
					+ "(SELECT COUNT(DISTINCT u.id) AS disabledUsers FROM User u WHERE u.enabled=false), "
					+ "(SELECT COUNT(DISTINCT u.id) AS enabledUsers FROM User u WHERE u.enabled=true), "
					+ "(SELECT COUNT(DISTINCT c.id) AS rootCategories FROM Category c WHERE c.parent is null), "
					+ "(SELECT COUNT(DISTINCT c.id) AS enabledCategories FROM Category c WHERE c.enabled=true), "
					+ "(SELECT COUNT(DISTINCT c.id) AS disabledCategories FROM Category c WHERE c.enabled=false), "
					+ "(SELECT COUNT(DISTINCT p.id) AS enabledProducts FROM Product p WHERE p.enabled=true), "
					+ "(SELECT COUNT(DISTINCT p.id) AS disabledProducts FROM Product p WHERE p.enabled=false), "
					+ "(SELECT COUNT(DISTINCT p.id) AS inStockProducts FROM Product p WHERE p.inStock=true), "
					+ "(SELECT COUNT(DISTINCT p.id) AS outOfStockProducts FROM Product p WHERE p.inStock=false), "
					+ "(SELECT COUNT(DISTINCT q.id) AS approvedQuestions FROM Question q WHERE q.approved=true), "
					+ "(SELECT COUNT(DISTINCT q.id) AS unapprovedQuestions FROM Question q WHERE q.approved=false), "
					+ "(SELECT COUNT(DISTINCT q.id) AS answeredQuestions FROM Question q WHERE q.answer is not null), "
					+ "(SELECT COUNT(DISTINCT q.id) AS unansweredQuestions FROM Question q WHERE q.answer is null), "
					+ "(SELECT COUNT(DISTINCT cu.id) AS enabledCustomers FROM Customer cu WHERE cu.enabled=true), "
					+ "(SELECT COUNT(DISTINCT cu.id) AS disabledCustomers FROM Customer cu WHERE cu.enabled=false), "
					+ "(SELECT COUNT(DISTINCT sr.id) AS codShippingRates FROM ShippingRate sr WHERE sr.codSupported=true), "
					+ "(SELECT COUNT(DISTINCT o.id) AS newOrders FROM Order o WHERE o.status = 'NEW'), "
					+ "(SELECT COUNT(DISTINCT o.id) AS newOrders FROM Order o WHERE o.status = 'DELIVERED'), "
					+ "(SELECT COUNT(DISTINCT o.id) AS newOrders FROM Order o WHERE o.status = 'PROCESSING'), "
					+ "(SELECT COUNT(DISTINCT a.id) AS menuBoundArticles FROM Article a WHERE a.type=0), "
					+ "(SELECT COUNT(DISTINCT a.id) AS freeArticles FROM Article a WHERE a.type=1), "
					+ "(SELECT COUNT(DISTINCT m.id) AS headerMenuItems FROM Menu m WHERE m.type=0), "
					+ "(SELECT COUNT(DISTINCT m.id) AS footerMenuItems FROM Menu m WHERE m.type=1), "
					+ "(SELECT COUNT(DISTINCT r.product.id) AS reviewedProducts FROM Review r), "
					+ "(SELECT COUNT(DISTINCT c.id) AS totalCountries FROM Country c), "
					+ "(SELECT COUNT(DISTINCT s.id) AS totalStates FROM State s), "
					+ "st.value as siteName,"
					+ "(SELECT st.value as currencySymbol FROM Setting st WHERE st.key='currency_symbol'), "
					+ "(SELECT st.value as decimalPointType FROM Setting st WHERE st.key='decimal_point_type'), "
					+ "(SELECT st.value as decimalDigits FROM Setting st WHERE st.key='decimal_digits'), "
					+ "(SELECT st.value as thousandsPointType FROM Setting st WHERE st.key='thousands_point_type') "
					+ "FROM Setting st WHERE st.key='site_name'");
		List<Object[]> entityCounts = query.getResultList();
		Object[] arrayCounts = entityCounts.get(0);
		
		summary.setTotalUsers((Long) arrayCounts[0]);
		summary.setTotalCategories((Long) arrayCounts[1]);
		summary.setTotalBrands((Long) arrayCounts[2]);
		summary.setTotalProducts((Long) arrayCounts[3]);
		summary.setTotalQuestions((Long) arrayCounts[4]);
		summary.setTotalReviews((Long) arrayCounts[5]);
		summary.setTotalCustomers((Long) arrayCounts[6]);
		summary.setTotalOrders((Long) arrayCounts[7]);
		summary.setTotalShippingRates((Long) arrayCounts[8]);
		summary.setTotalArticles((Long) arrayCounts[9]);
		summary.setTotalMenuItems((Long) arrayCounts[10]);
		summary.setTotalSections((Long) arrayCounts[11]);
		summary.setDisabledUsersCount((Long) arrayCounts[12]);
		summary.setEnabledUsersCount((Long) arrayCounts[13]);
		summary.setRootCategoriesCount((Long) arrayCounts[14]);
		summary.setEnabledCategoriesCount((Long) arrayCounts[15]);
		summary.setDisabledCategoriesCount((Long) arrayCounts[16]);
		
		summary.setEnabledProductsCount((Long) arrayCounts[17]);
		summary.setDisabledProductsCount((Long) arrayCounts[18]);
		summary.setInStockProductsCount((Long) arrayCounts[19]);
		summary.setOutOfStockProductsCount((Long) arrayCounts[20]);

		summary.setApprovedQuestionsCount((Long) arrayCounts[21]);
		summary.setUnapprovedQuestionsCount((Long) arrayCounts[22]);
		summary.setAnsweredQuestionsCount((Long) arrayCounts[23]);
		summary.setUnansweredQuestionsCount((Long) arrayCounts[24]);

		summary.setEnabledCustomersCount((Long) arrayCounts[25]);
		summary.setDisabledCustomersCount((Long) arrayCounts[26]);
		
		summary.setCodShippingRateCount((Long) arrayCounts[27]);
		summary.setNewOrdersCount((Long) arrayCounts[28]);
		summary.setDeliveredOrdersCount((Long) arrayCounts[29]);
		summary.setProcessingOrdersCount((Long) arrayCounts[30]);
		
		summary.setMenuBoundArticlesCount((Long) arrayCounts[31]);
		summary.setFreeArticlesCount((Long) arrayCounts[32]);
		
		summary.setHeaderMenuItemsCount((Long) arrayCounts[33]);
		summary.setFooterMenuItemsCount((Long) arrayCounts[34]);
		
		summary.setReviewedProductsCount((Long) arrayCounts[35]);

		summary.setTotalCountries((Long) arrayCounts[36]);
		summary.setTotalStates((Long) arrayCounts[37]);
		
		summary.setSiteName((String) arrayCounts[38]);
		summary.setCurrencySymbol((String) arrayCounts[39]);
		summary.setDecimalPointType((String) arrayCounts[40]);
		summary.setDecimalDigits((String) arrayCounts[41]);
		summary.setThousandPointType((String) arrayCounts[42]);
		
		return summary;
	}
}
