package com.shopme.admin.report;

public class OrderReportItem {
	private String identifier;
	private float grossSales;
	private float netSales;
	private int ordersCount;
	private int productsCount;

	public OrderReportItem() {
		
	}
	
	public OrderReportItem(String identifier) {
		this.identifier = identifier;
	}
	
	public OrderReportItem(String identifier, float grossSales, float netSales) {
		this.identifier = identifier;
		this.grossSales = grossSales;
		this.netSales = netSales;
	}
	
	public OrderReportItem(String identifier, float grossSales, float netSales, int productsCount) {
		this.identifier = identifier;
		this.grossSales = grossSales;
		this.netSales = netSales;
		this.productsCount = productsCount;
	}	

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public float getGrossSales() {
		return grossSales;
	}

	public void setGrossSales(float grossSales) {
		this.grossSales = grossSales;
	}

	public float getNetSales() {
		return netSales;
	}

	public void setNetSales(float netSales) {
		this.netSales = netSales;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderReportItem other = (OrderReportItem) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	public int getOrdersCount() {
		return ordersCount;
	}

	public void setOrdersCount(int ordersCount) {
		this.ordersCount = ordersCount;
	}

	public void increaseOrderCount() {
		this.ordersCount++;
	}

	public int getProductsCount() {
		return productsCount;
	}

	public void setProductsCount(int productsCount) {
		this.productsCount = productsCount;
	}
	
	public void addProductCount(int count) {
		this.productsCount += count;
	}
	
	public void addGrossSales(float grossSales) {
		this.grossSales += grossSales;
	}
	
	public void addNetSales(float netSales) {
		this.netSales += netSales;
	}

	@Override
	public String toString() {
		return "OrderReportItem [identifier=" + identifier + ", grossSales=" + grossSales + ", netSales=" + netSales
				+ ", ordersCount=" + ordersCount + ", productsCount=" + productsCount + "]";
	}
		
}
